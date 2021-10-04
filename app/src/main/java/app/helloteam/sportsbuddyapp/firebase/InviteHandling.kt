package app.helloteam.sportsbuddyapp.firebase

import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

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
               } else {
                  for (user in users) {

                     db.collection(
                        "User/" + user.id
                                + "/FriendCode"
                     ).whereEqualTo("code", code).get()
                        .addOnSuccessListener { inviting ->
                           for (u in inviting) {
                              db.collection("User").document(user.id)
                                 .collection("Invites").whereEqualTo("inviteType", "Team")
                                 .whereEqualTo("sender", FirebaseAuth.getInstance().currentUser?.uid.toString())
                                 .whereEqualTo("receiver", user.id).get().addOnSuccessListener { duplicate ->
                                    if(duplicate.isEmpty){
                                       val invite = hashMapOf(
                                          "sender" to FirebaseAuth.getInstance().currentUser?.uid.toString(),
                                          "receiver" to user.id,
                                          "inviteType" to "Team"
                                       )

                                       db.collection("User").document(user.id)
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
}