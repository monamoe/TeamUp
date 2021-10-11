/*
   monamoe
   10-10-2021
   allows the user to submit a form to create an event
   create event composable with xml AndroidView
   https://developer.android.com/jetpack/compose/interop/interop-apis#views-in-compose
 */

package app.helloteam.sportsbuddyapp.views

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.helloteam.sportsbuddyapp.R
import app.helloteam.sportsbuddyapp.databinding.ActivityCreateEventBinding
import app.helloteam.sportsbuddyapp.helperUI.BottomNavigationBar
import app.helloteam.sportsbuddyapp.helperUI.LoadingEvent
import app.helloteam.sportsbuddyapp.views.ui.theme.Purple200
import app.helloteam.sportsbuddyapp.views.ui.theme.Purple500
import app.helloteam.sportsbuddyapp.views.ui.theme.TeamUpTheme
import com.google.android.libraries.places.api.Places
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import androidx.compose.ui.viewinterop.AndroidViewBinding



@SuppressLint("StaticFieldLeak")
private lateinit var currentcontext: Context


class CreateEvent : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)


        setContent {
            currentcontext = LocalContext.current

            // compose UI
            TeamUpTheme() {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    CreateEventCompose()
                }
            }
        }
    }

    // Composable Preview
    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        TeamUpTheme() {
            CreateEventCompose()
        }
    }

    // Scaffold View
    @Composable
    fun CreateEventCompose() {
        val navController = rememberNavController()
        Scaffold(
            topBar = { TopBar() },
            content = {
                Box(
                    modifier = Modifier
                        .background(colorResource(id = R.color.landingPageBackground))
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text("XML VIEW")
                    CustomView()
                }
            },
            bottomBar = {
                BottomNavigationBar(
                    navController = navController,
                    onItemClicker = {
                        navController.navigate(it.route)
                    }
                )
            }
        )
    }

    // Top Bar
    @Composable
    fun TopBar() {
        TopAppBar(
            title = { Text(text = "Create Event", fontSize = 20.sp) },
            backgroundColor = Purple200,
            contentColor = Color.White
        )
    }


    @Composable
    fun CustomView() {
        ActivityCreateEventBinding(CreateEventActivity::inflate) {
        }
    }


}


