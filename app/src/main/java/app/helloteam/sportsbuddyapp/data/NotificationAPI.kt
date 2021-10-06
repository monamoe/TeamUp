package app.helloteam.sportsbuddyapp.data

import app.helloteam.sportsbuddyapp.R
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationAPI {

    @Headers("Authorization: key=AAAAw5w1h4Q:APA91bF6U2DSgs3F2jBBrEybDT6TBirRXaA1W2XVSM8i_hLUOmDQRpnUG9hmtvjI7LelKcvnPHZmemkLz68W5RIANchB_0jgmJ1XilgNGyZouC-x0U505r6AC2Il0JZSOkX9kQcoHFSD", "Content-Type:application/json") // hide key
    @POST("fcm/send")
    suspend fun postNotification( //creates post request to send notif via firebase server
        @Body notification: PushNotification
    ): Response<ResponseBody>
}