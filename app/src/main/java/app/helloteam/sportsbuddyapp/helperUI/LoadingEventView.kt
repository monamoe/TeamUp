package app.helloteam.sportsbuddyapp.helperUI

import android.content.Intent
import android.util.Log
import app.helloteam.sportsbuddyapp.views.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoadingEventView {

    companion object {

        // location info
        var locationName = "Location Name"
        var locationInfo = "No additional information on this location"
        var locationImage = "IDK"
        var locationLat = 1.1
        var locationLon = 1.1

        // host info
        var hostName = "Location Name"
        var hostImage = "Location Name"
        var hostRating = "Location Name"

        // event info
        lateinit var eventInfo: EventCard

        // attendee info
        var attendeeList: MutableList<AttendeesCard> = mutableListOf()


        // team members info TO DO


        // populate location event list
        private var eventViewDataCounter = 0
        private var attendingListDataCounter = 0

        fun eventViewData(locationID: String, eventID: String) {
            eventViewDataCounter = 0
            attendingListDataCounter = 0
            LoadingEventList.locationEventListDone = false
            val db = Firebase.firestore

            // location data = loc
            db.collection("Location").document(locationID).get().addOnSuccessListener { loc ->
                locationName = loc.get("Location Name").toString()
                locationInfo = loc.get("locationInfo").toString()
                locationImage = loc.get("StreetView").toString()
                locationLat = loc.get("Lat").toString().toDouble()
                locationLon = loc.get("Lon").toString().toDouble()

                // event data = event
                db.collection("Location").document(locationID).collection("Events")
                    .document(eventID).get().addOnSuccessListener { event ->

                        // host data = host
                        db.collection("Users").document(event.get("hostID").toString()).get()
                            .addOnSuccessListener { host ->
                                hostName = host.get("userName").toString()
                                hostImage = host.get("photoUrl").toString()
                                hostRating = "4"

                                eventInfo =
                                    EventCard(
                                        event.get("title").toString(),
                                        event.id,
                                        loc.id,
                                        loc.get("StreetView").toString(),
                                        false,
                                        if (hostName == "") "No Host" else hostName,
                                        event.get("information").toString(),
                                        event.get("eventSpace").toString().toInt(),
                                        event.get("currentlyAttending").toString().toInt(),
                                        event.get("activity").toString()
                                    )

                                // attendee data = attendees
                                db.collection("Location").document(locationID).collection("Events")
                                    .document(eventID).collection("Attendees").get()
                                    .addOnSuccessListener { attendees ->
                                        for (attendee in attendees) {
                                            // attending user data = user
                                            db.collection("Users")
                                                .document(attendee.get("userID").toString())
                                                .get().addOnSuccessListener { user ->
                                                    attendeeList +=
                                                        AttendeesCard(
                                                            user.get("userName").toString(),
                                                            user.get("photoUrl").toString()
                                                        )
                                                }
                                            attendingListDataCounter++
                                            if (attendingListDataCounter == attendees.size()) {
                                                toEventViewPage()
                                            }
                                        }
                                    }
                            }
                    }
            }
        }


        // to event list view
        fun toEventViewPage() {
            val intent = Intent(EventViewContext, EventCompose::class.java)
            context.startActivity(intent)
        }
    }
}