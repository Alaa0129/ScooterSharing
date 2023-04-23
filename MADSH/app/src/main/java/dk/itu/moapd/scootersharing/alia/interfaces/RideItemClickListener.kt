package dk.itu.moapd.scootersharing.alia.interfaces

import dk.itu.moapd.scootersharing.alia.models.Ride

interface RideItemClickListener {
    fun onItemClickListener(ride: Ride, position: Int)
}