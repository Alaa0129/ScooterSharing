package dk.itu.moapd.scootersharing.alia.utils

import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    private lateinit var geofenceList: List<Geofence>

    override fun onReceive(context: Context?, intent: Intent?) {
        val geofenceEvent = GeofencingEvent.fromIntent(intent!!)

        if (geofenceEvent!!.hasError()) {
            val errorMessage = GeofenceStatusCodes
                .getStatusCodeString(geofenceEvent.errorCode)
            Log.e(TAG, errorMessage)
            return
        }

        val transitionsTypes = geofenceEvent.geofenceTransition
        geofenceList = geofenceEvent.triggeringGeofences!!
        when (transitionsTypes) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                Toast.makeText(context, "You entered the geofence!", Toast.LENGTH_SHORT).show()
            }
            Geofence.GEOFENCE_TRANSITION_DWELL -> {
                Toast.makeText(context, "You are dwelling in the geofence!", Toast.LENGTH_SHORT).show()
            }
            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                Toast.makeText(context, "You left the geofence!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
