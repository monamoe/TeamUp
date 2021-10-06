package app.helloteam.sportsbuddyapp.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Bundle
import android.os.SystemClock
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.navigation.compose.rememberNavController
import app.helloteam.sportsbuddyapp.R
import app.helloteam.sportsbuddyapp.firebase.EventHandling.db
import app.helloteam.sportsbuddyapp.helperUI.*
import app.helloteam.sportsbuddyapp.views.ui.theme.*
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

//context
@SuppressLint("StaticFieldLeak")
lateinit private var currentcontext: Context

lateinit private var username: String

lateinit private var userLocation: String
lateinit private var weatherIcon: Icon
lateinit private var weatherString: Icon

private lateinit var hostingAttendingEventList: List<EventCard>

class LandingPage2 : ComponentActivity() {


    private val viewModel: LandingPageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        hostingAttendingEventList = viewModel.eventList.value
//        for (event in hostingAttendingEventList) {
//            Log.i("LOG_TAG", "onCreateView EventList: ${event.title}")
//        }

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
                    GreetingSection()
//                    ChipSection(chips = listOf("Soccer", "BasketBall", "Tennis"))
                    CurrentWeather()

                    ContentDivider()


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
                                3,
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
        horizontalArrangement = Arrangement.SpaceBetween,
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
                    backgroundColor = Color.Blue
                )
            ) {
                Text("Create Event")
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
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Good morning, $name",
                style = MaterialTheme.typography.h1,
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
                text = "Weather",
                style = MaterialTheme.typography.h2,
                color = colorResource(id = R.color.secondaryTextColor)
            )
            Text(
                text = "Mississauga • Ontario",
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
        Text(
            text = "Recommended Events",
            style = MaterialTheme.typography.h1,
            color = colorResource(id = R.color.secondaryTextColor),
            modifier = Modifier.padding(15.dp)
        )
        LazyRow(
            modifier = Modifier
                .fillMaxHeight()
        ) {
            items(events) { state ->
                EventCard(
                    state,
//                    navigateToEvent,
                    Modifier.padding(start = 16.dp, bottom = 16.dp)
                )
            }
        }
    }
}

// state hoisting
//@Composable
//fun HelloScreen() {
//    events: List<EventCard>,
//
//}


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
    Column() {
        Text(
            text = "Your Events",
            style = MaterialTheme.typography.h1,
            color = colorResource(id = R.color.secondaryTextColor),
            modifier = Modifier.padding(15.dp),
        )
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
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.h6,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = event.hostName,
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


//@Composable
//fun NavMenu(
//    items: List<NavMenuContent>,
//    mod: Modifier = Modifier,
//    activeHighlightColor: Color = ButtonBlue,
//    activeTextColor: Color = Color.White,
//    inactiveTextColor: Color = Color.Blue,
//    initialSelectedItemIndex: Int = 0
//) {
//    var selectedItemIndex by remember {
//        mutableStateOf(initialSelectedItemIndex)
//    }
//    Row(
//        horizontalArrangement = Arrangement.SpaceAround,
//        verticalAlignment = Alignment.CenterVertically,
//        modifier = mod
//            .fillMaxWidth()
//            .background(DeepBlue)
//            .padding(15.dp)
//    ) {
//        items.forEachIndexed { index, item ->
//            NavMenuItem(
//                item = item,
//                isSelected = true,
//                activeHighlightColor = activeHighlightColor,
//                activeTextColor = activeTextColor,
//                inactiveTextColor = inactiveTextColor,
//            ) {
//                selectedItemIndex = index
//            }
//        }
//    }
//}


//
//@Composable
//fun NavMenuItem(
//    item: NavMenuContent,
//    isSelected: Boolean = false,
//    activeHighlightColor: Color = ButtonBlue,
//    activeTextColor: Color = Color.White,
//    inactiveTextColor: Color = Color.Blue,
//    onItemClick: () -> Unit
//) {
//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center,
//        modifier = Modifier.clickable {
//            onItemClick()
//        }
//    ) {
//        Box(
//            contentAlignment = Alignment.Center,
//            modifier = Modifier
//                .clip(RoundedCornerShape(10.dp))
//                .background(
//                    if (isSelected)
//                        activeHighlightColor
//                    else
//                        Color.Transparent
//                )
//        ) {
//            Icon(
//                painter = painterResource(id = item.icon),
//                contentDescription = item.title,
//                tint = if (isSelected) activeTextColor else inactiveTextColor,
//                modifier = Modifier.size(30.dp)
//            )
//        }
//        Text(
//            text = item.title,
//            color = if (isSelected) activeTextColor else inactiveTextColor
//        )
//    }
//}

// DONT DELETE THIS YET WE NEED THIS TO ADD INFORMATION ONTO THE LANDING PAGE - AK
//class LandingPageActivity() : Fragment(), Parcelable {
//
//    override fun onCreate(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//
//
//        val binding = DataBindingUtil.inflate<ActivityLandingPageBinding>(
//            inflater, R.layout.activity_landing_page, container, false
//        )
//        binding.apply {
//            composeView.setContent {
//                MaterialTheme {
//                    Text("HELLO COMPOSABLE")
//                }
//            }
//        }
//
//        setHasOptionsMenu(true)
//        return binding.root
//    }
//
//    // is the user logged in
//    val uid = FirebaseAuth.getInstance().uid
//
//    constructor(parcel: Parcel) : this() {
//    }


//        if (uid == null) {
//            // clear activity stack, go to login page
//            val intent = Intent(this, LoginActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
//            startActivity(intent)
//        }
//        val db = Firebase.firestore
//
//        val location = db.collection("User").document(Firebase.auth.currentUser?.uid.toString())
//        location.get().addOnSuccessListener { user ->
//            findViewById<TextView>(R.id.ShowUsername).text =
//                "Welcome " + user.get("userName")
//        }
//
//        //create event button
//        findViewById<Button>(R.id.CreateEventBtn).setOnClickListener {
//            val intent = Intent(this, CreateEventActivity::class.java)
//            startActivity(intent)
//        }
//
//        temp = findViewById(R.id.temp)
//        forecast = findViewById(R.id.forecast)
//        icon = findViewById(R.id.conIcon)
//
//        getUserCity()


//fun afterLogout() {//method to go back to login screen after logout
//    finish()
//    val intent = Intent(this, LoginActivity::class.java)
//    startActivity(intent)
//}
//
//override fun onCreateOptionsMenu(menu: Menu): Boolean {
//    val inflater = menuInflater
//    inflater.inflate(R.menu.menu, menu)
//    return true
//}
//
//override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
//    R.id.action_profile -> {
//        val intent = Intent(this, ProfilePage::class.java)
//        startActivity(intent)
//        true
//    }
//    R.id.action_events -> {
//        val intent = Intent(this, ViewPlayerEvents::class.java)
//        startActivity(intent)
//        true
//    }
//    R.id.action_hosted -> {
//        val intent = Intent(this, HostEvents::class.java)
//        startActivity(intent)
//        true
//    }
//    R.id.action_logout -> {
//        val dialogBuilder = AlertDialog.Builder(this)
//        dialogBuilder.setMessage("Do you want to log out?")
//            .setCancelable(false)
//            .setPositiveButton("Logout", DialogInterface.OnClickListener { dialog, id ->
//                FirebaseAuth.getInstance().signOut()
//                afterLogout()
//            })
//            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->
//                dialog.cancel()
//            })
//        val alert = dialogBuilder.create()
//        alert.setTitle("Logout")
//        alert.show()
//        true
//    }
//    R.id.action_map -> {
//        val intent = Intent(this, map::class.java)
//        startActivity(intent)
//        true
//    }
//    R.id.action_team -> {
//        val intent = Intent(this, TeamsActivity::class.java)
//        startActivity(intent)
//        true
//    }
//    else -> {
//        super.onOptionsItemSelected(item)
//    }
//}

//override fun onRequestPermissionsResult(
//    requestCode: Int,
//    permissions: Array<out String>,
//    grantResults: IntArray
//) {
//    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//    when (requestCode) {
//        MY_PERMISSION_FINE_LOCATION ->
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                getUserCity()
//            } else {
//                //permission denied
//                Toast.makeText(
//                    applicationContext,
//                    "App requires location permission to be granted",
//                    Toast.LENGTH_SHORT
//                ).show()
//                finish()
//            }
//    }
//}
//
//fun getUserCity() {
//    var fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
//    var userLocationLat = 0.0
//    var userLocationLon = 0.0
//    var cityName = ""
//    if (ActivityCompat.checkSelfPermission(
//            this,
//            Manifest.permission.ACCESS_FINE_LOCATION
//        ) == PackageManager.PERMISSION_GRANTED
//    ) {
//        //this event only runs when the onMapReady funtion is finished running
//        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
//            //program has permission
//                location ->
//
//            if (location != null) {
//                //update user interface
//                userLocationLat = location.latitude
//                userLocationLon = location.longitude
//
//                val geocoder = Geocoder(this, Locale.getDefault())
//                val addresses: List<Address> =
//                    geocoder.getFromLocation(userLocationLat, userLocationLon, 1)
//                cityName = addresses[0].getLocality()
//            }
//            //render the marker on the users location.
//            weatherTask(icon).execute(
//                cityName,
//                getString(R.string.weather_api)
//            ) //gets weather for current location
//        }
//    }
//    //request permission
//    else {
//        requestPermissions(
//            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//            MY_PERMISSION_FINE_LOCATION
//        )
//    }
//override fun writeToParcel(parcel: Parcel, flags: Int) {
//
//}
//
//override fun describeContents(): Int {
//    return 0
//}
//
//companion object CREATOR : Parcelable.Creator<LandingPageActivity> {
//    override fun createFromParcel(parcel: Parcel): LandingPageActivity {
//        return LandingPageActivity(parcel)
//    }
//
//    override fun newArray(size: Int): Array<LandingPageActivity?> {
//        return arrayOfNulls(size)
//    }
//}
//
//
//fun getHostingAttendingList(userID: String): List<EventCard> {
//    var eventList: List<EventCard> = emptyList()
//
//    var hostingDone: Boolean = false
//    var attendingDone: Boolean = false
//
//    // current user information
//    db.collection("User").document(userID)
//        .get()
//        .addOnSuccessListener { users ->
//
//            // hosting
//            db.collection("User").document(userID).collection("Hosting")
//                .get()
//                .addOnSuccessListener { hosting ->
//                    for (host in hosting) {
//                        db.collection("Location").document(host.get("locationID").toString())
//                            .collection("Events").document(host.get("eventID").toString())
//                            .get()
//                            .addOnSuccessListener { event ->
//
//                                if (users != null) {
//                                    db.collection("Location")
//                                        .document(host.get("locationID").toString())
//                                        .get()
//                                        .addOnSuccessListener { loc ->
//
//                                            eventList +=
//                                                EventCard(
//                                                    event.get("title").toString(),
//                                                    host.get("eventID").toString(),
//                                                    loc.get("StreetView").toString(),
//                                                    true,
//                                                    users.get("userName").toString(),
//                                                    "Playing soccer with a couple friends, feel free to join in",
//                                                    event.get("eventSpace").toString().toInt(),
//                                                    event.get("currentlyAttending").toString()
//                                                        .toInt(),
//                                                )
//                                        }
//                                }
//                            }
//                    }
//                    hostingDone = true
//                    Log.i("LOG_TAG", "EVENT DISPLAY: HOSTING DONE ${eventList.toString()}")
//
//                }
//
//
//            // attending
//            db.collection("User").document(userID).collection("Attending")
//                .get()
//                .addOnSuccessListener { hosting ->
//                    for (host in hosting) {
//                        db.collection("Location")
//                            .document(host.get("locationID").toString())
//                            .collection("Events")
//                            .document(host.get("eventID").toString())
//                            .get()
//                            .addOnSuccessListener { event ->
//
//                                db.collection("Location")
//                                    .document(host.get("locationID").toString())
//                                    .get()
//                                    .addOnSuccessListener { loc ->
//                                        eventList +=
//                                            EventCard(
//                                                event.get("title").toString(),
//                                                host.get("eventID").toString(),
//                                                loc.get("StreetView").toString(),
//                                                false,
//                                                users.get("userName").toString(),
//                                                "Playing soccer with a couple friends, feel free to join in",
//                                                event.get("eventSpace").toString()
//                                                    .toInt(),
//                                                event.get("currentlyAttending")
//                                                    .toString()
//                                                    .toInt(),
//                                            )
//                                    }
//                            }
//                    }
//                    attendingDone = true
//                    Log.i("LOG_TAG", "EVENT DISPLAY: ATTENDING DONE ${eventList.toString()}")
//                }
//        }
//
//    while (true) {
//        Log.i("LOG_TAG", "EVENT DISPLAY : IN LOOP")
//        if (!hostingDone && !attendingDone)
//            break
//    }
//
//    return eventList
//}
