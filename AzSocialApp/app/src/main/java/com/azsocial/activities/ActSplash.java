package com.azsocial.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.azsocial.App;
import com.azsocial.R;
import com.azsocial.utils.PreferencesKeys;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;

//import com.google.firebase.crash.FirebaseCrash;


public class ActSplash extends Activity

{


    String TAG = "==ActSplacescreen==";
    private String mapResourcesDirPath;

    private static int TIME = 5000;

    App app;


    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseAuth mFirebaseAuth;

    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);

        try {
            app = (App) getApplicationContext();
            App.showLog(TAG);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.act_splash);

            // Initialize FirebaseAuth
            mFirebaseAuth = FirebaseAuth.getInstance();
            // Obtain the FirebaseAnalytics instance.
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

            initialization();

            String refreshedToken = App.sharePrefrences.getStringPref(PreferencesKeys.strDeviceId);
            App.showLog("====refreshedToken===device token===" + refreshedToken);
            if (refreshedToken != null && refreshedToken.length() > 5) {
                TIME = 2000;
            } else {
                getDeviceToken();
            }



            setSendDataAnalytics();
            //setSendCrashData();
            displaySplash();



        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

    }

    // Initialize local variables
    private void initialization() {
        try {
           // TextView tvTag = (TextView) findViewById(R.id.tvTag);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Get Device token - Function
    private void getDeviceToken() {
        try {
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            App.showLog(TAG, "Refreshed token: " + refreshedToken);
            App.showLog(TAG, "InstanceID token: " + FirebaseInstanceId.getInstance().getToken());
            if (refreshedToken != null && refreshedToken.length() > 5) {
                App.sharePrefrences.setPref(PreferencesKeys.strDeviceId, refreshedToken);
                TIME = 2000;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // splash screen set with timing
    private void displaySplash() {
        // TODO Auto-generated method stub b

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub

                Intent iv = new Intent(ActSplash.this, MainActivity.class);
                    //iv = new Intent(ActSplash.this, ActMapDemo.class);
                startActivity(iv);
                finish();

            }
        }, TIME);
    }

    private void setSendDataAnalytics() {
        try {
            App.showLog(TAG, "---FirebaseAnalytics.Event.SELECT_CONTENT------");

            mFirebaseAnalytics.setUserProperty("favorite_food", "AppOpen");

            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "111");
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "AznewsApp");
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "text");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            App.showLog(TAG, "---FirebaseAnalytics.Event.SHARE------");
            Bundle bundle2 = new Bundle();
            bundle2.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "aznews article");
            bundle2.putString(FirebaseAnalytics.Param.ITEM_ID, "az333");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, bundle);

            mFirebaseAnalytics.setCurrentScreen(this,"Splash","setSendDataAnalytics");
        } catch (Exception e) {
            App.showLog(TAG, "---setSendDataAnalytics-Error send analytics--");
            e.printStackTrace();
        }
    }

    /*private void setSendCrashData() {
        App.showLog(TAG, "---setSendCrashData--");
        FirebaseCrash.logcat(Log.ERROR, TAG, "crash caused");
        FirebaseCrash.report(new Exception("My first Android non-fatal error"));
        FirebaseCrash.log("Activity created");

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "id_a311");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "name_prince");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }*/


}
