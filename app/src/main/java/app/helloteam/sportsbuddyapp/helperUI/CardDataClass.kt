package app.helloteam.sportsbuddyapp.helperUI

/**
 * Event Object
 *
 * Holds event information, used in ...
 * Landing - your events
 * Landing - recommended events
 * EventList - Event List
 * Event View
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
    val startingTime: String,
    val startingDate: String,
    val locationName: String,
)

/**
 * Attendee Object
 * * Holds user attendee information, used in ...

 * Event View
 */
data class AttendeesCard(
    val userID: String,
    val name: String,
    val profileImage: String,
)








