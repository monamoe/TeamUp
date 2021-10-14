package app.helloteam.sportsbuddyapp.helperUI


import android.content.Intent
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import app.helloteam.sportsbuddyapp.views.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

var locationA: Location = Location("point A")
var locationB = Location("point B")

class LoadingEvent {
    companion object {
        var hostingAttendingEventList: MutableList<EventCard> = mutableListOf<EventCard>()
        var recommendedEventList: MutableList<EventCard> = mutableListOf<EventCard>()
        var recEventsDone = false
        var yourEventsDone = false
        var yourHostDone = false

        // Recommended Events lazy row card's data for landing page
        fun recommendedEventsListData(userID: String) {
            recEventsDone = false
            var currentlyAdded = 0
            val maxAdded = 5
            val db = Firebase.firestore
            db.collection("User").document(userID).get()
                .addOnSuccessListener { user ->
                    val favouriteSport = user.get("favouriteSport")

                    if (favouriteSport != "") {
                        Log.i("LOG_TAG", "RECOMMENDED LIST: inside userID")
                        db.collection("Location").get().addOnSuccessListener { location ->
                            for (loc in location) {
                                Log.i(
                                    "LOG_TAG",
                                    "RECOMMENDED LIST: inside location ${loc.get("Location Name")}"
                                )
                                if (currentlyAdded < maxAdded) {
                                    db.collection("Location")
                                        .document(loc.id)
                                        .collection("Events").get().addOnSuccessListener { events ->
                                            for (event in events) {
                                                Log.i("LOG_TAG", "RECOMMENDED LIST: inside events")
                                                if (currentlyAdded < maxAdded) {
                                                    if (event.get("activity")
                                                            .toString() == favouriteSport
                                                    ) {
                                                        Log.i(
                                                            "LOG_TAG",
                                                            "RECOMMENDED LIST: --------------------found matching activities!"
                                                        )
                                                        val hostName =
                                                            user.get("userName").toString()


                                                        // TO DO ADD A CHECK IF THE USER IS WITHIN THE KILOMETER RANGE SPECIFIED


                                                        locationB.latitude =
                                                            loc.get("Lat").toString()
                                                                .toDouble()
                                                        locationB.longitude =
                                                            loc.get("Lon").toString()
                                                                .toDouble()
                                                        val distance =
                                                            locationA.distanceTo(locationB)
                                                        var maxDistance =
                                                            user.get("distance").toString().toInt()

                                                        if (maxDistance == null) {
                                                            maxDistance = 20000
                                                        }
                                                        if (distance <= maxDistance) {

                                                            // if the current user is not the host
                                                            if (hostName != event.get("hostID")
                                                                    .toString()
                                                            ) {
                                                                var hostName2: String
                                                                db.collection("User").document(
                                                                    event.get("hostID").toString()
                                                                ).get().addOnSuccessListener { u ->
                                                                    hostName2 =
                                                                        u.get("userName").toString()
                                                                    if (currentlyAdded < maxAdded) {
                                                                        currentlyAdded++
                                                                        recommendedEventList.add(
                                                                            EventCard(
                                                                                event.get("title")
                                                                                    .toString(),
                                                                                event.id,
                                                                                loc.id,
                                                                                loc.get("StreetView")
                                                                                    .toString(),
                                                                                false,
                                                                                if (hostName2 == null) "N/A" else hostName2, //ignore the warning here
                                                                                event.get("information").toString(),
                                                                                event.get("eventSpace")
                                                                                    .toString()
                                                                                    .toInt(),
                                                                                event.get("currentlyAttending")
                                                                                    .toString()
                                                                                    .toInt(),
                                                                            )
                                                                        )
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
                        }
                    }
                }
            recEventsDone = true
            if (loggedIn && recEventsDone && yourEventsDone && yourHostDone) {
                toLanding()
            }
        }

        // Your Events lazy row card's data for landing page
        fun yourEventListData(userID: String) {
            yourHostDone = false
            val db = Firebase.firestore

            db.collection("User").document(userID)
                .get()
                .addOnSuccessListener { users ->
                    // hosting
                    db.collection("User").document(userID).collection("Hosting")
                        .get()
                        .addOnSuccessListener { hosting ->
                            if (hosting.isEmpty) {
                                yourHostDone = true
                                Log.i("hellooooo", "h empty")
                                if (loggedIn && recEventsDone && yourEventsDone && yourHostDone) {
                                    toLanding()

                                }
                            }
                            var h = 0
                            for (host in hosting) {
                                Log.i("LOG_TAG", "EVENT DISPLAY: THIS USER IS HOSTING AN EVENT:")

                                db.collection("Location")
                                    .document(host.get("locationID").toString())
                                    .collection("Events")
                                    .document(host.get("eventID").toString())
                                    .get()
                                    .addOnSuccessListener { event ->

                                        if (users != null) {
                                            db.collection("Location")
                                                .document(
                                                    host.get("locationID")
                                                        .toString()
                                                )
                                                .get()
                                                .addOnSuccessListener { loc ->
                                                    Log.i(
                                                        "LOG_TAG",
                                                        "EVENT DISPLAY: \t\t Adding event to list"
                                                    )
                                                    h++

                                                    Log.i(
                                                        "LOG_TAG",
                                                        "VIEW EVENT: IN LOADING EVENT: ${
                                                            event.get("title").toString()
                                                        } - ${event.id}  "
                                                    )
                                                try {
                                                    hostingAttendingEventList.add(
                                                        EventCard(
                                                            event.get("title").toString(),
                                                            event.id,
                                                            loc.id,
                                                            loc.get("StreetView").toString(),
                                                            true,
                                                            users.get("userName").toString(),
                                                            event.get("information").toString(),
                                                            event.get("eventSpace").toString()
                                                                .toInt(),
                                                            event.get("currentlyAttending")
                                                                .toString()
                                                                .toInt(),
                                                        )
                                                    )
                                                } catch (e: Exception){
                                                    Log.i("errorr", e.toString())
                                                    db.collection("User").document(userID).collection("Hosting")
                                                        .document(host.id).delete()
                                                }


                                                    if (h == hosting.size()) {
                                                        Log.i("hellooooo", "h full")

                                                        yourHostDone = true
                                                        if (loggedIn && recEventsDone && yourEventsDone && yourHostDone) {
                                                            Log.i("hellooooo", "h sending")

                                                            toLanding()
                                                        }
                                                    }
                                                }
                                        }
                                    }
                            }
                        }
                }
        }

        fun getAttending(userID: String) {
            yourEventsDone = false
            //attending
            // hosting
            val db = Firebase.firestore

            db.collection("User").document(userID)
                .get()
                .addOnSuccessListener { users ->
                    db.collection("User").document(userID).collection("Attending")
                        .get()
                        .addOnSuccessListener { hosting ->
                            if (hosting.isEmpty) {
                                yourEventsDone = true
                                Log.i("hellooooo", "a empty")
                                if (loggedIn && recEventsDone && yourEventsDone && yourHostDone) {
                                    toLanding()
                                }
                            }
                            var a = 0
                            for (host in hosting) {
                                db.collection("Location")
                                    .document(host.get("locationID").toString())
                                    .collection("Events")
                                    .document(host.get("eventID").toString())
                                    .get()
                                    .addOnSuccessListener { event ->
                                        if (users != null) {
                                            db.collection("Location")
                                                .document(
                                                    host.get("locationID")
                                                        .toString()
                                                )
                                                .get()
                                                .addOnSuccessListener { loc ->
                                                    db.collection("User")
                                                        .document(event.get("hostID").toString())
                                                        .get().addOnSuccessListener { user ->
                                                            a++
                                                            var hostName = "No Host"
                                                            if (user.exists()){
                                                                hostName =
                                                                    user.get("userName").toString()
                                                            }

                                                            hostingAttendingEventList.add(
                                                                EventCard(
                                                                    event.get("title").toString(),
                                                                    event.id,
                                                                    loc.id,
                                                                    loc.get("StreetView")
                                                                        .toString(),
                                                                    false,
                                                                    hostName,
                                                                    "Playing soccer with a couple friends, feel free to join in",
                                                                    event.get("eventSpace")
                                                                        .toString()
                                                                        .toInt(),
                                                                    event.get("currentlyAttending")
                                                                        .toString()
                                                                        .toInt(),
                                                                )
                                                            )
                                                            if (a == hosting.size()) {
                                                                yourEventsDone = true
                                                                if (loggedIn && recEventsDone && yourEventsDone && yourHostDone) {
                                                                    toLanding()
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

        fun getUserName() {
            val db = Firebase.firestore

            // getting the user name
            db.collection("User").document(Firebase.auth.currentUser?.uid.toString())
                .get()
                .addOnSuccessListener { user ->
                    username = user.get("userName").toString()
                }
        }

        fun toLanding(){
            val intent = Intent(context, LandingPage2::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}