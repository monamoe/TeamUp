package app.helloteam.sportsbuddyapp.views

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import app.helloteam.sportsbuddyapp.R
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


private lateinit var inviteList: ArrayList<TeamInvites.InviteDisplayer>

class TeamInvites : AppCompatActivity() {

    private lateinit var listview: ListView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_invites)

        listview = findViewById(R.id.listView)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        inviteList = ArrayList()

        supportActionBar?.title = "Team Invites"

        loadInvites()

        listview.setOnItemClickListener { _, _, position, _ ->
            val memberID = inviteList[position].getID()
            val inviteID = inviteList[position].getInviteID()
            val intent = Intent(this, ViewMemberProfileActivity::class.java)
            intent.putExtra("member", memberID)
            intent.putExtra("invite", inviteID)
            startActivity(intent)
            finish()
        }

        swipeRefreshLayout.setOnRefreshListener {
            loadInvites()
        }
    }

    private fun loadInvites() {
        inviteList.clear()  // Clear existing list before loading new data
        val db = Firebase.firestore
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid.toString()

        db.collection("User").document(currentUserId)
            .collection("Invites").whereEqualTo("inviteType", "Team")
            .get()
            .addOnSuccessListener { invites ->
                if (invites.isEmpty) {
                    // Optionally handle empty case, e.g., show empty message
                    inviteList.clear()
                    listview.adapter = TeamListAdapter(this)
                    swipeRefreshLayout.isRefreshing = false
                    return@addOnSuccessListener
                }

                var processedCount = 0
                for (invite in invites) {
                    val senderId = invite.get("sender").toString()
                    db.collection("User").document(senderId)
                        .get()
                        .addOnSuccessListener { user ->
                            val eventObj = InviteDisplayer(
                                user.id,
                                invite.id,
                                user.get("userName").toString(),
                                user.get("photoUrl").toString()
                            )
                            inviteList.add(eventObj)

                            processedCount++
                            // Only update adapter and stop refreshing when all invites are processed
                            if (processedCount == invites.size()) {
                                listview.adapter = TeamListAdapter(this)
                                swipeRefreshLayout.isRefreshing = false
                            }
                        }
                        .addOnFailureListener {
                            processedCount++
                            if (processedCount == invites.size()) {
                                listview.adapter = TeamListAdapter(this)
                                swipeRefreshLayout.isRefreshing = false
                            }
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load invites.", Toast.LENGTH_SHORT).show()
                swipeRefreshLayout.isRefreshing = false
            }
    }

    internal class TeamListAdapter(context: Context) : BaseAdapter() {
        private val mContext: Context = context

        override fun getCount(): Int = inviteList.size

        override fun getItem(position: Int): Any = inviteList[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val lI = LayoutInflater.from(mContext)
            val rowMain =
                convertView ?: lI.inflate(R.layout.invite_list_adapter_view, parent, false)

            val name = rowMain.findViewById<TextView>(R.id.eventTitle)
            val profileImage = rowMain.findViewById<ImageView>(R.id.profilepic)

            val invite = inviteList[position]

            name.text = invite.name
            if (invite.image.isNotEmpty() && invite.image != "null") {
                Glide.with(mContext).load(invite.image).into(profileImage)
            } else {
                profileImage.setImageResource(R.drawable.soccer) // fallback image
            }

            return rowMain
        }
    }

    class InviteDisplayer(
        private val id: String,
        private val inviteId: String,
        val name: String,
        val image: String
    ) {
        fun getID(): String = id
        fun getInviteID(): String = inviteId
    }
}
