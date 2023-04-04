package dk.itu.moapd.scootersharing.alia

import androidx.recyclerview.widget.RecyclerView
import dk.itu.moapd.scootersharing.alia.databinding.ListRidesBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class RidesHolder(private val binding: ListRidesBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(scooter: Scooter) {
        binding.rideName.text = binding.root.context.getString(R.string.scooter_name, scooter.name)
        binding.rideLocation.text = binding.root.context.getString(R.string.scooter_location, scooter.location)

        binding.deleteButton.setOnClickListener {
            showDeleteConfirmationDialog(scooter)
        }
    }

    private fun showDeleteConfirmationDialog(scooter: Scooter) {
        MaterialAlertDialogBuilder(binding.root.context)
            .setTitle(R.string.deleteRideDialogTitle)
            .setMessage(R.string.deleteRideDialogMessage)
            .setCancelable(true)
            .setPositiveButton(R.string.yes) { _, _ ->
                RidesDB.get(binding.root.context).removeScooter(scooter)
            }
            .setNegativeButton(R.string.no) { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    /*private fun showSnackbar() {
        val snackbar = Snackbar.make(, "Ride deleted", Snackbar.LENGTH_LONG)
    }*/
}