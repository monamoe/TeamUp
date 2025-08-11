package app.helloteam.sportsbuddyapp.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import app.helloteam.sportsbuddyapp.R
import app.helloteam.sportsbuddyapp.firebase.TeamHandling

class NewMessageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)
        supportActionBar?.title = "New Conversation"

        TeamHandling.getTeam(findViewById(R.id.listView), this, "Chat", "", "")

    }
}