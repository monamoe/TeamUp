package app.helloteam.sportsbuddyapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import app.helloteam.sportsbuddyapp.views.LandingPage
import app.helloteam.sportsbuddyapp.views.LoginActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // on start go to login
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, LoginActivity())
            .commit()

        fun toLanding() {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, LandingPage())
                .commit()
        }

    }


}

