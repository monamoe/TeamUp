package app.helloteam.sportsbuddyapp.views

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Icon
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.compose.rememberNavController
import app.helloteam.sportsbuddyapp.R
import app.helloteam.sportsbuddyapp.firebase.EventHandling.db
import app.helloteam.sportsbuddyapp.helperUI.*
import app.helloteam.sportsbuddyapp.models.weatherTask
import app.helloteam.sportsbuddyapp.views.ui.theme.*
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import java.util.*

//context
@SuppressLint("StaticFieldLeak")
private lateinit var currentcontext: Context
const val MY_PERMISSION_FINE_LOCATION: Int = 44

private var username: String = "user"

private lateinit var userLocation: String
private lateinit var weatherIcon: Icon
private lateinit var weatherString: Icon


const val weatherAPI = R.string.weather_api
lateinit var forecast: String
lateinit var temp: String
lateinit var icon: String

private var hostingAttendingEventList: MutableList<EventCard> = mutableListOf<EventCard>()

class LandingPage2 : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //empty the list
        hostingAttendingEventList.clear()


        // is the user isnt logged in
        val db = Firebase.firestore
        val userID = FirebaseAuth.getInstance().currentUser?.uid.toString()
        if (userID == null) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }


        // getting the user name
        db.collection("User").document(Firebase.auth.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener { user ->
                username = user.get("userName").toString()
            }


        // event list
        db.collection("User").document(userID)
            .get()
            .addOnSuccessListener { users ->

                // hosting
                db.collection("User").document(userID).collection("Hosting")
                    .get()
                    .addOnSuccessListener { hosting ->
                        for (host in hosting) {
                            Log.i("LOG_TAG", "EVENT DISPLAY: THIS USER IS HOSTING AN EVENT:")

                            db.collection("Location")
                                .document(host.get("locationID").toString())
                                .collection("Events")
                                .document(host.get("eventID").toString())
                                .get()
                                .addOnSuccessListener { event ->
                                    if (users != null) {
                                        db.collection("Location")
                                            .document(
                                                host.get("locationID")
                                                    .toString()
                                            )
                                            .get()
                                            .addOnSuccessListener { loc ->
                                                Log.i(
                                                    "LOG_TAG",
                                                    "EVENT DISPLAY: \t\t Adding event to list"
                                                )
                                                hostingAttendingEventList.add(
                                                    EventCard(
                                                        event.get("title").toString(),
                                                        host.get("eventID").toString(),
                                                        loc.get("StreetView").toString(),
                                                        true,
                                                        users.get("userName").toString(),
                                                        "Playing soccer with a couple friends, feel free to join in",
                                                        event.get("eventSpace").toString().toInt(),
                                                        event.get("currentlyAttending").toString()
                                                            .toInt(),
                                                    )
                                                )
                                            }
                                    }
                                }
                        }
                    }

                //attending
                // hosting
                db.collection("User").document(userID).collection("Attending")
                    .get()
                    .addOnSuccessListener { hosting ->
                        for (host in hosting) {
                            db.collection("Location")
                                .document(host.get("locationID").toString())
                                .collection("Events")
                                .document(host.get("eventID").toString())
                                .get()
                                .addOnSuccessListener { event ->
                                    if (users != null) {
                                        db.collection("Location")
                                            .document(
                                                host.get("locationID")
                                                    .toString()
                                            )
                                            .get()
                                            .addOnSuccessListener { loc ->
                                                hostingAttendingEventList.add(
                                                    EventCard(
                                                        event.get("title").toString(),
                                                        host.get("eventID").toString(),
                                                        loc.get("StreetView").toString(),
                                                        false,
                                                        users.get("userName").toString(),
                                                        "Playing soccer with a couple friends, feel free to join in",
                                                        event.get("eventSpace").toString().toInt(),
                                                        event.get("currentlyAttending").toString()
                                                            .toInt(),
                                                    )
                                                )
                                            }
                                    }
                                }
                        }
                    }
            }

        val handler = Handler()
        handler.postDelayed({
            for (event in hostingAttendingEventList) {
                Log.i("LOG_TAG", "onCreateView EventList: ${event.title}")
            }
            setContent {


                currentcontext = LocalContext.current

                // compose UI
                TeamUpTheme() {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colors.background
                    ) {
                        LandingPageCompose()
                    }
                }
            }
        }, 1000)


        getUserCity()
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
        var userLocationLat = 0.0
        var userLocationLon = 0.0
        var cityName = ""
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                //program has permission
                    location ->

                if (location != null) {
                    //update user interface
                    userLocationLat = location.latitude
                    userLocationLon = location.longitude

                    val geocoder = Geocoder(this, Locale.getDefault())
                    val addresses: List<Address> =
                        geocoder.getFromLocation(userLocationLat, userLocationLon, 1)
                    cityName = addresses[0].getLocality()
                }
                //render the marker on the users location.
//                weatherTask(icon).execute(
//                    cityName,
//                    getString(R.string.weather_api)
//                )
                //gets weather for current location
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

/**
 * Composeable Preview
 * @param name
 */
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TeamUpTheme() {
        LandingPageCompose()
    }
}


// COMPOSE //
@Composable
fun LandingPageCompose() {


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
                    GreetingSection(username)
                    CurrentWeather()
                    ContentDivider()

                    ContentDivider()
                    CreateEventButton()

                    // your events section
                    ContentDivider()
                    EventScroll()

                    // content divider
                    ContentDivider()
                    RecommendedEventScroll(
                        listOf(
                            EventCard(
                                "Title",
                                "eventid",
                                "imageId",
                                true,
                                "Host Name",
                                "HAHAHAH",
                                4,
                                2,
                            )
                        )
                    )


                    ContentDivider()
                    CreateEventButton()
                }
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

@Composable
fun CreateEventButton() {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    var intent = Intent(currentcontext, CreateEventActivity::class.java)
                    currentcontext.startActivity(intent)
                }, colors = ButtonDefaults.textButtonColors(
                    backgroundColor = colorResource(id = R.color.primaryDarkColor)
                )
            ) {
                Text(
                    text = "Create Event",
                    color = colorResource(id = R.color.secondaryTextColor),
                    style = MaterialTheme.typography.h2,
                )
            }
        }
    }
}


/**
 * Horizontal scrolling cards for Recommended Events
 *
 * @param name
 */
@Composable
fun GreetingSection(
    name: String = "User"
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Good morning, $name",
                style = MaterialTheme.typography.h1,
                color = colorResource(id = R.color.secondaryTextColor)
            )
            Text(
                text = "Thursday October 31st 2021",
                style = MaterialTheme.typography.body1,
                color = colorResource(id = R.color.secondaryTextColor)
            )
        }
        Icon(
            painter = painterResource(id = R.drawable.clipboard),
            contentDescription = "Search",
            tint = colorResource(id = R.color.secondaryTextColor),
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun ChipSection(
    chips: List<String>
) {
    var selectedChipIndex by remember {
        mutableStateOf(0)
    }
    LazyRow {
        items(chips.size) {
            Box(
                modifier = Modifier
                    .padding(start = 15.dp, top = 15.dp, bottom = 15.dp)
                    .clickable { selectedChipIndex = it }
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (selectedChipIndex == it)
                            ButtonBlue
                        else DarkerButtonBlue
                    )
                    .padding(15.dp)
            ) {
                Text(text = chips[it], color = colorResource(id = R.color.primaryTextColor))
            }
        }
    }
}

@Composable
fun CurrentWeather() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(15.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(colorResource(R.color.secondaryColor))
            .padding(horizontal = 15.dp, vertical = 15.dp)
            .fillMaxWidth()
    ) {
        Column {
            Text(
                text = "Mississauga • Ontario",
                style = MaterialTheme.typography.h2,
                color = colorResource(id = R.color.secondaryTextColor)
            )
            Text(
                text = "18°C Partly Cloudy",
                style = MaterialTheme.typography.body1,
                color = colorResource(id = R.color.secondaryTextColor)
            )
        }
        Column {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(ButtonBlue)
                    .padding(10.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.common_full_open_on_phone),
                    contentDescription = "Play",
                    tint = colorResource(id = R.color.primaryDarkColor),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

/**
 * Horizontal scrolling cards for Recommended Events
 *
 * @param events ListOf state events to display
 * @param navigateToEvent (event) request navigation to Article screen
 * navigateToEvent: (String) -> Unit
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecommendedEventScroll(
    events: List<EventCard>
) {
    Column() {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Recommended Events",
                style = MaterialTheme.typography.h2,
                color = colorResource(id = R.color.secondaryTextColor),
                modifier = Modifier.padding(15.dp)
            )
            Text(
                text = "See More",
                style = MaterialTheme.typography.h4,
                color = colorResource(id = R.color.secondaryTextColor),
                modifier = Modifier.padding(15.dp),
            )
        }

        LazyRow(
            modifier = Modifier
                .fillMaxHeight()
        ) {
            items(events) { state ->
                EventCard(
                    state,
                    Modifier.padding(start = 16.dp, bottom = 16.dp)
                )
            }
        }
    }
}


/**
 * Horizontal scrolling cards for your events
 *
 * @param events ListOf state events to display
 * @param navigateToEvent (event) request navigation to Article screen
 * navigateToEvent: (String) -> Unit
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EventScroll() {
    Column(
//        Modifier.background(color = colorResource(id = R.color.landingCardBackgroundColor))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Your Events",
                style = MaterialTheme.typography.h2,
                color = colorResource(id = R.color.secondaryTextColor),
                modifier = Modifier.padding(15.dp),
            )
            Text(
                text = "See More",
                style = MaterialTheme.typography.h4,
                color = colorResource(id = R.color.secondaryTextColor),
                modifier = Modifier.padding(15.dp),
            )
        }

        LazyRow() {
            items(hostingAttendingEventList) { event ->
                EventCard(
                    event,
                    Modifier.padding(start = 16.dp, bottom = 16.dp)
                )
            }
        }
    }
}

@Composable
fun EventCard(
    event: EventCard,
    modifier: Modifier = Modifier
) {
    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
            .size(280.dp, 240.dp)
            .background(colorResource(id = R.color.secondaryColor))
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .aspectRatio(1f)
                .clip(RoundedCornerShape(10.dp))
                .background(colorResource(id = R.color.secondaryColor))
        ) {

            Column(modifier = Modifier.clickable(onClick = { })) {

                // banner image TO DO GET THIS TO WORK
                Image(
                    painter = painterResource(R.drawable.clipboard),
                    contentDescription = null, // decorative
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(100.dp)
                        .fillMaxWidth()
                )

                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    )
                    {
                        Text(
                            text = event.title,
                            style = MaterialTheme.typography.h3,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Space: " + event.currentlyAttending.toString() + "/" + event.space.toString(),
                            style = MaterialTheme.typography.h6,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }


                    var eventhostline =
                        if (event.isHosting) "Hosted By You" else "Hosted by: " + event.hostName
                    Text(
                        text = eventhostline,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.body2
                    )
                    Text(
                        text = event.eventDesc,
                        style = MaterialTheme.typography.body2
                    )
                }
            }
        }
    }

}


/**
 * Full-width divider with padding
 */
@Composable
private fun ContentDivider() {
    Divider(
        modifier = Modifier.padding(horizontal = 14.dp),
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.08f)
    )
}

/**
 * Intent Navigation Router
 * @param name
 */
fun useIntentOnRoute(context: Context, route: String) {
    var intent = Intent(context, LandingPage2::class.java)
    when (route) {
        "home" -> Log.i("LOG_NAVIGATION", "ALREADY ON REQUESTED PAGE")
        "chat" -> intent = Intent(context, LatestMessagesActivity::class.java)
        "map" -> intent = Intent(context, map::class.java)
        "teams" -> intent = Intent(context, TeamsActivity::class.java)
        "profile" -> intent = Intent(context, ProfilePage::class.java)
        else -> {
            Log.i("LOG_TAG", "FATAL ERROR! UNABLE TO GO TO THE VIEW REQUESTED! ")
        }
    }
    context.startActivity(intent)
}
