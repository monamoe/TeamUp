/*
Author: monamoe
Created : 03-20-2021
shows inforamtion on the selected event, gives user option to attend the event
uses activity_event.xml
 */


package app.helloteam.sportsbuddyapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import app.helloteam.sportsbuddyapp.models.ParseCode

class event : AppCompatActivity() {

    val userId : Int = 1
    val eventId : Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)

        // get id for the event selected

        // update ui fields to data from the event database

        val attendBtn = findViewById<Button>(R.id.attendBtn)//gets logout button id

        //    EventAttend
        attendBtn.setOnClickListener {

            // get user id ? ? 
            ParseCode.EventAttend(userId, eventId)
        }

    }


}