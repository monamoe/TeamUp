package app.helloteam.sportsbuddyapp.parse

import android.app.Application
import com.parse.Parse
import com.parse.ParseACL

class ParseSetup : Application() {
    override fun onCreate() {
        super.onCreate()
        Parse.initialize(/////////////setting up the server
            Parse.Configuration.Builder(this)
                .applicationId("myappID") // if defined
                .clientKey("fayQhWEA7At2")
                .server("http://52.60.57.18/parse/")
                .build()
        )

        val defaultACL = ParseACL()
        defaultACL.publicReadAccess = true
        defaultACL.publicWriteAccess = true
        ParseACL.setDefaultACL(defaultACL, true)
    }
}