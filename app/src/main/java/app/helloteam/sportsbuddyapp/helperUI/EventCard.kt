package app.helloteam.sportsbuddyapp.helperUI

/**
 * Event Object
 *
 * This class returns a listOf EventCard objects
 * @param title event title
 * @param eventID eventID
 * @param imageId location's imageID for the event
 * @param isHosting if the currentUser is hosting this event
 * @param hostName the host's name
 * @param eventDesc event description
 * @param space the space available
 * @param currentlyAttending the number of people currently attending
 * @param activityType activity
 */

data class EventCard(
    val title: String,
    val eventID: String,
    val locationID: String,
    val imageId: String,
    val isHosting: Boolean,
    val hostName: String,
    val eventDesc: String,
    val space: Int,
    val currentlyAttending: Int,
    val activityType: String,
)

data class LocationCard(
    val title: String,

    )








