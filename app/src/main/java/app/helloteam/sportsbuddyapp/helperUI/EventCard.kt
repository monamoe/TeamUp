package app.helloteam.sportsbuddyapp.helperUI

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color

// TO DO: constructor for the class takes an EventID and fills in the rest of the data itself
// TO DO send images through here

/**
 * Event Object
 *
 * This class is used to retrive Event Related Information for the event view as well
 * as the event cards on the landing page
 *
 * @param id the eventID
 * @property name
 * @constructor
 */
data class Event(
    val title: String,
    val id: String,
    val imageId: String,
    @DrawableRes
    val activityIcon: Int,
    val lightColor: Color,
    val mediumColor: Color,
    val darkColor: Color,
    val isHosting: Boolean,
    val hostName: String,
) {

}
