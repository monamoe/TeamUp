package app.helloteam.sportsbuddyapp.firebase

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import app.helloteam.sportsbuddyapp.views.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


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

    fun BlockUser(userNameBlockID: String, context: Context, currentUser: FirebaseUser?) {
        val db = Firebase.firestore

        val blockHashMap = hashMapOf(
            "Blocked Users" to userNameBlockID
        )

        db.collection("User").document(currentUser?.uid.toString()).collection("BlockList").document()
                    .set(blockHashMap, SetOptions.merge())
                        .addOnSuccessListener{
                            val blockedUser = db.collection("User").document(userNameBlockID).get()
                                .addOnSuccessListener{blockedUser->
                                    Toast.makeText(context, "You have blocked ${blockedUser.get("userName")}" , Toast.LENGTH_LONG).show()
                                }

                        }



    }




    fun userDelete(currentUser: FirebaseUser?, context: Context) {
        val ref = FirebaseDatabase.getInstance()
        val db = Firebase.firestore
        ref.getReference("/latest-messages").child(currentUser?.uid.toString())
            .removeValue()//delete messages
        ref.getReference("/user-messages").child(currentUser?.uid.toString())
            .removeValue()//delete messages
        db.collection("User_Messages_Archive").document(currentUser?.uid.toString())
            .delete()//delete message archives
        FileHandling.deleteProfilePhoto(currentUser?.uid.toString())//delete user profile image
        db.collection("User").document(currentUser?.uid.toString()).collection("Hosting")
            .get().addOnSuccessListener { hosts ->
                for (host in hosts) {
                    db.collection("Location").document(host.get("locationID").toString())
                        .collection("Events").document(host.get("eventID").toString())
                        .update("hostID", "null")

                    db.collection("User").document(currentUser?.uid.toString())
                        .collection("Hosting").document(host.id)
                        .delete()
                }

                db.collection("User").document(currentUser?.uid.toString()).collection("FriendCode")
                    .get().addOnSuccessListener { friends ->
                        for (friend in friends) {
                            db.collection("User").document(currentUser?.uid.toString())
                                .collection("FriendCode")
                                .document(friend.id).delete()
                        }

                        db.collection("User").document(currentUser?.uid.toString())
                            .collection("Attendees")
                            .get().addOnSuccessListener { attends ->
                                for (attend in attends) {
                                    db.collection("User").document(currentUser?.uid.toString())
                                        .collection("Attendees")
                                        .document(attend.id).delete()
                                }
                                db.collection("User").document(currentUser?.uid.toString())
                                    .collection("Team")
                                    .get().addOnSuccessListener { teams ->
                                        for (team in teams) {
                                            db.collection("User")
                                                .document(currentUser?.uid.toString())
                                                .collection("Team")
                                                .document(team.id).delete()
                                        }
                                    }
                                db.collection("User").document(currentUser?.uid.toString())
                                    .delete()//delete user from user table
                            }
                    }
            }

        currentUser?.delete()?.addOnSuccessListener { // deletes user from auth
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
        }
    }


}