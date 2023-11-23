package  com.mamits.apnaonlines.userv.fcm

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
import com.mamits.apnaonlines.userv.R
import com.mamits.apnaonlines.userv.model.LocationUtils
import com.mamits.apnaonlines.userv.ui.activity.LoginActivity
import com.mamits.apnaonlines.userv.ui.activity.MainActivity
import com.mamits.apnaonlines.userv.util.CommonUtils
import com.mamits.apnaonlines.userv.util.Constants
import com.mamits.apnaonlines.userv.utils.PreferenceUtils
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


class MyFirebaseMessagingService : FirebaseMessagingService() {

    var mPreferenceUtils: PreferenceUtils = PreferenceUtils()
    var id = ""
    var type = ""
    var title = ""
    var body = ""

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        token?.run {
            Log.d(TAG, "Refreshed token: $token")
            PreferenceUtils.getInstance(this@MyFirebaseMessagingService)
                .setValue(Constants.FCM_TOKEN, token)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        CommonUtils.printLog(
            "FCM_PUSHH_0",
            "Message data payload: " + remoteMessage?.notification?.title
        )

        mPreferenceUtils = PreferenceUtils.getInstance(this)

        try {
            remoteMessage?.notification?.title
            remoteMessage?.data?.isNotEmpty()?.let {

                id = remoteMessage.data["id"]!!
                type = remoteMessage.data["type"]!!

                if (type?.equals("block")!!) {
                    mPreferenceUtils?.clear()
                    LocationUtils.setCurrentLocation(null)
                    var mIntent = Intent(this, LoginActivity::class.java)
                    mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    mIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(mIntent)
                }
//                }

            }
            remoteMessage?.notification?.let {
                body = remoteMessage?.notification?.body!!
                title = remoteMessage?.notification?.title!!
            }

            pushNotification(id, type, body, title, "")

        } catch (e: Exception) {
            CommonUtils.printLog("FCM_PUSHH_EXCEPTION", "Message data payload: " + e?.message)
            e.printStackTrace()
        }
    }

    fun pushNotification(
        notificationId: String?, notificationType: String?,
        message: String?, title: String?, date: String?
    ) {
        var mIntent: Intent? = null
        if (notificationType?.equals("block")!!) {
            mIntent = Intent(this, LoginActivity::class.java)
        } else {
            mIntent = Intent(this, MainActivity::class.java)
        }
        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        when (notificationType) {
            "chat" -> {
                mIntent.putExtra("from", "NOTI_chat")
            }
            "order" -> {
                mIntent.putExtra("from", "NOTI_history")
            }
            "enquiry" -> {
                mIntent.putExtra("from", "NOTI_enquiry")
            }
            "home" -> {
//                mIntent.putExtra("from", )
            }
        }
        val mNotificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val notificationID = if (!notificationId.isNullOrEmpty())
            notificationId.toInt()
        else
            0

        val pendingIntent = PendingIntent.getActivity(
            this,
            notificationID,
            mIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val channelId = "channel"
        val mBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification_icon)
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
        mBuilder.setStyle(
            NotificationCompat.BigTextStyle().bigText(Html.fromHtml(message))
        ) //<<commented
//        }
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var channelId = "com.mamits.apnaonlines.user.urgent"
            var channelName = "Urgent";
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
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
