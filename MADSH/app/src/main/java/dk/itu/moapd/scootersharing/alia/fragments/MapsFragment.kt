package dk.itu.moapd.scootersharing.alia.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
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
import dk.itu.moapd.scootersharing.alia.utils.DatabaseOperations
import dk.itu.moapd.scootersharing.alia.utils.FusedLocationService

class MapsFragment : Fragment(), OnMapReadyCallback, OnMarkerClickListener {

    private lateinit var startRideOverlay: FrameLayout
    private lateinit var endRideOverlay: FrameLayout

    companion object {
        private const val TAG = "MapsFragment"
        private var map: GoogleMap? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DatabaseOperations.initialize()

        FusedLocationService.initializeFusedLocation(requireContext())

        // Start the location-aware method.
        FusedLocationService.startLocationAware(this)
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
            .inflate(R.layout.popup_end_ride, null)
            .findViewById(R.id.end_ride_overlay)

        // Obtain the `SupportMapFragment` and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager.findFragmentById(R.id.google_maps) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onResume() {
        super.onResume()
        FusedLocationService.subscribeToLocationUpdates(requireContext())
    }

    override fun onPause() {
        super.onPause()
        FusedLocationService.unsubscribeToLocationUpdates()
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
        if (FusedLocationService.checkPermission(requireContext()))
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
                val location = LatLng(scooter.latitude, scooter.longitude)
                val marker = googleMap.addMarker(
                    MarkerOptions()
                        .position(location)
                        .title(scooter.name)
                )
                marker?.tag = scooter
            }
        }

        /*// Get device's location and move the camera.
        FusedLocationService.fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            val currentLocation = LatLng(location.latitude, location.longitude)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 18f))
        }*/
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val scooter = marker.tag as? Scooter ?: return false

        // Set the text and image for the popup/overlay.
        startRideOverlay.findViewById<TextView>(R.id.scooter_name).text = scooter.name

        startRideOverlay.findViewById<Button>(R.id.start_ride_button).setOnClickListener {
            DatabaseOperations.startNewRide(scooter.name, scooter.latitude, scooter.longitude)
            marker.remove()

            startRideOverlay.visibility = View.GONE
            (view as ViewGroup).removeView(startRideOverlay)

            // Inflate the popup_end_ride.xml layout.
            openEndRidePopup(scooter)
        }

        (view as ViewGroup).addView(startRideOverlay)
        startRideOverlay.visibility = View.VISIBLE

        return true
    }

    private fun openEndRidePopup(scooter: Scooter) {

        endRideOverlay.findViewById<TextView>(R.id.scooter_name).text = scooter.name

        endRideOverlay.findViewById<Button>(R.id.end_ride_button).setOnClickListener {
            showEndRideConfirmationDialog()
            FusedLocationService.getCurrentLocation(requireContext()) {
                val marker = map?.addMarker(
                    MarkerOptions()
                        .position(LatLng(it.latitude, it.longitude))
                        .title(scooter.name))
                marker?.tag = scooter
                endRideOverlay.visibility = View.GONE
                (view as ViewGroup).removeView(endRideOverlay)
            }
        }

        (view as ViewGroup).addView(endRideOverlay)
        endRideOverlay.visibility = View.VISIBLE
    }

    private fun showEndRideConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.endRideDialogTitle)
            .setMessage(R.string.endRideDialogMessage)
            .setCancelable(true)
            .setPositiveButton(R.string.yes) { _, _ ->
                DatabaseOperations.endCurrentRide(requireContext())
                endRideOverlay.visibility = View.GONE
                (view as ViewGroup).removeView(endRideOverlay)
            }
            .setNegativeButton(R.string.no) { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }
}