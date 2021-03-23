
/*
Author: monamoe
Created : 03-20-2021
shows information on the selected event, gives user option to attend the event
uses activity_event.xml
 */


package app.helloteam.sportsbuddyapp.views


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import app.helloteam.sportsbuddyapp.R
import app.helloteam.sportsbuddyapp.parse.ParseCode






class event : AppCompatActivity() {

    var userId: Int = 1
    var eventId: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)

        // get id for the event selected
        val bundle: Bundle? = intent.extras

        bundle?.let {
            bundle.apply {
                //Intent with data
                eventId = getInt("ID")
            }
        }

        Toast.makeText(this, eventId.toString(), Toast.LENGTH_SHORT).show()


        // update ui fields to data from the event database
        val attendBtn = findViewById<Button>(R.id.attendBtn)
        val eventTitle = findViewById<TextView>(R.id.eventTitle)
        val startTime = findViewById<TextView>(R.id.startTime)
        val endtime = findViewById<TextView>(R.id.endtime)
        val activity = findViewById<TextView>(R.id.activity)
        val space = findViewById<TextView>(R.id.space)
        val information = findViewById<TextView>(R.id.information)
        val hostname = findViewById<TextView>(R.id.hostname)
        val hostbio = findViewById<TextView>(R.id.hostbio)

        //    EventAttend
        attendBtn.setOnClickListener {

            // get user id ? ?
            ParseCode.EventAttend(userId, eventId)
        }

    }


}