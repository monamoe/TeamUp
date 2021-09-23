package app.helloteam.sportsbuddyapp.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import app.helloteam.sportsbuddyapp.R
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

lateinit private var memberList: ArrayList<TeamsActivity.TeamDisplayer>

class TeamsActivity : AppCompatActivity() {

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teams)

        val listview = findViewById<ListView>(R.id.listView)

        memberList = ArrayList()

        // populate array list with events that match the location ID of the marker selected
        Log.i("Teamssss", "i just wanna test")

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
                    listview.adapter = TeamListAdapter(this)
                }
                }
            }


        listview.setOnItemClickListener { parent, view, position, id ->
            val teamID = memberList.get(position).getID()
            val memberID = memberList.get(position).getMemberID()
            val intent = Intent(this, ViewMemberProfileActivity::class.java)
            intent.putExtra("member", memberID)
            intent.putExtra("invite", teamID)
            startActivity(intent)
            finish()
        }
        val context = this
        findViewById<Button>(R.id.addMemberButton).setOnClickListener {
           MaterialDialog(this).show {
                title(R.string.invite_title)
                input(hint = "6 characters long"){ dialog, text ->
                        var code = text.toString().trim()
                        // FIREBASE MIGRATION //
                        val db = Firebase.firestore
                    db.collection("User").get().addOnSuccessListener { users ->
                        db.collection(
                            "User/" + FirebaseAuth.getInstance().currentUser?.uid.toString()
                                    + "/FriendCode"
                        ).whereEqualTo("code", code).get()
                            .addOnSuccessListener { codes ->
                                var yourCode = ""
                                for (c in codes) {
                                    yourCode = c.get("code").toString()
                                }
                                if (code == yourCode) {
                                    Toast.makeText(
                                        context,
                                        "Can not invite yourself",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    for (user in users) {

                                        db.collection(
                                            "User/" + user.id
                                                    + "/FriendCode"
                                        ).whereEqualTo("code", code).get()
                                            .addOnSuccessListener { inviting ->
                                                for (u in inviting) {

                                                    val invite = hashMapOf(
                                                        "sender" to FirebaseAuth.getInstance().currentUser?.uid.toString(),
                                                        "receiver" to user.id,
                                                        "inviteType" to "Team"
                                                    )

                                                    db.collection("User").document(user.id)
                                                        .collection("Invites")
                                                        .add(invite)
                                                        .addOnSuccessListener {
                                                            finish()
                                                            Toast.makeText(
                                                                context,
                                                                "Invite Sent",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                        .addOnFailureListener {
                                                            Toast.makeText(
                                                                context,
                                                                "Invite Failed",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                }
                                            }
                                    }
                                }
                            }
                    }

                }
                positiveButton(R.string.submit)
                negativeButton(R.string.cancel)
           }
        }

        findViewById<Button>(R.id.invitesButton).setOnClickListener {
            val intent = Intent(this, TeamInvites::class.java)
            startActivity(intent)
            finish()
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

