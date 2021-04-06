package app.helloteam.sportsbuddyapp

import android.app.Application
import android.widget.Toast
import com.parse.Parse
import com.parse.ParseACL
import com.parse.ParseObject
import com.parse.ParseUser

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
////////////////////////////////////////sample code for using parse///////////////////////
/* PUSH TO SERVER
     val  score:ParseObject =  ParseObject("Score")
       score.put("username", "Alyssa")
       score.put("score", 6)
       score.saveInBackground( SaveCallback {
           fun done(ex: ParseException?) {
               if (ex == null) {
                   Log.i("Parse Result", "Successful!")
               } else {
                   Log.i("Parse Result", "Failed")
               }
           }
       })*/



/* PULL From SERVER
 val query2 = ParseQuery.getQuery<ParseObject>("Score")

 query2.getInBackground("miFCS2Om7e", GetCallback<ParseObject> { parseObject: ParseObject?, parseException: com.parse.ParseException? ->
         if (parseException == null) {
             var user: String? = parseObject?.getString("username")
             if (user != null) {
                 val userText = findViewById<TextView>(R.id.UserText)
                 userText.text = user
             }
         } else {
             val userText = findViewById<TextView>(R.id.UserText)
             userText.text = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"+ parseException
         }


 })*/


/*UPDATING/Pulling
val query2 = ParseQuery.getQuery<ParseObject>("Score")

 query2.getInBackground("miFCS2Om7e", GetCallback<ParseObject> { parseObject: ParseObject?, parseException: com.parse.ParseException? ->
     if (parseException == null) {
         if (parseObject != null) {
             parseObject.put("score", 82)
             parseObject.saveInBackground()
         }
         var user: String? = parseObject?.getString("username")
         var score: Int? = parseObject?.getInt("score")

         if (user != null ) {
             val userText = findViewById<TextView>(R.id.UserText)
             userText.text = user+" "+score
         }
     } else {
         val userText = findViewById<TextView>(R.id.UserText)
         userText.text = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"+ parseException
     }


 })*/

/* UPDATE ALL
 val query = ParseQuery.getQuery<ParseObject>("Score")

 query.findInBackground(object : FindCallback<ParseObject?> {
     override fun done(objects: MutableList<ParseObject?>?, e: com.parse.ParseException?) {
         if (objects != null) {
             if (e == null && objects.size>0) {
                 for (score in objects) {
                     if (score != null) {
                         score.put("score", score.getInt("score") + 20)

                         score.saveInBackground()
                     }
                 }
             }
         }
     }


 })
*/

/*UPDATE ONLY SPECIFIC USERNAME
val query = ParseQuery.getQuery<ParseObject>("Score")

query.whereEqualTo("username", "Alyssa")
query.setLimit(1)
query.findInBackground(object : FindCallback<ParseObject?> {
    override fun done(objects: MutableList<ParseObject?>?, e: com.parse.ParseException?) {
        if (objects != null) {
            if (e == null && objects.size>0) {
                for (score in objects) {
                    if (score != null) {
                        score.put("score", score.getInt("score") + 20)

                        score.saveInBackground()
                    }
                }
            }
        }
    }


})*/

/*DELETE ROW
val query = ParseQuery.getQuery<ParseObject>("Score")

query.whereEqualTo("username", "Bob")
query.setLimit(1)
query.findInBackground(object : FindCallback<ParseObject?> {
    override fun done(objects: MutableList<ParseObject?>?, e: com.parse.ParseException?) {
        if (objects != null) {
            if (e == null && objects.size>0) {
                for (score in objects) {
                    if (score != null) {
                        score.deleteInBackground()
                        score.saveInBackground()
                    }
                }
            }
        }
    }


})*/


/*   SIGNUP!!!!!!!!
val user = ParseUser()
user.username = "my name"
user.setPassword("my pass")
user.email = "email@example.com"

// other fields can be set just like with ParseObject

// other fields can be set just like with ParseObject
user.put("phone", "650-253-0000")

user.signUpInBackground { e ->
    if (e == null) {
        // Hooray! Let them use the app now.
    } else {
        // Sign up didn't succeed. Look at the ParseException
        // to figure out what went wrong
    }
}*/


/*  LOGINNNNNNNNNNNNNNNNNNNNN
ParseUser.logInInBackground("my name", "my pass") { user, e ->
      if (user != null) {
          val userText = findViewById<TextView>(R.id.UserText)
          userText.text = user.username
      } else {
          val userText = findViewById<TextView>(R.id.UserText)
          userText.text = "not found"          }
  }*/


/* GET CURRENT USERS
val currentUser = ParseUser.getCurrentUser()
if (currentUser != null) {
    val userText = findViewById<TextView>(R.id.UserText)
    userText.text = currentUser.username
} else {
    // show the signup or login screen
}*/
