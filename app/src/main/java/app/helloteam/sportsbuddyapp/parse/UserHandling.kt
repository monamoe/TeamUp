package app.helloteam.sportsbuddyapp.parse

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.parse.ParseUser
import java.lang.Exception


// this file has been replaced with code that supports firebase
// this file is essentially useless, but were keeping it here for now

object UserHandling {

    fun Login(emailTxt: String, passwordTxt: String, context: Context): Boolean {
        var success = true

        FirebaseAuth.getInstance().signInWithEmailAndPassword(emailTxt, passwordTxt)
            .addOnCompleteListener({
                if (it.isSuccessful) {
                    Log.i(
                        "LOG_TAG",
                        "HAHA LOGIN SUCCESSFUL: " + it.result.user?.uid
                    )

                }
            }).addOnFailureListener {
                success = false
                Log.i(
                    "LOG_TAG",
                    "HAHA login failed " + it.message
                )
            }
        return success

    }

    fun SignUp(userNameTxt: String, passwordTxt: String, email: String, context: Context): Boolean {
        var success = false

        //Firebase Auth to create user with username and password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, passwordTxt)
            .addOnCompleteListener({
                if (it.isSuccessful) {
                    success = true
                    Log.i(
                        "LOG_TAG",
                        "HAHA Created Firebase user profile with uid of: " + it.result.user?.uid
                    )
                }
            }).addOnFailureListener {
                Log.i(
                    "LOG_TAG",
                    "HAHA Failed to create firebase user profile " + it.message
                )
            }
        return success;
    }

    fun Logout() {//logs out user
//        ParseUser.logOut()
    }


}