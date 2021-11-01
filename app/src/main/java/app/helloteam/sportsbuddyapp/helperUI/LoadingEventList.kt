package app.helloteam.sportsbuddyapp.helperUI

import android.content.Intent
import android.util.Log
import app.helloteam.sportsbuddyapp.views.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoadingEventList {


    companion object {
        var locationEventList: MutableList<EventCard> = mutableListOf()
        var locationName = "Location Name"
        var locationInfo = "No additional information on this location"
        var locationImage = ""
        lateinit var eventInfo: EventCard

        var locationEventListDone = false


        // populate location event list
        var locationEventListDataCounter = 0


        fun locationEventListData(locationID: String) {
            locationEventListDone = false
            locationEventListDataCounter = 0
            val db = Firebase.firestore

            // location data = loc
            db.collection("Location").document(locationID).get().addOnSuccessListener { loc ->

                locationName = loc.get("Location Name").toString()
                locationInfo = if (loc.get("Location Info")
                        .toString() == ""
                ) "No additional information on this location" else loc.get("Location Info")
                    .toString()
                locationImage = loc.get("StreetView").toString()

                // event data = event
                db.collection("Location").document(locationID).collection("Events")
                    .get()
                    .addOnSuccessListener { events ->

                        for (event in events) {


                            if (event.get("hostID").toString() != "null") {
                                db.collection("User").document(event.get("hostID").toString())
                                    .get()
                                    .addOnSuccessListener { u ->
                                        locationEventListDataCounter++
                                        locationEventList += EventCard(
                                            event.get("title").toString(),
                                            event.id,
                                            loc.id,
                                            loc.get("StreetView").toString(),
                                            false,
                                            u.get("userName").toString(),
                                            event.get("information").toString(),
                                            event.get("eventSpace").toString().toInt(),
                                            event.get("currentlyAttending").toString().toInt(),
                                            event.get("activity")
                                                .toString(),
                                            event.get("date")
                                                .toString(),
                                            loc.get("endDate")
                                                .toString(),
                                            loc.get("Location Name")
                                                .toString(),
                                        )
                                        Log.i(
                                            "LOG_TAG",
                                            "LOADING EVENTS: SIZE COMPARING - $locationEventListDataCounter - ${events.size()}"
                                        )
                                        Log.i(
                                            "LOG_TAG",
                                            "LOADING EVENTS: ${locationEventList.size}"
                                        )
                                        if (locationEventListDataCounter == events.size()) {
                                            toEventListView()
                                        }
                                    }
                            } else {

                                locationEventListDataCounter++
                                locationEventList += EventCard(
                                    event.get("title").toString(),
                                    event.id,
                                    loc.id,
                                    loc.get("StreetView").toString(),
                                    false,
                                    "No Host",
                                    event.get("information").toString(),
                                    event.get("eventSpace").toString().toInt(),
                                    event.get("currentlyAttending").toString().toInt(),
                                    event.get("activity")
                                        .toString(),
                                    event.get("date")
                                        .toString(),
                                    loc.get("endDate")
                                        .toString(),
                                    loc.get("Location Name")
                                        .toString(),
                                )


                                Log.i(
                                    "LOG_TAG",
                                    "LOADING EVENTS: SIZE COMPARING - $locationEventListDataCounter - ${events.size()}"
                                )
                                Log.i("LOG_TAG", "LOADING EVENTS: ${locationEventList.size}")
                                if (locationEventListDataCounter == events.size()) {
                                    toEventListView()
                                }
                            }


                        }
                    }
            }
        }

        // to event list view
        private fun toEventListView() {
            val intent = Intent(EventListContext, EventListCompose::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}