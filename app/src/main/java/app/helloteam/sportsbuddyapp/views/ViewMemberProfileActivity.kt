package app.helloteam.sportsbuddyapp.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import app.helloteam.sportsbuddyapp.R
import app.helloteam.sportsbuddyapp.firebase.InviteHandling
import app.helloteam.sportsbuddyapp.firebase.UserHandling.BlockUser
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*

class ViewMemberProfileActivity : AppCompatActivity() {

    private lateinit var profilePic: CircleImageView
    private lateinit var userNameEdit: TextView
    private lateinit var dateText: TextView
    private lateinit var aboutMeText: TextView
    private lateinit var favSportText: TextView
    private lateinit var inviteButtons: View
    private lateinit var sendButton: Button
    private lateinit var removeButton: Button
    private lateinit var blockButton: Button
    private lateinit var acceptButton: Button
    private lateinit var declineButon: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_member_profile)

        val db = Firebase.firestore

        // Initialize views
        profilePic = findViewById(R.id.profilepic)
        userNameEdit = findViewById(R.id.userNameEdit)
        dateText = findViewById(R.id.dateText)
        aboutMeText = findViewById(R.id.aboutMeText)
        favSportText = findViewById(R.id.favSportText)
        inviteButtons = findViewById(R.id.inviteButtons)
        sendButton = findViewById(R.id.sendButton)
        removeButton = findViewById(R.id.removeButton)
        blockButton = findViewById(R.id.blockButton)
        acceptButton = findViewById(R.id.acceptButton)
        declineButon = findViewById(R.id.declineButon)

        var user = intent.getStringExtra("member").toString()
        var inviteId = intent.getStringExtra("invite").toString()

        Log.i("userrrrrrr", user)
        db.collection("User").document(user)
            .get()
            .addOnSuccessListener { User ->
                val userName = User.get("userName")
                val bio = User.get("bio")
                val favouriteSport = User.get("favouriteSport")

                val sfd = SimpleDateFormat("yyyy-MM-dd")
                val time: Timestamp = User.get("dateCreated") as Timestamp
                val dateCreated = sfd.format(Date(time.seconds * 1000))

                if (User.get("photoUrl") != null) {
                    Glide.with(this).load(User.get("photoUrl").toString()).into(profilePic)
                }
                if (userName != null) userNameEdit.text = userName.toString()
                if (dateCreated != null) dateText.text = dateCreated.toString()
                if (bio != "null" && bio != null && bio != "") aboutMeText.text = bio.toString()
                if (favouriteSport != null && favouriteSport != "none") favSportText.text = favouriteSport.toString()
            }
        db.collection("User").document(FirebaseAuth.getInstance().currentUser?.uid.toString())
            .collection("Team").whereEqualTo("member", user)
            .get().addOnSuccessListener { member ->
                db.collection("User")
                    .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                    .collection("Invites").whereEqualTo("sender", user)
                    .whereEqualTo("inviteType", "Team")
                    .get().addOnSuccessListener { invite ->
                        if (!member.isEmpty) {
                            inviteButtons.visibility = View.GONE
                            sendButton.visibility = View.GONE
                            removeButton.visibility = View.VISIBLE
                            for (m in member) {
                                db.collection("User").document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                                    .collection("Team").document(m.id).get()
                                    .addOnSuccessListener { team ->
                                        inviteId = team.id
                                    }
                            }
                        } else if (intent.getStringExtra("event").toString() == "event" && invite.isEmpty) {
                            inviteButtons.visibility = View.GONE
                            removeButton.visibility = View.GONE
                            sendButton.visibility = View.VISIBLE
                        } else {
                            removeButton.visibility = View.GONE
                            sendButton.visibility = View.GONE
                            for (i in invite) {
                                inviteId = i.id
                            }
                        }
                    }
            }

        blockButton.setOnClickListener {
            BlockUser(user, this, FirebaseAuth.getInstance().currentUser)
        }

        acceptButton.setOnClickListener { // accept team invite
            var member = hashMapOf(
                "member" to user
            )
            db.collection("User") // create team for user 1
                .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .collection("Team")
                .add(member)
                .addOnSuccessListener { team ->
                    member = hashMapOf(
                        "member" to FirebaseAuth.getInstance().currentUser?.uid.toString(),
                    )
                    db.collection("User").document(user)
                        .collection("Team").document(team.id) // create team for user 2 with same id
                        .set(member)
                        .addOnSuccessListener {
                            db.collection("User").document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                                .collection("Invites").document(inviteId)
                                .delete().addOnSuccessListener {
                                    Toast.makeText(this, "Invite Accepted", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, TeamsActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                        }
                }
        }

        declineButon.setOnClickListener {
            db.collection("User").document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .collection("Invites").document(inviteId)
                .delete().addOnSuccessListener {
                    Toast.makeText(this, "Invite Declined", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, TeamInvites::class.java)
                    startActivity(intent)
                    finish()
                }
        }

        removeButton.setOnClickListener {
            db.collection("User").document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .collection("Team").document(inviteId).delete().addOnSuccessListener {
                    db.collection("User").document(user)
                        .collection("Team").document(inviteId).delete().addOnSuccessListener {
                            Toast.makeText(this, "User Removed", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, TeamsActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                }
        }

        sendButton.setOnClickListener {
            finish()
            InviteHandling.sendTeamInviteFromEvent(user, this)
        }
    }
}
