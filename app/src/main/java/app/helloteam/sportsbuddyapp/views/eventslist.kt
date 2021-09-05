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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import app.helloteam.sportsbuddyapp.R
import com.google.firebase.firestore.ktx.firestore
import app.helloteam.sportsbuddyapp.models.EventDisplayer
import com.google.firebase.ktx.Firebase


lateinit private var eventList: ArrayList<eventslist.EventDisplayer>


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

        //this should include .whereGreaterThan("numberOfEvents", 0) once we add that to the database
        db.collection("Location").document(locationID).collection("Events")
            .get()
            .addOnSuccessListener { documents ->
                //loop through the events at that location
                for (event in documents) {
                    val eventObj = EventDisplayer(
                        event.id,
                        event.get("type").toString(),
                        event.get("eventPlaceId").toString(),
                        event.get("date").toString(),
                        "Host not setup at the moment"
                    )

                    eventObj.printString()


                    //get the username of the host for the event
//                    val hostID = event.get("userName").toString()

                    // Look through Users document and find the hosts information
//                    db.collection("Users")
//                        .document(hostID)
//                        .get()
//                        .addOnSuccessListener { document ->
//
//                            if (document != null) {
//                                Log.d("TAG", "DocumentSnapshot data: ${document.data}")
//                            } else {
//                                Log.d("TAG", "No such document")
//                            }
//                        }


                    eventList.add(eventObj)
                }
                // list view adapter
                listview.adapter = EventListAdapter(this)
            }



        listview.setOnItemClickListener { parent, view, position, id ->
            val eventID = eventList.get(position).getID()
            val intent = Intent(this, event::class.java)
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
            return eventList.size;
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
            val rowMain = lI.inflate(R.layout.event_list_adapter_view, viewGroup, false);

            val eventTitle = rowMain.findViewById<TextView>(R.id.eventTitle)
            val eventAddress = rowMain.findViewById<TextView>(R.id.eventAddress)
            val eventTime = rowMain.findViewById<TextView>(R.id.eventTime)
            val eventHost = rowMain.findViewById<TextView>(R.id.eventHost)

            eventTitle.setText(eventList.get(position).name)
            eventTitle.text = (eventList.get(position).name)
            eventAddress.text = (eventList.get(position).address)
            eventTime.text = (eventList.get(position).time)
            eventHost.text = (eventList.get(position).host)

            return rowMain;
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

        fun printString() {
            var output: String = id + "," + name + ", " + address + ", " + time + ", " + host;
            Log.i("LOG_TAG", "Event Displayer:" + output)
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




