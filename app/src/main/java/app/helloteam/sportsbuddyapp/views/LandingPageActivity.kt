package app.helloteam.sportsbuddyapp.views

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
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
import android.util.Log
import android.widget.ImageView
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

lateinit private var inviteList: ArrayList<TeamInvites.InviteDisplayer>

const val weatherAPI = "f00bd5c2f24390ab1393b5a7c5459b01"
var forecast: TextView? = null
var temp: TextView? = null
var icon: ImageView? = null

private val MY_PERMISSION_FINE_LOCATION: Int = 44

class LandingPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing_page)

        // is the user logged in
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            // clear activity stack, go to login page
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        val db = Firebase.firestore
        val location = db.collection("User").document(Firebase.auth.currentUser?.uid.toString())
        location.get().addOnSuccessListener { user ->
            findViewById<TextView>(R.id.ShowUsername).text =
                "Welcome " + user.get("userName")
        }

        //create event button
        findViewById<Button>(R.id.CreateEventBtn).setOnClickListener {
            val intent = Intent(this, CreateEventActivity::class.java)
            startActivity(intent)
        }

        temp = findViewById(R.id.temp)
        forecast = findViewById(R.id.forecast)
        icon = findViewById(R.id.conIcon)

        getUserCity()

        //temp notif call

        inviteList = ArrayList()

        db.collection("User").document(FirebaseAuth.getInstance().currentUser?.uid.toString())
            .collection("Invites").whereEqualTo("inviteType", "Team")
            .get().addOnSuccessListener { invites ->
                for (invite in invites){
                    db.collection("User").document(invite.get("sender").toString())
                        .get().addOnSuccessListener { user ->
                            val eventObj = TeamInvites.InviteDisplayer(
                                user.id,
                                invite.id,
                                user.get("userName").toString(),
                                user.get("photoUrl").toString()
                            )
                            inviteList.add(eventObj)

                            Log.i("Test", eventObj.toString())

                            val notificationManager =
                                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                            val channelId = "10"
                            val channelName: CharSequence = "Channel1"
                            val importance = NotificationManager.IMPORTANCE_HIGH
                            val notificationChannel = NotificationChannel(channelId, channelName, importance)
                            notificationChannel.enableLights(true)
                            notificationChannel.lightColor = Color.RED
                            notificationChannel.enableVibration(true)
                            notificationChannel.vibrationPattern =
                                longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
                            notificationManager.createNotificationChannel(notificationChannel)

                            var builder = NotificationCompat.Builder(this, "10")
                                .setSmallIcon(R.drawable.notifbell)
                                .setContentTitle("You have a team invite!")
                                .setContentText("Check your invitations to accept them!")
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                            if(!inviteList.isEmpty()){
                                with(NotificationManagerCompat.from(this)) {
                                    // notificationId is a unique int for each notification that you must define
                                    notify(10, builder.build())
                                }
                            }


                        }
                }
            }




    }

    fun afterLogout() {//method to go back to login screen after logout
        finish()
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
        R.id.action_team -> {
            val intent = Intent(this, TeamsActivity::class.java)
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

    fun getUserCity() {
        var fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
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
                    val addresses: List<Address> =
                        geocoder.getFromLocation(userLocationLat, userLocationLon, 1)
                    cityName = addresses[0].getLocality()
                }
                //render the marker on the users location.
                weatherTask(icon).execute(cityName, getString(R.string.weather_api)) //gets weather for current location
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