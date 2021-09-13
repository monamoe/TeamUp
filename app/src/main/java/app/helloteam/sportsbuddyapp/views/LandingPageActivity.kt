package app.helloteam.sportsbuddyapp.views

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import app.helloteam.sportsbuddyapp.*
import app.helloteam.sportsbuddyapp.models.weatherTask
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import java.util.*
import android.location.Geocoder




const val weatherAPI = "f00bd5c2f24390ab1393b5a7c5459b01"
var forecast: TextView? = null
var temp: TextView? = null

private val MY_PERMISSION_FINE_LOCATION: Int = 44

class LandingPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing_page)
        val date = Date()
        Toast.makeText(this, "$date", Toast.LENGTH_SHORT).show()


        // is the user logged in
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            // clear activity stack, go to login page
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }



        findViewById<TextView>(R.id.ShowUsername).text = "SOME USER"

        //create event button
        findViewById<Button>(R.id.CreateEventBtn).setOnClickListener {
            val intent = Intent(this, CreateEventActivity::class.java)
            startActivity(intent)
        }

        temp = findViewById(R.id.temp)
        forecast = findViewById(R.id.forecast)
        getUserCity()

    }

    fun afterLogout() {//method to go back to login screen after logout
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_profile -> {
            val intent = Intent(this, ProfilePage::class.java)
            startActivity(intent)
            true
        }
        R.id.action_events -> {
            val intent = Intent(this, ViewPlayerEvents::class.java)
            startActivity(intent)
            true
        }
        R.id.action_hosted -> {
            val intent = Intent(this, HostEvents::class.java)
            startActivity(intent)
            true
        }
        R.id.action_logout -> {
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setMessage("Do you want to log out?")
                .setCancelable(false)
                .setPositiveButton("Logout", DialogInterface.OnClickListener { dialog, id ->
                    FirebaseAuth.getInstance().signOut()
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
        R.id.action_map -> {
            val intent = Intent(this, map::class.java)
            startActivity(intent)
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSION_FINE_LOCATION ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getUserCity()
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

    fun getUserCity(){
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        var userLocationLat = 0.0
        var userLocationLon = 0.0
        var cityName = ""
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

                    val geocoder = Geocoder(this, Locale.getDefault())
                    val addresses: List<Address> = geocoder.getFromLocation(userLocationLat, userLocationLon, 1)
                    cityName = addresses[0].getLocality()
                }
                //render the marker on the users location.
                weatherTask().execute(cityName) //gets weather for current location
            }
        }
        //request permission
        else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSION_FINE_LOCATION
            )
        }
    }
}