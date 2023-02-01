package dk.itu.moapd.scootersharing.alia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.core.view.WindowCompat

class MainActivity : AppCompatActivity() {

    // A set of private constants used in this class.
    companion object {
        private val TAG = MainActivity::class.qualifiedName
    }

    // GUI variables.
    private lateinit var scooterName: com.google.android.material.textfield.TextInputEditText
    private lateinit var scooterLocation: com.google.android.material.textfield.TextInputEditText
    private lateinit var startRideBtn: Button

    private val scooter:Scooter = Scooter("", "")

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Edit texts.
        scooterName = findViewById(R.id.edit_text_name)
        scooterLocation = findViewById(R.id.edit_text_location)

        // Buttons.
        startRideBtn = findViewById(R.id.start_ride_button)

        startRideBtn.setOnClickListener() {
            if (scooterName.text!!.isNotEmpty() && scooterLocation.text!!.isNotEmpty()) {

                // Update the object attributes.
                val nameScooter = scooterName.text.toString().trim()
                val nameLocation = scooterLocation.text.toString().trim()

                scooter.setName(nameScooter)
                scooter.setLocation(nameLocation)

                // Reset the text fields and update the UI.
                scooterName.text?.clear()
                scooterLocation.text?.clear()
                showMessage()
            }
        }
    }

    private fun showMessage () {
        // Print a message in the ‘Logcat‘ system.
        Log.d(TAG, scooter.toString())
    }
}