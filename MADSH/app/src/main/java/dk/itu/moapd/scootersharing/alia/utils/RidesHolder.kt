package dk.itu.moapd.scootersharing.alia.utils

import androidx.recyclerview.widget.RecyclerView
import dk.itu.moapd.scootersharing.alia.databinding.ListRidesBinding
import dk.itu.moapd.scootersharing.alia.models.Ride

class RidesHolder(private val binding: ListRidesBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(ride: Ride) {
        binding.rideScooterName.text = ride.scooter
    }
}