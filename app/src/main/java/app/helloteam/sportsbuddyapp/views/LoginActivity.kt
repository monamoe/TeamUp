package app.helloteam.sportsbuddyapp.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import app.helloteam.sportsbuddyapp.R
import app.helloteam.sportsbuddyapp.parse.UserHandling
import com.google.firebase.auth.FirebaseAuth
import com.parse.ParseUser


class
LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // if the user is logged in already
        if (ParseUser.getCurrentUser() != null) {//if user is already logged in
            afterLogin()
        }

        //Sign Up button is pressed
        findViewById<TextView>(R.id.signUpButtonMain).setOnClickListener {//go to sign up activity
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }


        //login button pressed
        findViewById<Button>(R.id.LoginButton).setOnClickListener {

            val emailTxt = findViewById<TextView>(R.id.emailText).text.toString()
            val passwordTxt = findViewById<TextView>(R.id.PasswordText).text.toString()

            if (emailTxt.equals("") || passwordTxt.equals("")) {
                Toast.makeText(this, "Please enter required fields", Toast.LENGTH_SHORT).show()
            } else {

                //login with firebase Auth
                FirebaseAuth.getInstance().signInWithEmailAndPassword(emailTxt, passwordTxt)
                    .addOnCompleteListener({
                        if (it.isSuccessful) {
                            Log.i(
                                "LOG_TAG",
                                "Login Successful with uid of: " + it.result.user?.uid
                            )
                            afterLogin()
                        }
                    }).addOnFailureListener {
                        Log.i(
                            "LOG_TAG",
                            "Login failed: " + it.message
                        )
                        Toast.makeText(this, "Login Error $it.message ", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    //go to landing activity when called
    fun afterLogin() {
        val intent = Intent(this, LandingPageActivity::class.java)
        startActivity(intent)
    }

}