package app.helloteam.sportsbuddyapp.views

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import app.helloteam.sportsbuddyapp.R
import com.baoyz.widget.PullRefreshLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

lateinit private var inviteList: ArrayList<TeamInvites.InviteDisplayer>


class TeamInvites : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_invites)
        val listview = findViewById<ListView>(R.id.listView)

        inviteList = ArrayList()

        // populate array list with events that match the location ID of the marker selected
        Log.i("Teamssss", "i just wanna test")

        // FIREBASE MIGRATION //
        val db = Firebase.firestore
        db.collection("User").document(FirebaseAuth.getInstance().currentUser?.uid.toString())
            .collection("Invites").whereEqualTo("inviteType", "Team")
            .get().addOnSuccessListener { invites ->
                for (invite in invites){
                    db.collection("User").document(invite.get("sender").toString())
                        .get().addOnSuccessListener { user ->
                            val eventObj = InviteDisplayer(
                                user.id,
                                invite.id,
                                user.get("userName").toString(),
                                user.get("photoUrl").toString()
                            )
                            inviteList.add(eventObj)

                            // list view adapter
                            listview.adapter = TeamListAdapter(this)
                        }
                }
            }


        listview.setOnItemClickListener { parent, view, position, id ->
            val memberID = inviteList.get(position).getID()
            val inviteID = inviteList.get(position).getInviteID()
            val intent = Intent(this, ViewMemberProfileActivity::class.java)
            intent.putExtra("member", memberID)
            intent.putExtra("invite", inviteID)
            startActivity(intent)
            finish()
        }
       val layout = findViewById<PullRefreshLayout>(R.id.swipeRefreshLayout)
        layout.setOnRefreshListener {
            db.collection("User").document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .collection("Invites").whereEqualTo("inviteType", "Team")
                .get().addOnSuccessListener { invites ->
                    for (invite in invites){
                        db.collection("User").document(invite.get("sender").toString())
                            .get().addOnSuccessListener { user ->
                                val eventObj = InviteDisplayer(
                                    user.id,
                                    invite.id,
                                    user.get("userName").toString(),
                                    user.get("photoUrl").toString()
                                )
                                inviteList.add(eventObj)

                                // list view adapter
                                listview.adapter = TeamListAdapter(this)
                            }
                    }
                    layout.setRefreshing(false);

                }
        }
    }


    // Event Array List Adapter
    internal class TeamListAdapter(context: Context) : BaseAdapter() {

        private val mContext: Context = context

        // overrides
        override fun getCount(): Int {
            return inviteList.size;
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
            val rowMain = lI.inflate(R.layout.invite_list_adapter_view, viewGroup, false);

            val name = rowMain.findViewById<TextView>(R.id.eventTitle)
            val profileImage = rowMain.findViewById<ImageView>(R.id.profilepic)

            name.text = (inviteList.get(position).name)
            if (inviteList.get(position).image != null && inviteList.get(position).image != "null") {
                Picasso.get()
                    .load(inviteList.get(position).image)
                    .resize(100, 100)
                    .centerCrop()
                    .into(profileImage)
            }

            return rowMain;
        }
    }

    // Event Displayer class ( for array list)
    class InviteDisplayer {
        var id: String = ""
        var inviteId: String = " "
        var name: String = ""
        var image: String = ""

        fun getID(): String {
            return this.id
        }

        fun getInviteID(): String {
            return this.inviteId
        }


        // main constuctor
        constructor(id: String, inviteId: String, name: String, image: String) {
            this.id = id
            this.inviteId = inviteId
            this.name = name
            this.image = image
        }
    }
}
