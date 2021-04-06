/*
Author: monamoe
Created:  March 21 2020
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
import androidx.core.content.res.ColorStateListInflaterCompat.inflate
import app.helloteam.sportsbuddyapp.R
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser


lateinit private var eventList: ArrayList<eventslist.EventDisplayer>

class eventslist : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eventslist)

        val listview = findViewById<ListView>(R.id.listview)

        Log.i(
            "LOG_TAG",
            "-----------------------------------------------------------\n-----------------------------------------------------------\n"
        )

        // Get location ID
        val locationID: String = intent.getStringExtra("locationID").toString()

        // events array list
        eventList = ArrayList()

        // populate array list with events that match the location ID of the marker selected
        val query = ParseQuery.getQuery<ParseObject>("Event")
        val eventquery = query.find()
        for (event in eventquery) {
            val sportId = event.getString("sportPlaceID").toString()
            if (sportId.equals(locationID)) {
                val queryL = ParseQuery.getQuery<ParseObject>("Location")
                queryL.whereEqualTo("locationPlaceId", event.getString("sportPlaceID"))
                queryL.setLimit(1)
                val lQuery = queryL.find()
                    Log.i("LOG_TAG", "HAHA: ${event.getString("host")} ${ParseUser.getCurrentUser().username}")
                    var e1 = EventDisplayer(
                        event.objectId,
                        event.getString("eventType")!!,
                        lQuery.get(0).getString("Address")!!,
                        event.getDate("date").toString(),
                        "Hosted by: " + event.getString("host")!!
                    )
                    eventList.add(e1);

            }
        }

        Log.i("LOG_TAG", "HAHA: Found a total of matching events: " + eventList.size.toString())

        // list view adapter
        listview.adapter = EventListAdapter(this)

        listview.setOnItemClickListener { parent, view, position, id ->
            Log.i("LOG_TAG", "HAHA: position" + position)
            Log.i(
                "LOG_TAG",
                "HAHA: printing the position info " + eventList.get(position).toString()
            )
            val eventID = eventList.get(position).getID()
            val intent = Intent(this, event::class.java)
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

            Log.i(
                "LOG_TAG",
                "HAHA: Displaying data for position from event" + eventList.size.toString() + " " + position +" and "+ eventList.get(0).name
            )
            eventTitle.setText(eventList.get(position).name)
            eventTitle.text=(eventList.get(position).name)
            eventAddress.text=(eventList.get(position).address)
            eventTime.text=(eventList.get(position).time)
            eventHost.text=(eventList.get(position).host)

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

        // main constuctor
        constructor(id: String, name: String, address: String, time: String, host: String) {
            this.id = id
            this.name = name
            this.address=address
            this.time = time
            this.host = host

            //address
            val query = ParseQuery.getQuery<ParseObject>("Event")
            val eventlist = query.find()
            for (event in eventlist) {


                event.getString("eventType")!!
                event.getDouble("longitude")
            }
        }
    }
}


