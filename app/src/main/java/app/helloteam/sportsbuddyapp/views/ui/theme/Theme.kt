package app.helloteam.sportsbuddyapp.views.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun TeamUpTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    MaterialTheme(
        typography = app.helloteam.sportsbuddyapp.views.ui.theme.Typography,
        shapes = app.helloteam.sportsbuddyapp.views.ui.theme.Shapes,
        content = content
    )
}