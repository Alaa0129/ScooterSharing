package dk.itu.moapd.scootersharing.alia.interfaces

import dk.itu.moapd.scootersharing.alia.models.Ride
import dk.itu.moapd.scootersharing.alia.models.Scooter

interface ItemClickListener {
    fun onItemClickListener(ride: Ride, position: Int)
    fun onItemClickListener(scooter: Scooter, position: Int)
}