package app.helloteam.sportsbuddyapp.data

import app.helloteam.sportsbuddyapp.R
import android.content.res.Resources.getSystem
import com.google.common.io.Resources
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationAPI {
    @Headers("Authorization: key=${Constants.server_key}", "Content-Type:application/json") // hide key
    @POST("fcm/send")
    suspend fun postNotification( //creates post request to send notif via firebase server
        @Body notification: PushNotification
    ): Response<ResponseBody>
}