package dk.itu.moapd.scootersharing.alia

import android.util.Log
import com.google.android.material.snackbar.Snackbar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import dk.itu.moapd.scootersharing.alia.databinding.FragmentStartRideBinding

/**
 * An activity class with methods to manage the start ride activity of ScooterSharing application.
 */
class StartRideFragment : Fragment() {

    // A set of private constants used in this class.
    companion object {
        private val TAG = StartRideFragment::class.qualifiedName
        lateinit var ridesDB : RidesDB
    }

    /**
     * View binding is a feature that allows you to more easily write code that interacts with
     * views. Once view binding is enabled in a module, it generates a binding class for each XML
     * layout file present in that module. An instance of a binding class contains direct references
     * to all views that have an ID in the corresponding layout.
     */
    // GUI variables.
    private lateinit var startRideBinding: FragmentStartRideBinding

    /**
     * Called when the activity is starting. This is where most initialization should go: calling
     * `setContentView(int)` to inflate the activity's UI, using view binding to
     * programmatically interact with widgets in the UI.
     *
     * You can call `finish()` from within this function, in which case `onDestroy()` will be
     * immediately called after `onCreate()` without any of the rest of the activity lifecycle
     * (`onStart()`, `onResume()`, onPause()`, etc) executing.
     *
     * <em>Derived classes must call through to the super class's implementation of this method. If
     * they do not, an exception will be thrown.</em>
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     * down then this Bundle contains the data it most recently supplied in `onSaveInstanceState()`.
     * <b><i>Note: Otherwise it is null.</i></b>
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.fragment_start_ride)
        ridesDB = RidesDB.get(requireContext())

        // Migrate from Kotlin synthetics to Jetpack view binding.
        // https://developer.android.com/topic/libraries/view-binding/migration
        startRideBinding = FragmentStartRideBinding.inflate(layoutInflater)

        // Define the behaviour of the UI button.
        startRideBinding.apply {

            // The button "Start Ride" listener.
            startRideButton.setOnClickListener {
                if (editTextName.text!!.isNotEmpty() && editTextLocation.text!!.isNotEmpty()) {

                    // Update the object attributes.
                    val nameScooter = editTextName.text.toString().trim()
                    val nameLocation = editTextLocation.text.toString().trim()

                    ridesDB.addScooter(nameScooter, nameLocation)

                    // Reset the text fields and update the UI.
                    editTextName.text?.clear()
                    editTextLocation.text?.clear()

                    // Display ride info above editTextName, using a snackbar.
                    val snackbar = Snackbar.make(it, "Ride started using ${ridesDB.getCurrentScooter()}", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                    snackbar.setActionTextColor(getColor(requireContext(), R.color.lightGrey))
                    snackbar.anchorView = editTextName
                    val snackbarView = snackbar.view
                    snackbarView.setBackgroundColor(getColor(requireContext(), R.color.snackBarColor))
                    snackbar.duration = 5000

                    snackbar.show()
                    showMessage()
                }
            }
        }

        // Inflate the user interface into the current activity.
//        setContentView(startRideBinding.root)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_start_ride, container, false)
    }

    private fun showMessage () {
        // Print a message in the ‘Logcat‘ system.
        Log.d(StartRideFragment.TAG, ridesDB.getCurrentScooterInfo())
    }
}