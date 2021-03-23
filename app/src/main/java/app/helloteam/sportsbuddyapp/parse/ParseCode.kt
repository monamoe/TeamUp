package app.helloteam.sportsbuddyapp.parse

import android.util.Log
import app.helloteam.sportsbuddyapp.models.SportEvents
import app.helloteam.sportsbuddyapp.models.SportLocation
import app.helloteam.sportsbuddyapp.models.User
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import java.util.*
import kotlin.collections.ArrayList


object ParseCode {

    fun EventCreation(se: SportEvents) {
        var sportEvent = ParseObject("Event")
        sportEvent.put("eventType", se.type.sport)
        sportEvent.put("host", se.userName)
        sportEvent.put("hour", se.hour)
        sportEvent.put("minute", se.minute)
        sportEvent.put("year", se.year)
        sportEvent.put("month", se.month)
        sportEvent.put("day", se.day)
        sportEvent.put("sportPlaceID", se.eventPlaceID)
        sportEvent.put("date", se.date)
        sportEvent.save()
    }

    fun LocationCreation(ec: SportLocation) {
        var sl = ParseObject("Location")
        sl.put("locationPlaceId", ec.locationPlaceID)
        sl.put("Name", ec.name)
        sl.put("Address", ec.address)
        sl.put("latitude", ec.lat)
        sl.put("longitude", ec.long)
        sl.put("amount", ec.amount)
        sl.save();
    }

    fun EventDeletion(today: Date) {
        var deletedEvents = ArrayList<String>()
        val query = ParseQuery.getQuery<ParseObject>("Event")
        query.whereLessThan("date", today)//get all events where today's date is after their date
        Log.i("LOG_TAG", "HAHA today's date:" + today)
        query.findInBackground { eventList, e ->
            if (e == null && eventList.size > 0) {
                Log.d(
                    "Event",
                    "Deleted: " + eventList.size + " events"
                )//print how many events will be deleted
                for (event in eventList) {//loop through the expired events
                    Log.i("LOG_TAG", "HAHA Events's date:" + event.getDate("date"))
                    val innerQuery = ParseQuery.getQuery<ParseObject>("Location")
                    innerQuery.whereEqualTo(
                        "locationPlaceId",
                        event.get("sportPlaceID")
                    )//get the event locations
                    innerQuery.limit = 1//should only find one location
                    val matches = innerQuery.find()
                    for (match in matches) {
                        if (match.getInt("amount") == 1) {
                            match.deleteInBackground()
                        } else {
                            match.put("amount", match.getInt("amount") - 1)
                        }
                        match.save()
                    }
                    event.deleteInBackground()
                    event.save()
                }
            } else {
                Log.d("Event", "Error: " + e)
            }
        }
        val query2 = ParseQuery.getQuery<ParseObject>("Location")
        for (event in deletedEvents) {
            query2.whereEqualTo("locationPlaceId", event.toString())
        }
    }

    fun UpdateProfile(givenUser: User) {
        val query = ParseUser.getQuery()
        query.whereEqualTo("username", givenUser.userName)
        query.findInBackground { users, e ->
            if (e == null) {
                for (user in users) {
                    user.put("aboutMe", givenUser.aboutMe)
                    user.put("favouriteSport", givenUser.favSport)
                    Log.i("LOG_TAG", "HAHA saving profile")
                    user.saveInBackground()
                }
            } else {
                Log.i("LOG_TAG", "HAHA profile error" + e.message)
            }
        }
    }

    // Event Attend Table ( keeps track of which event the user is attending)
    // Records Many to Many relationship between Event and Users table
    fun EventAttend(userId: Int, eventId: Int) {
        var a = ParseObject("Location")
        a.put("userID", userId)
        a.put("eventID", eventId)
        a.save()
    }


}