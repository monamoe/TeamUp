package app.helloteam.sportsbuddyapp.helperUI

import android.content.Intent
import android.util.Log
import android.widget.Toast
import app.helloteam.sportsbuddyapp.views.*
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class LoadingEventView {

    companion object {
        // if the current user is the host or if they are already attending this event
        var hasHost = true
        var hosting = false
        var attending = false

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
        var locationIDa = ""
        var eventIDa = ""

        // loading data for event view
        fun eventViewData(locationID: String, eventID: String) {
            // reset variables
            hasHost = true
            hosting = false
            attending = false

            // location info
            locationName = "Location Name"
            locationInfo = "No additional information on this location"
            locationImage = "IDK"
            locationLat = 1.1
            locationLon = 1.1

            // host info
            hostName = "hostName"
            hostImage = "hostImage"
            hostRating = "hostRating"


            // lists
            attendeeList.clear()
            teamMemberList.clear()
            eventViewDataCounter = 0
            attendingListDataCounter = 0
            attendeeListDone = false

            // IDs
            locationIDa = locationID
            eventIDa = eventID
            teamMemberListDone = false



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

                                if (host == null) {
                                    hasHost = false
                                }
                                if (host.id == uid) {
                                    hosting = true
                                }

                                val sfd = SimpleDateFormat("yyyy-MM-dd hh:mm")
                                val startTimeStamp: Timestamp =
                                    event.get("date") as Timestamp
                                val eventStartTime = sfd.format(Date(startTimeStamp.seconds * 1000))

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
                                        "",
                                        eventStartTime,
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
                                            attendeeListDone = true
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

                                                    if (attendee.id == uid) {
                                                        attending = true
                                                    }

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
                                                        toEventViewPage()
                                                    }
                                                }
                                        }
                                    }
                            }
                    }
            }
        }


        // attend button handling
        fun hostLeaveEvent() {
            val db = Firebase.firestore

            db.collection("Location").document(locationIDa).collection("Events").document(eventIDa)
                .update("hostID", "null")
            db.collection("User").document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .collection("Hosting")
                .whereEqualTo("eventID", eventIDa).get()
                .addOnSuccessListener { hosting ->
                    for (host in hosting) {
                        db.collection("User")
                            .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                            .collection("Hosting")
                            .document(host.id).delete()
                    }
                    reloadEventView()
                }
        }

        private fun reloadEventView() {
            val intent = Intent(eventViewContext, SplashLoadingEventView::class.java)
            intent.putExtra("eventID", eventIDa)
            intent.putExtra("locationID", locationIDa)
            context.startActivity(intent)
        }

        // make the current user the host
        fun makeHost() {
            val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
            val db = Firebase.firestore

            db.collection("Location").document(locationIDa).collection("Events").document(eventIDa)
                .update("hostID", uid)

            val hostingHashMap = hashMapOf(
                "locationID" to locationIDa,
                "eventID" to eventIDa
            )

            // add the hosting data to the user
            db.collection("User")
                .document(FirebaseAuth.getInstance().uid.toString())
                .collection("Hosting").document(eventIDa)
                .set(hostingHashMap, SetOptions.merge())
                .addOnSuccessListener {
                    reloadEventView()
                }
        }

        // check if the event has room
        // increase the number of people attending in event
        // add the user uid to the event's attending list
        // add the eventID to the user's attending list
        fun addAttendance() {
            val db = Firebase.firestore
            val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()


            db.collection("Location").document(locationIDa).collection("Events").document(eventIDa)
                .get()
                .addOnSuccessListener { doc ->
                    var numCurrentlyAttending = doc.get("currentlyAttending").toString().toInt()

                    Log.i("LOG_TAG", "$numCurrentlyAttending numCurrentlyAttending!!")
                    // check if the event has room
                    if (doc.get("eventSpace").toString().toInt() > numCurrentlyAttending) {
                        // increase the number of people attending in event

                        numCurrentlyAttending++
                        val data =
                            hashMapOf("currentlyAttending" to (numCurrentlyAttending).toString())
                        db.collection("Location").document(locationIDa).collection("Events")
                            .document(eventIDa)
                            .set(data, SetOptions.merge())

                        // add the user uid to the event's attending list
                        val attendeeHashMap = hashMapOf(
                            "userID" to FirebaseAuth.getInstance().uid.toString()
                        )

                        db.collection("Location").document(locationIDa).collection("Events")
                            .document(eventIDa).collection("Attendees").document(uid)
                            .set(attendeeHashMap, SetOptions.merge())
                            .addOnSuccessListener {
                                Log.d("CreatingEvent", "Created Attendee")

                                // add the eventID to the user's attending list
                                val attendingHashMap = hashMapOf(
                                    "locationID" to locationIDa,
                                    "eventID" to eventIDa
                                )
                                db.collection("User")
                                    .document(FirebaseAuth.getInstance().uid.toString())
                                    .collection("Attending").document(eventIDa)
                                    .set(attendingHashMap, SetOptions.merge())
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            eventViewContext,
                                            "Successfully registered for this event",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        reloadEventView()
                                    }


                            }
                            .addOnFailureListener { e ->
                                Log.w("a", "Error creating Attendee document", e)
                            }


                    } else {
                        Toast.makeText(
                            eventViewContext,
                            "This event is currently full!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        // -1 the event space
        // delete uid in the events attending list
        // delete event id in the users attending list
        fun removeAttendance() {
            val db = Firebase.firestore
            val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()


            // -1 the event space
            db.collection("Location").document(locationIDa).collection("Events").document(eventIDa)
                .get()
                .addOnSuccessListener { doc ->
                    var numCurrentlyAttending = doc.get("currentlyAttending").toString().toInt()
                    numCurrentlyAttending--
                    val data =
                        hashMapOf("currentlyAttending" to (numCurrentlyAttending).toString())
                    db.collection("Location").document(locationIDa).collection("Events")
                        .document(eventIDa)
                        .set(data, SetOptions.merge())
                        .addOnSuccessListener {

                            // delete uid in the events attending list
                            db.collection("Location").document(locationIDa).collection("Events")
                                .document(eventIDa).collection("Attendees")
                                .document(FirebaseAuth.getInstance().uid.toString())
                                .delete()
                                .addOnSuccessListener {
                                    // removing eventID from the users data
                                    db.collection("User").document(uid)
                                        .collection("Attending").document(eventIDa)
                                        .delete()
                                        .addOnFailureListener { e ->
                                            Log.w(
                                                "LOG_TAG",
                                                "Error removing attendance",
                                                e
                                            )
                                        }
                                }
                                .addOnFailureListener { e ->
                                    Log.w(
                                        "LOG_TAG",
                                        "Error removing attendance",
                                        e
                                    )
                                }
                                .addOnSuccessListener {
                                    reloadEventView()
                                }
                        }
                }
        }

        // to event list view
        private fun toEventViewPage() {
            if (attendeeListDone && teamMemberListDone) {
                val intent = Intent(eventViewContext, EventCompose::class.java)
                context.startActivity(intent)
            }
        }
    }
}