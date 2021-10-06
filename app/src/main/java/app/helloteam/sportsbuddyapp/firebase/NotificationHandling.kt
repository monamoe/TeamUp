package app.helloteam.sportsbuddyapp.firebase

import android.content.Context
import android.util.Log
import app.helloteam.sportsbuddyapp.data.PushNotification
import app.helloteam.sportsbuddyapp.data.RetrofitInstance
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService



class NotificationHandling : FirebaseMessagingService() {

    companion object{
        fun getCurrentToken(context: Context) {
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

                val userHashMap = hashMapOf(
                    "token" to token
                )
                Firebase.firestore.collection("Notification").document(
                    FirebaseAuth.getInstance().currentUser?.uid.toString()
                ).set(userHashMap)
            })
        }
    }
}