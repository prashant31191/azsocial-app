package com.azsocial.services;


import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.azsocial.App;
import com.azsocial.R;
import com.azsocial.activities.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    String TAG = "===MyFirebaseMsgService====";

    // Check if message contains a data payload.
    String notification_id = "", noti_uid = "", msg = "", uname = "", type = "", sid = "";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @SuppressLint("LongLogTag")
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ

        Log.d(TAG, "====Arrive notification======onMessageReceived=====");
        //Log.d(TAG, "From: " + remoteMessage.getFrom());


        //if (App.sharePrefrences.getStringPref("strUserId") != null && App.sharePrefrences.getStringPref(PreferencesKeys.strUserId).length() > 0) {
            sendNotificationData(remoteMessage.getData());

            sendNotification(remoteMessage.getData().get("msg"));

       /* } else {
            Log.d(TAG, "====Arrive notification======onMessageReceived==Logout user===");
        }*/

        //Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        // fcm - sendNotification(""+ remoteMessage.getNotification().getBody());

        //sendNotification(""+ remoteMessage.getNotification().getBody());
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody) {
        try {

            Intent intent = null;

            intent = new Intent(this, MainActivity.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT); //PendingIntent.FLAG_UPDATE_CURRENT)

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_insert_photo_white_18dp)
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent)
                    .setColor(0x60000000);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            int notifitionIdTime = (int) System.currentTimeMillis();
            notificationManager.notify(notifitionIdTime /* ID of notification */, notificationBuilder.build());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @SuppressLint("LongLogTag")
    private void sendNotificationData(Map<String, String> data) {
        try {

            if (data != null && data.size() > 0) {
                Log.d(TAG, "Message data payload: " + data);
                noti_uid = data.get("uid");
                msg = data.get("msg");
                type = data.get("type");
                uname = data.get("uname");

                if (data.get("s_id") != null) {
                    sid = data.get("s_id");
                }

                if (data.get("noti_id") != null) {
                    notification_id = data.get("noti_id");
                }

                if (noti_uid != null) {
                    App.showLog(TAG, "--==noti_uid==uid--" + noti_uid);
                }
                if (msg != null) {
                    App.showLog(TAG, "--msg--" + msg);
                }
                if (uname != null) {
                    App.showLog(TAG, "--uname--" + uname);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}