package app.helloteam.sportsbuddyapp

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlin.math.log

class map : AppCompatActivity(), OnMapReadyCallback {

    //permission integer
    private val MY_PERMISSION_FINE_LOCATION: Int = 44

    //map view
    private lateinit var mMap: GoogleMap


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

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        Log.i("LOG_TAG", "HAHA: Map Fragmented")

        //componenets
        val backButton = findViewById<Button>(R.id.backBtn)
        val refreshButton = findViewById<Button>(R.id.refreshBtn)


        //event handlers
        backButton.setOnClickListener{
            Log.i("LOG_TAG", "HAHA: going back")
            val intent = Intent(this, LandingPageActivity::class.java)
            startActivity(intent)
        }
        refreshButton.setOnClickListener{



            //adding markers
            val toronto = LatLng(43.6532, -79.3832)
            val mississauga = LatLng(43.6532, -79.6441)
            //find a way to customize the marker icon

            //user location marker
            val userlocation = LatLng(userLocationLat, userLocationLon)

            mMap.addMarker(MarkerOptions().position(toronto).title("Toronto"))
            mMap.addMarker(MarkerOptions().position(mississauga).title("Mississauga"))

            mMap.addMarker(MarkerOptions().position(userlocation).title(userLocationLon.toString() + ", " + userLocationLat.toString()))
        }




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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray){
        super.onRequestPermissionsResult(requestCode, permissions,grantResults)
        when(requestCode){
            MY_PERMISSION_FINE_LOCATION ->
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //permission granted
                }
                else{
                    //permission denied
                    Toast.makeText(applicationContext, "App requires location permission to be granted", Toast.LENGTH_SHORT).show()
                    finish()
                }
        }
    }

    /**
     * map on ready
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        Log.i("LOG_TAG","HAHA: Map Setup Ready")
        //getting user location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if ( ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient!!.lastLocation.addOnSuccessListener {
                //program has permission
                    location->
                if(location != null){
                    //update user interface
                    userLocationLat = location.latitude
                    userLocationLon = location.longitude
                    Log.i("LOG_TAG", "HAHA: Users Location Lat: " + userLocationLat.toString() + ", Lon: " + userLocationLon.toString())





                    //checking accuracy
                    // idk why we need this, yet
                    if (location.hasAccuracy()){
                        // setting value to  variable = location.accuracy();
                        //
                        // https://www.youtube.com/watch?v=DPKtC2HA9sE
                    }
                }
            }
        }
        //request permission
        else{
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSION_FINE_LOCATION)
        }





        Log.i("LOG_TAG","Map Ready")
    }
}