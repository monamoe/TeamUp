package app.helloteam.sportsbuddyapp.views

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import app.helloteam.sportsbuddyapp.R
import app.helloteam.sportsbuddyapp.firebase.EventHandling.db
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private lateinit var eventList: ArrayList<EventInviteActivity.EventInviteDisplayer>
private lateinit var listViewtitle: TextView

class EventInviteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_invite)

        val listview = findViewById<ListView>(R.id.inviteList)
        listViewtitle = findViewById(R.id.listTitle)

        eventList = ArrayList()
        supportActionBar?.title = "Event Invites"

        getInvites(listview)

        listview.setOnItemClickListener { _, _, position, _ ->
            Log.i("LOG_TAG", "LOADING EVENT FROM INVITE")
            val intent = Intent(this, SplashLoadingEventView::class.java)
            intent.putExtra("locationID", eventList[position].locationID)
            intent.putExtra("eventID", eventList[position].eventID)
            startActivity(intent)
        }

        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            getInvites(listview)
            swipeRefreshLayout.isRefreshing = false
        }
    }

    fun getInvites(listview: ListView) {
        eventList = ArrayList()
        val db = Firebase.firestore
        db.collection("User").document(FirebaseAuth.getInstance().currentUser?.uid.toString())
            .collection("Invites").whereEqualTo("inviteType", "Event")
            .get().addOnSuccessListener { invites ->
                if (invites.isEmpty) {
                    updateTitleText()
                    listview.adapter = EventInviteListAdapter(this)
                    return@addOnSuccessListener
                }

                for (invite in invites) {
                    db.collection("User").document(invite.get("sender").toString())
                        .get().addOnSuccessListener { user ->
                            db.collection("Location/${invite.get("locationID")}/Events")
                                .document(invite.get("eventID").toString()).get()
                                .addOnSuccessListener { event ->
                                    if (event.exists()) {
                                        db.collection("Location")
                                            .document(invite.get("locationID").toString())
                                            .get().addOnSuccessListener { loc ->
                                                db.collection("User")
                                                    .document(event.get("hostID").toString())
                                                    .get().addOnSuccessListener { host ->
                                                        var hostUser =
                                                            host.get("userName")?.toString()
                                                                ?: "No Host"
                                                        if (hostUser == "null" || hostUser.isEmpty()) {
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
                                                            user.get("userName").toString(),
                                                            event.get("activity").toString()
                                                        )
                                                        eventList.add(eventObj)
                                                        updateTitleText()
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
                                                    "Some invites have been removed since events no longer exist",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                    }
                                }
                        }
                }
            }
    }

    private fun updateTitleText() {
        listViewtitle.text = when (eventList.size) {
            0 -> "You have no event invites!"
            1 -> "You have 1 event invite!"
            else -> "You have ${eventList.size} event invites!"
        }
    }

    internal class EventInviteListAdapter(context: Context) : BaseAdapter() {

        private val mContext: Context = context

        override fun getCount(): Int {
            return eventList.size
        }

        override fun getItem(position: Int): Any {
            return eventList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {
            val lI = LayoutInflater.from(mContext)
            val rowMain = lI.inflate(R.layout.event_invite_list_adapter, viewGroup, false)

            val eventTitle = rowMain.findViewById<TextView>(R.id.eventTitle)
            val eventAddress = rowMain.findViewById<TextView>(R.id.eventAddress)
            val eventTime = rowMain.findViewById<TextView>(R.id.memberTime)
            val eventHost = rowMain.findViewById<TextView>(R.id.eventHost)
            val eventSender = rowMain.findViewById<TextView>(R.id.invitedBy)
            val activity = rowMain.findViewById<TextView>(R.id.memberActivity)

            val sfd = SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.getDefault())
            val startTimeStamp: Timestamp = eventList[position].time
            val eventStartTime = sfd.format(Date(startTimeStamp.seconds * 1000))

            eventTitle.text = eventList[position].name
            eventAddress.text = eventList[position].address
            eventTime.text = eventStartTime
            eventHost.text = eventList[position].host
            eventSender.text = "Invite From: ${eventList[position].sender}"
            activity.text = eventList[position].activity

            rowMain.findViewById<Button>(R.id.deleteButton).setOnClickListener {
                Log.i("Invite ID", eventList[position].id)
                db.collection("User")
                    .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                    .collection("Invites").document(eventList[position].id)
                    .delete().addOnSuccessListener {
                        (mContext as Activity).finish()
                        val intent = Intent(mContext, EventInviteActivity::class.java)
                        mContext.startActivity(intent)
                    }
            }
            return rowMain
        }
    }

    class EventInviteDisplayer(
        var id: String,
        var eventID: String,
        var locationID: String,
        var name: String,
        var address: String,
        var time: Timestamp,
        var host: String,
        var sender: String,
        var activity: String
    )
}
