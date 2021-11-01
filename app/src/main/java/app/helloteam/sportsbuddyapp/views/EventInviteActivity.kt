package app.helloteam.sportsbuddyapp.views

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import app.helloteam.sportsbuddyapp.R
import app.helloteam.sportsbuddyapp.firebase.EventHandling.db
import com.baoyz.widget.PullRefreshLayout
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*


private lateinit var eventList: ArrayList<EventInviteActivity.EventInviteDisplayer>

class EventInviteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_invite)
        val listview = findViewById<ListView>(R.id.inviteList)
        // events array list
        eventList = ArrayList()
        supportActionBar?.title = "Event Invites"

        // populate array list with events that match the location ID of the marker selected
        getInvites(listview)

        listview.setOnItemClickListener { _, _, position, _ ->
            val intent = Intent(this, SplashLoadingEventView::class.java)
            intent.putExtra("locationID", eventList.get(position).locationID)
            intent.putExtra("eventID", eventList.get(position).eventID)
            startActivity(intent)
        }

        val layout = findViewById<PullRefreshLayout>(R.id.swipeRefreshLayout)
        layout.setOnRefreshListener {
            getInvites(listview)
            layout.setRefreshing(false)
        }
    }

    fun getInvites(listview: ListView) {
        // FIREBASE MIGRATION //
        eventList = ArrayList()
        val db = Firebase.firestore
        db.collection("User").document(FirebaseAuth.getInstance().currentUser?.uid.toString())
            .collection("Invites").whereEqualTo("inviteType", "Event")
            .get().addOnSuccessListener { invites ->
                for (invite in invites) {
                    db.collection("User").document(invite.get("sender").toString())
                        .get().addOnSuccessListener { user ->
                            db.collection(
                                "Location/" + invite.get("locationID").toString() +
                                        "/Events"
                            ).document(invite.get("eventID").toString()).get()
                                .addOnSuccessListener { event ->
                                    if (event.exists()) {
                                        db.collection("Location")
                                            .document(invite.get("locationID").toString())
                                            .get().addOnSuccessListener { loc ->
                                                db.collection("User")
                                                    .document(event.get("hostID").toString())
                                                    .get().addOnSuccessListener { host ->
                                                        var hostUser = host.get("userName").toString();
                                                        if(hostUser == "null" || hostUser == ""){
                                                            hostUser = "No Host"
                                                        }
                                                        val eventObj = EventInviteDisplayer(
                                                            invite.id,
                                                            invite.get("eventID").toString(),
                                                            invite.get("locationID").toString(),
                                                            event.get("title").toString(),
                                                            loc.get("Location Name").toString(),
                                                            event.get("date") as Timestamp,
                                                            hostUser,
                                                            user.get("userName").toString()
                                                        )
                                                        eventList.add(eventObj)

                                                        // list view adapter
                                                        listview.adapter =
                                                            EventInviteListAdapter(this)
                                                    }
                                            }
                                    } else {
                                        db.collection("User")
                                            .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                                            .collection("Invites").document(invite.id).delete()
                                            .addOnSuccessListener {
                                                Toast.makeText(
                                                    this,
                                                    "Some invites have been removed since events no longer exsist",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                    }
                                }
                        }
                }
            }
    }


    // Event Array List Adapter
    internal class EventInviteListAdapter(context: Context) : BaseAdapter() {

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
            val rowMain = lI.inflate(R.layout.event_invite_list_adapter, viewGroup, false)

            val eventTitle = rowMain.findViewById<TextView>(R.id.eventTitle)
            val eventAddress = rowMain.findViewById<TextView>(R.id.eventAddress)
            val eventTime = rowMain.findViewById<TextView>(R.id.memberSport)
            val eventHost = rowMain.findViewById<TextView>(R.id.eventHost)
            val eventSender = rowMain.findViewById<TextView>(R.id.invitedBy)

            val sfd = SimpleDateFormat("yyyy-MM-dd hh:mm")
            val startTimeStamp: Timestamp = eventList.get(position).time as Timestamp
            val eventStartTime = sfd.format(Date(startTimeStamp.seconds * 1000))

            eventTitle.text = (eventList.get(position).name)
            eventAddress.text = (eventList.get(position).address)
            eventTime.text = (eventStartTime)
            eventHost.text = (eventList.get(position).host)
            eventSender.text = ("Invited by: " + eventList.get(position).sender)
            rowMain.findViewById<Button>(R.id.deleteButton).setOnClickListener {
                Log.i("Invite ID", eventList.get(position).id)
                db.collection("User")
                    .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                    .collection("Invites").document(eventList.get(position).id)
                    .delete().addOnSuccessListener {
                        (mContext as Activity).finish()
                        val intent = Intent(mContext, EventInviteActivity::class.java)
                        mContext.startActivity(intent)
                    }

            }
            return rowMain
        }
    }

    // Event Displayer class ( for array list)
    class EventInviteDisplayer {
        var id: String = ""
        var eventID: String = ""
        var locationID: String = ""
        var name: String = ""
        var address: String = ""
        var time: Timestamp
        var host: String = ""
        var sender: String = ""

        fun getID(): String {
            return this.id
        }

        // main constuctor
        constructor(
            id: String,
            eventID: String,
            locationID: String,
            name: String,
            address: String,
            time: Timestamp,
            host: String,
            sender: String
        ) {
            this.id = id
            this.eventID = eventID
            this.locationID = locationID
            this.name = name
            this.address = address
            this.time = time
            this.host = host
            this.sender = sender
        }
    }
}

