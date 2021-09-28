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
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

lateinit private var eventList: ArrayList<HostEvents.EventDisplayer>

class HostEvents : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host_events)

        val listview = findViewById<ListView>(R.id.listview)


        // events array list
        eventList = ArrayList()

        // FIREBASE MIGRATION //
        val db = Firebase.firestore

        db.collection("User").document(FirebaseAuth.getInstance().uid.toString())
            .collection("Hosting")
            .get()
            .addOnSuccessListener { hostingList ->
                for (hostingList in hostingList) {
                    db.collection("Location").document(hostingList.get("locationID").toString())
                        .collection("Events")
                        .document(hostingList.get("eventID").toString())
                        .get()
                        .addOnSuccessListener { event ->
                            var address = ""
                            var hostname = ""
                            // getting the address from location doc
                            db.collection("Location")
                                .document(hostingList.get("locationID").toString())
                                .get()
                                .addOnSuccessListener { document ->
                                    if (document != null) {
                                        address = document.get("Location Name").toString()
                                        db.collection("User")
                                            .document(event.get("hostID").toString())
                                            .get()
                                            .addOnSuccessListener { hostuser ->
                                                var eventTime = event.get("date")
                                                if (event.get("date") != null) {
                                                    val sfd = SimpleDateFormat("yyyy-MM-dd hh:mm")
                                                    var time: Timestamp =
                                                        event.get("date") as Timestamp
                                                    eventTime =
                                                        sfd.format(Date(time.seconds * 1000))
                                                }
                                                hostname = hostuser.get("userName").toString()
                                                val ED = EventDisplayer(
                                                    event.id,
                                                    hostingList.get("locationID").toString(),
                                                    event.get("activity").toString(),
                                                    address,
                                                    eventTime.toString(),
                                                    hostname
                                                )

                                                eventList.add(ED);

                                                // list view adapter
                                                listview.adapter =
                                                    EventListAdapter(this)
                                            }
                                    }
                                }
                        }
                }
            }

        // list view adapter
        listview.adapter = EventListAdapter(this)

        listview.setOnItemClickListener { parent, view, position, id ->
            val eventID = eventList.get(position).getEventID()
            val locationID = eventList.get(position).getLocationID()
            val intent = Intent(this, event::class.java)
            intent.putExtra("eventID", eventID)
            intent.putExtra("locationID", locationID)
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
            val eventTime = rowMain.findViewById<TextView>(R.id.memberSport)
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
        var eventIDED: String = ""
        var locationIDED: String = ""
        var name: String = ""
        var address: String = ""
        var time: String = ""
        var host: String = ""


        fun getEventID(): String {
            return this.eventIDED
        }

        fun getLocationID(): String {
            return this.locationIDED
        }

        // main constuctor
        constructor(
            eventID: String,
            locationID: String,
            name: String,
            address: String,
            time: String,
            host: String
        ) {
            this.eventIDED = eventID
            this.locationIDED = locationID
            this.name = name
            this.address = address
            this.time = time
            this.host = host
        }
    }
}


