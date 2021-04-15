package app.helloteam.sportsbuddyapp.models
import app.helloteam.sportsbuddyapp.data.SportTypes
import java.time.LocalDateTime

class SportEvents constructor(
    val type: SportTypes,
    val userName: String,
    val eventPlaceID: String,
    val date: java.util.Date,
    val endDate: java.util.Date //for end time
) {
}