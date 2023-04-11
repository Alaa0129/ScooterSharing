package dk.itu.moapd.scootersharing.alia

import android.content.Context
import dk.itu.moapd.scootersharing.alia.models.Scooter
import java.text.DateFormat
import kotlin.collections.ArrayList

class RidesDB private constructor(context: Context) {
    private val rides = ArrayList<Scooter>()
    companion object : RidesDBHolder <RidesDB, Context>(:: RidesDB )

    init {
        rides.add(Scooter("CPH001", "ITU"))
        rides.add(Scooter("CPH002", "Fields"))
        rides.add(Scooter("CPH003", "DR Byen"))
        rides.add(Scooter("CPH004", "Nørrebro St."))
        rides.add(Scooter("CPH005", "Lufthavn"))
        rides.add(Scooter("CPH006", "Roskilde"))
        rides.add(Scooter("CPH007", "Fisketorvet"))
        rides.add(Scooter("CPH008", "Ballerup"))
        rides.add(Scooter("CPH009", "Vestamager"))
        rides.add(Scooter("CPH0010", "Islands Brygge"))
        rides.add(Scooter("CPH0011", "Nørreport"))
        rides.add(Scooter("CPH0012", "Vesterbro"))
        rides.add(Scooter("CPH0013", "Hovedbanegården"))
        rides.add(Scooter("CPH0014", "Istedgade"))
        rides.add(Scooter("CPH0015", "Rødovre Centrum"))
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
        return "Name: ${rides.last().name}, location: ${rides.last().location}, time: ${DateFormat.getDateTimeInstance().format(rides.last().timestamp)}"
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