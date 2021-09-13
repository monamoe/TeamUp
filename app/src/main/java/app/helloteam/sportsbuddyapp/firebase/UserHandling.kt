package app.helloteam.sportsbuddyapp.firebase

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth


// this file has been replaced with code that supports firebase

object UserHandling {

    fun Login(emailTxt: String, passwordTxt: String, context: Context): Boolean {
        var success = false

        FirebaseAuth.getInstance().signInWithEmailAndPassword(emailTxt, passwordTxt)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.i(
                        "LOG_TAG",
                        "HAHA LOGIN SUCCESSFUL: " + it.result.user?.uid
                    )
                    success = true

                }
            }
        return success
    }

    fun SignUp(userNameTxt: String, passwordTxt: String, email: String, context: Context): Boolean {
        var success = false

        //Firebase Auth to create user with username and password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, passwordTxt)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    success = true
                }
            }
        return success;
    }

    fun Logout() {//logs out user
        FirebaseAuth.getInstance().signOut()
    }


}