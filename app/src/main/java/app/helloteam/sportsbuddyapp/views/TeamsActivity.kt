package app.helloteam.sportsbuddyapp.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import app.helloteam.sportsbuddyapp.R

class TeamsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teams)

        findViewById<Button>(R.id.addMemberButton).setOnClickListener {
            finish()
            val intent = Intent(this, TeamSearchActivity::class.java)
            startActivity(intent)
        }
    }
}