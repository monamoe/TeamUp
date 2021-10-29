/*
 * monamoe
 * event view
 */
package app.helloteam.sportsbuddyapp.views

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import app.helloteam.sportsbuddyapp.R
import app.helloteam.sportsbuddyapp.helperUI.*
import app.helloteam.sportsbuddyapp.helperUI.LoadingEventView.Companion.attending
import app.helloteam.sportsbuddyapp.helperUI.LoadingEventView.Companion.hasHost
import app.helloteam.sportsbuddyapp.helperUI.LoadingEventView.Companion.hosting
import app.helloteam.sportsbuddyapp.views.ui.theme.TeamUpTheme
import coil.compose.rememberImagePainter
import com.afollestad.materialdialogs.MaterialDialog


@SuppressLint("StaticFieldLeak")
private lateinit var currentcontext: Context

class EventCompose : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

                        // Attend Button
                        AttendButton()
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
                Log.i(
                    "LOG_TAG",
                    "LOADING EVENT VIEW: RENDERING IMAGE ${LoadingEventView.locationImage}"
                )
                if (LoadingEventView.locationImage == "" || LoadingEventView.locationImage == "null") {
                    Image(
                        painter = painterResource(R.drawable.ic_baseline_broken_image_24),
                        contentDescription = null, // decorative
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .height(200.dp)
                            .fillMaxWidth()
                    )
                } else {
                    Image(
                        painter = rememberImagePainter(LoadingEventView.locationImage),
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
                .padding(start = 20.dp, end = 20.dp, bottom = 20.dp, top = 20.dp)
                .clip(RoundedCornerShape(10.dp))
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
                        text = LoadingEventView.locationName,
                        style = MaterialTheme.typography.h2,
                        color = colorResource(id = R.color.secondaryTextColor)
                    )
                    Text(
                        text = LoadingEventView.locationInfo,
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
                                    text = LoadingEventView.eventInfo.activityType,
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
                                    text = "${LoadingEventView.eventInfo.startingDate} ${LoadingEventView.eventInfo.startingTime}",
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
                                    text = LoadingEventView.eventInfo.locationName,
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
        if (LoadingEventView.attendeeList.size > 0) {
            Column {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
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
                        text = "${LoadingEventView.eventInfo.currentlyAttending} / ${LoadingEventView.eventInfo.space}",
                        style = MaterialTheme.typography.h3,
                        color = colorResource(id = R.color.secondaryTextColor),
                        modifier = Modifier
                            .padding(start = 20.dp, end = 20.dp)
                    )
                }
                LazyRow(modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 20.dp)) {
                    items(LoadingEventView.attendeeList) {
                        AttendeeCard(it)
                    }
                }
            }
        } else {
            Column {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 30.dp),
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
                        text = "There are currently 0 attendees for this event!",
                        style = MaterialTheme.typography.h3,
                        color = colorResource(id = R.color.secondaryTextColor),
                        modifier = Modifier
                            .padding(start = 20.dp, end = 20.dp)
                    )
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
                .size(140.dp, 120.dp)
                .fillMaxSize()
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(colorResource(id = R.color.secondaryColor))
            ) {
                Log.i("LOG_TAG", "LOADING EVENT VIEW: Profile Image ${attendee.profileImage}")
                if (attendee.profileImage == "" || attendee.profileImage == "null") {
                    Image(
                        painter = painterResource(R.drawable.baseline_person_24),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .height(90.dp)
                            .width(120.dp)
                            .padding(top = 10.dp)
                    )
                } else {
                    Image(
                        painter = rememberImagePainter(attendee.profileImage),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .height(90.dp)
                            .width(120.dp)
                    )
                }
                Text(
                    text = attendee.name,
                    style = MaterialTheme.typography.h4,
                    color = colorResource(id = R.color.secondaryTextColor),
                    modifier = Modifier
                        .padding(top = 5.dp, bottom = 5.dp)
                )
            }
        }
    }

    @Composable
    private fun InviteTeamMembers() {
        if (LoadingEventView.teamMemberList.size > 0) {
            Column {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Invite Team Members",
                        style = MaterialTheme.typography.h2,
                        color = colorResource(id = R.color.secondaryTextColor),
                        modifier = Modifier
                            .padding(start = 20.dp, end = 20.dp, top = 20.dp)
                    )
                }
                LazyRow(modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 20.dp)) {
                    items(LoadingEventView.teamMemberList) {
                        InviteTeamMemberCard(it)
                    }
                }
            }
        } else {
            Column {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Invite Team Members",
                        style = MaterialTheme.typography.h2,
                        color = colorResource(id = R.color.secondaryTextColor),
                        modifier = Modifier
                            .padding(start = 20.dp, end = 20.dp, top = 20.dp)
                    )
                    Text(
                        text = "You have no members in your team. DO SOMETHING TO INVITE THEM",
                        style = MaterialTheme.typography.h3,
                        color = colorResource(id = R.color.secondaryTextColor),
                        modifier = Modifier
                            .padding(start = 20.dp, end = 20.dp, top = 20.dp)
                    )
                }
            }
        }

    }

    @Composable
    private fun InviteTeamMemberCard(
        teamMember: AttendeesCard
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(colorResource(id = R.color.secondaryColor))
                .size(140.dp, 140.dp)
                .fillMaxSize()
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(colorResource(id = R.color.secondaryColor))
            ) {
                Log.i("LOG_TAG", "LOADING EVENT VIEW: Profile Image ${teamMember.profileImage}")
                if (teamMember.profileImage == "" || teamMember.profileImage == "null") {
                    Image(
                        painter = painterResource(R.drawable.baseline_person_24),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .height(120.dp)
                            .width(120.dp)
                            .padding(top = 10.dp)
                    )
                } else {
                    Image(
                        painter = rememberImagePainter(teamMember.profileImage),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .height(90.dp)
                            .width(120.dp)
                    )
                }
                Text(
                    text = teamMember.name,
                    style = MaterialTheme.typography.h4,
                    color = colorResource(id = R.color.secondaryTextColor),
                    modifier = Modifier
                        .padding(top = 5.dp, bottom = 5.dp)
                )
                Button(
                    onClick = {
                        // this button does nothing, the invite handling is done by the lazy row event listener
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


    @Composable
    private fun AttendButton() {
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
                        if (hosting) {
                            MaterialDialog(currentcontext).show {
                                title(text = "Are you sure you want to leave this event as host?")
                                positiveButton(R.string.yes) {
                                    LoadingEventView.hostLeaveEvent()
                                }
                                negativeButton(R.string.cancel)
                            }
                        } else {
                            if (LoadingEventView.attending) {
                                LoadingEventView.removeAttendance()
                                Toast.makeText(
                                    currentcontext,
                                    "Successfully left event",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                LoadingEventView.addAttendance()
                            }
                        }

                    }, colors = ButtonDefaults.textButtonColors(
                        backgroundColor = colorResource(id = R.color.secondaryColor)
                    )
                ) {
                    var joinButtonText = if (hosting)
                        "Unhost Event"
                    else
                        if (attending)
                            "Leave Event"
                        else
                            "Join Event"
                    Text(
                        text = joinButtonText,
                        color = colorResource(id = R.color.secondaryTextColor),
                        style = MaterialTheme.typography.h2,
                    )
                }


                if (!hasHost) {

                    Text(
                        text = "This event has no host!",
                        color = colorResource(id = R.color.secondaryTextColor),
                        style = MaterialTheme.typography.h2,
                        modifier = Modifier.padding(10.dp)
                    )
                    // become host button
                    Button(
                        onClick = {
                            MaterialDialog(currentcontext).show {
                                title(text = "Are you sure you want to become the host for this event?")
                                positiveButton(R.string.yes) {
                                    LoadingEventView.makeHost();

                                }
                                negativeButton(R.string.cancel)
                            }
                        }, colors = ButtonDefaults.textButtonColors(
                            backgroundColor = colorResource(id = R.color.secondaryColor)
                        ),
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Text(
                            text = "Become Host",
                            color = colorResource(id = R.color.secondaryTextColor),
                            style = MaterialTheme.typography.h2,
                        )
                    }
                }
            }
        }

    }
}
