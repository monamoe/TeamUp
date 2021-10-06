package app.helloteam.sportsbuddyapp.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.RecyclerView
import app.helloteam.sportsbuddyapp.R
import app.helloteam.sportsbuddyapp.data.NotificationData
import app.helloteam.sportsbuddyapp.data.PushNotification
import app.helloteam.sportsbuddyapp.data.RetrofitInstance
import app.helloteam.sportsbuddyapp.models.MessageModel
import app.helloteam.sportsbuddyapp.views.ChatLogActivity
import app.helloteam.sportsbuddyapp.views.CustomAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import io.karn.notify.Notify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object InviteHandling {
   val db = Firebase.firestore

   fun sendTeamInvite(code: String, context: Context){
      val username = FirebaseAuth.getInstance().currentUser?.displayName
      db.collection("User").get().addOnSuccessListener { users ->
         db.collection(
            "User/" + FirebaseAuth.getInstance().currentUser?.uid.toString()
                    + "/FriendCode"
         ).whereEqualTo("code", code).get()
            .addOnSuccessListener { codes ->
               var yourCode = ""
               for (c in codes) {
                  yourCode = c.get("code").toString()
               }
               if (code == yourCode) {
                  Toast.makeText(
                     context,
                     "Can not invite yourself",
                     Toast.LENGTH_SHORT
                  ).show()
               }
               else {
                  for (user in users) {

                     db.collection(
                        "User/" + user.id
                                + "/FriendCode"
                     ).whereEqualTo("code", code).get()
                        .addOnSuccessListener { inviting ->
                           if (inviting.isEmpty) {
                              Toast.makeText(
                                 context,
                                 "Invite Failed",
                                 Toast.LENGTH_SHORT

                              ).show()
                           } else {
                           for (u in inviting) {
                              db.collection("User").document(user.id)
                                 .collection("Invites").whereEqualTo("inviteType", "Team")
                                 .whereEqualTo(
                                    "sender",
                                    FirebaseAuth.getInstance().currentUser?.uid.toString()
                                 )
                                 .whereEqualTo("receiver", user.id).get()
                                 .addOnSuccessListener { duplicate ->
                                    if (duplicate.isEmpty) {
                                       val invite = hashMapOf(
                                          "sender" to FirebaseAuth.getInstance().currentUser?.uid.toString(),
                                          "receiver" to user.id,
                                          "inviteType" to "Team"
                                       )

                                       db.collection("User").document(user.id)
                                          .collection("Invites")
                                          .add(invite)
                                          .addOnSuccessListener {
                                             db.collection("Notification").document(user.id).get()
                                                .addOnSuccessListener { reciver ->
                                                   PushNotification(
                                                      NotificationData(
                                                         "Team Invite",
                                                         "You have been invited to ${username.toString()}'s team"
                                                      ),
                                                      reciver.get("second").toString()
                                                   ).also {
                                                      sendNotification(it)
                                                   }

                                                }
                                             Toast.makeText(
                                                context,
                                                "Invite Sent",
                                                Toast.LENGTH_SHORT
                                             ).show()
                                          }
                                    } else {
                                       Toast.makeText(
                                          context,
                                          "Invite pending",
                                          Toast.LENGTH_SHORT
                                       ).show()
                                    }

                                 }
                           }
                        }
                        }
                  }
               }
            }
      }
   }

   fun sendEventInvite(event: String, location:String, receiver: String, context: Context){
      db.collection("User").document(receiver)
         .collection("Invites").whereEqualTo("sender", FirebaseAuth.getInstance().currentUser?.uid.toString())
         .whereEqualTo("inviteType", "Event").whereEqualTo("eventID", event).get()
         .addOnSuccessListener {duplicate ->
            if(duplicate.isEmpty){
               val invite = hashMapOf(
                  "sender" to FirebaseAuth.getInstance().currentUser?.uid.toString(),
                  "receiver" to receiver,
                  "inviteType" to "Event",
                  "eventID" to event,
                  "locationID" to location
               )

               db.collection("User").document(receiver)
                  .collection("Invites")
                  .add(invite)
                  .addOnSuccessListener {
                     Toast.makeText(
                        context,
                        "Invite Sent",
                        Toast.LENGTH_SHORT
                     ).show()
                  }
            } else {
               Toast.makeText(
                  context,
                  "Invite already sent",
                  Toast.LENGTH_SHORT
               ).show()
            }
         }
   }


   private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
      try{
         val response = RetrofitInstance.api.postNotification(notification)
         if (response.isSuccessful){
            Log.d("Notiffff", Gson().toJson(response).toString())
         } else{
            Log.e("Notiffff", response.errorBody().toString())
         }
      } catch (e: Exception) {
         Log.e("Notiffff", e.toString())
      }
   }
}