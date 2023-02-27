package dk.itu.moapd.scootersharing.alia

import android.content.Context
import java.text.DateFormat
import java.util.Random

class RidesDB private constructor(context: Context) {
    private val rides = ArrayList<Scooter>()
    companion object : RidesDBHolder <RidesDB, Context>(:: RidesDB )

    init {
        rides.add(Scooter("CPH001", "ITU", randomDate()))
        rides.add(Scooter("CPH002", "Fields", randomDate()))
        rides.add(Scooter("CPH003", "DR Byen", randomDate()))
        rides.add(Scooter("CPH004", "Nørrebro St.", randomDate()))
        rides.add(Scooter("CPH005", "Lufthavn", randomDate()))
    }

    /**
     * @return A list of all scooters.
     */
    fun getRidesList() : List<Scooter> {
        return rides
    }

    /**
     * Add a scooter to the singleton.
     */
    fun addScooter(name: String, location: String) {
        rides.add(Scooter(name, location))
    }

    /**
     * Update the location of most recently created scooter.
     */
    fun updateCurrentScooterLocation(location : String) {
        rides.last().location = location
    }

    /**
     * @return The most recently created scooter.
     */
    fun getCurrentScooter () : Scooter {
        return rides.last()
    }

    /**
     * Display information about the most recently created scooter.
     *
     * @return Information about the most recently created scooter.
     */
    fun getCurrentScooterInfo() : String {
        return "Name: ${rides.last().name}, location: ${rides.last().location}, time: ${DateFormat.getDateTimeInstance().format(rides.last().timestamp)}"
    }

    /**
     * Generate a random timestamp in the last 365 days .
     *
     * @return A random timestamp in the last year .
     */
    private fun randomDate() : Long {
        val random = Random()
        val now = System.currentTimeMillis()
        val year = random.nextDouble() * 1000 * 60 * 60 * 24 * 365
        return (now - year).toLong()
    }
}

open class RidesDBHolder<out T: Any, in A>(creator: (A) -> T) {
    private var creator: ((A) -> T)? = creator

    @Volatile private var instance : T ? = null

    fun get ( arg : A ) : T {
        val checkInstance = instance
        if (checkInstance != null)
            return checkInstance

        return synchronized(this) {
            val checkInstanceAgain = instance
            if (checkInstanceAgain != null)
                checkInstanceAgain
            else {
                val created = creator!!(arg)
                instance = created
                creator = null
                created
            }
        }
    }
}