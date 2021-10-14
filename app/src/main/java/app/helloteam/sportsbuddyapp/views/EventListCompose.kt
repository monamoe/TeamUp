package app.helloteam.sportsbuddyapp.views

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import app.helloteam.sportsbuddyapp.R
import app.helloteam.sportsbuddyapp.helperUI.BottomNavigationBar
import app.helloteam.sportsbuddyapp.views.ui.theme.TeamUpTheme
import app.helloteam.sportsbuddyapp.helperUI.ContentDivider
import app.helloteam.sportsbuddyapp.helperUI.ExtraPadding


//context
private lateinit var currentcontext: Context

class EventListCompose : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            currentcontext = LocalContext.current

            TeamUpTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    EventList()
                }
            }
        }
    }
}

// Composable Preview
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TeamUpTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            EventList()
        }
    }
}

@Composable
fun EventList() {
    val navController = rememberNavController()
    Scaffold(
        content = {
            Box(
                modifier = Modifier
                    .background(colorResource(id = R.color.landingPageBackground))
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Column {
                    // greeting
//                    LocationInfo()
                    ContentDivider()


//                    EventListScroll()

                    ExtraPadding()
                }
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

