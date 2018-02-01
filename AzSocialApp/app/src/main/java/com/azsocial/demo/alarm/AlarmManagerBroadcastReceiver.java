package com.azsocial.demo.alarm;

/**
 * Created by prashant.chovatiya on 1/30/2018.
 */

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.PowerManager;

import com.azsocial.App;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {

    final public static String ONE_TIME = "onetime";
    Context mContext;


    String TAG = "===---AlarmManagerBroadcastReceiver---===";

    @Override
    public void onReceive(Context context, Intent intent) {

        App.showLog(TAG+"==onReceive==");

        mContext = context;

        if(mContext == null)
        {
            mContext = App.mContext;
        }


        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "YOUR TAG");
        //Acquire the lock
        wl.acquire();
        //You can do the processing here.
        Bundle extras = intent.getExtras();
        StringBuilder msgStr = new StringBuilder();

        if (extras != null && extras.getBoolean(ONE_TIME, Boolean.FALSE)) {
            //Make sure this intent has been sent by the one-time timer button.
            msgStr.append("One time Timer : ");
        }


        Format formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm a");

        msgStr.append(formatter.format(new Date()));

        String strCurrTime = msgStr.toString();

        //App.showLog2(TAG+"==onReceive Time ==" + strCurrTime);
        //App.showLog2(TAG+"==--1111--==strCurrTime=" + strCurrTime+"==App.strPrevTime=" + App.strPrevTime);

        if (strCurrTime.equalsIgnoreCase(App.strPrevTime)) {
            // App.showLog2(TAG+"==Same date Time == xxxx ==No need to call===");
            //App.showLog2(TAG+"==-- xxxxxxx 22222--==strCurrTime=" + strCurrTime+"==App.strPrevTime=" + App.strPrevTime);
        } else {
            App.strPrevTime = strCurrTime;
            //App.showLog2(TAG+"==Same date Time = yyyyy ===need to call===");

                checkDatabseDataForNotify(strCurrTime);

        }


        //Release the lock
        wl.release();
    }

    public void SetAlarm(Context context) {
        App.showLog("==SetAlarm==");
        mContext = context;
        //  strPrevTime = "";
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        intent.putExtra(ONE_TIME, Boolean.FALSE);

        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 20, alarmIntent);
    }

    public void CancelAlarm(Context context) {
        App.showLog("==CancelAlarm==");
        mContext = context;
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    public void setOnetimeTimer(Context context) {
        App.showLog("==setOnetimeTimer==");
        mContext = context;
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        intent.putExtra(ONE_TIME, Boolean.TRUE);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pi);
    }


    private void setLocalNotification(Context context, String date, String lat, String time) {
        try {
            mContext = context;
            App.showLog("==setLocalNotification==");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    DatabaseUtils db;

    @SuppressLint("LongLogTag")
    private void checkDatabseDataForNotify(String currentDateTime) {
        try {
            db = new DatabaseUtils(mContext);
            Cursor cursor = db.getNotificationList(DatabaseUtils.TABLE_NOTIFICATION);
            try {
                while (cursor.moveToNext()) {

                    String strKEY_nid = cursor.getString(cursor.getColumnIndex(DatabaseUtils.AlarmFields.KEY_nid));

                    String strKEY_ttl = cursor.getString(cursor.getColumnIndex(DatabaseUtils.AlarmFields.KEY_ttl));
                    String strKEY_img = cursor.getString(cursor.getColumnIndex(DatabaseUtils.AlarmFields.KEY_img));
                    String strKEY_category = cursor.getString(cursor.getColumnIndex(DatabaseUtils.AlarmFields.KEY_category));
                    String strKEY_start_date_time = cursor.getString(cursor.getColumnIndex(DatabaseUtils.AlarmFields.KEY_start_date_time));
                    String strKEY_end_date_time = cursor.getString(cursor.getColumnIndex(DatabaseUtils.AlarmFields.KEY_end_date_time));
                    String strKEY_event_alert = cursor.getString(cursor.getColumnIndex(DatabaseUtils.AlarmFields.KEY_event_alert));
                    String strKEY_event_repeat = cursor.getString(cursor.getColumnIndex(DatabaseUtils.AlarmFields.KEY_event_repeat));
                    String strKEY_stop_repeat = cursor.getString(cursor.getColumnIndex(DatabaseUtils.AlarmFields.KEY_stop_repeat));
                    String strKEY_isNotify = cursor.getString(cursor.getColumnIndex(DatabaseUtils.AlarmFields.KEY_isNotify));
                    String strKEY_isRead = cursor.getString(cursor.getColumnIndex(DatabaseUtils.AlarmFields.KEY_isRead));

                    App.showLog(TAG+"====Notify - strKEY_nid========" + strKEY_nid);
                    App.showLog(TAG+"====Notify - strKEY_start_date_time========" + strKEY_start_date_time);
                    App.showLog(TAG+"====Notify - strKEY_isNotify========" + strKEY_isNotify);



                }
                cursor.close();
            } finally {
                // cursor.close();
            }
        } catch (Exception e) {
            App.showLog(TAG+"====Notify Exception Alarm service checkDatabseDataForNotify ========");
            e.printStackTrace();
        }
    }
}
