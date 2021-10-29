/*
 * monamoe
 * loads in the data for the event list view before rendering the view
 *
 * uses the SplashScreen() composable from SplashActivity.kt
 */
package app.helloteam.sportsbuddyapp.views

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import app.helloteam.sportsbuddyapp.helperUI.LoadingEventList
import app.helloteam.sportsbuddyapp.helperUI.LoadingEventView.Companion.eventIDa
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


lateinit var EventListContext: Context

class SplashLoadingEventList : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var locationID = intent.getStringExtra("locationID").toString()
        var user = FirebaseAuth.getInstance().getCurrentUser()?.uid
        val db = Firebase.firestore
        var testUser = false

        // context
        EventListContext = this

        // getting value from intent
        val locationIDa = intent.getStringExtra("locationID").toString()

        Log.i("LOG_TAG", "LOADING EVENTS: GOT LOCATION ID : $locationIDa")
        Log.i("LOG_TAG", "LOADING EVENTS: GOT EVENT ID : $eventIDa")
        // load event list
        LoadingEventList.locationEventList.clear()
        LoadingEventList.locationEventListData(locationIDa)



        setContent {
            Surface(color = Color.White, modifier = Modifier.fillMaxSize()) {
                Navigation()
                EventListContext = LocalContext.current
            }
        }
    }
}

