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

class MapActivity : AppCompatActivity(), GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback {

    private val parklocations = ArrayList<ParkLocationMarker>()

    private var locationA: Location = Location("point A")
    private var locationB = Location("point B")

    private val MY_PERMISSION_FINE_LOCATION = 44

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var userLocationLat = 32.00
    private var userLocationLon = 32.00

    internal inner class CustomInfoWindowAdapter : GoogleMap.InfoWindowAdapter {

        @SuppressLint("InflateParams")
        private val window: View = layoutInflater.inflate(R.layout.custom_info_window, null)

        override fun getInfoWindow(marker: Marker): View {
            render(marker, window)
            return window
        }

        override fun getInfoContents(marker: Marker): View? = null

        private fun render(marker: Marker, view: View) {
            val parkLocationMarker = parklocations.find { it.getName() == marker.title }

            val locationText = parkLocationMarker?.getName() ?: "Your Location"
            val locationTextView = view.findViewById<TextView>(R.id.location)
            locationTextView.text = locationText

            if (parkLocationMarker == null) {
                Log.i("LOG_TAG", "MAP : Unable to run onClick for this marker")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_PERMISSION_FINE_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission granted - you might want to trigger location update here or elsewhere
            } else {
                Toast.makeText(
                    applicationContext,
                    "App requires location permission to be granted",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    userLocationLat = location.latitude
                    userLocationLon = location.longitude
                    locationA.latitude = userLocationLat
                    locationA.longitude = userLocationLon
                }
                updateUserMarker()
            }
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSION_FINE_LOCATION)
        }

        val db = Firebase.firestore

        db.collection("User").document(Firebase.auth.currentUser?.uid ?: "")
            .get().addOnSuccessListener { user ->
                db.collection("Location")
                    .get()
                    .addOnSuccessListener { documents ->
                        for (location in documents) {
                            db.collection("Location").document(location.id).collection("Events")
                                .get()
                                .addOnSuccessListener { eventDocuments ->
                                    if (!eventDocuments.isEmpty) {
                                        val park = ParkLocationMarker().apply {
                                            createParkLocationMarker(
                                                location.id,
                                                location.getString("Location Name") ?: "Unknown",
                                                location.getDouble("Lat") ?: 0.0,
                                                location.getDouble("Lon") ?: 0.0
                                            )
                                        }
                                        locationB.latitude = location.getDouble("Lat") ?: 0.0
                                        locationB.longitude = location.getDouble("Lon") ?: 0.0

                                        val distance = locationA.distanceTo(locationB)
                                        val maxDistance = user.get("distance") as? Number

                                        if (maxDistance == null) {
                                            if (distance <= 20000) {
                                                parklocations.add(park)
                                            }
                                        } else {
                                            if (distance <= maxDistance.toInt() * 1000) {
                                                parklocations.add(park)
                                            }
                                        }
                                        parklocations.forEach {
                                            mMap.addMarker(
                                                MarkerOptions()
                                                    .position(LatLng(it.getLat(), it.getLon()))
                                                    .title(it.getName())
                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                                            )
                                            Log.i(
                                                "DisplayingMarkers",
                                                "adding marker to the map: ${it.getID()}, ${it.getLat()}, ${it.getLon()}"
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

        mMap.setInfoWindowAdapter(CustomInfoWindowAdapter())
        mMap.setOnInfoWindowClickListener(this)
    }

    private fun updateUserMarker() {
        val userLocation = LatLng(userLocationLat, userLocationLon)
        mMap.addMarker(
            MarkerOptions()
                .position(userLocation)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        )
        mMap.animateCamera(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.Builder().target(userLocation)
                    .zoom(17f)
                    .tilt(72f)
                    .build()
            )
        )
    }

    override fun onInfoWindowClick(p0: Marker) {
        val markerPosition = p0.position

        Log.i("LOG_TAG", "MAP :  $markerPosition - $userLocationLat, $userLocationLon")

        if (markerPosition != LatLng(userLocationLat, userLocationLon)) {
            var locationId = ""
            parklocations.find {
                it.getLatLng() == markerPosition
            }?.let {
                locationId = it.getID().toString()
            }

            val intent = Intent(this, SplashLoadingEventList::class.java)
            intent.putExtra("locationID", locationId)
            Log.i("LOG_TAG", "LOADING EVENTS: INTENT TO SPLASH LOADING EVENTLIST")
            startActivity(intent)
            Log.i("LOG_TAG", "LOADING EVENTS: SPLASH LOADING EVENTLIST STARTED")
        } else {
            Log.i("LOG_TAG", "MAP : USER CLICKED THEIR OWN MARKER")
        }
    }

}
