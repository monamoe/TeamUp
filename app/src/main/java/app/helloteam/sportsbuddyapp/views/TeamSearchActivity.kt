package app.helloteam.sportsbuddyapp.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import app.helloteam.sportsbuddyapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class TeamSearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_search)

        findViewById<Button>(R.id.inviteMemberButton).setOnClickListener {
            var email = findViewById<EditText>(R.id.memberEmail).text
            // FIREBASE MIGRATION //
            val db = Firebase.firestore
            if (email.toString() == FirebaseAuth.getInstance().currentUser?.email){
                Toast.makeText(this, "Can not invite yourself", Toast.LENGTH_SHORT).show()
            } else {
                db.collection("User").whereEqualTo("userEmail", email.toString())
                    .get().addOnSuccessListener { users ->
                        for (user in users) {
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
                                    Toast.makeText(this, "Invite Sent", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, TeamsActivity::class.java)
                                    startActivity(intent)
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Invite Failed", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, TeamsActivity::class.java)
                                    startActivity(intent)
                                }
                        }
                        if (users.isEmpty) {
                            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }
}