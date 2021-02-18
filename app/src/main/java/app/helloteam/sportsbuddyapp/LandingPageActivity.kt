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

        showuser.text=ParseUser.getCurrentUser().username

        val logoutBtn = findViewById<Button>(R.id.LogoutBtn)//gets logout button id

        logoutBtn.setOnClickListener {//logs user out when they click the logout button
            UserHandling.Logout()
            afterLogout()
        }
    }

    fun afterLogout() {//method to go back to login screen after logout
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}