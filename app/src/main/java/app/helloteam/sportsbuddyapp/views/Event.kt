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

        // populate array list with events that match the location ID of the marker selected
        // this query needs to be redone
        val query = ParseQuery.getQuery<ParseObject>("Event")
        val eventquery = query.find()
        for (event in eventquery) {
            if (event.getString("objectId").toString().equals(eventID)) {
                startTime.setText(event.getString("").toString())
                eventTitle.setText("THIS EVENT DOESNT HAVE A TITLE")
                hostname.setText(event.getString("host").toString())
                hostbio.setText("this even is hosted by someone prolly nammed riley who hates cows")
            }
        }


        //
        attendBtn.setOnClickListener {
            val currentUser = ParseUser.getCurrentUser()
            if (currentUser != null) {
                userId = currentUser.objectId
            }

            ParseCode.EventAttend(userId, eventID)
            Toast.makeText(this, "Successfully registered for this event", Toast.LENGTH_SHORT)
                .show()
            val intent = Intent(this, map::class.java)
            startActivity(intent)
        }

    }


}