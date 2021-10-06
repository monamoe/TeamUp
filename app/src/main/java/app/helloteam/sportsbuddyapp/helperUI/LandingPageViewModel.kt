package app.helloteam.sportsbuddyapp.helperUI

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class LandingPageViewModel : ViewModel() {

    val eventList: MutableState<List<EventCard>> = mutableStateOf(listOf())

    private val db = Firebase.firestore

    init {
        viewModelScope.launch {

            val userID = FirebaseAuth.getInstance().currentUser?.uid.toString()

            var eventListTemp: MutableList<EventCard> = mutableListOf<EventCard>()



            // event list
            db.collection("User").document(userID)
                .get()
                .addOnSuccessListener { users ->

                    // hosting
                    db.collection("User").document(userID).collection("Hosting")
                        .get()
                        .addOnSuccessListener { hosting ->
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
                                                    eventListTemp.add(
                                                        EventCard(
                                                            event.get("title")
                                                                .toString(),
                                                            host.get("eventID")
                                                                .toString(),
                                                            loc.get("StreetView")
                                                                .toString(),
                                                            true,
                                                            users.get("userName")
                                                                .toString(),
                                                            "Playing soccer with a couple friends, feel free to join in",
                                                            event.get("eventSpace")
                                                                .toString()
                                                                .toInt(),
                                                            event.get("currentlyAttending")
                                                                .toString()
                                                                .toInt(),
                                                        )
                                                    )
                                                    eventList.value = eventListTemp

                                                    Log.i(
                                                        "LOG_TAG",
                                                        "EVENT DISPLAY: \t\t eventListTemp ${eventListTemp}"
                                                    )
                                                }
                                        }
                                    }
                            }
                        }


                    // attending
                    db.collection("User").document(userID).collection("Attending")
                        .get()
                        .addOnSuccessListener { hosting ->
                            for (host in hosting) {
                                db.collection("Location")
                                    .document(host.get("locationID").toString())
                                    .collection("Events")
                                    .document(host.get("eventID").toString())
                                    .get()
                                    .addOnSuccessListener { event ->

                                        db.collection("Location")
                                            .document(
                                                host.get("locationID").toString()
                                            )
                                            .get()
                                            .addOnSuccessListener { loc ->
                                                eventListTemp.add(
                                                    EventCard(
                                                        event.get("title")
                                                            .toString(),
                                                        host.get("eventID")
                                                            .toString(),
                                                        loc.get("StreetView")
                                                            .toString(),
                                                        false,
                                                        users.get("userName")
                                                            .toString(),
                                                        "Playing soccer with a couple friends, feel free to join in",
                                                        event.get("eventSpace")
                                                            .toString()
                                                            .toInt(),
                                                        event.get("currentlyAttending")
                                                            .toString()
                                                            .toInt(),
                                                    )
                                                )
                                                eventList.value = eventListTemp
                                            }
                                    }
                            }
                        }
                }

            Log.i("LOG_TAG", "EVENT DISPLAY: GOT VALUES FROM DB:  ${eventList.value}")
            // end event list


            eventList.value = eventListTemp
        }
    }
}


