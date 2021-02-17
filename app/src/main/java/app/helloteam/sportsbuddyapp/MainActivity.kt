package app.helloteam.sportsbuddyapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.parse.FindCallback
import com.parse.ParseAnalytics
import com.parse.ParseObject
import com.parse.ParseQuery
import java.text.ParseException


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

        ParseAnalytics.trackAppOpenedInBackground(getIntent());


    }
}