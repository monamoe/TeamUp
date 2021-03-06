package app.helloteam.sportsbuddyapp

import android.util.Log
import com.parse.ParseObject

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
        sportEvent.save()
    }

    fun LocationCreation(ec: SportLocation) {
        var sl = ParseObject("Location")
        sl.put("locationPlaceId", ec.locationPlaceID)
        sl.put("Name", ec.name)
        sl.put("Address", ec.address)
        sl.put("latitude", ec.lat)
        sl.put("longitude", ec.long)

        sl.save();
    }
}