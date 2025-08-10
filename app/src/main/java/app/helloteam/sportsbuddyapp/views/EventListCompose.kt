/*
 * monamoe
 * List of event offered at that location
 */

package app.helloteam.sportsbuddyapp.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
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
private var locationImage = "null"

// context
@SuppressLint("StaticFieldLeak")
private lateinit var currentcontext: Context

class EventListCompose : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // init variables from your helper object
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
        val intent = Intent(this, MapActivity::class.java)
        startActivity(intent)
        finish()
        super.onBackPressed()  // call super after your code
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
                            val intent = Intent(currentcontext, LandingPage2::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            currentcontext.startActivity(intent)
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                            contentDescription = "Back"
                        )
                    }
                },
                elevation = 10.dp
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .background(colorResource(id = R.color.landingPageBackground))
                    .fillMaxSize()
                    .padding(paddingValues) // Apply scaffold padding here
            ) {
                Column {
                    LocationInfo()
                    ContentDivider()
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
            modifier = Modifier.fillMaxWidth()
        ) {
            if (locationImage != "null" && locationImage.isNotBlank()) {
                Image(
                    painter = rememberImagePainter(
                        data = locationImage,
                        builder = {
                            error(R.drawable.ic_baseline_broken_image_24)
                            placeholder(R.drawable.ic_baseline_broken_image_24)
                        }
                    ),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth()
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.ic_baseline_broken_image_24),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .padding(20.dp)
                        .height(100.dp)
                        .fillMaxWidth()
                )
            }

            Box(modifier = Modifier.padding(20.dp)) {
                Column {
                    Text(
                        text = locationName,
                        style = MaterialTheme.typography.h1,
                        color = colorResource(id = R.color.secondaryTextColor)
                    )
//                    Uncomment if you want to show locationInfo
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
        Text(
            text = "Current Events at this Location",
            style = MaterialTheme.typography.h2,
            color = colorResource(id = R.color.secondaryTextColor),
            modifier = Modifier.padding(20.dp)
        )
        LazyColumn {
            items(locationEventList) { event ->
                LocationEventCard(
                    event,
                    Modifier
                        .padding(bottom = 20.dp, start = 20.dp, end = 20.dp)
                        .clickable {
                            val intent = Intent(currentcontext, SplashLoadingEventView::class.java)
                            intent.putExtra("eventID", event.eventID)
                            intent.putExtra("locationID", event.locationID)
                            currentcontext.startActivity(intent)
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
            modifier = Modifier.background(colorResource(id = R.color.secondaryColor)),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
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
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
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
