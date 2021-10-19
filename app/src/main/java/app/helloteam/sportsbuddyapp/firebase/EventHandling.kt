package app.helloteam.sportsbuddyapp.firebase

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import app.helloteam.sportsbuddyapp.R
import app.helloteam.sportsbuddyapp.views.ProfilePage
import app.helloteam.sportsbuddyapp.views.ViewMemberProfileActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object EventHandling {
    lateinit private var attendeeList: ArrayList<AttendeeDisplayer>

    val db = Firebase.firestore

    fun getSpacesLeft(locationID: String, eventID: String, totalSpace: Int, textView: TextView) {
        db.collection("Location/" + locationID + "/Events/" + eventID + "/Attendees")
            .get()
            .addOnSuccessListener { users ->
                textView.setText("Space Left: " + (totalSpace - users.size()))
            }
    }
    fun getSpacesLeft(locationID: String, eventID: String, totalSpace: Int, button: Button) {
        db.collection("Location/" + locationID + "/Events/" + eventID + "/Attendees")
            .get()
            .addOnSuccessListener { users ->
                if(users.size() == totalSpace){
                    button.text = "Event Full"
                    button.isEnabled = false
                }
                for (user in users) {
                    if (user.get("userID").toString() == FirebaseAuth.getInstance().currentUser?.uid) {
                        button.text = "Leave"
                        button.isEnabled = true
                    }
                }
            }
    }
    fun getAttendees(listview: ListView, context: Context, locID: String, eventID: String){
        attendeeList = ArrayList()
        // FIREBASE MIGRATION //
        val db = Firebase.firestore
        db.collection("Location/" + locID + "/Events").document(eventID)
            .collection("Attendees") //creates team inside user
            .get().addOnSuccessListener { members ->
                for (member in members) {
                    db.collection("User").document(member.get("userID").toString())
                        .get().addOnSuccessListener { u ->
                            val eventObj = AttendeeDisplayer(
                                member.id,
                                member.get("userID").toString(),
                                u.get("userName").toString(),
                                u.get("favouriteSport").toString(),
                                u.get("photoUrl").toString()
                            )
                            attendeeList.add(eventObj)

                            // list view adapter
                            listview.adapter = AttendeeListAdapter(context)
                        }
                }
            }
        listview.setOnItemClickListener { parent, view, position, id ->
                val teamID = attendeeList.get(position).getID()
                val memberID = attendeeList.get(position).getMemberID()
                var intent = Intent(context, ViewMemberProfileActivity::class.java)
            if(attendeeList.get(position).getMemberID() == FirebaseAuth.getInstance().currentUser?.uid){
                intent = Intent(context, ProfilePage::class.java)
            }else{
                intent.putExtra("event", "event")
            }
                intent.putExtra("member", memberID)
                intent.putExtra("invite", teamID)
                context.startActivity(intent)
            }
        }

    // Event Array List Adapter
    internal class AttendeeListAdapter(context: Context) : BaseAdapter() {

        private val mContext: Context = context

        // overrides
        override fun getCount(): Int {
            return attendeeList.size;
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
            val rowMain = lI.inflate(R.layout.team_list_adapter_view, viewGroup, false);

            val name = rowMain.findViewById<TextView>(R.id.eventTitle)
            val sport = rowMain.findViewById<TextView>(R.id.memberSport)
            val profileImage = rowMain.findViewById<ImageView>(R.id.profilepic)

            name.text = (attendeeList.get(position).name)
            sport.text = (attendeeList.get(position).favSport)
            if (attendeeList.get(position).image != null && attendeeList.get(position).image != "null") {
                if (viewGroup != null) {
                    Glide.with(viewGroup).load(attendeeList.get(position).image).into(profileImage)
                };
            }
            return rowMain;
        }
    }

    // Event Displayer class ( for array list)
    class AttendeeDisplayer {
        var id: String = ""
        var memberId: String = ""
        var name: String = ""
        var favSport: String = ""
        var image: String = ""

        fun getID(): String {
            return this.id
        }

        fun getMemberID(): String {
            return this.memberId
        }

        // main constuctor
        constructor(id: String, memberId: String, name: String, favSport: String, image: String) {
            this.id = id
            this.memberId = memberId
            this.name = name
            this.favSport = favSport
            this.image = image
        }
    }
}

