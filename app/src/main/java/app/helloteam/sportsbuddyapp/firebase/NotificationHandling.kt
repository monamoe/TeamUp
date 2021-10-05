package app.helloteam.sportsbuddyapp.firebase

import android.content.Context
import android.util.Log
import android.widget.Toast
import app.helloteam.sportsbuddyapp.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

object NotificationHandling {

    fun getCurrentToken(context: Context){
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("Notif", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            val msg = ("Notif: " + token)
            Log.d("Notif", msg)

            Firebase.firestore.collection("User").document(
                FirebaseAuth.getInstance().currentUser?.uid.toString()).update("token", token)
        })
    }
}