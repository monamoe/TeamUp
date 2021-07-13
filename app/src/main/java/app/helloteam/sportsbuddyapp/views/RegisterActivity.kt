package app.helloteam.sportsbuddyapp.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import app.helloteam.sportsbuddyapp.R
import app.helloteam.sportsbuddyapp.parse.UserHandling
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // register event
        findViewById<Button>(R.id.signupBtn).setOnClickListener {// when user clicks sign up button

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
                if (passwordTxt.equals(passwordConfirmTxt)) {


                    // store the new user data in firestore
                    val userHashMap = hashMapOf(
                        "userName" to userNameTxt,
                        "userEmail" to emailTxt
                    )


                    // add location unless it already exists
                    Firebase.firestore.collection("User")
                        .document(FirebaseAuth.getInstance().uid.toString())
                        .set(userHashMap, SetOptions.merge())
                        .addOnSuccessListener {
                            Log.d("CreatingEvent", "Created new user")
                        }
                        .addOnFailureListener { e ->
                            Log.w("a", "Error creating location document", e)
                        }


                    //Firebase Auth to record user Auth, and user data
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailTxt, passwordTxt)
                        .addOnCompleteListener({
                            if (it.isSuccessful) {
                                val intent = Intent(this, LandingPageActivity::class.java)
                                startActivity(intent)
                            }
                        }).addOnFailureListener {
                            Log.i(
                                "LOG_TAG",
                                "HAHA Failed to create firebase user profile " + it.message
                            )
                        }


                } else {//if passwords dont match
                    Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show()
                }

            }

        }


        findViewById<TextView>(R.id.backBtn).setOnClickListener {//allows user to go back to login page from signup
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}




























































