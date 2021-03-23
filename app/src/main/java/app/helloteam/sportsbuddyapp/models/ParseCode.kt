package app.helloteam.sportsbuddyapp.models

import app.helloteam.sportsbuddyapp.models.SportEvents
import app.helloteam.sportsbuddyapp.models.SportLocation
import com.parse.ParseObject
import java.util.*


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

        sl.save()
    }

    // Event Attend Table ( keeps track of which event the user is attending)
    // Records Many to Many relationship between Event and Users table
    fun EventAttend(userId: Int, eventId: Int) {
        var a = ParseObject("Location")
        a.put("userID", userId)
        a.put("eventID", eventId)
        a.save()
    }

    fun EventDeletion(today: Date) {
        /*  val query = ParseQuery.getQuery<ParseObject>("Event")
          query.whereLessThan("date", today)
          query.findInBackground { eventList, e ->
              if (e == null&& eventList.size>0) {
                  Log.d("Event", "Deleted: " + eventList.size + " events")
                  for (event in eventList){
                      event.deleteInBackground()
                      event.save()
                  }
              } else {
                  Log.d("Event", "Error: " + e)
              }
          }*/
    }
}