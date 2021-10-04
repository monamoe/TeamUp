package app.helloteam.sportsbuddyapp.views

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import app.helloteam.sportsbuddyapp.R
import app.helloteam.sportsbuddyapp.firebase.InviteHandling
import app.helloteam.sportsbuddyapp.firebase.TeamHandling
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.google.android.material.internal.ContextUtils.getActivity
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
                    InviteHandling.sendTeamInvite(code, context)
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

