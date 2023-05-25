package dk.itu.moapd.scootersharing.alia.utils

import android.content.Context
import android.content.ContextWrapper
import android.location.Location
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.maps.model.LatLng

class GeofenceHelper(context: Context) : ContextWrapper(context) {
    fun getGeofencingRequest(geofence: Geofence) : GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            addGeofence(geofence)
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
        }.build()
    }

    fun getGeofence(id: String, latLng: LatLng, radius: Float) : Geofence? {
        return Geofence.Builder()
            .setCircularRegion(latLng.latitude, latLng.longitude, radius)
            .setRequestId(id)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .setLoiteringDelay(1000)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .build()
    }

    fun isInGeofence(geofence: Geofence, latLng: LatLng) : Boolean {
        val geofenceLocation = Location("").apply {
            latitude = geofence.latitude
            longitude = geofence.longitude
        }

        val scooterLocation = Location("").apply {
            latitude = latLng.latitude
            longitude = latLng.longitude
        }

        val distance = scooterLocation.distanceTo(geofenceLocation)
        return distance <= geofence.radius
    }
}
