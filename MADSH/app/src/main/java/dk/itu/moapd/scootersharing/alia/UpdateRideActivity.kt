package dk.itu.moapd.scootersharing.alia

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import dk.itu.moapd.scootersharing.alia.databinding.ActivityUpdateRideBinding

/**
 * An activity class with methods to manage the start ride activity of ScooterSharing application.
 */
class UpdateRideActivity : AppCompatActivity() {

    // A set of private constants used in this class.
    companion object {
        private val TAG = UpdateRideActivity::class.qualifiedName
        lateinit var ridesDB : RidesDB
    }

    /**
     * View binding is a feature that allows you to more easily write code that interacts with
     * views. Once view binding is enabled in a module, it generates a binding class for each XML
     * layout file present in that module. An instance of a binding class contains direct references
     * to all views that have an ID in the corresponding layout.
     */
    // GUI variables.
    private lateinit var updateRideBinding: ActivityUpdateRideBinding

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
        setContentView(R.layout.activity_update_ride)
        ridesDB = RidesDB.get(this)


        // Migrate from Kotlin synthetics to Jetpack view binding.
        // https://developer.android.com/topic/libraries/view-binding/migration
        updateRideBinding = ActivityUpdateRideBinding.inflate(layoutInflater)

        // Define the behaviour of the UI button.
        updateRideBinding.apply {

           // The button "Update Ride" listener.
            updateRideButton.setOnClickListener {
                if (editTextLocation.text!!.isNotEmpty()) {

                    // Update the object attributes.
                    val nameLocation = editTextLocation.text.toString().trim()

                    ridesDB.updateCurrentScooterLocation(nameLocation)

                    // Reset the text fields and update the UI.
                    editTextLocation.text?.clear()

                    // Display ride info above editTextName, using a snackbar.
                    val snackbar = Snackbar.make(it, "Ride started using ${ridesDB.getCurrentScooter()}", Snackbar.LENGTH_LONG)
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
        setContentView(updateRideBinding.root)
    }

    private fun showMessage () {
        // Print a message in the ‘Logcat‘ system.
        Log.d(UpdateRideActivity.TAG, ridesDB.getCurrentScooterInfo())
    }
}