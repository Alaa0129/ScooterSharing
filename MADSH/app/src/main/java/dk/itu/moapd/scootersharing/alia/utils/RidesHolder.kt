package dk.itu.moapd.scootersharing.alia.utils

import androidx.recyclerview.widget.RecyclerView
import dk.itu.moapd.scootersharing.alia.R
import dk.itu.moapd.scootersharing.alia.databinding.ListRidesBinding
import dk.itu.moapd.scootersharing.alia.models.Ride
import java.lang.Math.round
import kotlin.math.roundToInt

class RidesHolder(private val binding: ListRidesBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(ride: Ride) {
        binding.rideScooterName.text = binding.root.context.getString(R.string.ride_scooter_name, ride.scooter)
        if (ride.endTime != null) {
            val duration = ((ride.endTime!! - ride.startTime!!).toDouble() / 1000 / 60 * 100).roundToInt() / 100.0
            binding.rideDuration.text = binding.root.context.getString(R.string.ride_duration, duration.toString())
            binding.ridePrice.text = binding.root.context.getString(R.string.ride_price, ((duration * 1.5 * 100).roundToInt() / 100.0).toString())
        }
    }
}