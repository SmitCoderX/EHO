package com.driver.eho.utils

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.driver.eho.R
import com.driver.eho.SharedPreferenceManager
import com.driver.eho.ui.activity.NotificationActivity
import com.driver.eho.utils.Constants.TAG
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.truffleuser.app.fcm.NotificationHelper
import org.json.JSONObject
import java.util.*

class MyFirebaseMessagingService : FirebaseMessagingService() {

    var message: String? = null
    var click_action: String? = null
    var intent: Intent? = null

    //    var prefs = SharedPreferenceManager(baseContext)
    override fun onNewToken(firebaseId: String) {
//        prefs.setFCMToken(firebaseId)
        SharedPreferenceManager(baseContext).setFCMToken(firebaseId)
        Log.d(TAG, "onNewToken: $firebaseId")

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        var data = remoteMessage.data
        message = data["message"]
        var notificationId = Random().nextInt(60000)
        if (remoteMessage.data.isNotEmpty()) {
            var data = remoteMessage.data
            var maindata_object = JSONObject(data as Map<String, String>)
            if (maindata_object.has("type")) {
                try {
                    /*  when {
                          maindata_object.getString("type")
                              .equals("DashBoardActivity", ignoreCase = true) -> {
                              message = remoteMessage.notification!!.body
                              click_action = remoteMessage.notification!!.clickAction
                              intent = Intent(maindata_object.getString("click_action"))
                              intent!!.putExtra("jasondata", maindata_object.toString())
                              intent!!.putExtra("type", "DashBoardActivity")
                              intent!!.putExtra("notification_id", notificationId)
                              intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                          }
                          else -> {*/
                    message = Objects.requireNonNull(remoteMessage.notification)?.body
                    click_action = remoteMessage.notification!!.clickAction
                    intent = Intent(applicationContext, NotificationActivity::class.java)
                    intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                        }
//                }
                } catch (e: Exception) {
                    e.message
                    e.printStackTrace()
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                showArrivedNotificationAPI26(message, notificationId)
            } else {
                showArrivedNotification(message, notificationId)
            }
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun showArrivedNotificationAPI26(body: String?, notificationId: Int) {
        try {
            val contentIntent = PendingIntent.getActivity(
                baseContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
                        or PendingIntent.FLAG_ONE_SHOT
            )
            val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationHelper = NotificationHelper(baseContext)
            val builder = notificationHelper.getNotification(
                getString(R.string.app_name),
                body,
                contentIntent,
                defaultSound
            )
            notificationHelper.manger!!.notify(notificationId, builder.build())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showArrivedNotification(body: String?, notificationId: Int) {
        val contentIntent = PendingIntent.getActivity(
            baseContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
                    or PendingIntent.FLAG_ONE_SHOT
        )
        val builder = NotificationCompat.Builder(baseContext)
        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        builder.setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_SOUND)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.brand_logo)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(500, 500))
            .setSound(defaultSound)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(body)
            .setContentIntent(contentIntent)
        val manager = baseContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId, builder.build())
    }
}