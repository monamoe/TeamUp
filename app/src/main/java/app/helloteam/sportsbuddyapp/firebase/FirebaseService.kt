package app.helloteam.sportsbuddyapp.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import app.helloteam.sportsbuddyapp.R
import app.helloteam.sportsbuddyapp.views.TeamInvites
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

private const val CHANNEL_ID = "my_channel"

class FirebaseService : FirebaseMessagingService(){

    override fun onMessageReceived(message: RemoteMessage){// when user get notif
        super.onMessageReceived(message)

        val intent = Intent(this, TeamInvites::class.java)// set intent that notification will take user too
        val notificationManger = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt()

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){ // if phone is at least oreo we need to make a notif channel
            createNotificationChannel(notificationManger)
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)//clear other open intents

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_ONE_SHOT)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(message.data["title"]) // set notif title
            .setContentText(message.data["message"]) //set notif body
            .setSmallIcon(R.drawable.logoteamupsmall) //set notif image
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManger.notify(notificationID, notification)
    }

    private fun createNotificationChannel(notificationManager: NotificationManager){
        val channelName = "channelName"
        val channel = NotificationChannel(CHANNEL_ID, channelName, IMPORTANCE_HIGH).apply {
            description = "My channel description"
            enableLights(true)
            lightColor = Color.GREEN
        }
        notificationManager.createNotificationChannel(channel)
    }
}