package dk.itu.moapd.scootersharing.alia

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class DatabaseOperations {
    companion object {
        private lateinit var database: DatabaseReference
        private lateinit var storage: StorageReference
        private var user: FirebaseUser? = null

        fun initialize() {
            database = Firebase.database("https://scootersharing-jokf-alia-default-rtdb.europe-west1.firebasedatabase.app").reference
            storage = Firebase.storage("").reference
            user = Firebase.auth.currentUser
        }

        fun getScooterRefByName(name: String): DatabaseReference {
            return database.child("scooters").child(name)
        }

        fun getCurrentRideRef(): DatabaseReference? {
            if (user == null)
                throw Exception("User is not logged in")
            else
            {
                var ref: DatabaseReference? = null
                database.child("rides").child(user!!.uid).orderByKey().limitToLast(1).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (child in snapshot.children) {
                            var endTime = child.child("endTime").getValue(Long::class.java)
                            if (endTime == null) {
                                ref = child.ref
                                break
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        throw error.toException()
                    }
                })
                return ref
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

        fun endCurrentRide() {
            if (user == null)
                throw Exception("User is not logged in")
            else
            {
                val currentRideRef = getCurrentRideRef() ?: return

                // Update latitude and longitude to user's current location
                val userLatitude = 0.0
                val userLongitude = 0.0
                val endTime = ServerValue.TIMESTAMP
                val price = 69.0f // Update this to be based on time.

                val endValuesMap = HashMap<String, Any>()
                endValuesMap["endTime"] = endTime
                endValuesMap["endLatitude"] = userLatitude
                endValuesMap["endLongitude"] = userLongitude
                endValuesMap["price"] = price
                currentRideRef.updateChildren(endValuesMap)
            }
        }
    }
}