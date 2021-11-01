/*
Author: monamoe
Created:  Feb 22nd 2021
Manages map component of SportBuddy app
uses ParkLocationMarker.kt for creating markers

 */
package app.helloteam.sportsbuddyapp.views

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import app.helloteam.sportsbuddyapp.R
import app.helloteam.sportsbuddyapp.models.ParkLocationMarker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.lang.Exception

class map : AppCompatActivity(), GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback {

    // park locations arraylist
    private var parklocations = ArrayList<ParkLocationMarker>()

    private var locationA: Location = Location("point A")
    private var locationB = Location("point B")

    // custom Info Windows Rendering
    // https://developers.google.com/android/reference/com/google/android/gms/maps/GoogleMap.InfoWindowAdapter
    // https://github.com/googlemaps/android-samples/blob/main/ApiDemos/kotlin/app/src/gms/java/com/example/kotlindemos/MarkerDemoActivity.kt
    internal inner class CustomInfoWindowAdapter : GoogleMap.InfoWindowAdapter {

        // this is used to convert the xml activity (custom_info_window.xml) into a view object
        @SuppressLint("InflateParams")
        private val window: View = layoutInflater.inflate(R.layout.custom_info_window, null)

        // To replace the default info window, override getInfoWindow(Marker) with your custom rendering and return null for getInfoContents(Marker)
        override fun getInfoWindow(p0: Marker): View {
            render(p0, window)
            return window
        }


        // render info window for marker
        private fun render(marker: Marker, inputView: View) {
            // get the marker data out of the manager array list
            lateinit var PLM: ParkLocationMarker
            loop@ for (i in 0 until parklocations.size) {
                // this is comparing the name of the location to get its marker title but it should be comparing id or something.
                if (parklocations[i].getName() == marker.title) {
                    PLM = parklocations[i]
                    break@loop
                }

            }

            try {
                //updating ui components on the info window
                val locationUI: String = PLM.getName()
                val locationComp = inputView.findViewById<TextView>(R.id.location)
                locationComp.text = locationUI
            } catch (e: Exception) {
                //updating ui components on the info window
                val locationUI = "Your Location"
                val locationComp = inputView.findViewById<TextView>(R.id.location)
                locationComp.text = locationUI
                // if the user clicks their owner marker
                Log.i(
                    "LOG_TAG",
                    "MAP : Unable to run onClick for this marker"
                )
            }

        }

        override fun getInfoContents(p0: Marker): View {
            return null!!
        }
    }


    //permission integer
    private val MY_PERMISSION_FINE_LOCATION: Int = 44

    // google map property, init in onMapReady
    private lateinit var mMap: GoogleMap

    //for users location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient


    //default user location values
    private var userLocationLon = 32.00
    private var userLocationLat = 32.00


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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
        mMap = googleMap

        //getting user location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            //this event only runs when the onMapReady function is finished running
            fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                //program has permission
                    location ->

                if (location != null) {
                    //update user interface
                    userLocationLat = location.latitude
                    userLocationLon = location.longitude
                    locationA.latitude = userLocationLat
                    locationA.longitude = userLocationLon
                }
                //render the marker on the users location.
                updateUserMarker()
            }
        }
        //request permission
        else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSION_FINE_LOCATION
            )
        }

        // FIREBASE MIGRATION //
        val db = Firebase.firestore

        db.collection("User").document(Firebase.auth.currentUser?.uid.toString())
            .get().addOnSuccessListener { user ->
                db.collection("Location")
                    .get()
                    .addOnSuccessListener { documents ->
                        for (location in documents) {
                            // events exist at this location
                            db.collection("Location").document(location.id).collection("Events")
                                .get()
                                .addOnSuccessListener { document ->
                                    if (!document.isEmpty) {
                                        val park1 = ParkLocationMarker()
                                        park1.createParkLocationMarker(
                                            location.id,
                                            location.get("Location Name").toString(),
                                            location.get("Lat").toString().toDouble(),
                                            location.get("Lon").toString().toDouble()
                                        )
                                        locationB.latitude =
                                            location.get("Lat").toString().toDouble()
                                        locationB.longitude =
                                            location.get("Lon").toString().toDouble()
                                        val distance = locationA.distanceTo(locationB)
                                        val maxDistance = user.get("distance")
                                        if (maxDistance == null) {
                                            if (distance <= 20000) {
                                                parklocations.add(park1)
                                            }
                                        } else {
                                            if (distance <= user.get("distance").toString()
                                                    .toInt() * 1000
                                            ) {
                                                parklocations.add(park1)
                                            }
                                        }
                                        for (i in 0 until parklocations.size) {
                                            mMap.addMarker(
                                                MarkerOptions()
                                                    .position(
                                                        LatLng(
                                                            parklocations[i].getLat(),
                                                            parklocations[i].getLon()
                                                        )
                                                    )
                                                    .title(parklocations[i].getName())
                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                                            )
                                            Log.i(
                                                "DisplayingMarkers",
                                                "adding marker to the map :" + parklocations[i]
                                                    .getID() + ", " + parklocations[i].getLat()
                                                    .toString() + ", " + parklocations[i]
                                                    .getLon()
                                                    .toString()
                                            )
                                        }
                                    }
                                }
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.w("CreatingParkLocation", "Error getting documents: ", exception)
                    }
            }

        //set the info windows and click listeners for the markers
        mMap.setInfoWindowAdapter(CustomInfoWindowAdapter())
        mMap.setOnInfoWindowClickListener(this)

    }


    private fun updateUserMarker() {
        // get user location
        val userlocation = LatLng(userLocationLat, userLocationLon)
        mMap.addMarker(
            MarkerOptions()
                .position(userlocation)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        )

        // move camera to user location
        mMap.animateCamera(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.Builder().target(userlocation)
                    .zoom(21f)
                    .tilt(72f) // viewing angle
                    .build()
            )
        )
    }

    // info window event handler ( redirects to the view eventslist.kt )
    override fun onInfoWindowClick(p0: Marker?) {


        //get the lat lng position from the marker clicked
        val markerPosition = p0?.position

        // if the user clicks their owner marker
        Log.i(
            "LOG_TAG",
            "MAP :  ${markerPosition.toString()} - $userLocationLat, $userLocationLon"
        )


        if (markerPosition != LatLng(userLocationLat, userLocationLon)) {
            var locationId = ""
            //find which lat lng that belongs to
            for (i in 0 until parklocations.size) {
                if (markerPosition?.equals(parklocations[i].getLatLng())!!) {
                    locationId = parklocations[i].getID().toString()
                    break
                }
            }

            // go to event list loading page
            val intent = Intent(this, SplashLoadingEventList::class.java)
            intent.putExtra("locationID", locationId)
            Log.i("LOG_TAG", "LOADING EVENTS: INTENT TO SPLASH LOADING EVENTLIST")
            startActivity(intent)
            Log.i("LOG_TAG", "LOADING EVENTS: SPLASH LOADING EVENTLIST STARTED")
        } else {
            Log.i(
                "LOG_TAG",
                "MAP :  USER CLICKED THEIR OWN MARKER"
            )
        }
    }
}
