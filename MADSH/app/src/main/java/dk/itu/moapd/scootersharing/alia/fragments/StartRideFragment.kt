package dk.itu.moapd.scootersharing.alia.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.ServerValue
import dk.itu.moapd.scootersharing.alia.Database
import dk.itu.moapd.scootersharing.alia.R
import dk.itu.moapd.scootersharing.alia.databinding.FragmentStartRideBinding
import dk.itu.moapd.scootersharing.alia.models.Ride

class StartRideFragment : Fragment() {
    private var _binding: FragmentStartRideBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }
    companion object {
        private val TAG = StartRideFragment::class.qualifiedName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Database.initialize()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStartRideBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.startRideButton.setOnClickListener {
            val scooterRef = Database.getScooterRefByName("CPH03")
            if (scooterRef.key != null)
                Database.insertNewRide(Ride(scooterRef.key, ServerValue.TIMESTAMP, null, 55.61880355676757, 12.573134755857527, 55.69891195742094, 12.594372775696716, 0.0f))

            if (binding.editTextName.text!!.isNotEmpty() && binding.editTextLocation.text!!.isNotEmpty()) {

                // Reset the text fields and update the UI.
                binding.editTextName.text?.clear()
                binding.editTextLocation.text?.clear()

                // Display ride info above editTextName, using a snackbar.
                val snackbar =
                    Snackbar
                        .make(
                            it,
                            "Ride started",
                            Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                snackbar.setActionTextColor(requireContext().getColor(R.color.lightGrey))
                snackbar.anchorView = binding.editTextName
                val snackbarView = snackbar.view
                snackbarView.setBackgroundColor(requireContext().getColor(R.color.snackBarColor))
                snackbar.duration = 5000

                snackbar.show()
            }
        }
    }
}