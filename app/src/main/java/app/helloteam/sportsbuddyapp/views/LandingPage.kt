package app.helloteam.sportsbuddyapp.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
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
import app.helloteam.sportsbuddyapp.utils.BottomNavigationBar
import app.helloteam.sportsbuddyapp.views.ui.ContentDivider
import app.helloteam.sportsbuddyapp.utils.EventCard
import app.helloteam.sportsbuddyapp.utils.ExtraPadding
import app.helloteam.sportsbuddyapp.utils.LoadingEvent
import app.helloteam.sportsbuddyapp.views.ui.TeamUpTheme
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.afollestad.materialdialogs.MaterialDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

// context
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
        if (userID == "null" || userID.isEmpty()) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
            return
        }

        Firebase.firestore.collection("User").document(userID)
            .get().addOnSuccessListener { user ->
                if (user.get("newUser").toString().toBoolean()) {
                    Firebase.firestore.collection("User").document(userID)
                        .update("newUser", false)

                    MaterialDialog(currentcontext).show {
                        title(text = "Set up your profile now to stand out from the crowd!")
                        positiveButton(R.string.yes) {
                            val intent = Intent(currentcontext, EditProfilePage::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            currentcontext.startActivity(intent)
                        }
                        negativeButton(R.string.cancel)
                    }
                }
            }

        val dt = LocalDateTime.now()
        hostingAttendingEventList = LoadingEvent.hostingAttendingEventList
        recommendedEventList = LoadingEvent.recommendedEventList
        todayWithZeroTime = dt.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))

        val lt = LocalTime.now()
        welcomeMessage = when {
            lt.isBefore(LocalTime.NOON) -> "Good Morning"
            lt.isBefore(LocalTime.of(17, 0)) -> "Good Afternoon"
            else -> "Good Evening"
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
            content = { paddingValues ->
                Box(
                    modifier = Modifier
                        .background(colorResource(id = R.color.landingPageBackground))
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(paddingValues) // to respect Scaffold's inner padding
                ) {
                    Column {
                        GreetingSection(username)
                        CurrentWeather()
                        ContentDivider()

                        ContentDivider()
                        CreateEventButton()

                        ContentDivider()
                        EventScroll()

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
        if (recommendedEventList.isEmpty()) {
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
                            val intent = Intent(currentcontext, SplashLoadingEventView::class.java)
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
                            currentcontext.startActivity(intent)
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

            Box(
                modifier = Modifier
                    .padding(5.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(colorResource(id = R.color.secondaryColor))
                    .clickable {
                        val intent = Intent(currentcontext, EventInviteActivity::class.java)
                        currentcontext.startActivity(intent)
                    }
            ) {
                Text(
                    text = "View Invites",
                    style = MaterialTheme.typography.h4,
                    color = colorResource(id = R.color.secondaryTextColor),
                    modifier = Modifier.padding(5.dp)
                )
            }
        }

        if (hostingAttendingEventList.isEmpty()) {
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
                        text = "You aren't registered for any events!",
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
                            Log.i("LOG_TAG", "VIEW EVENT: IT ${e.eventID}, ${e.title}")
                            val intent = Intent(currentcontext, SplashLoadingEventView::class.java)
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
                            currentcontext.startActivity(intent)
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
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .clip(RoundedCornerShape(20.dp))
                .background(colorResource(id = R.color.secondaryColor))
        ) {
            Column {
                // banner image
                Log.i(
                    "LOG_TAG",
                    "Landing: Is null ${event.imageId != "null"} - ${event.imageId != ""}"
                )
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
                    )
                }
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = event.title,
                            style = MaterialTheme.typography.h3,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Space: ${event.currentlyAttending}/${event.space}",
                            style = MaterialTheme.typography.h4,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    val eventhostline =
                        if (event.isHosting) "Hosted By You" else "Hosted by: ${event.hostName}"
                    Text(
                        text = eventhostline,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.body2
                    )
                    if (event.eventDesc.isNullOrBlank() || event.eventDesc == "null") {
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
