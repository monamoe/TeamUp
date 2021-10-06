package app.helloteam.sportsbuddyapp.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {
    companion object{
        private val retrofit by lazy {
            Retrofit.Builder().baseUrl("https://fcm.googleapis.com").addConverterFactory(GsonConverterFactory.create()) //creates url for sending post
                .build()
        }

        val api by lazy {
            retrofit.create(NotificationAPI::class.java)
        }
    }
}