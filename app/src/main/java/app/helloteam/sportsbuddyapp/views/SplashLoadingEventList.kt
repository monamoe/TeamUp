/*
 * monamoe
 * loads in the data for the event list view before rendering the view
 *
 * uses the SplashScreen() composable from SplashActivity.kt
 */
package app.helloteam.sportsbuddyapp.views

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import app.helloteam.sportsbuddyapp.helperUI.LoadingEvent
import app.helloteam.sportsbuddyapp.helperUI.LoadingEventList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


var EventListLoggedIn = false
lateinit var EventListContext: Context

class SplashLoadingEventList : ComponentActivity() {

    private var loggedIn = false
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        var locationID = intent.getStringExtra("locationID").toString()

        super.onCreate(savedInstanceState)
        var user = FirebaseAuth.getInstance().getCurrentUser()?.uid
        val db = Firebase.firestore
        var testUser = false

        // context
        context = this

        // getting value from intent
        val locationIDa = intent.getStringExtra("locationID").toString()

        // load event list
        LoadEventList(locationIDa)



        setContent {
            Surface(color = Color.White, modifier = Modifier.fillMaxSize()) {
                Navigation()
                context = LocalContext.current
            }
        }


    }

    fun LoadEventList(locationID: String) {
        LoadingEventList.locationEventList.clear()
        LoadingEventList.locationEventListData(locationID)
    }
}

