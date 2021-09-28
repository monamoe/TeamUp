package app.helloteam.sportsbuddyapp.helperUI

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.vector.ImageVector

data class NavMenuContent(
    val title: String,
    val icon: ImageVector,
    val route: String,
    val badgeCount: Int = 0
)
