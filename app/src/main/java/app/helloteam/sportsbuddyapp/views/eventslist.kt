/*
Author: monamoe
Created:  March 21 2020
Manages List of events
 */

package app.helloteam.sportsbuddyapp.views

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.core.content.res.ColorStateListInflaterCompat.inflate
import app.helloteam.sportsbuddyapp.R
import com.parse.ParseObject
import com.parse.ParseQuery


lateinit private var eventList: ArrayList<eventslist.EventDisplayer>

class eventslist : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eventslist)

        val listview = findViewById<ListView>(R.id.listview)

        // Get location ID
        val locationID: String = intent.getStringExtra("locationID").toString()

        // events array list
        eventList = ArrayList<EventDisplayer>()

        // populate array list with events that match the location ID of the marker selected
        val query = ParseQuery.getQuery<ParseObject>("Event")
        query.whereEqualTo("sportPlaceID", locationID);
        val eventlist = query.find()
        for (event in eventlist) {
            var e1 = EventDisplayer(
                event.getString("objectId").toString(),
                "Event Name"
            )
            eventList.add(e1);
        }

        var adapter: EventListAdapter = EventListAdapter(this)
    }


    // Event Array List Adapter
    internal class EventListAdapter(context: Context) : BaseAdapter() {

        private val mContext: Context = context

        // overrides
        override fun getCount(): Int {
            return 400;
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
            val rowMain = lI.inflate(R.layout.activity_eventslist, viewGroup, false);

            val eventTitle = rowMain.findViewById<TextView>(R.id.eventTitle)
            val eventAddress = rowMain.findViewById<TextView>(R.id.eventAddress)
            val eventTime = rowMain.findViewById<TextView>(R.id.eventTime)
            val eventHost = rowMain.findViewById<TextView>(R.id.eventHost)

            eventTitle.text = eventList.get(position).name
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

        //temp construtor
        constructor(id: String, name: String) {
            this.id = id
            this.name = name
        }


        // main constuctor
        constructor(id: String, name: String, time: String, host: String) {
            this.id = id
            this.name = name
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