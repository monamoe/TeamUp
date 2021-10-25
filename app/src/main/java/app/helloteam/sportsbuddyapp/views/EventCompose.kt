/*
 * monamoe
 * event view
 */
package app.helloteam.sportsbuddyapp.views

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import app.helloteam.sportsbuddyapp.helperUI.LoadingEventList
import app.helloteam.sportsbuddyapp.views.ui.theme.TeamUpTheme


private var locationName = "Location Name"
private var locationInfo = "Location Info"
private var locationImage = "IDK"

@SuppressLint("StaticFieldLeak")
private lateinit var currentcontext: Context

class EventCompose : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //init variables
        locationName = LoadingEventList.locationName
        locationInfo = LoadingEventList.locationInfo
        locationImage = LoadingEventList.locationImage



        setContent {
            TeamUpTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview2() {
    TeamUpTheme {
        Greeting("Android")
    }
}