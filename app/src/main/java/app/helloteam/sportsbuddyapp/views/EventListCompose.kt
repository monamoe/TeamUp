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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import app.helloteam.sportsbuddyapp.R
import app.helloteam.sportsbuddyapp.helperUI.ContentDivider
import app.helloteam.sportsbuddyapp.helperUI.EventCard
import app.helloteam.sportsbuddyapp.helperUI.ExtraPadding
import app.helloteam.sportsbuddyapp.views.ui.theme.TeamUpTheme
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter

//private var locationEventList: MutableList<EventCard> = mutableListOf<EventCard>()
private var locationEventList: List<EventCard> = listOf(
    EventCard(
        "A",
        "A",
        "A",
        "A",
        true,
        "a",
        "a",
        1,
        1
    ),
    EventCard(
        "A",
        "A",
        "A",
        "A",
        true,
        "a",
        "a",
        1,
        1
    ),
    EventCard(
        "A",
        "A",
        "A",
        "A",
        true,
        "a",
        "a",
        1,
        1
    ),
    EventCard(
        "A",
        "A",
        "A",
        "A",
        true,
        "a",
        "a",
        1,
        1
    ),
)


//context
@SuppressLint("StaticFieldLeak")
private lateinit var currentcontext: Context

class EventListCompose : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
fun EventList() {
    val navController = rememberNavController()
    Scaffold(
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


                    EventListScroll()

                    ExtraPadding()
                }
            }
        },
//        bottomBar = {
//            BottomNavigationBar(
//                navController = navController,
//                onItemClicker = {
//                    navController.navigate(it.route)
//                },
//                context = currentcontext
//            )
//        }
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
            .padding(20.dp)
            .clip(RoundedCornerShape(10.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Image(
                painter = rememberImagePainter(weatherIcon),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
            )
            Text(
                text = "Location Name",
                style = MaterialTheme.typography.h1,
                color = colorResource(id = R.color.secondaryTextColor)
            )
            Text(
                text = "Small community park offering tennis, soccer, basketball and a playground.",
                style = MaterialTheme.typography.h2,
                color = colorResource(id = R.color.secondaryTextColor)
            )
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
                    .clip(RoundedCornerShape(10.dp))
            )
        }
        LazyColumn {
            items(locationEventList) { e ->
                LocationEventCard(
                    e,
                    Modifier
                        .padding(bottom = 5.dp, start = 20.dp, end = 20.dp)
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


@Composable
fun LocationEventCard(
    event: EventCard,
    modifier: Modifier = Modifier
) {
    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(colorResource(id = R.color.secondaryColor))
        ) {
            // title and currently attending
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Event Title",
                    style = MaterialTheme.typography.h3,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Attending 3/10",
                    style = MaterialTheme.typography.h5,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp)
                    .fillMaxWidth()
            ) {
                // activity type
                Text(
                    text = "Soccer",
                    style = MaterialTheme.typography.h3,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}


