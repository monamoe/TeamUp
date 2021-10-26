/*
 * monamoe
 * event view
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.HorizontalAlignmentLine
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
import coil.compose.rememberImagePainter
import com.google.android.gms.maps.model.Circle


private var locationName = "Location Name"
private var locationInfo = "Location Info"
private var locationImage = ""
private var locationLat = 1.1
private var locationLon = 1.1

// host info
private var hostName = "Location Name"
private var hostImage = "Location Name"
private var hostRating = "Location Name"

// event info
private lateinit var eventInfo: EventCard

// attendee info
private var attendeeList: MutableList<AttendeesCard> = mutableListOf()


@SuppressLint("StaticFieldLeak")
private lateinit var currentcontext: Context

class EventCompose : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //init variables
        locationName = LoadingEventView.locationName
        locationInfo = LoadingEventList.locationInfo
        locationImage = LoadingEventList.locationImage
        locationLat = LoadingEventView.locationLat!!
        locationLon = LoadingEventView.locationLon!!

        // host info
        hostName = LoadingEventView.hostName
        hostImage = LoadingEventView.hostImage
        hostRating = LoadingEventView.hostRating

        // event info
        eventInfo = LoadingEventView.eventInfo

        // attendee info
        attendeeList = LoadingEventView.attendeeList


        // set content
        setContent {
            currentcontext = LocalContext.current

            TeamUpTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    EventViewCompose()
                }
            }
        }
    }

    // Composable Preview
    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        TeamUpTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
                EventViewCompose()
            }
        }
    }

    // composable
    // Scaffold View
    @Composable
    fun EventViewCompose() {
        Scaffold(
            content = {
                Box(
                    modifier = Modifier
                        .background(colorResource(id = R.color.landingPageBackground))
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Column {
                        // image banner
                        ImageBanner()
                        ContentDivider()

                        // event information
                        EventInformation()
                        ContentDivider()

                        //Attendee List
                        AttendeeList()
                        ContentDivider()

                        //TO DO Team Members
                        InviteTeamMembers()
                        ContentDivider()

                        ExtraPadding()
                    }
                }
            }
        )
    }


    // Image Banner
    @Composable
    fun ImageBanner() {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .background(colorResource(id = R.color.secondaryColor))
                .padding(bottom = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                if (locationImage != "null" && locationImage != "") {
                    Log.i("LOG_TAG", "LOADING EVENT VIEW: RENDERING IMAGE $locationImage")
                    Image(
                        painter = rememberImagePainter(locationImage),
                        contentDescription = null, // decorative
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .height(200.dp)
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
            }
        }
    }


    @Composable
    fun EventInformation() {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(colorResource(id = R.color.secondaryColor))
                    .clip(RoundedCornerShape(20.dp))
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Column {
                    Text(
                        text = locationName,
                        style = MaterialTheme.typography.h2,
                        color = colorResource(id = R.color.secondaryTextColor)
                    )
                    Text(
                        text = locationInfo,
                        style = MaterialTheme.typography.h3,
                        color = colorResource(id = R.color.secondaryTextColor)
                    )
                    Box(modifier = Modifier.padding(top = 10.dp)) {
                        Column {
//                            // activity
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 10.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_sports_soccer_24),
                                    contentDescription = "Activity Icon"
                                )
                                Text(
                                    text = eventInfo.activityType,
                                    style = MaterialTheme.typography.h4,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            // timing
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 10.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_schedule_24),
                                    contentDescription = "Schedule Icon"
                                )
                                Text(
                                    text = "${eventInfo.startingData} ${eventInfo.startingTime}",
                                    style = MaterialTheme.typography.h4,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            //location
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 10.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_place_24),
                                    contentDescription = "Place Icon"
                                )
                                Text(
                                    text = eventInfo.locationName,
                                    style = MaterialTheme.typography.h4,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun AttendeeList() {
        Column {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Attendees",
                    style = MaterialTheme.typography.h2,
                    color = colorResource(id = R.color.secondaryTextColor),
                    modifier = Modifier
                        .padding(start = 20.dp, end = 20.dp, top = 20.dp)
                )
                Text(
                    text = "${eventInfo.currentlyAttending} / ${eventInfo.space}",
                    style = MaterialTheme.typography.h3,
                    color = colorResource(id = R.color.secondaryTextColor),
                    modifier = Modifier
                        .padding(20.dp)
                )
            }
            LazyRow {
                items(attendeeList) {
                    AttendeeCard(it)
                }
            }
        }
    }

    @Composable
    private fun AttendeeCard(
        attendee: AttendeesCard
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(colorResource(id = R.color.secondaryColor))
                .padding(bottom = 20.dp, start = 20.dp, end = 20.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(colorResource(id = R.color.secondaryColor))
            ) {
                Image(
                    painter = rememberImagePainter(attendee.profileImage),
                    contentDescription = null, // decorative
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(30.dp)
                        .width(30.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
                Text(
                    text = attendee.name,
                    style = MaterialTheme.typography.h4,
                    color = colorResource(id = R.color.secondaryTextColor),
                    modifier = Modifier
                        .padding(top = 5.dp)
                )
            }
        }
    }

    @Composable
    fun InviteTeamMembers() {

    }


}

