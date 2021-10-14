/*
Author: monamoe
Created : 03-20-2021
shows information on the selected event, gives user option to attend the event
uses activity_event.xml
 */


package app.helloteam.sportsbuddyapp.views


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import app.helloteam.sportsbuddyapp.R
import app.helloteam.sportsbuddyapp.firebase.EventHandling
import app.helloteam.sportsbuddyapp.firebase.TeamHandling
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import net.cachapa.expandablelayout.ExpandableLayout
import java.text.SimpleDateFormat
import java.util.*


class ViewEvent : AppCompatActivity() {

    var userId: String = "0000000"
    var eventID: String = "0000000"
    var locationID: String = "0000000"
    var attending: Boolean = false
    var hosting: Boolean = false

    lateinit var db: FirebaseFirestore
    lateinit var uid: String

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)

        val listview = findViewById<ListView>(R.id.listView)
        listview.isNestedScrollingEnabled = true;

        val listViewAttendees = findViewById<ListView>(R.id.listViewAttendees)
        listViewAttendees.isNestedScrollingEnabled = true;

        // get id for the event selected
        eventID = intent.getStringExtra("eventID").toString()
        locationID = intent.getStringExtra("locationID").toString()
        Log.i("LOG_TAG", "VIEW EVENT: eventID $eventID")
        Log.i("LOG_TAG", "VIEW EVENT: locationID $locationID")


        TeamHandling.getTeam(listview, this, "Event", eventID, locationID)
        EventHandling.getAttendees(listViewAttendees, this, locationID, eventID)
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
        findViewById<Button>(R.id.becomeHostButton).visibility = View.GONE

        // populate array list with events that match the location ID of the marker selected
        // this query needs to be redone

        // FIREBASE MIGRATION //
        db = Firebase.firestore
        uid = FirebaseAuth.getInstance().uid.toString()


        db.collection("Location").document(locationID).collection("Events").document(eventID)
            .get()
            .addOnSuccessListener { document ->
                Log.i("LOG_TAG", "VIEW EVENT: $document")
                if (!document.exists()) {
                    val intent = Intent(this, SplashActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "eventID", Toast.LENGTH_SHORT).show()
                } else {
                    Log.d("LOG_TAG", "DocumentSnapshot data: ${eventID}")

                    val sfd = SimpleDateFormat("yyyy-MM-dd hh:mm")
                    val startTimeStamp: Timestamp = document.get("date") as Timestamp
                    val eventStartTime = sfd.format(Date(startTimeStamp.seconds * 1000))
                    val endTimeStamp: Timestamp = document.get("endDate") as Timestamp
                    val eventEndTime = sfd.format(Date(endTimeStamp.seconds * 1000))
                    // populate text fields
                    eventTitle.setText(document.get("title").toString())
                    activity.setText(document.get("activity").toString())
                    startTime.setText("Start Time: \n" + eventStartTime.toString())
                    endtime.setText("End Time: \n" + eventEndTime.toString())
                    EventHandling.getSpacesLeft(
                        locationID,
                        eventID,
                        document.get("eventSpace").toString().toInt(),
                        space
                    )

                    db.collection("Location").document(locationID).get()
                        .addOnSuccessListener { loc ->
                            information.setText(loc.get("Location Name").toString())
                            Glide.with(this).load(loc.get("StreetView").toString())
                                .into(findViewById(R.id.eventImage));
                        }

                    val hostID = document.get("hostID").toString()


                    if (hostID.equals(uid)) {
                        // the current user is the one who made this event. display appropriate options
                        hosting = true;
                        attendBtn.text = "Leave Event"
                    } else {
                        hosting = false;

                        // check if the user is already attending
                        db.collection("Location").document(locationID).collection("Events")
                            .document(eventID).collection("Attendees")
                            .get()
                            .addOnSuccessListener { users ->
                                attendBtn.text = "Attend"
                                attending = false
                                EventHandling.getSpacesLeft(
                                    locationID,
                                    eventID,
                                    document.get("eventSpace").toString().toInt(),
                                    attendBtn
                                )
                                for (user in users) {
                                    if (user.get("userID").toString() == uid) {
                                        attending = true
                                        findViewById<Button>(R.id.becomeHostButton).visibility = View.VISIBLE
                                    }
                                }
                            }
                    }

                    // get host's information to display on event page
                    if (hostID != "null") {
                        db.collection("User").document(hostID)
                            .get()
                            .addOnSuccessListener { userDoc ->
                                // host name and host BIO
                                hostname.setText(userDoc.get("userName").toString())
                                var bio = userDoc.get("bio")
                                if (bio != "null" && bio != null && bio != "") hostbio.setText(bio.toString())
                            }
                    } else {
                        hostname.setText("Host: ")
                        hostbio.setText("This event has no host")
                        findViewById<LinearLayout>(R.id.becomeHostLayout).visibility = View.VISIBLE
                    }

                }
            }
            .addOnFailureListener { exception ->
                Log.d("LOG_TAG", "get failed with ", exception)
            }




        attendBtn.setOnClickListener {
            if (!hosting) {
                if (attending) {
                    removeAttendance()
                    Toast.makeText(this, "Successfully left event", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    addAttendance()
                }
            } else {
                MaterialDialog(this).show {
                    title(text = "Are you sure you want to leave this event as host?")
                    positiveButton(R.string.yes) { dialog ->
                        hostLeaveEvent()
                    }
                    negativeButton(R.string.cancel)
                }
            }
        }
        findViewById<LinearLayout>(R.id.inviteLayout).setOnClickListener {
            findViewById<ExpandableLayout>(R.id.expandable_layout).toggle()
            if (findViewById<ExpandableLayout>(R.id.expandable_layout).isExpanded) {
                findViewById<TextView>(R.id.inviteText).setText("Invite Team Members ▼")
            } else {
                findViewById<TextView>(R.id.inviteText).setText("Invite Team Members ►")
            }
        }
        findViewById<LinearLayout>(R.id.attendeeLayout).setOnClickListener {
            findViewById<ExpandableLayout>(R.id.expandable_layout_attendees).toggle()
            if (findViewById<ExpandableLayout>(R.id.expandable_layout_attendees).isExpanded) {
                findViewById<TextView>(R.id.attendeeText).setText("View Attendees ▼")
            } else {
                findViewById<TextView>(R.id.attendeeText).setText("View Attendees ►")
            }
        }

        findViewById<Button>(R.id.becomeHostButton).setOnClickListener {
            MaterialDialog(this).show {
                title(text = "Are you sure you want to become the host? You will be responsible for leading the event.")
                positiveButton(R.string.yes) { dialog ->
                    makeHost(FirebaseAuth.getInstance().currentUser?.uid.toString())
                    removeAttendance()
                    Toast.makeText(context, "Successfully became host", Toast.LENGTH_SHORT)
                        .show()
                }
                negativeButton(R.string.cancel)
            }
        }

    }


    private fun hostLeaveEvent() {
        db.collection("Location").document(locationID).collection("Events").document(eventID)
            .update("hostID", "null")
        db.collection("User").document(FirebaseAuth.getInstance().currentUser?.uid.toString()).collection("Hosting")
            .whereEqualTo("eventID", eventID).get()
            .addOnSuccessListener { hosting ->
                for (host in hosting){
                    db.collection("User").document(FirebaseAuth.getInstance().currentUser?.uid.toString()).collection("Hosting")
                        .document(host.id).delete()
                }
                val intent = Intent(context, SplashActivity::class.java)
                startActivity(intent)
            }


    }

    private fun makeHost(newHostID: String) {
        db.collection("Location").document(locationID).collection("Events").document(eventID)
            .update("hostID", newHostID)

        val hostingHashMap = hashMapOf(
            "locationID" to locationID,
            "eventID" to eventID
        )

        // add the hosting data to the user
        db.collection("User")
            .document(FirebaseAuth.getInstance().uid.toString())
            .collection("Hosting").document(eventID)
            .set(hostingHashMap, SetOptions.merge())
            .addOnSuccessListener {
                val intent = Intent(this, SplashActivity::class.java)
                startActivity(intent)
            }
    }

    // check if the event has room
    // increace the number of people attending in event
    // add the user uid to the event's attending list
    // add the eventID to the user's attending list
    private fun addAttendance() {

        db.collection("Location").document(locationID).collection("Events").document(eventID)
            .get()
            .addOnSuccessListener { doc ->
                var numCurrentlyAttending = doc.get("currentlyAttending").toString().toInt()

                Log.i("LOG_TAG", "$numCurrentlyAttending numCurrentlyAttending!!")
                // check if the event has room
                if (doc.get("eventSpace").toString().toInt() > numCurrentlyAttending) {
                    // increace the number of people attending in event

                    numCurrentlyAttending++
                    val data =
                        hashMapOf("currentlyAttending" to (numCurrentlyAttending).toString())
                    db.collection("Location").document(locationID).collection("Events")
                        .document(eventID)
                        .set(data, SetOptions.merge())

                    // add the user uid to the event's attending list
                    val attendeeHashMap = hashMapOf(
                        "userID" to FirebaseAuth.getInstance().uid.toString()
                    )

                    db.collection("Location").document(locationID).collection("Events")
                        .document(eventID).collection("Attendees").document(uid)
                        .set(attendeeHashMap, SetOptions.merge())
                        .addOnSuccessListener {
                            Log.d("CreatingEvent", "Created Attendee")

                            // add the eventID to the user's attending list
                            val attendingHashMap = hashMapOf(
                                "locationID" to locationID,
                                "eventID" to eventID
                            )
                            db.collection("User")
                                .document(FirebaseAuth.getInstance().uid.toString())
                                .collection("Attending").document(eventID)
                                .set(attendingHashMap, SetOptions.merge()).addOnSuccessListener {
                                    val intent = Intent(this, SplashActivity::class.java)
                                    startActivity(intent)
                                    Toast.makeText(
                                        this,
                                        "Successfully registered for this event",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }


                        }
                        .addOnFailureListener { e ->
                            Log.w("a", "Error creating Attendee document", e)
                        }




                } else {
                    Toast.makeText(
                        this,
                        "This event is currently full!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }


    // -1 the event space
    // delete uid in the events attending list
    // delete event id in the users attending list
    private fun removeAttendance() {

        // -1 the event space
        db.collection("Location").document(locationID).collection("Events").document(eventID)
            .get()
            .addOnSuccessListener { doc ->
                var numCurrentlyAttending = doc.get("currentlyAttending").toString().toInt()
                numCurrentlyAttending--
                val data =
                    hashMapOf("currentlyAttending" to (numCurrentlyAttending).toString())
                db.collection("Location").document(locationID).collection("Events")
                    .document(eventID)
                    .set(data, SetOptions.merge())
            }

        // delete uid in the events attending list
        db.collection("Location").document(locationID).collection("Events")
            .document(eventID).collection("Attendees")
            .document(FirebaseAuth.getInstance().uid.toString())
            .delete()
            .addOnSuccessListener {

            }
            .addOnFailureListener { e ->
                Log.w(
                    "LOG_TAG",
                    "Error removing attendence",
                    e
                )
            }

        // removing eventID from the users data
        db.collection("User").document(uid)
            .collection("Attending").document(eventID)
            .delete()
            .addOnFailureListener { e ->
                Log.w(
                    "LOG_TAG",
                    "Error removing attendence",
                    e
                )
            }
    }
}