package app.helloteam.sportsbuddyapp

import android.app.Application
import com.parse.Parse
import com.parse.ParseACL
import com.parse.ParseObject
import com.parse.ParseUser

class ParseCode : Application() {
    override fun onCreate() {
        super.onCreate()
        Parse.initialize(
            Parse.Configuration.Builder(this)
                .applicationId("myappID") // if defined
                .clientKey("fayQhWEA7At2")
                .server("http://52.60.57.18/parse/")
                .build()
        )
      /*Testing Code
       val gameScore = ParseObject("GameScore")
        gameScore.put("score", 2)
        gameScore.put("playerName", "Riley Gray")
        gameScore.put("cheatMode", true)
        gameScore.saveInBackground()*/

        ParseUser.enableAutomaticUser()

        val defaultACL = ParseACL()
        defaultACL.publicReadAccess = true
        defaultACL.publicWriteAccess = true
        ParseACL.setDefaultACL(defaultACL, true)
    }
}