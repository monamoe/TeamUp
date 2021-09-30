package app.helloteam.sportsbuddyapp.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import app.helloteam.sportsbuddyapp.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class LatestMessagesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)

        findViewById<FloatingActionButton>(R.id.newMessage).setOnClickListener {
            val intent = Intent(this, NewMessageActivity::class.java)
            startActivity(intent)
        }
    }
}