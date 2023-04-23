package dk.itu.moapd.scootersharing.alia.models

data class Ride(
    val scooter: String? = null,
    val startTime: Map<String, String>? = null,
    val endTime: Map<String, String>? = null,
    val startLatitude: Double? = null,
    val startLongitude: Double? = null,
    val endLatitude: Double? = null,
    val endLongitude: Double? = null,
    val price: Float? = null)