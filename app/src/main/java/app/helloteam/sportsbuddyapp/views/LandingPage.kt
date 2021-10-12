package app.helloteam.sportsbuddyapp.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import app.helloteam.sportsbuddyapp.helperUI.BottomNavigationBar
import app.helloteam.sportsbuddyapp.helperUI.EventCard
import app.helloteam.sportsbuddyapp.helperUI.LoadingEvent
import app.helloteam.sportsbuddyapp.models.EventViewModel
import app.helloteam.sportsbuddyapp.views.ui.theme.ButtonBlue
import app.helloteam.sportsbuddyapp.views.ui.theme.TeamUpTheme
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.google.firebase.auth.FirebaseAuth
import org.joda.time.DateTime
import org.joda.time.LocalTime
import java.time.LocalDate
import java.util.*


//context
@SuppressLint("StaticFieldLeak")
private lateinit var currentcontext: Context

const val MY_PERMISSION_FINE_LOCATION: Int = 44

private var userID: String = "1"
var username: String = "user"

var userLocationLat = 0.0
var userLocationLon = 0.0
var weatherIcon = ""
var cityName = ""
var prov = ""
var forecast: String = "Weather error"
var temp: String = ""
var icon: String = ""
var welcomeMessage = "Hello"

private var hostingAttendingEventList: MutableList<EventCard> = mutableListOf<EventCard>()
private var recommendedEventList: MutableList<EventCard> = mutableListOf<EventCard>()
lateinit var todayWithZeroTime: String


class LandingPage2 : ComponentActivity() {

    private val eventViewModel by viewModels<EventViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dt = DateTime()


        hostingAttendingEventList = LoadingEvent.hostingAttendingEventList
        recommendedEventList = LoadingEvent.recommendedEventList

        todayWithZeroTime =
            dt.monthOfYear().asText + " " + dt.dayOfMonth().asText + ", " + dt.year().asText

        val lt = LocalTime()

        if(lt< lt.withHourOfDay(12)){
            welcomeMessage = "Good Morning"
        } else if (lt > lt.withHourOfDay(12) && lt < lt.withHourOfDay(17)){
            welcomeMessage = "Good Afternoon"
        } else if (lt >= lt.withHourOfDay(17)){
            welcomeMessage = "Good Evening"
        }

        // is the user isnt logged in
        userID = FirebaseAuth.getInstance().currentUser?.uid.toString()
        if (userID.equals(null)) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }


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
                    onItemClicker = {
                        navController.navigate(it.route)
                    }
                )
            }
        )
    }

    @Composable
    private fun ExtraPadding() {
        Box(
            modifier = Modifier
                .padding(50.dp)
                .fillMaxWidth()
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
                    .background(ButtonBlue)
                    .padding(10.dp)
            ) {
                Image(
                    painter = rememberImagePainter(weatherIcon),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
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
            Text(
                text = "See More",
                style = MaterialTheme.typography.h4,
                color = colorResource(id = R.color.secondaryTextColor),
                modifier = Modifier.padding(15.dp),
            )
        }

        if (recommendedEventList.size == 0) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .background(colorResource(id = R.color.secondaryColor))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopStart),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Unable to find any event recommedations in your area!",
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
                            val intent = Intent(context, ViewEvent::class.java)
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
                text = "See More",
                style = MaterialTheme.typography.h6,
                color = colorResource(id = R.color.secondaryTextColor),
                modifier = Modifier.padding(15.dp),
            )
        }

        LazyRow {
            items(hostingAttendingEventList) { e ->
                EventCard(
                    e,
                    Modifier
                        .padding(start = 16.dp, bottom = 16.dp)
                        .clickable {
                            // updates current event in view model (doesnt use viewmodel for intent but im keeping it here)
                            // navigates to the event page
                            Log.i("LOG_TAG", "VIEW EVENT: IT ${e.eventID}, ${e.title}")
                            val intent = Intent(context, ViewEvent::class.java)
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


@OptIn(ExperimentalMaterialApi::class)
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
            .clip(RoundedCornerShape(20.dp))
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .aspectRatio(1f)
                .clip(RoundedCornerShape(20.dp))
                .background(colorResource(id = R.color.secondaryColor))
        ) {
            Column {

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
 * @param context current context
 * @param route decides on navigation
 */
// TO DO : change routing to compare values NavContent and Context to determine weather or not to switch the view
fun useIntentOnRoute(context: Context, route: String) {
    Log.i("LOG_tAG", "CURRENT CONTEXT: $context")
    Log.i("LOG_tAG", "CURRENT CONTEXT: ${context.applicationContext}")
    if (route != "home") {

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
}
