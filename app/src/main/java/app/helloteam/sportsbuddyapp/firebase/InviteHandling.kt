package app.helloteam.sportsbuddyapp.firebase

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.material.contentColorFor
import app.helloteam.sportsbuddyapp.data.NotificationData
import app.helloteam.sportsbuddyapp.data.PushNotification
import app.helloteam.sportsbuddyapp.data.RetrofitInstance
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object InviteHandling {
   val db = Firebase.firestore

   fun sendTeamInvite(code: String, context: Context){
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

                                       db.collection("User").document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                                          .get().addOnSuccessListener { sender ->
                                             db.collection("User").document(user.id)
                                                .collection("Invites")
                                                .add(invite)
                                                .addOnSuccessListener {

                                                   db.collection("Notification").document(user.id).get() //gets user notif token
                                                      .addOnSuccessListener { reciever ->
                                                         PushNotification(
                                                            NotificationData( //creates invite message
                                                               "Team Invite",
                                                               "You have been invited to ${sender.get("userName")}'s team"
                                                            ),
                                                            reciever.get("token").toString() //gets token
                                                         ).also {
                                                            sendNotification(it) //sends result
                                                         }

                                                      }

                                                   Toast.makeText(
                                                      context,
                                                      "Invite Sent",
                                                      Toast.LENGTH_SHORT
                                                   ).show()
                                                }
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

                     db.collection("Notification").document(receiver).get() //gets user notif token
                        .addOnSuccessListener { reciever ->
                           PushNotification(
                              NotificationData( //creates invite message
                                 "Event Invite",
                                 "You have a event Invite."
                              ),
                              reciever.get("token").toString() //gets token
                           ).also {
                              sendNotification(it) //sends result
                           }

                        }
                  }

            }else {
               Toast.makeText(
                  context,
                  "Invite already sent",
                  Toast.LENGTH_SHORT
               ).show()
            }
         }
   }

   fun sendTeamInviteFromEvent(user: String, context: Context) {
      db.collection("User").document(user)
         .collection("Invites").whereEqualTo("inviteType", "Team")
         .whereEqualTo(
            "sender",
            FirebaseAuth.getInstance().currentUser?.uid.toString()
         )
         .whereEqualTo("receiver", user).get()
         .addOnSuccessListener { duplicate ->
            if (duplicate.isEmpty) {
               val invite = hashMapOf(
                  "sender" to FirebaseAuth.getInstance().currentUser?.uid.toString(),
                  "receiver" to user,
                  "inviteType" to "Team"
               )

               db.collection("User").document(user)
                  .collection("Invites")
                  .add(invite)
                  .addOnSuccessListener {
                     Toast.makeText(
                        context,
                        "Invite Sent",
                        Toast.LENGTH_SHORT
                     ).show()
                  }
                  .addOnFailureListener {
                     Toast.makeText(
                        context,
                        "Invite Failed",
                        Toast.LENGTH_SHORT
                     ).show()
                  }
            } else {
               Toast.makeText(
                  context,
                  "Invite Pending",
                  Toast.LENGTH_SHORT
               ).show()
            }
         }
   }


   private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
      try{
         val response = RetrofitInstance.api.postNotification(notification) //creates instance of api
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