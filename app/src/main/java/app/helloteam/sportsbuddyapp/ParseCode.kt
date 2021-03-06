package app.helloteam.sportsbuddyapp

import android.util.Log
import com.parse.ParseObject

object ParseCode {


    fun EventCreation(se: SportEvents) {
        val sportEvent = ParseObject("Event")
        sportEvent.put("eventType", se.type.sport)
        sportEvent.put("host", se.userName)
        sportEvent.put("hour", se.hour)
        sportEvent.put("minute", se.minute)
        sportEvent.put("year", se.year)
        sportEvent.put("month", se.month)
        sportEvent.put("day", se.day)
        sportEvent.save()

    }

    fun LocationCreation(ec: SportLocation) {
        val sl = ParseObject("Location")
        sl.put("Name", ec.name)
        sl.put("Address", ec.address)
        sl.put("latitude", ec.lat)
        sl.put("longitude", ec.long)

        sl.save();
        Log.i("LOG_TAG", "HAHA: saved")

    }
}