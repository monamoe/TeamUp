package app.helloteam.sportsbuddyapp.firebase

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat.startActivity
import app.helloteam.sportsbuddyapp.R
import app.helloteam.sportsbuddyapp.views.ViewMemberProfileActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object TeamHandling {
    lateinit private var memberList: ArrayList<TeamDisplayer>

    fun getTeam(listview: ListView, context: Context, from: String, event: String, location: String){
        memberList = ArrayList()
        // FIREBASE MIGRATION //
        val db = Firebase.firestore
        db.collection("User").document(FirebaseAuth.getInstance().currentUser?.uid.toString())
            .collection("Team") //creates team inside user
            .get().addOnSuccessListener { members ->
                for (member in members) {
                    db.collection("User").document(member.get("member").toString())
                        .get().addOnSuccessListener { u ->
                            val eventObj = TeamDisplayer(
                                member.id,
                                member.get("member").toString(),
                                u.get("userName").toString(),
                                u.get("favouriteSport").toString(),
                                u.get("photoUrl").toString()
                            )
                            memberList.add(eventObj)

                            // list view adapter
                            listview.adapter = TeamListAdapter(context)
                        }
                }
            }
        listview.setOnItemClickListener { parent, view, position, id ->
            if (from == "Event"){
                MaterialDialog(context).show {
                    title(text = "Invite " + memberList.get(position).name + "?")
                    positiveButton(R.string.yes) { dialog ->
                        val invite = hashMapOf(
                            "sender" to FirebaseAuth.getInstance().currentUser?.uid.toString(),
                            "receiver" to memberList.get(position).getMemberID(),
                            "inviteType" to "Event",
                            "eventID" to event,
                            "locationID" to location
                        )

                        db.collection("User").document(memberList.get(position).getMemberID())
                            .collection("Invites")
                            .add(invite)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    context,
                                    "Invite Sent",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                    negativeButton(R.string.cancel)
                }
            }else {
                val teamID = memberList.get(position).getID()
                val memberID = memberList.get(position).getMemberID()
                val intent = Intent(context, ViewMemberProfileActivity::class.java)
                intent.putExtra("member", memberID)
                intent.putExtra("invite", teamID)
                context.startActivity(intent)
            }
        }
    }

    // Event Array List Adapter
    internal class TeamListAdapter(context: Context) : BaseAdapter() {

        private val mContext: Context = context

        // overrides
        override fun getCount(): Int {
            return memberList.size;
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

            name.text = (memberList.get(position).name)
            sport.text = (memberList.get(position).favSport)
            if (memberList.get(position).image != null && memberList.get(position).image != "null") {
                if (viewGroup != null) {
                    Glide.with(viewGroup).load(memberList.get(position).image).into(profileImage)
                };
            }
            return rowMain;
        }
    }

    // Event Displayer class ( for array list)
    class TeamDisplayer {
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