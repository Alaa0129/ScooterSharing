package dk.itu.moapd.scootersharing.alia.fragments

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dk.itu.moapd.scootersharing.alia.R
import dk.itu.moapd.scootersharing.alia.models.Scooter
import dk.itu.moapd.scootersharing.alia.services.LocationService
import dk.itu.moapd.scootersharing.alia.utils.DatabaseOperations
import java.io.ByteArrayOutputStream

class MapsFragment : Fragment(), OnMapReadyCallback, OnMarkerClickListener {

    private lateinit var startRideOverlay: FrameLayout
    private lateinit var endRideOverlay: FrameLayout

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var rideScooter: Scooter? = null

    companion object {
        private const val TAG = "MapsFragment"
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private var map: GoogleMap? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DatabaseOperations.initialize(requireContext())
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startRideOverlay = LayoutInflater.from(requireContext())
            .inflate(R.layout.start_ride_overlay, null)
            .findViewById(R.id.start_ride_overlay)

        endRideOverlay = LayoutInflater.from(requireContext())
            .inflate(R.layout.end_ride_overlay, null)
            .findViewById(R.id.end_ride_overlay)

        // Obtain the `SupportMapFragment` and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager.findFragmentById(R.id.google_maps) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Show the "end ride" overlay if a ride is ongoing.
        /*DatabaseOperations.getCurrentRideRef {
            if (it != null) {


                DatabaseOperations.getScooterDetailsByName(it.child())
            }
        }*/
    }

    /**
     * Called when the map is ready to be used.  Note that this does not guarantee that the map has
     * undergone layout.  Therefore, the map's size may not have been determined by the time the
     * callback method is called.  If you need to know the dimensions or call a method in the API
     * that needs to know the dimensions, get the map's `View` and register an
     * `ViewTreeObserver.OnGlobalLayoutListener` as well.
     *
     * @param googleMap A non-null instance of a `GoogleMap` associated with the `MapFragment` or
     *      `MapView` that defines the callback.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Check if the user allows the application to access the location-aware resources.
        if (LocationService.checkPermission(requireContext()))
            return

        // Show the current device's location as a blue dot.
        googleMap.isMyLocationEnabled = true

        // Set the default map type.
        googleMap.mapType = GoogleMap.MAP_TYPE_HYBRID

        // Setup the UI settings state.
        googleMap.uiSettings.apply {
            isCompassEnabled = true
            isIndoorLevelPickerEnabled = true
            isMyLocationButtonEnabled = true
            isRotateGesturesEnabled = true
            isScrollGesturesEnabled = true
            isTiltGesturesEnabled = true
            isZoomControlsEnabled = true
            isZoomGesturesEnabled = true
        }

        // Move the Google Maps UI buttons under the OS top bar.
        googleMap.setPadding(0, 100, 0, 0)

        // Set a listener for marker click.
        googleMap.setOnMarkerClickListener(this)

        // Fetch list of scooters from the database.
        DatabaseOperations.getAllScooters {
            // Add a marker for each scooter.
            it!!.forEach { scooter ->
                if (scooter.available) {
                    val location = LatLng(scooter.latitude, scooter.longitude)
                    val marker = googleMap.addMarker(
                        MarkerOptions()
                            .position(location)
                            .title(scooter.name)
                    )
                    marker?.tag = scooter
                }
            }
        }

        // Get device's location and move the camera.
        fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener {
            val currentLocation = LatLng(it.latitude, it.longitude)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 18f))
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val scooter = marker.tag as? Scooter ?: return false

        // Set the text and image for the popup/overlay.
        startRideOverlay.findViewById<TextView>(R.id.scooter_name).text = scooter.name
        Glide.with(requireContext())
            .load(scooter.lastPhoto)
            .into(startRideOverlay.findViewById(R.id.scooter_image))


        startRideOverlay.findViewById<Button>(R.id.start_ride_button).setOnClickListener {
            DatabaseOperations.startNewRide(scooter.name, scooter.latitude, scooter.longitude)
            marker.remove()

            Intent(requireContext(), LocationService::class.java).also { intent ->
                requireContext().startService(intent)
            }

            (view as ViewGroup).removeView(startRideOverlay)

            openEndRidePopup(scooter)
        }

        (view as ViewGroup).addView(startRideOverlay)

        return true
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            val imageAsByteArray = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, imageAsByteArray)
            DatabaseOperations.uploadNewScooterPhoto(rideScooter!!.name, imageAsByteArray.toByteArray())
            rideScooter = null
        }
    }

    private fun openEndRidePopup(scooter: Scooter) {

        endRideOverlay.findViewById<TextView>(R.id.scooter_name).text = scooter.name

        if (LocationService.checkPermission(requireContext()))
            return

        endRideOverlay.findViewById<Button>(R.id.end_ride_button).setOnClickListener {
            showEndRideConfirmationDialog()

            rideScooter = scooter

            fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                val marker = map?.addMarker(
                    MarkerOptions()
                        .position(LatLng(it.latitude, it.longitude))
                        .title(scooter.name))
                marker?.tag = scooter

                Intent(requireContext(), LocationService::class.java).also { intent ->
                    requireContext().stopService(intent)
                }
            }
            (view as ViewGroup).removeView(endRideOverlay)
        }
        (view as ViewGroup).addView(endRideOverlay)
    }

    private fun showEndRideConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.endRideDialogTitle)
            .setMessage(R.string.endRideDialogMessage)
            .setCancelable(true)
            .setPositiveButton(R.string.yes) { _, _ ->
                DatabaseOperations.endCurrentRide(requireContext())

                (view as ViewGroup).removeView(endRideOverlay)

                showTakePhotoDialog()
            }
            .setNegativeButton(R.string.no) { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    private fun showTakePhotoDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Take photo")
            .setMessage("Please take a photo of the scooter")
            .setCancelable(false)
            .setPositiveButton("Take photo") { _, _ ->
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                try {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                } catch (e: ActivityNotFoundException) {
                    // display error state to the user
                }
            }
            .setNegativeButton("Close") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }
}