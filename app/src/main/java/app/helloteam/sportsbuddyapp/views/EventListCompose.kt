/*
 * monamoe
 * List of event offered at that location
 */

package app.helloteam.sportsbuddyapp.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import app.helloteam.sportsbuddyapp.R
import app.helloteam.sportsbuddyapp.helperUI.*
import app.helloteam.sportsbuddyapp.views.ui.theme.TeamUpTheme
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter

private var locationEventList: MutableList<EventCard> = mutableListOf()
private var locationName = "Location Name"
private var locationInfo = "Location Info"
private var locationImage = "IDK"

//context
@SuppressLint("StaticFieldLeak")
private lateinit var currentcontext: Context

class EventListCompose : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // init variables
        locationEventList = LoadingEventList.locationEventList

        locationName = LoadingEventList.locationName
        locationInfo = LoadingEventList.locationInfo
        locationImage = LoadingEventList.locationImage

        setContent {
            currentcontext = LocalContext.current

            TeamUpTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    EventList()
                }
            }
        }
    }
    override fun onBackPressed() {
        val intent = Intent(this, map::class.java)
        startActivity(intent)
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
            EventList()
        }
    }
}

@Composable
private fun EventList() {
    val navController = rememberNavController()
    Scaffold(
        topBar = {
            InsetAwareTopAppBar(
                title = {
                    Text(
                        text = locationName,
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
            ) {
                Column {
                    // greeting
                    LocationInfo()
                    ContentDivider()

                    // scroll list of events at the location
                    EventListScroll()

                    ExtraPadding()
                }
            }
        }
    )
}

@OptIn(ExperimentalCoilApi::class)
@Composable
private fun LocationInfo() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .background(colorResource(id = R.color.secondaryColor))
            .clip(RoundedCornerShape(10.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            if (locationImage != "null" && locationImage != "") {
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

            Box(modifier = Modifier.padding(20.dp)) {
                Column {
                    Text(
                        text = locationName,
                        style = MaterialTheme.typography.h1,
                        color = colorResource(id = R.color.secondaryTextColor)
                    )
//                    Text(
//                        text = locationInfo,
//                        style = MaterialTheme.typography.h2,
//                        color = colorResource(id = R.color.secondaryTextColor)
//                    )
                }
            }
        }
    }
}

@Composable
private fun EventListScroll() {
    Column {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Current Events at this Location",
                style = MaterialTheme.typography.h2,
                color = colorResource(id = R.color.secondaryTextColor),
                modifier = Modifier
                    .padding(20.dp)
            )
        }
        LazyColumn {
            items(locationEventList) { e ->
                LocationEventCard(
                    e,
                    Modifier
                        .padding(bottom = 20.dp, start = 20.dp, end = 20.dp)
                        .clickable {
                            val intent = Intent(context, SplashLoadingEventView::class.java)
                            intent.putExtra("eventID", e.eventID)
                            intent.putExtra("locationID", e.locationID)
                            context.startActivity(intent)
                        }
                )
            }
        }
    }
}


@Composable
private fun LocationEventCard(
    event: EventCard,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(colorResource(id = R.color.secondaryColor))
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .background(colorResource(id = R.color.secondaryColor))
        ) {
            // title and currently attending
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.h3,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Space Available: ${event.currentlyAttending} / ${event.space}",
                    style = MaterialTheme.typography.h5,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
                    .fillMaxWidth()
            ) {
                // activity type
                Text(
                    text = "Activity: ${event.activityType}",
                    style = MaterialTheme.typography.h3,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}


