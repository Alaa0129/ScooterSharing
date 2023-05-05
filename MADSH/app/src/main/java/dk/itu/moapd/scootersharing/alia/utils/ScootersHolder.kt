package dk.itu.moapd.scootersharing.alia.utils

import androidx.recyclerview.widget.RecyclerView
import dk.itu.moapd.scootersharing.alia.R
import dk.itu.moapd.scootersharing.alia.databinding.ListScootersBinding
import dk.itu.moapd.scootersharing.alia.models.Scooter

class ScootersHolder(private val binding: ListScootersBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(scooter: Scooter) {
        binding.scooterName.text = binding.root.context.getString(R.string.scooter_name, scooter.name)
    }
}