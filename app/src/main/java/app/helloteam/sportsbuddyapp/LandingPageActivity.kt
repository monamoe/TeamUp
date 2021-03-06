package app.helloteam.sportsbuddyapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.parse.ParseUser

class LandingPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing_page)


        val showuser = findViewById<TextView>(R.id.ShowUsername)

        showuser.text = ParseUser.getCurrentUser().username

        val logoutBtn = findViewById<Button>(R.id.LogoutBtn)//gets logout button id
        val profileBtn = findViewById<Button>(R.id.ToProfileBtn) // gets profile button id
        val mapBtn = findViewById<Button>(R.id.mapBtn)//switch to map layout
        val createEventBtn = findViewById<Button>(R.id.CreateEventBtn)//create event layout

        logoutBtn.setOnClickListener {//logs user out when they click the logout button
            UserHandling.Logout()
            afterLogout()
        }


        profileBtn.setOnClickListener {
            val intent = Intent(this, ProfilePage::class.java)
            startActivity(intent)
        }

        //switches view to map
        mapBtn.setOnClickListener{
            val intent = Intent(this, map::class.java)
            startActivity(intent)
        }

        createEventBtn.setOnClickListener{
            val intent = Intent(this, CreateEvent::class.java)
            startActivity(intent)
        }

    }

    fun afterLogout() {//method to go back to login screen after logout
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}