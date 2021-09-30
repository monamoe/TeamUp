package app.helloteam.sportsbuddyapp.helperUI

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import app.helloteam.sportsbuddyapp.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// TO DO: constructor for the class takes an EventID and fills in the rest of the data itself
// TO DO send images through here

/**
 * Event Object
 *
 * This class is used to retrive Event Related Information
 */
data class EventCard(
    val title: String,
    val eventID: String,
    val imageId: String,
    @DrawableRes
    val activityIcon: Int,
    val lightColor: Color,
    val mediumColor: Color,
    val darkColor: Color,
    val isHosting: Boolean,
    val hostName: String,
    val eventDesc: String,
) {


//    EventCard(
//    "Title",
//    "eventID",
//    "imageID",
//    R.drawable.common_google_signin_btn_icon_light,
//    BlueViolet1,
//    BlueViolet2,
//    BlueViolet3,
//    false,
//    "AK"
//    ),
}


/**
 * Event Object
 *
 * This class returns a listOf EventCard objects
 * @param userID
 */
fun GetListOfUserEventCards(
    userID: String
) {
    val db = Firebase.firestore
    db.collection("Users").document(userID).collection("Hosting")
        .get()
        .addOnSuccessListener { hostingList ->
            for (hosting in hostingList) {
                db.collection("Location").document(hosting.get("locationID").toString())
                    .collection("Events").document(hosting.get("eventID").toString())
                    .get()
                    .addOnSuccessListener { event ->
                        EventCard(
                            "Title",
                            "eventID",
                            "imageID",
                            R.drawable.common_google_signin_btn_icon_light,
                            BlueViolet1,
                            BlueViolet2,
                            BlueViolet3,
                            false,
                            "AK",
                            "YEAH"
                        )
                    }
            }
        }
    var output = listOf<EventCard>()

}








