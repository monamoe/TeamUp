package app.helloteam.sportsbuddyapp.views

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import app.helloteam.sportsbuddyapp.R
import app.helloteam.sportsbuddyapp.firebase.TeamHandling
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class TeamsActivity : AppCompatActivity() {

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teams)

        val listview = findViewById<ListView>(R.id.listView)

        TeamHandling.getTeam(listview,this, "Team", "", "")

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
}

