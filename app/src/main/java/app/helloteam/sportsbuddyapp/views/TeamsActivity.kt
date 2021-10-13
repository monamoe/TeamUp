package app.helloteam.sportsbuddyapp.views

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import app.helloteam.sportsbuddyapp.R
import app.helloteam.sportsbuddyapp.firebase.TeamHandling
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.widget.Toast





class TeamsActivity : AppCompatActivity() {

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teams)

        val listview = findViewById<ListView>(R.id.listView)

        TeamHandling.getTeam(listview,this, "Team", "", "")
        actionBar?.setIcon(R.drawable.ic_baseline_send_24);
        supportActionBar?.title = "Team Memebers"

        val context = this
        findViewById<FloatingActionButton>(R.id.addMemberButton).setOnClickListener {
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
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.invite_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id: Int = item.getItemId()
        if (id == R.id.view_invites) {
            val intent = Intent(this, TeamInvites::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
}

