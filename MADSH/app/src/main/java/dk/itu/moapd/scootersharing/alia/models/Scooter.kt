package dk.itu.moapd.scootersharing.alia.models

data class Scooter (
    var name: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var available: Boolean = true,
    var lastPhoto: String = "") {

    override fun toString():String {
        return "'$name' is placed at ${coordinatesToLocation()}"
    }

    private fun coordinatesToLocation(): String {
        return ""
        //return "($latitude, $longitude)"
    }

    private fun dateFormatted(): String {
        return ""
        //return DateFormat.getDateTimeInstance().format(timestamp)
    }
}