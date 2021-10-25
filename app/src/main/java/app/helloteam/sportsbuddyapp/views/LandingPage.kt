/*
 * monamoe
 * 10/21/21
 * Landing Page Compose
 */
package app.helloteam.sportsbuddyapp.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import app.helloteam.sportsbuddyapp.R
import app.helloteam.sportsbuddyapp.helperUI.*
import app.helloteam.sportsbuddyapp.views.ui.theme.TeamUpTheme
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.google.firebase.auth.FirebaseAuth
import org.joda.time.DateTime
import org.joda.time.LocalTime


//context
@SuppressLint("StaticFieldLeak")
private lateinit var currentcontext: Context

// location permission integer
const val MY_PERMISSION_FINE_LOCATION: Int = 44

private var userID: String = "1"
var userLocationLat = 0.0
var userLocationLon = 0.0

// screen values
var username: String = "user"
var weatherIcon = ""
var cityName = ""
var prov = ""
var forecast: String = "Weather error"
var temp: String = ""
var icon: String = ""
var welcomeMessage = "Hello"

// lists
private var hostingAttendingEventList: MutableList<EventCard> = mutableListOf()
private var recommendedEventList: MutableList<EventCard> = mutableListOf()
lateinit var todayWithZeroTime: String


class LandingPage2 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // check if the user is logged in
        userID = FirebaseAuth.getInstance().currentUser?.uid.toString()
        if (userID.equals(null)) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        // init variables
        val dt = DateTime()
        hostingAttendingEventList = LoadingEvent.hostingAttendingEventList
        recommendedEventList = LoadingEvent.recommendedEventList
        todayWithZeroTime =
            dt.monthOfYear().asText + " " + dt.dayOfMonth().asText + ", " + dt.year().asText
        val lt = LocalTime()
        if (lt < lt.withHourOfDay(12)) {
            welcomeMessage = "Good Morning"
        } else if (lt > lt.withHourOfDay(12) && lt < lt.withHourOfDay(17)) {
            welcomeMessage = "Good Afternoon"
        } else if (lt >= lt.withHourOfDay(17)) {
            welcomeMessage = "Good Evening"
        }

        // set content
        setContent {
            currentcontext = LocalContext.current

            TeamUpTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    LandingPageCompose()
                }
            }
        }
    }

    // Composable Preview
    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        TeamUpTheme {
            LandingPageCompose()
        }
    }

    // Scaffold View
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

                        // your events
                        ContentDivider()
                        EventScroll()
                        // recommended events
                        ContentDivider()
                        RecommendedEventScroll()

                        ContentDivider()

                        ExtraPadding()
                    }
                }
            },
            bottomBar = {
                BottomNavigationBar(
                    navController = navController,
                    context = currentcontext
                )
            }
        )
    }
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
                    val intent = Intent(currentcontext, CreateEventActivity::class.java)
                    currentcontext.startActivity(intent)
                }, colors = ButtonDefaults.textButtonColors(
                    backgroundColor = colorResource(id = R.color.secondaryColor)
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
    name: String = "User",
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
                text = "$welcomeMessage, $name",
                style = MaterialTheme.typography.h1,
                color = colorResource(id = R.color.secondaryTextColor)
            )
            Text(
                text = todayWithZeroTime,
                style = MaterialTheme.typography.body1,
                color = colorResource(id = R.color.secondaryTextColor)
            )
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
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
                text = "$cityName • $prov",
                style = MaterialTheme.typography.h2,
                color = colorResource(id = R.color.secondaryTextColor)
            )
            Text(
                text = "$temp $forecast",
                style = MaterialTheme.typography.body1,
                color = colorResource(id = R.color.secondaryTextColor)
            )
        }
        Column {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(colorResource(id = R.color.secondaryColor))
                    .padding(10.dp)
            ) {
                Image(
                    painter = rememberImagePainter(weatherIcon),
                    contentDescription = null,
                    modifier = Modifier.size(70.dp)
                )
            }
        }
    }
}

/**
 * Horizontal scrolling cards for Recommended Events
 *
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecommendedEventScroll() {
    Column {
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
        }
        Log.i("LOG_CAT", "RECOMMENDED LIST: $recommendedEventList")
        if (recommendedEventList.size == 0) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(colorResource(id = R.color.secondaryColor))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopStart),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.Start,
                ) {
                    Text(
                        text = "Unable to find any event recommendations in your area!",
                        style = MaterialTheme.typography.h4,
                        color = colorResource(id = R.color.secondaryTextColor),
                        modifier = Modifier.padding(15.dp)
                    )
                    Text(
                        text = "Remember to set your favourite activity in settings!",
                        style = MaterialTheme.typography.h4,
                        color = colorResource(id = R.color.secondaryTextColor),
                        modifier = Modifier.padding(15.dp)
                    )
                }
            }
        }
        LazyRow(
            modifier = Modifier
                .fillMaxHeight()
        ) {
            items(recommendedEventList) { e ->
                EventCard(
                    e,
                    Modifier
                        .padding(start = 16.dp, bottom = 16.dp)
                        .clickable {
                            Log.i("LOG_TAG", "VIEW EVENT: IT ${e.eventID}, ${e.title}")
                            val intent = Intent(context, SplashLoadingEventView::class.java)
                            Log.i(
                                "LOG_TAG",
                                "VIEW EVENT: BEFORE: eventID ${e.eventID}"
                            )
                            intent.putExtra("eventID", e.eventID)
                            intent.putExtra("locationID", e.locationID)
                            Log.i(
                                "LOG_TAG",
                                "VIEW EVENT: BEFORE: locationID ${e.locationID}"
                            )
                            context.startActivity(intent)
                        }
                )
            }
        }
    }
}


/**
 * Horizontal scrolling cards for your events
 *
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EventScroll() {
    Column {
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
                text = "View Invites",
                style = MaterialTheme.typography.h4,
                color = colorResource(id = R.color.secondaryTextColor),
                modifier = Modifier
                    .padding(15.dp)
                    .clickable {

                        val intent = Intent(context, EventInviteActivity::class.java)
                        context.startActivity(intent)
                    }
            )
        }

        if (hostingAttendingEventList.size == 0) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(colorResource(id = R.color.secondaryColor))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopStart),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.Start,
                ) {
                    Text(
                        text = "You aren't registed for any events!",
                        style = MaterialTheme.typography.h4,
                        color = colorResource(id = R.color.secondaryTextColor),
                        modifier = Modifier.padding(15.dp)
                    )
                    Text(
                        text = "Click Create Event to host an event, or go to the map view and " +
                                "find an event in your area!",
                        style = MaterialTheme.typography.h4,
                        color = colorResource(id = R.color.secondaryTextColor),
                        modifier = Modifier.padding(15.dp)
                    )
                }
            }
        }

        LazyRow {
            items(hostingAttendingEventList) { e ->
                EventCard(
                    e,
                    Modifier
                        .padding(start = 16.dp, bottom = 16.dp)
                        .clickable {
                            // updates current event in view model (doesn't use view model for intent but im keeping it here)
                            // navigates to the event page
                            Log.i("LOG_TAG", "VIEW EVENT: IT ${e.eventID}, ${e.title}")
                            val intent = Intent(context, SplashLoadingEventView::class.java)
                            Log.i(
                                "LOG_TAG",
                                "VIEW EVENT: BEFORE: eventID ${e.eventID}"
                            )
                            intent.putExtra("eventID", e.eventID)
                            intent.putExtra("locationID", e.locationID)
                            Log.i(
                                "LOG_TAG",
                                "VIEW EVENT: BEFORE: locationID ${e.locationID}"
                            )
                            context.startActivity(intent)
                        }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterialApi::class, ExperimentalCoilApi::class)
@Composable
fun EventCard(
    event: EventCard,
    modifier: Modifier = Modifier
) {
    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
            .size(280.dp, 240.dp)
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .aspectRatio(1f)
                .clip(RoundedCornerShape(20.dp))
                .background(colorResource(id = R.color.secondaryColor))
        ) {
            Column {
                // banner image
                if (event.imageId != "null" && event.imageId != "") {
                    Image(
                        painter = rememberImagePainter(event.imageId),
                        contentDescription = null, // decorative
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .height(100.dp)
                            .fillMaxWidth()
                    )
                } else {
                    Image(
                        painter = painterResource(R.drawable.ic_baseline_broken_image_24),
                        contentDescription = null, // decorative
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .padding(20.dp)
                            .height(100.dp)
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(20.dp)
                    )
                }
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
                            style = MaterialTheme.typography.h3,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }


                    val eventhostline =
                        if (event.isHosting) "Hosted By You" else "Hosted by: " + event.hostName
                    Text(
                        text = eventhostline,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.body2
                    )
                    if (event.eventDesc == "" || event.eventDesc == "null") {
                        Text(
                            text = "No Description",
                            style = MaterialTheme.typography.body2
                        )
                    } else {
                        Text(
                            text = event.eventDesc,
                            style = MaterialTheme.typography.body2
                        )
                    }
                }
            }
        }
    }

}
