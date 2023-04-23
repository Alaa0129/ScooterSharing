package dk.itu.moapd.scootersharing.alia.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import dk.itu.moapd.scootersharing.alia.DatabaseOperations
import dk.itu.moapd.scootersharing.alia.R
import dk.itu.moapd.scootersharing.alia.RidesDB
import dk.itu.moapd.scootersharing.alia.databinding.FragmentUpdateRideBinding

class UpdateRideFragment : Fragment() {
    private var _binding: FragmentUpdateRideBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }
    companion object {
        private val TAG = FragmentUpdateRideBinding::class.qualifiedName
        private lateinit var ridesDB: RidesDB
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ridesDB = RidesDB.get(requireContext())
        DatabaseOperations.initialize()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateRideBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.updateRideButton.setOnClickListener {

            DatabaseOperations.endCurrentRide()

            if (binding.editTextLocation.text!!.isNotEmpty()) {

                // Reset the text fields and update the UI.
                binding.editTextLocation.text?.clear()

                // Display ride info above editTextName, using a snackbar.
                val snackbar =
                    Snackbar
                        .make(
                            it,
                            "Ride started using ${ridesDB.getCurrentScooter()}",
                            Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                snackbar.setActionTextColor(requireContext().getColor(R.color.lightGrey))
                snackbar.anchorView = binding.editTextName
                val snackbarView = snackbar.view
                snackbarView.setBackgroundColor(requireContext().getColor(R.color.snackBarColor))
                snackbar.duration = 5000

                snackbar.show()

                showMessage()
            }
        }
    }

    private fun showMessage () {
        // Print a message in the ‘Logcat‘ system.
        Log.d(TAG, ridesDB.getCurrentScooterInfo())
    }
}