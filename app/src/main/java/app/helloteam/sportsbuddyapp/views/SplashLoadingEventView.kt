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
import app.helloteam.sportsbuddyapp.helperUI.LoadingEventView


lateinit var EventViewContext: Context

class SplashLoadingEventView : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // context
        EventViewContext = this

        // getting value from intent
        val locationIDa = intent.getStringExtra("locationID").toString()
        val eventIDa = intent.getStringExtra("eventID").toString()

        Log.i("LOG_TAG", "LOADING EVENT VIEW: GOT LOCATION ID : $locationIDa")
        Log.i("LOG_TAG", "LOADING EVENT VIEW: GOT EVENT ID : $eventIDa")


        // load event list
        LoadingEventView.eventViewData(locationIDa, eventIDa)

        setContent {
            Surface(color = Color.White, modifier = Modifier.fillMaxSize()) {
                Navigation()
                EventListContext = LocalContext.current
            }
        }


    }

}
