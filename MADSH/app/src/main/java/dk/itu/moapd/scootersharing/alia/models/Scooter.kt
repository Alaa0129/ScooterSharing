package dk.itu.moapd.scootersharing.alia.models

data class Scooter (
    var name: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var available: Boolean = true,
    var lastPhoto: String = "")