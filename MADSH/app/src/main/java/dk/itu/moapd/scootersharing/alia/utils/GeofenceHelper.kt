package dk.itu.moapd.scootersharing.alia.utils

import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.maps.model.LatLng

class GeofenceHelper(context: Context) : ContextWrapper(context) {
    fun  getGeofencingRequest(geofence: Geofence) : GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            addGeofence(geofence)
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
        }.build()
    }

    fun getGeofence(id: String, latLng: LatLng, radius: Double, transitionTypes: Int) : Geofence? {
        return Geofence.Builder()
            .setCircularRegion(latLng.latitude, latLng.longitude, radius.toFloat())
            .setRequestId(id)
            .setTransitionTypes(transitionTypes)
            .setLoiteringDelay(10000)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .build()
    }

    val pendingIntent : PendingIntent by lazy {
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }


}
