package dk.itu.moapd.scootersharing.alia

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import dk.itu.moapd.scootersharing.alia.databinding.FragmentStartRideBinding

class StartRideFragment : Fragment() {
    private var _binding: FragmentStartRideBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }
    companion object {
        private val TAG = StartRideFragment::class.qualifiedName
        private lateinit var ridesDB: RidesDB
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ridesDB = RidesDB.get(requireContext())
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
            if (binding.editTextName.text!!.isNotEmpty() && binding.editTextLocation.text!!.isNotEmpty()) {

                // Update the object attributes.
                val nameScooter = binding.editTextName.text.toString().trim()
                val nameLocation = binding.editTextLocation.text.toString().trim()

                ridesDB.addScooter(nameScooter, nameLocation)

                // Reset the text fields and update the UI.
                binding.editTextName.text?.clear()
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