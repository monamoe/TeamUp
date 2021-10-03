package app.helloteam.sportsbuddyapp.views

// activity view fragment with a compose element
// using intens for navigation, make the compose code run an intent function outside the composable

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import app.helloteam.sportsbuddyapp.R
import app.helloteam.sportsbuddyapp.helperUI.*
import app.helloteam.sportsbuddyapp.views.ui.theme.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

const val weatherAPI = "f00bd5c2f24390ab1393b5a7c5459b01"
private const val MY_PERMISSION_FINE_LOCATION: Int = 44

// Init variables
lateinit private var username: String
lateinit private var title: String

class LandingPage : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // username
        val db = Firebase.firestore
        val location = db.collection("User").document(Firebase.auth.currentUser?.uid.toString())
        location.get().addOnSuccessListener { user ->
            username = user.get("username").toString()
        }

        setContent {
            TeamUpTheme {
                LandingPageCompose()
            }
        }
    }
}


/**
 * Composeable Preview
 *
 * @param name
 */
@Preview
@Composable
fun PreviewLandingPage() {
    TeamUpTheme {
        LandingPageCompose()
    }
}

