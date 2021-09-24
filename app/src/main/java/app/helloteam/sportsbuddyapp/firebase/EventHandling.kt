package app.helloteam.sportsbuddyapp.firebase

import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object EventHandling {
    val db = Firebase.firestore

    fun getSpacesLeft(locationID: String, eventID: String, totalSpace: Int, textView: TextView) {
        db.collection("Location/" + locationID + "/Events/" + eventID + "/Attendees")
            .get()
            .addOnSuccessListener { users ->
                textView.setText("Space Left: " + (totalSpace - users.size()))
            }
    }
    fun getSpacesLeft(locationID: String, eventID: String, totalSpace: Int, button: Button) {
        db.collection("Location/" + locationID + "/Events/" + eventID + "/Attendees")
            .get()
            .addOnSuccessListener { users ->
                if(users.size() == totalSpace){
                    button.text = "Event Full"
                    button.isEnabled = false
                }
                for (user in users) {
                    if (user.get("userID").toString() == FirebaseAuth.getInstance().currentUser?.uid) {
                        button.text = "Leave"
                        button.isEnabled = true
                    }
                }
            }
    }


}
