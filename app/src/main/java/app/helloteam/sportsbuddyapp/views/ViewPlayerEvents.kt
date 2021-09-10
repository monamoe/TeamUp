package app.helloteam.sportsbuddyapp.views

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import app.helloteam.sportsbuddyapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

lateinit private var eventList: ArrayList<ViewPlayerEvents.EventDisplayer>


class ViewPlayerEvents : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_player_events)

        val listview = findViewById<ListView>(R.id.listview)


        // events array list, represents the events the user is enrolled in
        eventList = ArrayList()

        // FIREBASE MIGRATION //
        val db = Firebase.firestore

        // list of events the user is attending
        db.collection("User").document(FirebaseAuth.getInstance().uid.toString())
            .collection("Attending")
            .get()
            .addOnSuccessListener { attendingList ->
                Log.i("LOG_TAG", "Attending size " + attendingList.size().toString())
                for (attendingList in attendingList) {
                    // get location and event data for the event the user is enrolled in
                    db.collection("Location").document(attendingList.get("locationID").toString())
                        .collection("Event")
                        .document(attendingList.get("eventID").toString())
                        .get()
                        .addOnSuccessListener { event ->
                            if (event == null) {
                                Log.i(
                                    "LOG_TAG",
                                    "Fatal Error: trying to find an event ID that doesn't exit!"
                                )
                            } else {
                                Log.i("LOG_TAG", "Found Matching Event")
                            }
                            // getting the address from location doc
                            var address = ""
                            db.collection("Location")
                                .document(attendingList.get("locationID").toString())
                                .get()
                                .addOnSuccessListener { document ->
                                    if (document != null) {
                                        address = document.get("Address").toString()
                                    }
                                }
                            Log.i("LOG_TAG", "Found address of : " + address)
                            var hostname = ""
                            db.collection("User").document(event.get("hostID").toString())
                                .get()
                                .addOnSuccessListener { hostuser ->
                                    hostname = hostuser.get("username").toString()
                                }
                            Log.i("LOG_TAG", "Found hostname of : " + address)

                            val ED = EventDisplayer(
                                event.get("eventID").toString(),
                                event.get("activity").toString(),
                                address,
                                event.get("date").toString(),
                                hostname
                            )
                            eventList.add(ED);
                        }
                }
            }


        Log.i("LOG_TAG", "HAHA: Found a total of matching events: " + eventList.size.toString())

        // list view adapter
        listview.adapter = EventListAdapter(this)

        listview.setOnItemClickListener { parent, view, position, id ->
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
                "HAHA: Displaying data for position from event " + eventList.size.toString() + " " + position + " " +
                        eventList[position].name
            )
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


