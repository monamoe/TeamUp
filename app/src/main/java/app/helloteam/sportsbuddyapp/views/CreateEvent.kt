package app.helloteam.sportsbuddyapp.views

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import app.helloteam.sportsbuddyapp.views.ui.theme.Purple200


class CreateEvent : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DropDownView()
        }
    }


    @Composable
    @Preview
    fun DropDownView() {
        Scaffole(
            topBar = { TopBar() },
            backgroundColor = Purple200
        )
    }

    @Composable
    fun DropDown(

    ) {

    }

    @Composable
    fun TopBar() {
        TopAppBar {
            title = { Text(text = "Create Event", fontSize = 20.sp) }
        }
    }
}


