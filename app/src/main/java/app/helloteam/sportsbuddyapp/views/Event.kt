/*
Author: monamoe
Created : 03-20-2021
shows information on the selected event, gives user option to attend the event
uses activity_event.xml
 */


package app.helloteam.sportsbuddyapp.views


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import app.helloteam.sportsbuddyapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class event : AppCompatActivity() {

    var userId: String = "0000000"
    var eventID: String = "0000000"
    var locationID: String = "0000000"
    var attending: Boolean = false
    var hosting: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)

        // get id for the event selected
        eventID = intent.getStringExtra("eventID").toString()
        locationID = intent.getStringExtra("locationID").toString()

        // ui fields to data from the event database
        val attendBtn = findViewById<Button>(R.id.attendBtn)
        val eventTitle = findViewById<TextView>(R.id.eventTitle)
        val startTime = findViewById<TextView>(R.id.startTime)
        val endtime = findViewById<TextView>(R.id.endtime)
        //val activity = findViewById<TextView>(R.id.activity)
        val space = findViewById<TextView>(R.id.space)
        val activity = findViewById<TextView>(R.id.activity)
        val information = findViewById<TextView>(R.id.information)
        val hostname = findViewById<TextView>(R.id.hostname)
        val hostbio = findViewById<TextView>(R.id.hostbio)
        attendBtn.text = "Attend"

        // populate array list with events that match the location ID of the marker selected
        // this query needs to be redone

        // FIREBASE MIGRATION //
        val db = Firebase.firestore
        db.collection("Location").document(locationID).collection("Events").document(eventID)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("LOG_TAG", "DocumentSnapshot data: ${document.data}")
                } else {
                    Log.d("LOG_TAG", "No such document")
                }

                // populate text fields
                eventTitle.setText(document.get("title").toString())
                activity.setText(document.get("activity").toString())
                startTime.setText("Start Time: \n" + document.get("date").toString())
                endtime.setText("End Time: \n" + document.get("endDate").toString())

                val hostID = document.get("hostID").toString()

                val currentUser = FirebaseAuth.getInstance().uid.toString()
                if (hostID.equals(currentUser)) {
                    // the current user is the one who made this event. display appropriate options
                    hosting = true;
                    attendBtn.text = "Cancel Event"
                } else {
                    hosting = false;
                    // check if the user is already attending
                    db.collection("Location").document(locationID).collection("Events")
                        .document(eventID).collection("Attendees").document(currentUser)
                        .get()
                        .addOnSuccessListener {
                            // user is already attending
                            attendBtn.text = "Attend"
                            attending = false
                        }
                        .addOnFailureListener {
                            // user isnt currently attending
                            attendBtn.text = "Leave"
                            attending = true
                        }
                }

                // get host's information to display on event page
                db.collection("User").document(hostID)
                    .get()
                    .addOnSuccessListener { userDoc ->
                        // host name and host BIO
                        hostname.setText(userDoc.get("userName").toString())
                    }
            }
            .addOnFailureListener { exception ->
                Log.d("LOG_TAG", "get failed with ", exception)
            }




        attendBtn.setOnClickListener {
            if (!hosting) {
                if (attending) {
                    // user is already attending, remove them from attendee list
                    db.collection("Location").document(locationID).collection("Events")
                        .document(eventID).collection("Attendees")
                        .document(FirebaseAuth.getInstance().uid.toString())
                        .delete()
                        .addOnSuccessListener {
                            Toast.makeText(this, "Successfully left event", Toast.LENGTH_SHORT)
                                .show()
                        }
                        .addOnFailureListener { e ->
                            Log.w(
                                "LOG_TAG",
                                "Error removing attendence",
                                e
                            )
                        }

                    // removing eventID from the users data
                    db.collection("User").document(FirebaseAuth.getInstance().uid.toString())
                        .collection("Attending").document(eventID)
                        .delete()

                } else {
                    // user is not attending, add them to attendee list
                    val attendeeHashMap = hashMapOf(
                        "userID" to FirebaseAuth.getInstance().uid.toString()
                    )

                    db.collection("Location").document(locationID).collection("Events")
                        .document(eventID).collection("Attendees").document()
                        .set(attendeeHashMap, SetOptions.merge())
                        .addOnSuccessListener {
                            Log.d("CreatingEvent", "Created Attendee")
                        }
                        .addOnFailureListener { e ->
                            Log.w("a", "Error creating Attendee document", e)
                        }
                    Toast.makeText(
                        this,
                        "Successfully registered for this event",
                        Toast.LENGTH_SHORT
                    ).show()

                    val attendingHashMap = hashMapOf(
                        "locationID" to locationID,
                        "eventID" to eventID
                    )
                    db.collection("User").document(FirebaseAuth.getInstance().uid.toString())
                        .collection("Attending").document()
                        .set(attendingHashMap, SetOptions.merge())

                }
            } else {
                db.collection("Location").document(locationID).collection("Events")
                    .document(eventID)
                    .delete()
                    .addOnSuccessListener {
                        Log.d(
                            "LOG_TAG",
                            "Event successfully deleted!"
                        )
                    }
                    .addOnFailureListener { e -> Log.w("LOG_TAG", "Error deleting Event", e) }
                Toast.makeText(this, "Successfully cancelled event", Toast.LENGTH_SHORT)
                    .show()
            }
            val intent = Intent(this, LandingPageActivity::class.java)
            startActivity(intent)
        }
    }
}