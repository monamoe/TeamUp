package app.helloteam.sportsbuddyapp.views

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.helloteam.sportsbuddyapp.R
import app.helloteam.sportsbuddyapp.helperUI.LoadingEvent
import app.helloteam.sportsbuddyapp.helperUI.LoadingEvent.Companion.getUserName
import app.helloteam.sportsbuddyapp.helperUI.LoadingEvent.Companion.recEventsDone
import app.helloteam.sportsbuddyapp.helperUI.LoadingEvent.Companion.yourEventsDone
import app.helloteam.sportsbuddyapp.helperUI.LoadingEvent.Companion.yourHostDone
import app.helloteam.sportsbuddyapp.models.weatherTask
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import org.joda.time.DateTime
import org.joda.time.LocalTime
import java.util.*


var loggedIn = false
lateinit var context: Context

class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var user = FirebaseAuth.getInstance().getCurrentUser()?.uid
        val db = Firebase.firestore
        var testUser = false
        db.collection("User")
            .document(user.toString())
            .get()
            .addOnSuccessListener { document ->
                testUser = document.get("testUser").toString().toBoolean()
                if (user != null && (FirebaseAuth.getInstance().currentUser?.isEmailVerified == true || testUser)) {
                    loggedIn = true
                    getUserCity()
                }
            }
        context = this
        if (user == "null" || user == null) {
            context.startActivity(Intent(context, LoginActivity::class.java))
            finish()
        }
        setContent {
            Surface(color = Color.White, modifier = Modifier.fillMaxSize()) {
                Navigation()
                context = LocalContext.current
            }
        }
    }

    // LANDING PAGE STUFF FROM OLD CODE
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSION_FINE_LOCATION ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getUserCity()
                } else {
                    //permission denied
                    Toast.makeText(
                        applicationContext,
                        "App requires location permission to be granted",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
        }
    }


    fun getUserCity() {
        var fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.i("hellooooo", "h empty")

            fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                //program has permission
                    location ->
                Log.i("hellooooo", "l empty")

                if (location != null) {
                    Log.i("hellooooo", "l not empty")

                    //update user interface
                    userLocationLat = location.latitude
                    userLocationLon = location.longitude
                    Log.i("hellooooo", "lat: $userLocationLat ,  Long: $userLocationLon")
                    val geocoder = Geocoder(this, Locale.getDefault())
                    val addresses: List<Address> =
                        geocoder.getFromLocation(userLocationLat, userLocationLon, 1)
                    cityName = addresses[0].getLocality()
                    prov = addresses[0].adminArea
                    //render the marker on the users location.
                    weatherTask().execute(
                        userLocationLat.toString(),
                        userLocationLon.toString(),
                        getString(R.string.weather_api)
                    )
                }
                recEventsDone = false
                yourEventsDone = false
                yourHostDone = false
                LoadingEvent.hostingAttendingEventList.clear()
                LoadingEvent.recommendedEventList.clear()
                LoadingEvent.getAttending(FirebaseAuth.getInstance().currentUser?.uid.toString())
                LoadingEvent.yourEventListData(FirebaseAuth.getInstance().currentUser?.uid.toString())
                LoadingEvent.recommendedEventsListData(
                    FirebaseAuth.getInstance().currentUser?.uid.toString(),
                    userLocationLat,
                    userLocationLon
                )
                getUserName()
                //gets weather for current location
            }
            fusedLocationProviderClient.lastLocation.addOnFailureListener {
                Log.i("hellooooo", it.toString())
                Toast.makeText(
                    applicationContext,
                    "Can not find Location",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }

        }
        //request permission
        else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSION_FINE_LOCATION
            )
        }
    }
}

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "splash_screen"
    ) {
        composable("splash_screen") {
            SplashScreen()
        }
    }
}

@Composable
fun SplashScreen() {
    val scale = remember {
        Animatable(0f)
    }
    val infiniteTransition = rememberInfiniteTransition()
    val angle by infiniteTransition.animateFloat(
        initialValue = 0F,
        targetValue = 360F,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing)
        )
    )

    // Animation
    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 0.7f,
            // tween Animation
            animationSpec = tween(
                durationMillis = 800,
                easing = {
                    OvershootInterpolator(4f).getInterpolation(it)
                })
        )
    }

    // Image
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Change the logo
        Image(
            painter = painterResource(id = R.drawable.logoteamup),
            contentDescription = "Logo",
            modifier = Modifier.scale(scale.value)
        )
        Image(
            painter = painterResource(R.drawable.soccer),
            "image",
            Modifier
                .size(50.dp)
                .rotate(angle),
            contentScale = ContentScale.Fit,
        )
    }
}
