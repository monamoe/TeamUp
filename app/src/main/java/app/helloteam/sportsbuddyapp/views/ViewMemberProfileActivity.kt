package app.helloteam.sportsbuddyapp.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import app.helloteam.sportsbuddyapp.R
import app.helloteam.sportsbuddyapp.databinding.ActivityViewMemberProfileBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class ViewMemberProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewMemberProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewMemberProfileBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        val db = Firebase.firestore

        var user = intent.getStringExtra("member").toString()
        var inviteId = intent.getStringExtra("invite").toString()
        Log.i("userrrrrrr", user)
        db.collection("User").document(user)
            .get()
            .addOnSuccessListener { User ->
                var userName = User.get("userName")
                var bio = User.get("bio")
                var favouriteSport = User.get("favouriteSport")

                val sfd = SimpleDateFormat("yyyy-MM-dd")
                var time: Timestamp = User.get("dateCreated") as Timestamp
                var dateCreated = sfd.format(Date(time.seconds*1000))

                if (User.get("photoUrl") != null) {
                    Picasso.get()
                        .load(User.get("photoUrl").toString())
                        .resize(100, 100)
                        .centerCrop()
                        .into(binding.profilepic)

                }
                if (userName != null) binding.userNameEdit.text = userName.toString()
                if (dateCreated != null) binding.dateText.text = dateCreated.toString()
                if (bio != "null" && bio != null && bio != "") binding.aboutMeText.text = bio.toString()
                if (favouriteSport != null && favouriteSport != "none") binding.favSportText.text = favouriteSport.toString()
            }
        db.collection("User").document(FirebaseAuth.getInstance().currentUser?.uid.toString())
            .collection("Team").whereEqualTo("member", user) //creates team inside user
            .get().addOnSuccessListener { member ->
                if(!member.isEmpty){
                    binding.inviteButtons.setVisibility(View.GONE)
                    binding.removeButton.setVisibility(View.VISIBLE)
                } else {
                    binding.removeButton.setVisibility(View.GONE)
                }
            }

        binding.acceptButton.setOnClickListener { //accept team invite
                    var member = hashMapOf(
                        "member" to user
                    )
                    db.collection("User") //create team for user 1
                        .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                        .collection("Team")
                        .add(member)
                        .addOnSuccessListener { team ->
                                    member = hashMapOf(
                                        "member" to FirebaseAuth.getInstance().currentUser?.uid.toString(),
                                    )
                                    db.collection("User").document(user)
                                        .collection("Team").document(team.id) //create team for user 2 with same id
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
        binding.declineButon.setOnClickListener {
            db.collection("User").document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .collection("Invites").document(inviteId)
                .delete().addOnSuccessListener {
                    Toast.makeText(this, "Invite Declined", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, TeamInvites::class.java)
                    startActivity(intent)
                    finish()
                }
        }
        binding.removeButton.setOnClickListener {
            db.collection("User").document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .collection("Team").document(inviteId).delete().addOnSuccessListener {
                    db.collection("User").document(user)
                        .collection("Team").document(inviteId).delete().addOnSuccessListener {
                            Toast.makeText(this, "Team Member Removed", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, TeamsActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                }
        }
    }
}