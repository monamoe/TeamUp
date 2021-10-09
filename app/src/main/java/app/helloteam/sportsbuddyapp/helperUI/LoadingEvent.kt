package app.helloteam.sportsbuddyapp.helperUI


import android.content.Intent
import android.util.Log
import app.helloteam.sportsbuddyapp.views.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoadingEvent {
companion object {
    var hostingAttendingEventList: MutableList<EventCard> = mutableListOf<EventCard>()
    var recommendedEventList: MutableList<EventCard> = mutableListOf<EventCard>()
    var recEventsDone = false
    var yourEventsDone = false
    var yourHostDone = false

    fun recommendedEventsListData(userID: String) {
        recEventsDone = false
        var currentlyAdded = 0;
        val maxAdded = 5;
        val db = Firebase.firestore
        db.collection("User").document(userID).get()
            .addOnSuccessListener { user ->
                val favouriteSport = user.get("favouriteSport")
                if (favouriteSport != "") {

                    Log.i("LOG_TAG", "RECOMMENDED LIST: inside userID")
                    db.collection("Location").get().addOnSuccessListener { location ->
                        for (loc in location) {
                            Log.i("LOG_TAG", "RECOMMENDED LIST: inside location")
                            if (currentlyAdded < maxAdded) {
                                db.collection("Location").document(loc.get("locationID").toString())
                                    .collection("Event").get().addOnSuccessListener { events ->
                                        for (event in events) {
                                            Log.i("LOG_TAG", "RECOMMENDED LIST: inside events")
                                            if (currentlyAdded < maxAdded) {
                                                if (event.get("activity")
                                                        .toString() == favouriteSport
                                                ) {

                                                    val hostName =
                                                        user.get("userName").toString()
                                                    Log.i(
                                                        "LOG_TAG",
                                                        "RECOMMENDED LIST: adding event"
                                                    )
                                                    if (currentlyAdded < maxAdded) {
                                                        recommendedEventList.add(
                                                            app.helloteam.sportsbuddyapp.helperUI.EventCard(
                                                                event.get("title").toString(),
                                                                event.get("eventID").toString(),
                                                                loc.get("StreetView").toString(),
                                                                false,
                                                                hostName,
                                                                "Playing soccer with a couple friends, feel free to join in",
                                                                event.get("eventSpace").toString()
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
        recEventsDone = true
        if (loggedIn && recEventsDone && yourEventsDone && yourHostDone){
            context.startActivity(Intent(context, LandingPage2::class.java))
        }
    }

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
                        if (hosting.isEmpty){
                            yourHostDone = true
                            Log.i("hellooooo", "h empty")
                            if (loggedIn && recEventsDone && yourEventsDone && yourHostDone) {
                                context.startActivity(
                                    Intent(
                                        context,
                                        LandingPage2::class.java
                                    )
                                )
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

                                                hostingAttendingEventList.add(
                                                    app.helloteam.sportsbuddyapp.helperUI.EventCard(
                                                        event.get("title").toString(),
                                                        event.get("eventID").toString(),
                                                        loc.get("StreetView").toString(),
                                                        true,
                                                        users.get("userName").toString(),
                                                        "Playing soccer with a couple friends, feel free to join in",
                                                        event.get("eventSpace").toString().toInt(),
                                                        event.get("currentlyAttending").toString()
                                                            .toInt(),
                                                    )
                                                )
                                                if(h == hosting.size()) {
                                                    Log.i("hellooooo", "h full")

                                                    yourHostDone = true
                                                    if (loggedIn && recEventsDone && yourEventsDone && yourHostDone) {
                                                        Log.i("hellooooo", "h sending")

                                                        context.startActivity(
                                                            Intent(
                                                                context,
                                                                LandingPage2::class.java
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

    fun getAttending(userID: String){
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
                        if(hosting.isEmpty){
                            yourEventsDone = true
                            Log.i("hellooooo", "a empty")
                            if (loggedIn && recEventsDone && yourEventsDone && yourHostDone) {
                                context.startActivity(
                                    Intent(
                                        context,
                                        LandingPage2::class.java
                                    )
                                )
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

                                                        val hostName =
                                                            user.get("userName").toString()
                                                        hostingAttendingEventList.add(
                                                            app.helloteam.sportsbuddyapp.helperUI.EventCard(
                                                                event.get("title").toString(),
                                                                event.get("eventID").toString(),
                                                                loc.get("StreetView").toString(),
                                                                false,
                                                                hostName,
                                                                "Playing soccer with a couple friends, feel free to join in",
                                                                event.get("eventSpace").toString()
                                                                    .toInt(),
                                                                event.get("currentlyAttending")
                                                                    .toString()
                                                                    .toInt(),
                                                            )
                                                        )
                                                        if (a == hosting.size()) {
                                                            yourEventsDone = true
                                                            if (loggedIn && recEventsDone && yourEventsDone && yourHostDone) {
                                                                context.startActivity(
                                                                    Intent(
                                                                        context,
                                                                        LandingPage2::class.java
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

        fun getUserName(){
            val db = Firebase.firestore

            // getting the user name
            db.collection("User").document(Firebase.auth.currentUser?.uid.toString())
                .get()
                .addOnSuccessListener { user ->
                    username = user.get("userName").toString()
                }
        }
    }
}