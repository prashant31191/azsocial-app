package com.azsocial.demo.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.azsocial.App;

/**
 * Created by prashant.chovatiya on 1/30/2018.
 */

public class ReceiverRunOnStartup extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            /*Intent i = new Intent(context, ActSplashScreen.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);*/
              App.showLog("=======Phone start up=Start alarm--app-===");
            App.startAlarmServices(context);
        }
    }

}