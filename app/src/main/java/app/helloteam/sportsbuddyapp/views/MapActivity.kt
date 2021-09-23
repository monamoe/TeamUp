/*
Author: monamoe
Created:  Feb 22nd 2021
Manages map component of SportBuddy app
uses ParkLocationMarker.kt for creating markers
uses SportLocation to retrive marker locations
 */


package app.helloteam.sportsbuddyapp.views


import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import app.helloteam.sportsbuddyapp.R
import app.helloteam.sportsbuddyapp.R.id.backBtn
import app.helloteam.sportsbuddyapp.models.ParkLocationMarker
import app.helloteam.sportsbuddyapp.firebase.UserHandling
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.firestore.ktx.*
import com.google.firebase.ktx.Firebase


class map : AppCompatActivity(), GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback {


    // park locations arraylist
    var parklocations = ArrayList<ParkLocationMarker>()


    // custom Info Windows Rendering
    // https://developers.google.com/android/reference/com/google/android/gms/maps/GoogleMap.InfoWindowAdapter
    // https://github.com/googlemaps/android-samples/blob/main/ApiDemos/kotlin/app/src/gms/java/com/example/kotlindemos/MarkerDemoActivity.kt
    //    /** Demonstrates customizing the info window and/or its contents.  */
    internal inner class CustomInfoWindowAdapter : GoogleMap.InfoWindowAdapter {

        // this is used to convert the xml activity (custom_info_window.xml) into a view obect
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
            loop@ for (i in 0..parklocations.size - 1) {
                // this is comparing the name of the location to get its marker title but it should be comparing id or something.
                if (parklocations.get(i).getName() == marker.title) {
                    PLM = parklocations.get(i)
                    break@loop
                }

            }

            //updating ui components on the info window
            val locationUI: String? = PLM.getName()
            Log.i("LOG_TAG", "locationUI:" + locationUI)
            val locationComp = inputView.findViewById<TextView>(R.id.location)
            if (locationUI != null) {
                locationComp.text = locationUI;
            } else {
                locationComp.text = "Null"
            }
        }

        override fun getInfoContents(p0: Marker): View {
            return null!!
        }
    }


    //permission integer
    private val MY_PERMISSION_FINE_LOCATION: Int = 44

    /** This is ok to be lateinit as it is initialised in onMapReady */
    private lateinit var mMap: GoogleMap

    //for users location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient


    //default user location values
    var userLocationLon = 69.420
    var userLocationLat = 69.420


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // back button
        findViewById<Button>(backBtn).setOnClickListener {
            val intent = Intent(this, LandingPageActivity::class.java)
            startActivity(intent)
        }

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
        Log.i("onMapReady", "Inside onMapReady()")

        //getting user location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            //this event only runs when the onMapReady funtion is finished running
            fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                //program has permission
                    location ->

                if (location != null) {
                    //update user interface
                    userLocationLat = location.latitude
                    userLocationLon = location.longitude

                    //checking accuracy
                    // idk why we need this, yet
                    if (location.hasAccuracy()) {
                        // setting value to  variable = location.accuracy()
                        // https://www.youtube.com/watch?v=DPKtC2HA9sE
                    }
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

        //this should include .whereGreaterThan("numberOfEvents", 0) once we add that to the database
        db.collection("Location")
            .get()
            .addOnSuccessListener { documents ->
                for (location in documents) {
                    val park1 = ParkLocationMarker()

//                    Log.i(
//                        "CreatingParkLocation",
//                        "heres the info were getting from the database : " + location.get("Lat")
//                            .toString().toDouble() + " " +
//                                location.get("Lon").toString().toDouble()
//                    )
                    Log.i("DisplayingMarkers", "FUCK " + location.get("Location Name").toString())
                    Log.i("DisplayingMarkers", "FUCK FUCK: " + location.id)

                    // creates the marker object
                    park1.createParkLocationMarker(
                        location.id,
                        location.get("Location Name").toString(),
                        location.get("Lat").toString().toDouble(),
                        location.get("Lon").toString().toDouble()
                    )
                    parklocations.add(park1)

                }


                //display the markers
                Log.i("DisplayingMarkers", "park locations size " + parklocations.size)
                for (i in 0..parklocations.size - 1) {
                    mMap.addMarker(
                        MarkerOptions()
                            .position(
                                LatLng(
                                    parklocations.get(i).getLat(),
                                    parklocations.get(i).getLon()
                                )
                            )
                            .title(parklocations.get(i).getName())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                    )
                    Log.i(
                        "DisplayingMarkers",
                        "adding marker to the map :" + parklocations.get(i)
                            .getID() + ", " + parklocations.get(i).getLat()
                            .toString() + ", " + parklocations.get(i).getLon().toString()
                    )
                }


            }
            .addOnFailureListener { exception ->
                Log.w("CreatingParkLocation", "Error getting documents: ", exception)
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
                    .zoom(15.5f)
                    .tilt(70f) // viewing angle
                    .build()
            )
        )


    }

    // info window event handler ( redirects to the view eventslist.kt )
    override fun onInfoWindowClick(p0: Marker?) {
        //get the latlng position from the marker
        val markerPosition = p0?.position

        // find out which object in the arraylist matches with the
        // if the user clicks their own
        Log.i("onInfoWindowClick", "if the user clicks their own marker "+markerPosition.toString() + " " + LatLng(userLocationLat, userLocationLon))
        if (markerPosition != LatLng(userLocationLat, userLocationLon)) {
            var locationId = ""
            //find which latlng that belongs to
            for (i in 0..parklocations.size - 1) {
                if (markerPosition?.equals(parklocations.get(i).getLatLng())!!) {
                    locationId = parklocations.get(i).getID().toString()
                    break
                }
            }
            Log.i(
                "onInfoWindowClick",
                "redirecting to info window with id of: " + locationId
            )
            val intent = Intent(this, eventslist::class.java)
            intent.putExtra("locationID", locationId)
            startActivity(intent)
        }
    }


    // navigation purposes
    fun afterLogout() {//method to go back to login screen after logout
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_map, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_profile -> {
            val intent = Intent(this, ProfilePage::class.java)
            startActivity(intent)
            true
        }
        R.id.action_logout -> {
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setMessage("Do you want to log out?")
                .setCancelable(false)
                .setPositiveButton("Logout", DialogInterface.OnClickListener { dialog, id ->
                    UserHandling.Logout()
                    afterLogout()
                })
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->
                    dialog.cancel()
                })
            val alert = dialogBuilder.create()
            alert.setTitle("Logout")
            alert.show()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }
}
