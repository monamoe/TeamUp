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
import app.helloteam.sportsbuddyapp.parse.ParseCode
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser


class event : AppCompatActivity() {

    var userId: String = "0000000"
    var eventID: String = "0000000"
    var attending: Boolean= false
    var hosting: Boolean=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)

        // get id for the event selected
        eventID = intent.getStringExtra("eventID").toString()
        Log.i(
            "LOG_TAG",
            "HAHA: recieved eventID of : " + eventID
        )


        // ui fields to data from the event database
        val attendBtn = findViewById<Button>(R.id.attendBtn)
        val eventTitle = findViewById<TextView>(R.id.eventTitle)
        val startTime = findViewById<TextView>(R.id.startTime)
        val endtime = findViewById<TextView>(R.id.endtime)
        val activity = findViewById<TextView>(R.id.activity)
        val space = findViewById<TextView>(R.id.space)
        val information = findViewById<TextView>(R.id.information)
        val hostname = findViewById<TextView>(R.id.hostname)
        val hostbio = findViewById<TextView>(R.id.hostbio)
        attendBtn.text = "Attend"

        // populate array list with events that match the location ID of the marker selected
        // this query needs to be redone
        val query = ParseQuery.getQuery<ParseObject>("Event")
        query.whereEqualTo("objectId", eventID)
        val eventquery = query.find()
        for (event in eventquery) {
            val locationQuery = ParseQuery.getQuery<ParseObject>("Location")
            locationQuery.whereEqualTo("locationPlaceId", event.getString("sportPlaceID"))
            val location=locationQuery.find()
            val queryU = ParseUser.getQuery()
            queryU.whereEqualTo("username", event.getString("host"))
            if(event.getString("host")==ParseUser.getCurrentUser().username){
                hosting=true
                attendBtn.text = "Cancel"
            }
            val host= queryU.find()
                startTime.setText("Start Time: \n" + event.getDate("date").toString())
                endtime.setText("End Time: \n" + event.getDate("endDate").toString())
                eventTitle.setText(event.getString("eventType").toString())
                hostname.setText(event.getString("host").toString())
                 space.setText(location[0].getString("Address").toString())
                hostbio.setText(host[0].getString("aboutMe").toString())
            }
        val currentUser = ParseUser.getCurrentUser()
        if (currentUser != null) {
            userId = currentUser.objectId
        }
            var objectID: String = ""
        if(!hosting) {
            val queryA = ParseQuery.getQuery<ParseObject>("AttendeeList")
            queryA.whereEqualTo("userID", userId)
            val attendees = queryA.find()
            for (a in attendees) {
                if (a.getString("eventID") == eventID) {
                    attending = true
                    objectID = a.objectId
                    attendBtn.text = "Leave"
                }
            }
        }


        //
        attendBtn.setOnClickListener {
if (!hosting) {
    if (attending) {
        ParseCode.EventLeave(objectID)
        Toast.makeText(this, "Successfully left event", Toast.LENGTH_SHORT)
            .show()
    } else {
        ParseCode.EventAttend(userId, eventID)
        Toast.makeText(this, "Successfully registered for this event", Toast.LENGTH_SHORT)
            .show()
    }
}else{
    ParseCode.CancelEvent(eventID)
    Toast.makeText(this, "Successfully cancelled event", Toast.LENGTH_SHORT)
        .show()
}
            val intent = Intent(this, LandingPageActivity::class.java)
            startActivity(intent)
        }

    }


}