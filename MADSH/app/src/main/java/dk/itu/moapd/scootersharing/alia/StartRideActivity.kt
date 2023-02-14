package dk.itu.moapd.scootersharing.alia

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import dk.itu.moapd.scootersharing.alia.databinding.ActivityStartRideBinding

/**
 * An activity class with methods to manage the start ride activity of ScooterSharing application.
 */
class StartRideActivity : AppCompatActivity() {

    // A set of private constants used in this class.
    companion object {
        private val TAG = StartRideActivity::class.qualifiedName
    }

    /**
     * View binding is a feature that allows you to more easily write code that interacts with
     * views. Once view binding is enabled in a module, it generates a binding class for each XML
     * layout file present in that module. An instance of a binding class contains direct references
     * to all views that have an ID in the corresponding layout.
     */
    // GUI variables.
    private lateinit var startRideBinding: ActivityStartRideBinding

    /**
     * A @property Scooter used to pass data into by the user. Initially @returns an empty scooter.
     */
    private val scooter:Scooter = Scooter("", "")

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
        setContentView(R.layout.activity_start_ride)

        // Migrate from Kotlin synthetics to Jetpack view binding.
        // https://developer.android.com/topic/libraries/view-binding/migration
        startRideBinding = ActivityStartRideBinding.inflate(layoutInflater)

        // Define the behaviour of the UI button.
        startRideBinding.apply {

            // The button "Start Ride" listener.
            startRideButton.setOnClickListener {
                if (editTextName.text!!.isNotEmpty() && editTextLocation.text!!.isNotEmpty()) {

                    // Update the object attributes.
                    val nameScooter = editTextName.text.toString().trim()
                    val nameLocation = editTextLocation.text.toString().trim()

                    scooter.name = nameScooter
                    scooter.location = nameLocation

                    // Reset the text fields and update the UI.
                    editTextName.text?.clear()
                    editTextLocation.text?.clear()

                    // Display ride info above editTextName, using a snackbar.
                    val snackbar = Snackbar.make(it, "Ride started using $scooter", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                    snackbar.setActionTextColor(getColor(R.color.lightGrey))
                    snackbar.anchorView = editTextName
                    val snackbarView = snackbar.view
                    snackbarView.setBackgroundColor(getColor(R.color.snackBarColor))
                    snackbar.duration = 5000

                    snackbar.show()

                    showMessage()
                }
            }
        }

        // Inflate the user interface into the current activity.
        setContentView(startRideBinding.root)
    }

    private fun showMessage () {
        // Print a message in the ‘Logcat‘ system.
        Log.d(StartRideActivity.TAG, scooter.toString())
    }
}