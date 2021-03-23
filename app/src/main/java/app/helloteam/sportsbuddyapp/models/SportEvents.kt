package app.helloteam.sportsbuddyapp.models

class SportEvents constructor(
    val type: SportTypes,
    val userName: String,
    val hour: Int,
    val minute: Int,
    val year: Int,
    val month: Int,
    val day: Int,
    val eventPlaceID: String,
    val date: java.util.Date
) {
}