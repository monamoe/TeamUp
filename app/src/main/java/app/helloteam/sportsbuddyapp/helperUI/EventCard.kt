package app.helloteam.sportsbuddyapp.helperUI

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color

data class EventCard(
    val title: String,
    @DrawableRes
    val activityIcon: Int,
    val lightColor: Color,
    val mediumColor: Color,
    val darkColor: Color,
    val isHosting: Boolean,
) {

}
