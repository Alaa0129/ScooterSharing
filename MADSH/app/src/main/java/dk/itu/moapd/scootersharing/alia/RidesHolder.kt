package dk.itu.moapd.scootersharing.alia

import androidx.recyclerview.widget.RecyclerView
import dk.itu.moapd.scootersharing.alia.databinding.ListRidesBinding

class RidesHolder(private val binding: ListRidesBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(scooter: Scooter) {
        binding.rideName.text = binding.root.context.getString(R.string.scooter_name, scooter.name)
        binding.rideLocation.text = binding.root.context.getString(R.string.scooter_location, scooter.location)
        binding.rideDate.text = binding.root.context.getString(R.string.ride_date, scooter.dateFormatted())
    }
}