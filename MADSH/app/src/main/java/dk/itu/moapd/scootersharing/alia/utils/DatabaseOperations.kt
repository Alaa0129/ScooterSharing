package dk.itu.moapd.scootersharing.alia.utils

import android.content.Context
import android.location.Location
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import dk.itu.moapd.scootersharing.alia.models.Ride
import dk.itu.moapd.scootersharing.alia.models.Scooter
import dk.itu.moapd.scootersharing.alia.services.LocationService
import java.text.SimpleDateFormat
import java.util.*

class DatabaseOperations {
    companion object {
        const val TAG = "DatabaseOperations"

        private var user: FirebaseUser? = null

        private lateinit var database: DatabaseReference
        private lateinit var storage: StorageReference
        private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

        fun initialize(context: Context) {
            database = Firebase.database("https://scootersharing-jokf-alia-default-rtdb.europe-west1.firebasedatabase.app").reference
            storage = Firebase.storage("gs://scootersharing-jokf-alia.appspot.com/").reference
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
            user = Firebase.auth.currentUser
        }

        fun getScooterRefByName(name: String): DatabaseReference {
            return database.child("scooters").child(name)
        }

        fun getScooterDetailsByName(name: String, callback: (Scooter?) -> Unit) {
            database.child("scooters").child(name).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val scooter = snapshot.getValue(Scooter::class.java)
                    if (scooter != null) {
                        scooter.name = snapshot.key!!
                    }
                    callback(scooter)
                }

                override fun onCancelled(error: DatabaseError) {
                    throw error.toException()
                }
            })
        }

        fun getAllScooters(callback: (List<Scooter>?) -> Unit) {
            if (user == null) {
                throw Exception("User is not logged in")
            } else {
                database.child("scooters").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val scooters = mutableListOf<Scooter>()
                        for (child in snapshot.children) {
                            val scooter = child.getValue(Scooter::class.java)
                            if (scooter != null) {
                                scooter.name = child.key!!
                                scooters.add(scooter)
                            }
                        }
                        callback(scooters)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        throw error.toException()
                    }
                })
            }
        }

        fun getCurrentRideRef(callback: (DatabaseReference?) -> Unit) {
            if (user == null) {
                throw Exception("User is not logged in")
            } else {
                database.child("rides").child(user!!.uid).orderByKey().limitToLast(1).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var ref: DatabaseReference? = null
                        for (child in snapshot.children) {
                            val endTime = child.child("endTime").getValue(Long::class.java)
                            if (endTime == null) {
                                ref = child.ref
                                break
                            }
                        }
                        callback(ref)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        throw error.toException()
                    }
                })
            }
        }

        fun startNewRide(scooterName: String, startLatitude: Double, startLongitude: Double) {
            if (user == null)
                throw Exception("User is not logged in")
            else
            {
                val newNodeRef = database.child("rides").child(user!!.uid).push()
                newNodeRef.child("scooter").setValue(scooterName)
                newNodeRef.child("startTime").setValue(ServerValue.TIMESTAMP)
                newNodeRef.child("startLatitude").setValue(startLatitude)
                newNodeRef.child("startLongitude").setValue(startLongitude)

                // Make scooter unavailable.
                val scooterRef = getScooterRefByName(scooterName)
                scooterRef.child("available").setValue(false)
            }
        }

        fun endCurrentRide(context: Context) {
            if (user == null)
                throw Exception("User is not logged in")
            else
            {
                if (LocationService.checkPermission(context))
                    return

                getCurrentRideRef { ref ->
                    if (ref != null) {
                        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
                            if (location != null) {
                                val endValuesMap = HashMap<String, Any>()

                                val endTime = ServerValue.TIMESTAMP
                                endValuesMap["endTime"] = endTime
                                ref.updateChildren(endValuesMap)

                                ref.get().addOnSuccessListener { ride ->
                                    val rideDetails = ride.getValue(Ride::class.java)

                                    val userLatitude = location.latitude
                                    val userLongitude = location.longitude
                                    val rideDurationInMs = (rideDetails!!.endTime!! - rideDetails.startTime!!).toDouble()
                                    val price = (rideDurationInMs / 1000 / 60 * 1.75)

                                    endValuesMap["endLatitude"] = userLatitude
                                    endValuesMap["endLongitude"] = userLongitude
                                    endValuesMap["price"] = price
                                    ref.updateChildren(endValuesMap)

                                    // Update scooter values.
                                    val scooterRef = getScooterRefByName(rideDetails.scooter!!)
                                    scooterRef.child("available").setValue(true)
                                    scooterRef.child("latitude").setValue(userLatitude)
                                    scooterRef.child("longitude").setValue(userLongitude)
                                }
                            }
                        }
                    } else {
                        Log.d(TAG,"No ongoing ride")
                    }
                }
            }
        }

        fun uploadNewScooterPhoto(scooterName: String, photo: ByteArray) {
            if (user == null)
                throw Exception("User is not logged in")
            else
            {
                // Upload photo to Firebase Storage.
                val today = Calendar.getInstance().time
                val formatter = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US)
                val date = formatter.format(today)
                val newPhotoRef = storage.child("scooter_images").child(scooterName).child("${date}.jpg")
                val uploadTask = newPhotoRef.putBytes(photo)
                uploadTask.addOnFailureListener {
                    Log.d(TAG, "Failed to upload photo")
                }.addOnSuccessListener {
                    Log.d(TAG, "Successfully uploaded photo")

                    // Update scooter photo URL in database.
                    newPhotoRef.downloadUrl.addOnSuccessListener {
                        val scooterRef = getScooterRefByName(scooterName)
                        scooterRef.child("lastPhoto").setValue(it.toString())
                    }.addOnFailureListener {
                        Log.d(TAG, "Failed to get photo URL")
                    }
                }
            }
        }
    }
}