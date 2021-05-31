package app.helloteam.sportsbuddyapp.views

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import app.helloteam.sportsbuddyapp.R
import app.helloteam.sportsbuddyapp.parse.UserHandling
import com.parse.ParseUser


class
MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if (ParseUser.getCurrentUser() != null) {//if user is already logged in
            afterLogin()
        }

        //Sign Up button is pressed
        val signUpbtn = findViewById<TextView>(R.id.signUpButtonMain)

        signUpbtn.setOnClickListener {//go to sign up activity
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }


        //Login users
        val loginBtn = findViewById<Button>(R.id.LoginButton)
        loginBtn.setOnClickListener {

            val emailTxt = findViewById<TextView>(R.id.userNameText).text.toString()
            val passwordTxt = findViewById<TextView>(R.id.PasswordText).text.toString()

            if (emailTxt.equals("") || passwordTxt.equals("")) {
                Toast.makeText(this, "Please enter required fields", Toast.LENGTH_SHORT).show()
            } else {
                if (UserHandling.Login(
                        emailTxt,
                        passwordTxt,
                        this
                    )
                ) { // send data to login method in userhandling object class
                    //if login is successful
                    afterLogin()
                }

            }
        }


    }

    fun afterLogin() {//go to landing activity when called
        val intent = Intent(this, LandingPageActivity::class.java)
        startActivity(intent)
    }


}