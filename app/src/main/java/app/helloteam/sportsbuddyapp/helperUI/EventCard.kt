package app.helloteam.sportsbuddyapp.helperUI

/**
 * Event Object
 *
 * This class returns a listOf EventCard objects
 * @param title
 * @param eventID
 * @param imageId
 * @param isHosting
 * @param hostName
 * @param eventDesc
 * @param space
 * @param currentlyAttending
 */

data class EventCard(
    val title: String,
    val eventID: String,
    val imageId: String,
    val isHosting: Boolean,
    val hostName: String,
    val eventDesc: String,
    val space: Int,
    val currentlyAttending: Int,
) {


}








