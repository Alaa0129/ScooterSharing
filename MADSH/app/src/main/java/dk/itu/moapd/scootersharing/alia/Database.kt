package dk.itu.moapd.scootersharing.alia

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.scootersharing.alia.models.Ride
import dk.itu.moapd.scootersharing.alia.models.Scooter

class Database {
    companion object {
        private lateinit var database: DatabaseReference
        private var user: FirebaseUser? = null

        fun initialize() {
            database = Firebase.database("https://scootersharing-jokf-alia-default-rtdb.europe-west1.firebasedatabase.app").reference
            user = Firebase.auth.currentUser
        }

        fun getScooterByName(name: String): Scooter? {
            val scooterRef = database.child("scooters").child(name)
            return scooterRef.get().result.getValue(Scooter::class.java)
        }

        fun getScooterRefByName(name: String): DatabaseReference {
            return database.child("scooters").child(name)
        }

        fun insertNewRide(ride: Ride) {
            if (user == null)
                throw Exception("User is not logged in")
            else
            {
                val newNodeRef = database.child("rides").child(user!!.uid).push()
                newNodeRef.setValue(ride)
            }
        }

        fun deleteRideFromPosition(position: Int) {
            if (user == null)
                throw Exception("User is not logged in")
            else
            {
                val rideRef = database.child("rides").child(user!!.uid).child(position.toString())
                rideRef.removeValue()
            }
        }
    }
}