package app.helloteam.sportsbuddyapp.views


import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.helloteam.sportsbuddyapp.R
import app.helloteam.sportsbuddyapp.helperUI.*

const val weatherAPI = "f00bd5c2f24390ab1393b5a7c5459b01"
private const val MY_PERMISSION_FINE_LOCATION: Int = 44

class LandingPage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TeamUpTheme {
                LandingPage("Landing Page")
            }
        }
    }
}

@Preview
@Composable
fun PreviewMessageCard() {
    TeamUpTheme {
        LandingPage("Landing Page")
    }
}


@Composable
fun LandingPage(name: String) {
    Box(
        modifier = Modifier
            .background(Color.Blue)
            .fillMaxSize()
    ) {
        Column {
            GreetingSection("AK")
//            ChipSection(chips = listOf("Soccer", "BasketBall", "Tennis"))
            CurrentWeather()
            EventScroll(
                features = listOf(
                    EventCard(
                        title = "Event1",
                        R.drawable.common_full_open_on_phone,
                        BlueViolet1,
                        BlueViolet2,
                        BlueViolet3,
                        true
                    ),
                    EventCard(
                        title = "Event2",
                        R.drawable.common_full_open_on_phone,
                        LightGreen1,
                        LightGreen2,
                        LightGreen3,
                        false
                    ),
                    EventCard(
                        title = "Event2",
                        R.drawable.common_full_open_on_phone,
                        OrangeYellow1,
                        OrangeYellow2,
                        OrangeYellow3,
                        false
                    ),
                    EventCard(
                        title = "Event2",
                        R.drawable.common_full_open_on_phone,
                        Beige1,
                        Beige2,
                        Beige3,
                        false
                    )
                )
            )
        }
    }

}

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
                style = MaterialTheme.typography.h1
            )
            Text(
                text = "We wish you have a good day!",
                style = MaterialTheme.typography.body1
            )
        }
        Icon(
            painter = painterResource(id = R.drawable.clipboard),
            contentDescription = "Search",
            tint = Color.White,
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
                Text(text = chips[it], color = TextWhite)
            }
        }
    }
}

@Composable
fun CurrentWeather(
    color: Color = LightRed
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(15.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(color)
            .padding(horizontal = 15.dp, vertical = 20.dp)
            .fillMaxWidth()
    ) {
        Column {
            Text(
                text = "Weather",
                style = MaterialTheme.typography.h2
            )
            Text(
                text = "Mississauga • Ontario",
                style = MaterialTheme.typography.body1,
                color = TextWhite
            )
        }
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
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EventScroll(features: List<EventCard>) {
    Column(
        modifier = Modifier
            .background(Color.Blue)
            .fillMaxSize()
    ) {
        Text(
            text = "Your Events",
            style = MaterialTheme.typography.h1,
            modifier = Modifier.padding(15.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(start = 7.5.dp, end = 7.5.dp),
//            modifier = Modifier.fillMaxHeight()
        ) {
            items(features.size) {
                EventCard(feature = features[it])
            }
        }
    }
}

@Composable
fun EventCard(feature: EventCard) {
    BoxWithConstraints(
        modifier = Modifier
            .padding(7.5.dp)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(10.dp))
            .background(feature.darkColor)
    ) {
        // draw path
        val width = constraints.maxWidth
        val height = constraints.maxHeight

        //create a point generation function

        // dark path
        val darkColoredPoint1 = Offset(0f, height * 0.3f)
        val darkColoredPoint2 = Offset(width * 0.1f, height * 0.35f)
        val darkColoredPoint3 = Offset(width * 0.4f, height * 0.05f)
        val darkColoredPoint4 = Offset(width * 0.75f, height * 0.7f)
        val darkColoredPoint5 = Offset(width * 1.4f, -height.toFloat())

        val mediumColoredPath = Path().apply {
            moveTo(darkColoredPoint1.x, darkColoredPoint1.y)
            standardQuadFromTo(darkColoredPoint1, darkColoredPoint2)
            standardQuadFromTo(darkColoredPoint2, darkColoredPoint3)
            standardQuadFromTo(darkColoredPoint3, darkColoredPoint4)
            standardQuadFromTo(darkColoredPoint4, darkColoredPoint5)
            //out of box points
            lineTo(width.toFloat() + 100f, height.toFloat() + 100f)
            lineTo(-100f, height.toFloat() + 100f)
            close()
        }

        val lightPoint1 = Offset(0f, height * 0.35f)
        val lightPoint2 = Offset(width * 0.1f, height * 0.4f)
        val lightPoint3 = Offset(width * 0.3f, height * 0.35f)
        val lightPoint4 = Offset(width * 0.65f, height.toFloat())
        val lightPoint5 = Offset(width * 1.4f, -height.toFloat() / 3f)

        val lightColoredPath = Path().apply {
            moveTo(lightPoint1.x, lightPoint1.y)
            standardQuadFromTo(lightPoint1, lightPoint2)
            standardQuadFromTo(lightPoint2, lightPoint3)
            standardQuadFromTo(lightPoint3, lightPoint4)
            standardQuadFromTo(lightPoint4, lightPoint5)
            lineTo(width.toFloat() + 100f, height.toFloat() + 100f)
            lineTo(-100f, height.toFloat() + 100f)
            close()
        }
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawPath(
                path = mediumColoredPath,
                color = feature.mediumColor
            )
            drawPath(
                path = lightColoredPath,
                color = feature.lightColor
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp)
        ) {
            Text(
                text = feature.title,
                style = MaterialTheme.typography.h2,
                lineHeight = 26.sp,
                modifier = Modifier.align(Alignment.TopStart)
            )
//                Icon(
//                    painter = painterResource(id = feature.IconID),
//                    contentDescription = feature.title,
//                    tint = Color.White,
//                    modifier = Modifier.align(Alignment.BottomStart),
//                )
            Text(
                text = "Start",
                color = TextWhite,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable {
                        // go to event view
                    }
                    .align(Alignment.BottomEnd)
                    .clip(RoundedCornerShape(10.dp))
                    .background(ButtonBlue)
                    .padding(vertical = 6.dp, horizontal = 15.dp)
            )
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

//
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
//
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



