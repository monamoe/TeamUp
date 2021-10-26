package app.helloteam.sportsbuddyapp.helperUI

import android.content.Intent
import android.util.Log
import app.helloteam.sportsbuddyapp.views.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoadingEventView {

    companion object {

        // location info
        var locationName = "Location Name"
        var locationInfo = "No additional information on this location"
        var locationImage = "IDK"
        var locationLat: Double? = 1.1
        var locationLon: Double? = 1.1

        // host info
        var hostName = "Location Name"
        var hostImage = "Location Name"
        var hostRating = "Location Name"

        // event info
        lateinit var eventInfo: EventCard
        private var eventViewDataCounter = 0

        // attendee info
        var attendeeList: MutableList<AttendeesCard> = mutableListOf()
        var attendeeListDone = false
        private var attendingListDataCounter = 0


        // team members - using AttendeeCard since its the same data values
        var teamMemberList: MutableList<AttendeesCard> = mutableListOf()
        var teamMemberListDone = false
        private var teamMemberListCounter = 0

        // populate location event list

        fun eventViewData(locationID: String, eventID: String) {
            eventViewDataCounter = 0
            attendingListDataCounter = 0
            LoadingEventList.locationEventListDone = false
            val db = Firebase.firestore
            val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()

            // location data = loc
            db.collection("Location").document(locationID).get().addOnSuccessListener { loc ->
                locationName = loc.get("Location Name").toString()
                locationInfo = loc.get("locationInfo").toString()
                locationImage = loc.get("StreetView").toString()
                locationLat =
                    if (loc.get("Lat").toString() == "") 0.00 else loc.get("Lat").toString()
                        .toDoubleOrNull()
                locationLon =
                    if (loc.get("Lon").toString() == "") 0.00 else loc.get("Lat").toString()
                        .toDoubleOrNull()


                Log.i(
                    "LOG_TAG",
                    "LOADING EVENT VIEW: Inside Location $locationName"
                )


                // getting list of team members
                db.collection("User").document(uid).collection("Team").get()
                    .addOnSuccessListener { members ->

                        // if the user has no team members
                        if (teamMemberListCounter == members.size()) {
                            teamMemberListDone = true
                            toEventViewPage()
                        }

                        for (member in members) {

                            Log.i(
                                "LOG_TAG",
                                "LOADING EVENT VIEW: Inside members : ${
                                    member.get("userName").toString()
                                }"
                            )

                            db.collection("User").document(member.id).get()
                                .addOnSuccessListener { user ->
                                    teamMemberList += AttendeesCard(
                                        user.id,
                                        user.get("userName").toString(),
                                        user.get("photoUrl").toString(),
                                    )
                                    teamMemberListCounter++

                                    if (teamMemberListCounter == members.size()) {
                                        teamMemberListDone = true
                                        toEventViewPage()
                                    }
                                }
                        }
                    }

                // event data = event
                db.collection("Location").document(locationID).collection("Events")
                    .document(eventID).get().addOnSuccessListener { event ->


                        Log.i(
                            "LOG_TAG",
                            "LOADING EVENT VIEW: Inside event ${event.get("title").toString()}"
                        )

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
                                        event.get("activity").toString(),
                                        event.get("date").toString(),
                                        loc.get("endDate").toString(),
                                        loc.get("Location Name").toString(),
                                    )

                                Log.i(
                                    "LOG_TAG",
                                    "LOADING EVENT VIEW: Got Event Info"
                                )

                                // attendee data = attendees
                                db.collection("Location").document(locationID).collection("Events")
                                    .document(eventID).collection("Attendees").get()
                                    .addOnSuccessListener { attendees ->

                                        // intent check goes before as well since there coule be events with 0 attendees
                                        Log.i(
                                            "LOG_TAG",
                                            "LOADING EVENT VIEW: Comparing $attendingListDataCounter - ${attendees.size()}"
                                        )
                                        if (attendingListDataCounter == attendees.size()) {
                                            toEventViewPage()
                                        }

                                        for (attendee in attendees) {
                                            // attending user data = user
                                            db.collection("User")
                                                .document(attendee.get("userID").toString())
                                                .get().addOnSuccessListener { user ->
                                                    Log.i(
                                                        "LOG_TAG",
                                                        "LOADING EVENT VIEW: Inside Attendees : ${
                                                            user.get(
                                                                "userName"
                                                            ).toString()
                                                        }"
                                                    )

                                                    attendeeList +=
                                                        AttendeesCard(
                                                            user.id,
                                                            user.get("userName").toString(),
                                                            user.get("photoUrl").toString(),
                                                        )



                                                    attendingListDataCounter++
                                                    Log.i(
                                                        "LOG_TAG",
                                                        "LOADING EVENT VIEW: Comparing $attendingListDataCounter - ${attendees.size()}"
                                                    )
                                                    if (attendingListDataCounter == attendees.size()) {
                                                        attendeeListDone = true
                                                    }
                                                    toEventViewPage()
                                                }
                                        }
                                    }
                            }
                    }
            }
        }

        // to event list view
        private fun toEventViewPage() {
            if (attendeeListDone && teamMemberListDone) {
                val intent = Intent(EventViewContext, EventCompose::class.java)
                context.startActivity(intent)
            }
        }
    }
}