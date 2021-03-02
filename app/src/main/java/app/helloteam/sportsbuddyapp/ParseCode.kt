package app.helloteam.sportsbuddyapp

import com.parse.ParseObject

object ParseCode {
    fun EventCreation(se: SportEvents){

        val sportEvent = ParseObject("Event")
        sportEvent.put("eventType", se.type.sport)
        sportEvent.put("address", se.address)
        sportEvent.put("host", se.userName)
        sportEvent.save()

    }
}