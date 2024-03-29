/*
Author: monamoe
Created:  March 21 2021
Manages List of events
 */

package app.helloteam.sportsbuddyapp.views

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import app.helloteam.sportsbuddyapp.R
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


private lateinit var eventList: ArrayList<eventslist.EventDisplayer>


class eventslist : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eventslist)

        val listview = findViewById<ListView>(R.id.listview)

        // Get location ID
        val locationID: String = intent.getStringExtra("locationID").toString()

        // events array list
        eventList = ArrayList()

        // populate array list with events that match the location ID of the marker selected

        // FIREBASE MIGRATION //
        val db = Firebase.firestore
        val location = db.collection("Location").document(locationID)
        location.get().addOnSuccessListener { x ->
            val address = x.get("Location Name").toString()
        //this should include .whereGreaterThan("numberOfEvents", 0) once we add that to the database
        db.collection("User").get()
            .addOnSuccessListener { users ->
                location.collection("Events")
                    .get()
                    .addOnSuccessListener { documents ->
                        for (event in documents) {
                            var userName = ""
                            for (user in users){
                                if (user.id == event.get("hostID"))
                                    userName = user.get("userName").toString()
                            }
                            val sfd = SimpleDateFormat("yyyy-MM-dd hh:mm")
                            var time: Timestamp = event.get("date") as Timestamp
                            var eventTime = sfd.format(Date(time.seconds * 1000))
                            val eventObj = EventDisplayer(
                                event.id,
                                event.get("activity").toString(),
                                address,
                                eventTime,
                                if (userName == "null") "No Host" else userName
                            )
                            eventList.add(eventObj)

                        }
                        // list view adapter
                        listview.adapter = EventListAdapter(this)
                    }
            }
    }


        listview.setOnItemClickListener { parent, view, position, id ->
            val eventID = eventList.get(position).getID()
            val intent = Intent(this, ViewEvent::class.java)
            intent.putExtra("locationID", locationID)
            intent.putExtra("eventID", eventID)
            startActivity(intent)
        }
    }


    // Event Array List Adapter
    internal class EventListAdapter(context: Context) : BaseAdapter() {

        private val mContext: Context = context

        // overrides
        override fun getCount(): Int {
            return eventList.size
        }

        override fun getItem(position: Int): Any {
            return "return override"
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        // render each row
        override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {
            val lI = LayoutInflater.from(mContext)
            val rowMain = lI.inflate(R.layout.event_list_adapter_view, viewGroup, false)

            val eventTitle = rowMain.findViewById<TextView>(R.id.eventTitle)
            val eventAddress = rowMain.findViewById<TextView>(R.id.eventAddress)
            val eventTime = rowMain.findViewById<TextView>(R.id.memberSport)
            val eventHost = rowMain.findViewById<TextView>(R.id.eventHost)

            eventTitle.setText(eventList.get(position).name)
            eventTitle.text = (eventList.get(position).name)
            eventAddress.text = (eventList.get(position).address)
            eventTime.text = (eventList.get(position).time)
            eventHost.text = (eventList.get(position).host)

            return rowMain
        }
    }

    // Event Displayer class ( for array list)
    class EventDisplayer {
        var id: String = ""
        var name: String = ""
        var address: String = ""
        var time: String = ""
        var host: String = ""

        fun getID(): String {
            return this.id
        }

        // main constuctor
        constructor(id: String, name: String, address: String, time: String, host: String) {
            this.id = id
            this.name = name
            this.address = address
            this.time = time
            this.host = host
        }
    }
}

