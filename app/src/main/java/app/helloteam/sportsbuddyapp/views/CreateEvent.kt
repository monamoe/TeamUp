/*
   monamoe
   10-10-2021
   allows the user to submit a form to create an event
   create event composable with xml AndroidView
   https://developer.android.com/jetpack/compose/interop/interop-apis#views-in-compose
 */

package app.helloteam.sportsbuddyapp.views

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import app.helloteam.sportsbuddyapp.R
import app.helloteam.sportsbuddyapp.helperUI.BottomNavigationBar
import app.helloteam.sportsbuddyapp.views.ui.theme.Purple200
import app.helloteam.sportsbuddyapp.views.ui.theme.TeamUpTheme


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
//                    CustomView()
                }
            },
            bottomBar = {
                BottomNavigationBar(
                    navController = navController,
                    onItemClicker = {
                        navController.navigate(it.route)
                    },
                    context = currentcontext
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


//    @Composable
//    fun CustomView() {
//        ActivityCreateEventBinding(CreateEventActivity::inflate) {
//        }
//    }


}


