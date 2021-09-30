package app.helloteam.sportsbuddyapp.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import app.helloteam.sportsbuddyapp.R
import app.helloteam.sportsbuddyapp.firebase.TeamHandling

class NewMessageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        TeamHandling.getTeam(findViewById(R.id.listView),this, "Chat", "", "")

    }
}