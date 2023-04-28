package dk.itu.moapd.scootersharing.alia.utils

import android.content.Context
import android.location.Location
import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import dk.itu.moapd.scootersharing.alia.models.Ride
import dk.itu.moapd.scootersharing.alia.models.Scooter
import java.text.SimpleDateFormat
import java.util.*

class DatabaseOperations {
    companion object {
        private lateinit var database: DatabaseReference
        private lateinit var storage: StorageReference
        private var user: FirebaseUser? = null


        fun initialize() {
            database = Firebase.database("https://scootersharing-jokf-alia-default-rtdb.europe-west1.firebasedatabase.app").reference
            storage = Firebase.storage("gs://scootersharing-jokf-alia.appspot.com/").reference
            user = Firebase.auth.currentUser
        }

        fun getScooterRefByName(name: String): DatabaseReference {
            return database.child("scooters").child(name)
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
            }
        }

        fun endCurrentRide(context: Context) {
            if (user == null)
                throw Exception("User is not logged in")
            else
            {
                FusedLocationService.initializeFusedLocation(context)

                if (FusedLocationService.checkPermission(context))
                    return

                getCurrentRideRef { ref ->
                    if (ref != null) {
                        FusedLocationService.fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
                            if (location != null) {
                                val endValuesMap = HashMap<String, Any>()

                                val endTime = ServerValue.TIMESTAMP
                                endValuesMap["endTime"] = endTime
                                ref.updateChildren(endValuesMap)

                                ref.get().addOnSuccessListener { ride ->
                                    val rideDetails = ride.getValue(Ride::class.java)

                                    val userLatitude = location.latitude
                                    val userLongitude = location.longitude
                                    val rideDurationInMs = rideDetails!!.endTime!! - rideDetails.startTime!!
                                    val price = (rideDurationInMs / 1000 / 60 * 1.75).toFloat()

                                    endValuesMap["endLatitude"] = userLatitude
                                    endValuesMap["endLongitude"] = userLongitude
                                    endValuesMap["price"] = price
                                    ref.updateChildren(endValuesMap)
                                }
                            }
                        }
                    } else {
                        Log.d("DatabaseOperations","No ongoing ride")
                    }
                }
            }
        }

        fun uploadNewScooterPhoto(scooterName: String, photo: ByteArray) {
            if (user == null)
                throw Exception("User is not logged in")
            else
            {
                val today = Calendar.getInstance().time
                val formatter = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US)
                val date = formatter.format(today)
                val newPhotoRef = storage.child("scooter_images").child(scooterName).child("${date}.jpg")
                newPhotoRef.putBytes(photo)
            }
        }
    }
}