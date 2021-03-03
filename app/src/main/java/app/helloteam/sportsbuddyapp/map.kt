package app.helloteam.sportsbuddyapp

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.parse.ParseObject
import com.parse.ParseQuery


class map : AppCompatActivity(), GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback {


// custom Info Windows Rendering
// https://developers.google.com/android/reference/com/google/android/gms/maps/GoogleMap.InfoWindowAdapter
// https://github.com/googlemaps/android-samples/blob/main/ApiDemos/kotlin/app/src/gms/java/com/example/kotlindemos/MarkerDemoActivity.kt

    //    /** Demonstrates customizing the info window and/or its contents.  */
    internal inner class CustomInfoWindowAdapter : GoogleMap.InfoWindowAdapter {

        // this is used to convert the xml activity (custom_info_window.xml) into a view obect
        private val window: View = layoutInflater.inflate(R.layout.custom_info_window, null)
        private lateinit var mContext: Context

        // To replace the default info window, override getInfoWindow(Marker) with your custom rendering and return null for getInfoContents(Marker)
        override fun getInfoWindow(p0: Marker): View {
            render(p0, window)
            return window
        }


        private fun render(marker: Marker, view: View) {
            //the image for the card. //not in use currently
//            val badge = when (marker.title) {
//                "Brisbane" -> R.drawable.badge_qld
//                "Adelaide" -> R.drawable.badge_sa
//                "Sydney" -> R.drawable.badge_nsw
//                "Melbourne" -> R.drawable.badge_victoria
//                "Perth" -> R.drawable.badge_wa
//                in "Darwin Marker 1".."Darwin Marker 4" -> R.drawable.badge_nt
//                else -> 0 // Passing 0 to setImageResource will clear the image view.
//            }

            //cant find this even tho it should
            // something to do with LayoutInflater
            findViewById<TextView>(R.id.location).text = "BBBBBBBBBBBBBBBBBB"


        }


        override fun getInfoContents(p0: Marker): View {
            return null!!
        }
    }


    // is the map ready
    private var isMapReady = 0;

    //permission integer
    private val MY_PERMISSION_FINE_LOCATION: Int = 44

    /** This is ok to be lateinit as it is initialised in onMapReady */
    private lateinit var mMap: GoogleMap

    //for users location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient


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

        Log.i("LOG_TAG", "HAHA: Out of the loop")


    }

    // requesting location permission from user
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSION_FINE_LOCATION ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission granted
                } else {
                    //permission denied
                    Toast.makeText(
                        applicationContext,
                        "App requires location permission to be granted",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
        }
    }


    // when the map is ready
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap;
        Log.i("LOG_TAG", "Inside onMapReady()")

        //getting user location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            //this event only runs when the onMapReady funtion is finished running
            fusedLocationProviderClient!!.lastLocation.addOnSuccessListener {
                //program has permission
                    location ->

                if (location != null) {
                    //update user interface
                    userLocationLat = location.latitude
                    userLocationLon = location.longitude
                    Log.i(
                        "LOG_TAG",
                        "HAHA: Users Location Lat: " + userLocationLat.toString() + ", Lon: " + userLocationLon.toString()
                    )


                    //checking accuracy
                    // idk why we need this, yet
                    if (location.hasAccuracy()) {
                        // setting value to  variable = location.accuracy();
                        // https://www.youtube.com/watch?v=DPKtC2HA9sE
                    }
                }

                updateUserMarker();
            }
        }
        //request permission
        else {
            requestPermissions(
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSION_FINE_LOCATION
            )
        }

        isMapReady = 1;
        Log.i("LOG_TAG", "Map Ready")

        // initialize park markers (will probbaly be an array list)
        // populate array list park markers
        var parklocations = ArrayList<ParkLocationMarker>();
//        Log.i("LOG_TAG", "HAHA: created parklocations array list ")

        //loop through events table and find unique locations to make the markers
        val query = ParseQuery.getQuery<ParseObject>("Events")


        //for every unique location, get the marker info
        //id
        //name
        //lat
        //lng
        //create the ParkLocationMarker object and
        var park1 = ParkLocationMarker()
        var park2 = ParkLocationMarker()
        park1.createParkLocationMarker("1", "Toronto", 43.6532, -79.3832)
        park2.createParkLocationMarker("2", "Mississauga", 43.6532, -79.6441)
        parklocations.add(park1)
        parklocations.add(park2)
        Log.i("LOG_TAG", "HAHA: populated arraylist")


        var a = CustomInfoWindowAdapter()
        mMap.setInfoWindowAdapter(a)


        //loop through array list of unique locations and create markers
        for (i in 0..parklocations.size - 1) {
            mMap.addMarker(
                MarkerOptions()
                    .position(parklocations.get(i).getPosition())
            )
        }


        //find a way to customize the marker icon
        Log.i("LOG_TAG", "HAHA: adding markers to map")
        mMap.addMarker(
            MarkerOptions()
                .position(LatLng(43.591291, -79.677743))
                .title("Century City Park")
                .snippet("Park")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.soccer_ball))

        )
    }

    private fun updateUserMarker() {
        // get user location
        val userlocation = LatLng(userLocationLat, userLocationLon)
        Log.i("LOG_TAG", "HAHA: recieved user location lat and lon")
        mMap.addMarker(
            MarkerOptions()
                .position(userlocation)
                .title(userLocationLon.toString() + ", " + userLocationLat.toString())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        )

        // use map to move camera into position
        val INIT = CameraPosition.Builder().target(userlocation).zoom(15.5f) // orientation
            .tilt(70f) // viewing angle
            .build()


        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(INIT))
    }


    //when an info window is clicked
    override fun onInfoWindowClick(p0: Marker?) {
        //find out which marker is clicked


        //set the in

        //redirect the page to the event being clicked
        TODO("Not yet implemented")
    }
}

