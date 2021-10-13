package app.helloteam.sportsbuddyapp.helperUI

import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import app.helloteam.sportsbuddyapp.views.ui.theme.Purple200


// Top Bar
@Composable
fun TopBar() {
    TopAppBar(
        title = { Text(text = "Create Event", fontSize = 20.sp) },
        backgroundColor = Purple200,
        contentColor = Color.White
    )
}