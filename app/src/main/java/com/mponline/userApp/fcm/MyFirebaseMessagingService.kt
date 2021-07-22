package  com.mponline.userApp.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.text.Html
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mponline.userApp.R
import com.mponline.userApp.ui.activity.MainActivity
import com.mponline.userApp.util.CommonUtils
import com.mponline.userApp.utils.Constants
import com.mponline.userApp.utils.PreferenceUtils
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


class MyFirebaseMessagingService : FirebaseMessagingService() {

    var mPreferenceUtils: PreferenceUtils = PreferenceUtils()

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        token?.run {
            Log.d(TAG, "Refreshed token: $token")
            PreferenceUtils.getInstance(this@MyFirebaseMessagingService).setValue(Constants.FCM_TOKEN, this)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        mPreferenceUtils = PreferenceUtils.getInstance(this)

        try {
            Log.d(TAG, "From: ${remoteMessage?.from}")
            remoteMessage?.data?.isNotEmpty()?.let {
                CommonUtils.printLog(TAG, "Message data payload: " + remoteMessage.data.toString())
                CommonUtils.printLog(TAG, "From: " + remoteMessage.from!!)

//                if (mPreferenceUtils.getValue(Constants.MOBILE_NO).isNotEmpty()) {
//                "{id=65, date=2019/06/14, text=Test's status mark as completed, type=0, title=Patient process completed}"
                val id: String? = remoteMessage.data["id"]
                val type: String? = remoteMessage.data["type"]
                val body: String? = remoteMessage.data["text"]
                val title: String? = remoteMessage.data["title"]
                val date: String? = remoteMessage.data["date"]

                pushNotification(id, "", body, title, "")

//                }

            }
            remoteMessage?.notification?.let {
                Log.d(TAG, "Message Notification Body: ${it.body}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun pushNotification(
        notificationId: String?, notificationType: String?,
        message: String?, title: String?, date: String?
    ) {

        val mIntent = Intent(this, MainActivity::class.java)
        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        mIntent.putExtra(Constants.FROM, "FCMCloudMessage")

        val mNotificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val notificationID = if (!notificationId.isNullOrEmpty())
            notificationId.toInt()
        else
            0


        val pendingIntent = PendingIntent.getActivity(this, notificationID, mIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val channelId = "channel"
        val mBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
//            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
            .setContentTitle(Html.fromHtml(title))
            .setContentText(Html.fromHtml(message)) // <<commented
            .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
            .setAutoCancel(true)
            .setLights(Color.MAGENTA, 500, 500)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(longArrayOf(1000))
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        mBuilder.setStyle(NotificationCompat.BigTextStyle().bigText(title))


//        if (projectImage != null && projectImage != "") {
//            val mNotificationDrawable = getBitmapFromURL(projectImage)
//            if (mNotificationDrawable != null) {
//                mBuilder.setStyle(
//                    NotificationCompat.BigPictureStyle().bigPicture(mNotificationDrawable).setSummaryText(Html.fromHtml(message))
//                )
//            }
//        } else {
            mBuilder.setStyle(NotificationCompat.BigTextStyle().bigText(Html.fromHtml(message))) //<<commented
//        }
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, "Projects Related", NotificationManager.IMPORTANCE_DEFAULT)
            mNotificationManager.createNotificationChannel(channel)
        }

        mBuilder.setContentIntent(pendingIntent)
        mNotificationManager.notify(notificationID, mBuilder.build())
    }


    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }

    fun getBitmapFromURL(strURL: String): Bitmap {
        return try {
            val url = URL(strURL)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.getInputStream()
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
            BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
        }

    }
}
