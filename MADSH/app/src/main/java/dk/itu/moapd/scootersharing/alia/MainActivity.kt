package dk.itu.moapd.scootersharing.alia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.core.view.WindowCompat

class MainActivity : AppCompatActivity() {

    // A set of private constants used in this class.
    companion object {
        private val TAG = MainActivity::class.qualifiedName
    }

    // GUI variables.
    private lateinit var scooterName: EditText
    private lateinit var locationName: EditText
    private lateinit var startRideBtn: Button

    private val scooter:Scooter = Scooter("", "")

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    // Edit texts.
    

    private fun showMessage () {
        // Print a message in the ‘Logcat‘ system.
        Log.d(TAG, scooter.toString())
    }
}