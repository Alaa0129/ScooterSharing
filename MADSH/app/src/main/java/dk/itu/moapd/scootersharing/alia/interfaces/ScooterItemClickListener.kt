package dk.itu.moapd.scootersharing.alia.interfaces

import dk.itu.moapd.scootersharing.alia.models.Scooter

interface ScooterItemClickListener {
    fun onItemClickListener(scooter: Scooter, position: Int)
}