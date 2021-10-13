package app.helloteam.sportsbuddyapp.models

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import app.helloteam.sportsbuddyapp.helperUI.EventCard


class EventViewModel : ViewModel() {

    /*This variable shall store the photo,
    Which I plan to use as an ID for the Profile Card.*/
    var selectedEvent by mutableStateOf(
        EventCard(
            "AAA",
            "AAA",
            "AAA",
            "AAA",
            false,
            "AAA",
            "AAA",
            333,
            111
        )
    )
        private set


    fun onSelectedEvent(event: EventCard) {
        selectedEvent = event
        Log.i(
            "LOG_TAG",
            "VIEW EVENT: UPDATED SELECTED EVENT ${event.eventID} - ${selectedEvent.eventID}"
        )
    }
}