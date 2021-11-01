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
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
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
import app.helloteam.sportsbuddyapp.R
import app.helloteam.sportsbuddyapp.firebase.InviteHandling.sendEventInvite
import app.helloteam.sportsbuddyapp.helperUI.*
import app.helloteam.sportsbuddyapp.helperUI.LoadingEventView.Companion.attendeeList
import app.helloteam.sportsbuddyapp.helperUI.LoadingEventView.Companion.attending
import app.helloteam.sportsbuddyapp.helperUI.LoadingEventView.Companion.eventInfo
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

    override fun onBackPressed() {
        val intent = Intent(this, SplashActivity::class.java)
        startActivity(intent)
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
            topBar = {
                InsetAwareTopAppBar(
                    title = {
                        Text(
                            text = eventInfo.title,
                            style = MaterialTheme.typography.h1,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start,
                            color = colorResource(R.color.primaryTextColor)
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                val intent = Intent(context, LandingPage2::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                                context.startActivity(intent)
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                                contentDescription = "Activity Icon",
                            )
                        }
                    },
                    elevation = 10.dp
                )
            },
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
                        if (!hosting) {
                            AttendButton()
                        }

                        // Host button
                        if (hosting) {
                            LeaveHost()
                        }

                        // Become host button
                        if (!hasHost) {
                            BecomeHost()
                        }
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
                .background(colorResource(id = R.color.black))
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Log.i(
                    "LOG_TAG",
                    "LOADING EVENT VIEW: RENDERING IMAGE ${LoadingEventView.locationImage}"
                )
                if (LoadingEventView.locationImage == "" || LoadingEventView.locationImage == "null") {
                    Image(
                        painter = painterResource(R.drawable.ic_baseline_broken_image_24),
                        contentDescription = null, // decorative
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .height(200.dp)
                            .fillMaxWidth()
                    )
                } else {
                    Image(
                        painter = rememberImagePainter(LoadingEventView.locationImage),
                        contentDescription = null, // decorative
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .height(200.dp)
                            .fillMaxWidth()
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
                        text = eventInfo.title,
                        style = MaterialTheme.typography.h3,
                        color = colorResource(id = R.color.secondaryTextColor)
                    )
                    Box(modifier = Modifier.padding(top = 10.dp)) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.Start
                        ) {
//                            // activity
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_sports_soccer_24),
                                    contentDescription = "Activity Icon",
                                )
                                Text(
                                    text = eventInfo.activityType,
                                    style = MaterialTheme.typography.h4,
                                    maxLines = 1,
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
                                    contentDescription = "Schedule Icon",
                                )
                                Text(
                                    text = "${eventInfo.startingDate}   ${eventInfo.startingTime}",
                                    style = MaterialTheme.typography.h4,
                                    maxLines = 1,
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
                                    contentDescription = "Place Icon",
                                )
                                Text(
                                    text = eventInfo.locationName,
                                    style = MaterialTheme.typography.h4,
                                    maxLines = 1,
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
        if (attendeeList.size > 0) {
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
                        text = "${attendeeList.size} / ${eventInfo.space}",
                        style = MaterialTheme.typography.h3,
                        color = colorResource(id = R.color.secondaryTextColor),
                        modifier = Modifier
                            .padding(start = 20.dp, end = 20.dp)
                    )
                }
                LazyRow(modifier = Modifier.padding(start = 4.dp, end = 20.dp, bottom = 20.dp)) {
                    items(attendeeList) {
                        AttendeeCard(it, modifier = Modifier.padding(start = 16.dp, bottom = 16.dp))
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
        attendee: AttendeesCard,
        modifier: Modifier = Modifier
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
//                .background(colorResource(id = R.color.secondaryColor))
                .size(200.dp, 180.dp)
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
                            .padding(top = 10.dp, start = 10.dp, end = 10.dp)
                            .height(100.dp)
                            .fillMaxWidth()
                    )
                } else {
                    Image(
                        painter = rememberImagePainter(attendee.profileImage),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .padding(top = 10.dp, start = 10.dp, end = 10.dp)
                            .height(100.dp)
                            .fillMaxWidth()
                    )
                }
                Text(
                    text = attendee.name,
                    style = MaterialTheme.typography.h3,
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
                LazyRow(modifier = Modifier.padding(start = 4.dp, end = 20.dp, bottom = 20.dp)) {
                    items(LoadingEventView.teamMemberList) {
                        InviteTeamMemberCard(
                            it, modifier = Modifier.padding(start = 16.dp, bottom = 16.dp)
                        )
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
        teamMember: AttendeesCard,
        modifier: Modifier = Modifier
    ) {
        Card(
            modifier = modifier
                .size(200.dp, 200.dp)
                .fillMaxWidth()
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
                            .padding(top = 10.dp, start = 10.dp, end = 10.dp)
                            .height(100.dp)
                            .fillMaxWidth()
                    )
                } else {
                    Image(
                        painter = rememberImagePainter(teamMember.profileImage),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .padding(top = 10.dp, start = 10.dp, end = 10.dp)
                            .height(100.dp)
                            .fillMaxWidth()
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
                        sendEventInvite(
                            LoadingEventView.eventIDa,
                            LoadingEventView.locationIDa,
                            teamMember.userID,
                            currentcontext
                        )
                    }, colors = ButtonDefaults.textButtonColors(
                        backgroundColor = colorResource(id = R.color.secondaryColor)
                    )
                ) {
                    Text(
                        text = "Invite",
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
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val attendButtonText = if (attending)
                    "You are currently attending this event"
                else
                    "This event has space available!"
                Text(
                    text = attendButtonText,
                    color = colorResource(id = R.color.secondaryTextColor),
                    style = MaterialTheme.typography.h2,
                )
                Button(
                    onClick = {
                        if (attending) {
                            LoadingEventView.removeAttendance()
                            Toast.makeText(
                                currentcontext,
                                "Successfully left event",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            LoadingEventView.addAttendance()
                        }

                    }, colors = ButtonDefaults.textButtonColors(
                        backgroundColor = colorResource(id = R.color.secondaryColor)
                    )
                ) {
                    val joinButtonText = if (attending)
                        "Leave Event"
                    else
                        "Join Event"
                    Text(
                        text = joinButtonText,
                        color = colorResource(id = R.color.secondaryTextColor),
                        style = MaterialTheme.typography.h3,
                    )
                }


            }
        }

    }


    @Composable
    private fun LeaveHost() {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "You are the host for this event",
                    color = colorResource(id = R.color.secondaryTextColor),
                    style = MaterialTheme.typography.h2,
                )
                // become host button
                Button(
                    onClick = {
                        MaterialDialog(currentcontext).show {
                            title(text = "Are you sure you want to leave this event as host?")
                            positiveButton(R.string.yes) {
                                LoadingEventView.hostLeaveEvent()
                            }
                            negativeButton(R.string.cancel)
                        }
                    }, colors = ButtonDefaults.textButtonColors(
                        backgroundColor = colorResource(id = R.color.secondaryColor)
                    ),
                    modifier = Modifier.padding(10.dp)
                ) {
                    Text(
                        text = "Unhost",
                        color = colorResource(id = R.color.secondaryTextColor),
                        style = MaterialTheme.typography.h2,
                    )
                }
            }
        }
    }


    @Composable
    private fun BecomeHost() {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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
                                LoadingEventView.makeHost()
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
