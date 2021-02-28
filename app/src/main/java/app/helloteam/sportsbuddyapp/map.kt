package app.helloteam.sportsbuddyapp

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class map : AppCompatActivity(), GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback {


// custom Info Windows Rendering
// https://developers.google.com/android/reference/com/google/android/gms/maps/GoogleMap.InfoWindowAdapter
// https://github.com/googlemaps/android-samples/blob/main/ApiDemos/kotlin/app/src/gms/java/com/example/kotlindemos/MarkerDemoActivity.kt

//    /** Demonstrates customizing the info window and/or its contents.  */
//    internal inner class CustomInfoWindowAdapter : InfoWindowAdapter {
//
//        // These are both view groups containing an ImageView with id "badge" and two
//        // TextViews with id "title" and "snippet".
//        private val window: View = layoutInflater.inflate(R.layout.custom_info_window, null)
//        private val contents: View = layoutInflater.inflate(R.layout.custom_info_contents, null)
//
//        override fun getInfoWindow(marker: Marker): View? {
//            if (options.checkedRadioButtonId != R.id.custom_info_window) {
//                // This means that getInfoContents will be called.
//                return null
//            }
//            render(marker, window)
//            return window
//        }
//
//        override fun getInfoContents(marker: Marker): View? {
//            if (options.checkedRadioButtonId != R.id.custom_info_contents) {
//                // This means that the default info contents will be used.
//                return null
//            }
//            render(marker, contents)
//            return contents
//        }
//
//        private fun render(marker: Marker, view: View) {
//            val badge = when (marker.title) {
//                "Brisbane" -> R.drawable.badge_qld
//                "Adelaide" -> R.drawable.badge_sa
//                "Sydney" -> R.drawable.badge_nsw
//                "Melbourne" -> R.drawable.badge_victoria
//                "Perth" -> R.drawable.badge_wa
//                in "Darwin Marker 1".."Darwin Marker 4" -> R.drawable.badge_nt
//                else -> 0 // Passing 0 to setImageResource will clear the image view.
//            }
//
//            view.findViewById<ImageView>(R.id.badge).setImageResource(badge)
//
//            // Set the title and snippet for the custom info window
//            val title: String? = marker.title
//            val titleUi = view.findViewById<TextView>(R.id.title)
//
//            if (title != null) {
//                // Spannable string allows us to edit the formatting of the text.
//                titleUi.text = SpannableString(title).apply {
//                    setSpan(ForegroundColorSpan(Color.RED), 0, length, 0)
//                }
//            } else {
//                titleUi.text = ""
//            }
//
//            val snippet: String? = marker.snippet
//            val snippetUi = view.findViewById<TextView>(R.id.snippet)
//            if (snippet != null && snippet.length > 12) {
//                snippetUi.text = SpannableString(snippet).apply {
//                    setSpan(ForegroundColorSpan(Color.MAGENTA), 0, 10, 0)
//                    setSpan(ForegroundColorSpan(Color.BLUE), 12, snippet.length, 0)
//                }
//            } else {
//                snippetUi.text = ""
//            }
//        }
//    }


    // is the map ready
    private var isMapReady = 0;

    //permission integer
    private val MY_PERMISSION_FINE_LOCATION: Int = 44

    /** This is ok to be lateinit as it is initialised in onMapReady */
    private lateinit var mMap: GoogleMap

    /**
     * Keeps track of the last selected marker (though it may no longer be selected).  This is
     * useful for refreshing the info window.
     *
     * Must be nullable as it is null when no marker has been selected
     */
    private var lastSelectedMarker: Marker? = null

    private val markerRainbow = ArrayList<Marker>()

    /** These can be lateinit as they are set in onCreate */
    private lateinit var topText: TextView
    private lateinit var rotationBar: SeekBar
    private lateinit var flatBox: CheckBox
    private lateinit var options: RadioGroup


    //for users location
    // FusedLocationProviderClient - Main class for receiving location updates.
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    // LocationRequest - Requirements for the location updates, i.e., how often you
    // should receive updates, the priority, etc.
    private lateinit var locationRequest: LocationRequest

    // LocationCallback - Called when FusedLocationProviderClient has a new Location.
    private lateinit var locationCallback: LocationCallback

    // Used only for local storage of the last known location. Usually, this would be saved to your
    // database, but because this is a simplified sample without a full database, we only need the
    // last location to create a Notification if the user navigates away from the app.
    private var currentLocation: Location? = null

    //default user location values
    var userLocationLon = 0.1;
    var userLocationLat = 0.1;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)


        // back button
        findViewById<Button>(R.id.backBtn).setOnClickListener {
            val intent = Intent(this, LandingPageActivity::class.java)
            // add data to intents using .putExtra("name", "value");
            startActivity(intent)
        }


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        Log.i("LOG_TAG", "HAHA: going into the loop")
        //wait until the map is ready
        while (isMapReady == 0) {
            Thread.sleep(1_000)
        }
        Log.i("LOG_TAG", "HAHA: Out of the loop")




        // get user location
        val userlocation = LatLng(userLocationLat, userLocationLon)

        // initialize park markers (will probbaly be an array list)
        // populate array list park markers
        var parklocations = ArrayList<ParkLocationMarker>();

        var park1 = ParkLocationMarker()
        var park2= ParkLocationMarker()
        park1.createParkLocationMarker(1, "Toronto", 43.6532, -79.3832)
        park2.createParkLocationMarker(1, "Mississauga", 43.6532, -79.6441)
        parklocations.add(park1)
        parklocations.add(park2)

        //adding markers to view
//        for (i in 0..parklocations.size - 1)
//        {
//            mMap.addMarker(MarkerOptions().position(LatLng(parklocations.get(i).getLon(), parklocations.get(i).getLat())).title(parklocations.get(i).getName()))
//        }


        //find a way to customize the marker icon
         mMap.addMarker(MarkerOptions().position(LatLng(parklocations.get(0).getLon(), parklocations.get(0).getLat())).title(parklocations.get(0).getName()))


        val melbourne = mMap.addMarker(
                MarkerOptions()
                        .position(LatLng(-37.81319, 144.96298))
                        .title("Melbourne")
                        .snippet("Population: 4,137,400")
        )


        //adding user location marker temp
        mMap.addMarker(MarkerOptions().position(userlocation).title(userLocationLon.toString() + ", " + userLocationLat.toString()))

        //for timing, refreshing user location, probs wont use this bcos its useless
        // and its confusing
//        locationRequest = LocationRequest().apply {
//            // Sets the desired interval for active location updates. This interval is inexact. You
//            // may not receive updates at all if no location sources are available, or you may
//            // receive them less frequently than requested. You may also receive updates more
//            // frequently than requested if other applications are requesting location at a more
//            // frequent interval.
//            //
//            // IMPORTANT NOTE: Apps running on Android 8.0 and higher devices (regardless of
//            // targetSdkVersion) may receive updates less frequently than this interval when the app
//            // is no longer in the foreground.
//            interval = TimeUnit.SECONDS.toMillis(60)
//
//            // Sets the fastest rate for active location updates. This interval is exact, and your
//            // application will never receive updates more frequently than this value.
//            fastestInterval = TimeUnit.SECONDS.toMillis(30)
//
//            // Sets the maximum time when batched location updates are delivered. Updates may be
//            // delivered sooner than this interval.
//            maxWaitTime = TimeUnit.MINUTES.toMillis(2)
//
//            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//        }


    }

    // requesting location permission from user
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSION_FINE_LOCATION ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission granted
                } else {
                    //permission denied
                    Toast.makeText(applicationContext, "App requires location permission to be granted", Toast.LENGTH_SHORT).show()
                    finish()
                }
        }
    }


    // when the map is ready
    override fun onMapReady(googleMap: GoogleMap) {


        //getting user location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient!!.lastLocation.addOnSuccessListener {
                //program has permission
                location ->
                if (location != null) {
                    //update user interface
                    userLocationLat = location.latitude
                    userLocationLon = location.longitude
                    Log.i("LOG_TAG", "HAHA: Users Location Lat: " + userLocationLat.toString() + ", Lon: " + userLocationLon.toString())


                    //checking accuracy
                    // idk why we need this, yet
                    if (location.hasAccuracy()) {
                        // setting value to  variable = location.accuracy();
                        //
                        // https://www.youtube.com/watch?v=DPKtC2HA9sE
                    }
                }
            }
        }
        //request permission
        else {
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSION_FINE_LOCATION)
        }

        isMapReady = 1;
        Log.i("LOG_TAG", "Map Ready")
    }


    fun addInfoMarker() {

    }

    //when an info window is clicked
    override fun onInfoWindowClick(p0: Marker?) {
        //find out which event is clicked

        //redirect the page to the event being clicked
        TODO("Not yet implemented")
    }
}


class ParkLocationMarker {
    private var id: Int = 0;
    private var name: String = ""
    private var lat: Double = 0.0
    private var lon: Double = 0.0
//    val name: String = ""

    //constructor
     fun createParkLocationMarker(inputId : Int, inputName: String, inputLat: Double, InputLon: Double)
    {
        id = inputId
        name = inputName
        lat = inputLat
        lon = InputLon
    }

    //setters
    fun setName(a: String)
    {
        name = a
    }
    fun setLat(a: Double)
    {
        lat = a
    }
    fun setLon(a: Double)
    {
        lon = a
    }

    //getters
    fun getID(): Int
    {
        return id
    }
    fun getName(): String
    {
        return name
    }
    fun getLat(): Double
    {
        return lat;
    }
    fun getLon(): Double
    {
        return lon;
    }




}