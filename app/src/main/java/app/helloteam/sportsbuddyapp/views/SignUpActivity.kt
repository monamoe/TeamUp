package app.helloteam.sportsbuddyapp.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import app.helloteam.sportsbuddyapp.R
import app.helloteam.sportsbuddyapp.parse.UserHandling

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val signupbtn = findViewById<Button>(R.id.signupBtn)//get sign in button id

        signupbtn.setOnClickListener {// when user clicks sign up button

            //gets data from all text fields by id
            val userNameTxt = findViewById<TextView>(R.id.usernameTxt).text.toString()
            val passwordTxt = findViewById<TextView>(R.id.passwordTxt).text.toString()
            val passwordConfirmTxt = findViewById<TextView>(R.id.confirmPasswordTxt).text.toString()
            val emailTxt = findViewById<TextView>(R.id.emailTxt).text.toString()


            if (userNameTxt.equals("") || passwordTxt.equals("")//checks if all fields are filled
                || passwordConfirmTxt.equals("") || emailTxt.equals("")
            ) {
                Toast.makeText(this, "Please enter required fields", Toast.LENGTH_SHORT).show()
            } else {

                if (passwordTxt.equals(passwordConfirmTxt)) {//checks if passwords match

                    if (UserHandling.SignUp(
                            userNameTxt,
                            passwordTxt,
                            emailTxt,
                            this
                        )
                    ) {//send data to sign in method in userhandling object file
                        //if signup was successful go to landing page
                        val intent = Intent(this, LandingPageActivity::class.java)
                        startActivity(intent)
                    }

                } else {//if passwords dont match
                    Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show()
                }

            }

        }

        val backBtn = findViewById<TextView>(R.id.backBtn)

        backBtn.setOnClickListener {//allows user to go back to login page from signup
            val intent = Intent(this, MainActivity::class.java)


            startActivity(intent)
        }
    }
}