package dk.itu.moapd.scootersharing.alia.services

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import com.google.android.gms.location.Priority
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LocationService : Service() {

    /**
     * This callback is called when `FusedLocationProviderClient` has a new `Location`.
     */
    private lateinit var locationCallback: LocationCallback

    /**
     * The primary instance for receiving location updates.
     */
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    companion object {
        private const val TAG = "LocationService"

        /**
         * This method checks if the user allows the application uses all location-aware resources to
         * monitor the user's location.
         *
         * @return A boolean value with the user permission agreement.
         */
        fun checkPermission(context: Context) =
            ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                        context, Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate() {
        super.onCreate()

        fusedLocationProviderClient = getFusedLocationProviderClient(this)
        startLocationAware()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "LocationService",
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, "LocationService")
            .setContentTitle("Location Service")
            .setContentText("Tracking location...")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .build()

        startForeground(1, notification)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        subscribeToLocationUpdates(this)

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        unsubscribeToLocationUpdates()
    }

    private fun startLocationAware() {
        locationCallback = object : LocationCallback() {
            /**
             * This method will be executed when `FusedLocationProviderClient` has a new location.
             *
             * @param locationResult The last known location.
             */
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                // Updates the user interface components with GPS data location.
                locationResult.lastLocation?.let { location ->
                    if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
                        Log.d(TAG,"Latitude: ${location.latitude}, Longitude: ${location.longitude}, Date: ${location.time.toDateString()}")
                    else
                        setAddress(location.latitude, location.longitude)
                }
            }
        }
    }

    /**
     * Subscribes this application to get the location changes via the `locationCallback()`.
     */
    private fun subscribeToLocationUpdates(context: Context) {

        // Check if the user allows the application to access the location-aware resources.
        if (checkPermission(context))
            return

        // Sets the accuracy and desired interval for active location updates.
        val locationRequest = LocationRequest
            .Builder(Priority.PRIORITY_HIGH_ACCURACY, 20)
            .build()

        // Subscribe to location changes.
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest, locationCallback, Looper.getMainLooper()
        )
    }

    /**
     * Unsubscribes this application of getting the location changes from the`locationCallback()`.
     */
    private fun unsubscribeToLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    /**
     * Return the timestamp as a `String`.
     *
     * @return The timestamp formatted as a `String` using the default locale.
     */
    private fun Long.toDateString() : String {
        val date = Date(this)
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        return format.format(date)
    }

    /**
     * Use Geocoder API to convert the current location into a `String` address, and update the
     * corresponding UI component.
     *
     * @param latitude The current latitude coordinate.
     * @param longitude The current longitude coordinate.
     */
    private fun setAddress(latitude: Double, longitude: Double) {
        if (!Geocoder.isPresent())
            return

        // Create the `Geocoder` instance.
        val geocoder = Geocoder(this, Locale.getDefault())

        // After `Tiramisu Android OS`, it is needed to use a listener to avoid blocking the main
        // thread waiting for results.
        val geocodeListener = Geocoder.GeocodeListener { addresses ->
            addresses.firstOrNull()?.toAddressString()?.let { address ->
                Log.d(TAG, "Address: $address")
            }
        }

        // Return an array of Addresses that attempt to describe the area immediately surrounding
        // the given latitude and longitude.
        if (Build.VERSION.SDK_INT >= 33)
            geocoder.getFromLocation(latitude, longitude, 1, geocodeListener)
        else
            geocoder.getFromLocation(latitude, longitude, 1)
                ?.let {  addresses ->
                    addresses.firstOrNull()?.toAddressString()?.let { address ->
                        Log.d(TAG, "Address: $address")
                    }
                }
    }

    /**
     * Converts the `Address` instance into a `String` representation.
     *
     * @return A `String` with the current address.
     */
    private fun Address.toAddressString() : String {
        val address = this

        // Create a `String` with multiple lines.
        val stringBuilder = StringBuilder()
        stringBuilder.apply {
            append(address.getAddressLine(0)).append("\n")
            append(address.postalCode).append(" ")
            append(address.locality).append("\n")
            append(address.countryName)
        }

        return stringBuilder.toString()
    }
}