package app.helloteam.sportsbuddyapp.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import app.helloteam.sportsbuddyapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.actionCodeSettings
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
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
            val testUser = findViewById<CheckBox>(R.id.testUser).isChecked



            if (userNameTxt.equals("") || passwordTxt.equals("") ||
                passwordConfirmTxt.equals("") || emailTxt.equals("")
            ) {
                Toast.makeText(this, "Please enter required fields", Toast.LENGTH_SHORT).show()
            } else {
                if (passwordTxt.equals(passwordConfirmTxt)) {

                    //check if that user with that email / username exists already


                    //Firebase Auth to record user Auth, and user data
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailTxt, passwordTxt)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                val user = Firebase.auth.currentUser// sets username
                                val profileUpdates = userProfileChangeRequest {
                                    displayName = userNameTxt
                                }
                                if(!testUser) {
                                    user!!.updateProfile(profileUpdates)
                                    user!!.sendEmailVerification()//sends verification email
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                Toast.makeText(
                                                    this,
                                                    "Email Sent",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        }
                                }
                                // store the new user data in firestore
                                val userHashMap = hashMapOf(
                                    "userName" to userNameTxt,
                                    "userEmail" to emailTxt,
                                    "testUser" to testUser
                                )
                                Firebase.firestore.collection("User")
                                    .document(FirebaseAuth.getInstance().uid.toString())
                                    .set(userHashMap, SetOptions.merge())
                                    .addOnSuccessListener {
                                        Log.d("CreatingEvent", "Created new user")
                                        val intent = Intent(this, LoginActivity::class.java)
                                        startActivity(intent)
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w("a", "Error creating location document", e)
                                    }

                                val intent = Intent(this, LandingPageActivity::class.java)
                                startActivity(intent)
                            }
                        }.addOnFailureListener {
                            Toast.makeText(
                                this,
                                "There was a problem with creating the User Account, Please try again later..." + it.message,
                                Toast.LENGTH_LONG
                            ).show()
                        }


                } else {//if passwords dont match
                    Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show()
                }
            }
        }

        //allows user to go back to login page from signup
        findViewById<Button>(R.id.backBtn).setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}




























































