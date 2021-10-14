package app.helloteam.sportsbuddyapp.helperUI

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Extra padding at the bottom of the UI to prevent elements from
 * displaying underneath the NavBar
 */
@Composable
fun ExtraPadding() {
    Box(
        modifier = Modifier
            .padding(50.dp)
            .fillMaxWidth()
    )
}