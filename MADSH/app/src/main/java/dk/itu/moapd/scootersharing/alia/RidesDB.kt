package dk.itu.moapd.scootersharing.alia

import android.content.Context
import dk.itu.moapd.scootersharing.alia.models.Scooter
import java.text.DateFormat
import kotlin.collections.ArrayList

class RidesDB private constructor(context: Context) {
    private val rides = ArrayList<Scooter>()
    companion object : RidesDBHolder <RidesDB, Context>(:: RidesDB )

    init {
        rides.add(Scooter("CPH001", "", 55.65969852879757, 12.59095799848658, true))
        rides.add(Scooter("CPH002", "", 55.65969852879757, 12.59095799848658, true))
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
    fun addScooter(name: String) {
        rides.add(Scooter(name, "", 55.65969852879757, 12.59095799848658, true))
    }

    fun removeScooter(scooter: Scooter) {
        rides.remove(scooter)
    }

    fun removeScooter(name: String) {
        val scooterToRemove = rides.firstOrNull { scooter -> scooter.name == name }
        if (scooterToRemove != null) {
            rides.remove(scooterToRemove)
        }
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
        return "Name: ${rides.last().name}}"
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