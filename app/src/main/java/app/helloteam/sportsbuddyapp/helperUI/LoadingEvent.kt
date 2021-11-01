/*
rileygray & monamoe
collect and load data for the landing page
 */
package app.helloteam.sportsbuddyapp.helperUI

import android.content.Intent
import android.location.Location
import android.util.Log
import app.helloteam.sportsbuddyapp.helperUI.LoadingEventView.Companion.hasHost
import app.helloteam.sportsbuddyapp.views.LandingPage2
import app.helloteam.sportsbuddyapp.views.context
import app.helloteam.sportsbuddyapp.views.loggedIn
import app.helloteam.sportsbuddyapp.views.username
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

var locationA: Location = Location("point A")
var locationB = Location("point B")

val logi = "LoadingLanding"

class LoadingEvent {
    companion object {
        var hostingAttendingEventList: MutableList<EventCard> = mutableListOf()
        var recommendedEventList: MutableList<EventCard> = mutableListOf()

        var recEventsDone = false
        var yourEventsDone = false
        var yourHostDone = false

        // Recommended Events lazy row card's data for landing page
        fun recommendedEventsListData(
            userID: String,
            userLocationLat: Double,
            userLocationLon: Double
        ) {
            recEventsDone = false
            var currentlyAdded = 0
            val maxAdded = 5
            val db = Firebase.firestore

            // counters
            var locationCounter = 0
            var eventsCounter = 0

            // user location
            locationA.latitude = userLocationLat
            locationA.longitude = userLocationLon

            db.collection("User").document(userID).get()
                .addOnSuccessListener { user ->
                    val favouriteSport = user.get("favouriteSport")

                    Log.i(logi, "Favourite Sport: $favouriteSport")

                    if (favouriteSport != "none") {
                        db.collection("Location").get().addOnSuccessListener { location ->
                            for (loc in location) {
                                locationCounter++
                                if (currentlyAdded < maxAdded) {

                                    db.collection("Location")
                                        .document(loc.id)
                                        .collection("Events").get().addOnSuccessListener { events ->
                                            for (event in events) {
                                                eventsCounter++
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

                                                        // if the user is within the range specified
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
                                                        } else {
                                                            maxDistance *= 1000
                                                        }
                                                        Log.i(
                                                            "HELLOOOOOOO 4",
                                                            "$distance $maxDistance"
                                                        )
                                                        if (distance <= maxDistance) {
                                                            Log.i(
                                                                "HELLOOOOOOO 4",
                                                                event.id
                                                            )

                                                            // if the current user is not the host
                                                            if (user.id != event.get("hostID")
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
                                                                                if (hostName2 == "null") "N/A" else hostName2, //ignore the warning here
                                                                                event.get("information")
                                                                                    .toString(),
                                                                                event.get("eventSpace")
                                                                                    .toString()
                                                                                    .toInt(),
                                                                                event.get("currentlyAttending")
                                                                                    .toString()
                                                                                    .toInt(),
                                                                                event.get("activity")
                                                                                    .toString(),
                                                                                event.get("date")
                                                                                    .toString(),
                                                                                loc.get("endDate")
                                                                                    .toString(),
                                                                                loc.get("Location Name")
                                                                                    .toString(),
                                                                            )
                                                                        )
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            Log.i(
                                                "LOG_TAG",
                                                "RECOMMENDED LIST: $eventsCounter - ${events.size()}"
                                            )
                                            if (eventsCounter == events.size() && locationCounter == location.size()) {
                                                recEventsDone = true
                                                toLanding()
                                            }
                                        }
                                }
                            }
                        }
                    } else {
                        // user doesn't have a favourite event set
                        recEventsDone = true
                        toLanding()
                    }
                }
        }

        // Your Events lazy row card's data for landing page
        fun yourEventListData(userID: String) {
            yourHostDone = false
            val db = Firebase.firestore
            var hostingCounter = 0

            db.collection("User").document(userID)
                .get()
                .addOnSuccessListener { users ->
                    // hosting
                    db.collection("User").document(userID).collection("Hosting")
                        .get()
                        .addOnSuccessListener { hosting ->
                            if (hosting.isEmpty) {
                                yourHostDone = true
                                toLanding()
                            }
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
                                                        "EVENT DISPLAY: inside location - ${loc.id}"
                                                    )
                                                    hostingCounter++

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
                                                                event.get("activity")
                                                                    .toString(),
                                                                event.get("date")
                                                                    .toString(),
                                                                loc.get("endDate")
                                                                    .toString(),
                                                                loc.get("Location Name")
                                                                    .toString(),
                                                            )
                                                        )

                                                    } catch (e: Exception) {
                                                        Log.i("errorr", e.toString())
                                                        db.collection("User").document(userID)
                                                            .collection("Hosting")
                                                            .document(host.id).delete()
                                                    }


                                                    if (hostingCounter == hosting.size()) {
                                                        yourHostDone = true
                                                        toLanding()
                                                    }
                                                }
                                        }
                                    }
                            }
                        }
                }
        }


        // get the events the user is attending
        fun getAttending(userID: String) {
            Log.i(logi, "Get Attending: Inside getAttending")

            yourEventsDone = false
            //attending
            // hosting
            val db = Firebase.firestore

            db.collection("User").document(userID)
                .get()
                .addOnSuccessListener { users ->
                    db.collection("User").document(userID).collection("Attending")
                        .get()
                        .addOnSuccessListener { attendingList ->

                            if (attendingList.isEmpty) {
                                Log.i(logi, "Get Attending: Attending list was empty")
                                yourEventsDone = true
                                toLanding()
                            }

                            var a = 0
                            for (attending in attendingList) {
                                Log.i(logi, "Get Attending: Inside Attending")
                                db.collection("Location")
                                    .document(attending.get("locationID").toString())
                                    .collection("Events")
                                    .document(attending.get("eventID").toString())
                                    .get()
                                    .addOnSuccessListener { event ->
                                        Log.i(logi, "Get Attending: Inside Attending")


                                        db.collection("Location")
                                            .document(attending.get("locationID").toString())
                                            .get()
                                            .addOnSuccessListener { loc ->
                                                Log.i(logi, "Get Attending: Inside Location")

                                                // if the event has a host
                                                if (event.get("hostID").toString() != "null") {
                                                    db.collection("User")
                                                        .document(event.get("hostID").toString())
                                                        .get()
                                                        .addOnSuccessListener { eventHost ->

                                                            Log.i(logi, "Inside User")

                                                            a++
                                                            var hostName = eventHost.get("userName")
                                                                .toString()

                                                            hostingAttendingEventList.add(
                                                                EventCard(
                                                                    event.get("title").toString(),
                                                                    event.id,
                                                                    loc.id,
                                                                    loc.get("StreetView")
                                                                        .toString(),
                                                                    false,
                                                                    hostName,
                                                                    event.get("information")
                                                                        .toString(),
                                                                    event.get("eventSpace")
                                                                        .toString()
                                                                        .toInt(),
                                                                    event.get("currentlyAttending")
                                                                        .toString()
                                                                        .toInt(),
                                                                    event.get("activity")
                                                                        .toString(),
                                                                    event.get("date")
                                                                        .toString(),
                                                                    loc.get("endDate")
                                                                        .toString(),
                                                                    loc.get("Location Name")
                                                                        .toString(),
                                                                )
                                                            )
                                                            Log.i(
                                                                logi,
                                                                "Comparing a: $a - ${attendingList.size()}"
                                                            )
                                                            if (a == attendingList.size()) {
                                                                yourEventsDone = true
                                                                toLanding()
                                                            }
                                                        }
                                                } else {
                                                    a++
                                                    var hostName = "No Host"

                                                    hostingAttendingEventList.add(
                                                        EventCard(
                                                            event.get("title").toString(),
                                                            event.id,
                                                            loc.id,
                                                            loc.get("StreetView")
                                                                .toString(),
                                                            false,
                                                            hostName,
                                                            event.get("information")
                                                                .toString(),
                                                            event.get("eventSpace")
                                                                .toString()
                                                                .toInt(),
                                                            event.get("currentlyAttending")
                                                                .toString()
                                                                .toInt(),
                                                            event.get("activity")
                                                                .toString(),
                                                            event.get("date")
                                                                .toString(),
                                                            loc.get("endDate")
                                                                .toString(),
                                                            loc.get("Location Name")
                                                                .toString(),
                                                        )
                                                    )
                                                    Log.i(
                                                        logi,
                                                        "Comparing a: $a - ${attendingList.size()}"
                                                    )
                                                    if (a == attendingList.size()) {
                                                        yourEventsDone = true
                                                        toLanding()
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

        private fun toLanding() {
            Log.i(logi, "To Landing: $loggedIn $recEventsDone $yourEventsDone $yourHostDone")
            if (loggedIn && recEventsDone && yourEventsDone && yourHostDone) {
                val intent = Intent(context, LandingPage2::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
        }
    }
}