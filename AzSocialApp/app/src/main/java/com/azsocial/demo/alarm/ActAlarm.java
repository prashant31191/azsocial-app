package com.azsocial.demo.alarm;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;

import com.azsocial.App;
import com.azsocial.R;
import com.azsocial.activities.BaseActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by prashant.chovatiya on 1/30/2018.
 */

public class ActAlarm extends BaseActivity {
    @BindView(R.id.btn_click_me)
    Button btn_click_me;

    AlarmListResponse mAlarmListResponse = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_news);
        ButterKnife.bind(this);


        btn_click_me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                asyncGetAlarmList();
            }
        });
    }


    private void asyncGetAlarmList() {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);


            OkHttpClient httpClient = new OkHttpClient();
            String url = "http://asdasd.com/ws.asdasd.php?op=asdasd&swd=sw&aaa=asdasd&asa=&pageno=0";
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            //httpClient.newCall(request).enqueue(new Callback() {
            App.getClient().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    App.showLog("error in getting response using async okhttp call");

                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    final ResponseBody responseBody = response.body();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (!response.isSuccessful()) {
                                    throw new IOException("Error response " + response);
                                }

                                String result = responseBody.string();
                                App.showLog("=result==" + result);

                                if (result != null) {
                                    App.showLog("==result==" + result.toString());

                                    Gson gson = new GsonBuilder().create();
                                    mAlarmListResponse = gson.fromJson(result.toString(), AlarmListResponse.class);

                                    insertToDatabase();

                                }

                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                    });


                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void insertToDatabase() {
        try {
            if (mAlarmListResponse != null) {

              /*  Date curDate = new Date();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
                String DateToStr = format.format(curDate);
                System.out.println(DateToStr);*/

                DatabaseUtils databaseUtils = new DatabaseUtils(this);


                if (mAlarmListResponse.arrayListAlarmData != null && mAlarmListResponse.arrayListAlarmData.size() > 0) {

                    databaseUtils.deleteAll(databaseUtils.TABLE_NOTIFICATION);
                    App.startAlarmServices(ActAlarm.this);

                    Intent intent = new Intent();
                    intent.setAction("NOTIFICATION_SERVICE");
                    sendBroadcast(intent);

                    for (int i = 0; i < mAlarmListResponse.arrayListAlarmData.size(); i++) {
                        AlarmData alarmData = mAlarmListResponse.arrayListAlarmData.get(i);

/*
                      public static String KEY_nid = "KEY_nid";
                      public static String KEY_ttl = "KEY_ttl";
                      public static String KEY_img = "KEY_img";
                      public static String KEY_category = "KEY_category";

                      public static String KEY_start_date_time = "KEY_start_date_time";
                      public static String KEY_end_date_time = "KEY_end_date_time";

                      public static String KEY_event_alert = "KEY_event_alert";
                      public static String KEY_event_repeat = "KEY_event_repeat";

                      public static String KEY_stop_repeat = "KEY_stop_repeat";

                      public static String KEY_isNotify = "KEY_isNotify";
                      public static String KEY_isRead = "KEY_isRead";

                      */

                        if (isValidDate(alarmData.event_end) == true) {

                            Hashtable<String, String> hashtableData = new Hashtable<>();

                            hashtableData.put(DatabaseUtils.AlarmFields.KEY_nid, "" + i);
                            hashtableData.put(DatabaseUtils.AlarmFields.KEY_ttl, alarmData.title);
                            hashtableData.put(DatabaseUtils.AlarmFields.KEY_img, alarmData.category_image);
                            hashtableData.put(DatabaseUtils.AlarmFields.KEY_category, alarmData.category);
                            hashtableData.put(DatabaseUtils.AlarmFields.KEY_start_date_time, alarmData.event_start);
                            hashtableData.put(DatabaseUtils.AlarmFields.KEY_end_date_time, alarmData.event_end);

                            Random random = new Random();
                            int iRadndomMinute = random.nextInt(5);
                            int iRadndomDays = random.nextInt(2);

                            App.showLog("======iRadndomMinute===" + iRadndomMinute);
                            App.showLog("====iRadndomDays=====" + iRadndomDays);

                            hashtableData.put(DatabaseUtils.AlarmFields.KEY_event_alert, "" + iRadndomMinute);
                            hashtableData.put(DatabaseUtils.AlarmFields.KEY_event_repeat, "" + iRadndomDays);

                            hashtableData.put(DatabaseUtils.AlarmFields.KEY_stop_repeat, alarmData.stop_repeat); // not used

                            hashtableData.put(DatabaseUtils.AlarmFields.KEY_isNotify, "0"); // used external
                            hashtableData.put(DatabaseUtils.AlarmFields.KEY_isRead, "0"); // used external


                            databaseUtils.insertRecord(hashtableData, databaseUtils.TABLE_NOTIFICATION);

                        } else {
                            App.showLog("====Expired==event_end===" + alarmData.event_end);
                        }
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private boolean isValidDate(String valid_until) {
        try {
            //start -  2018-01-30 14:26:00
            // end - 2018-02-02 11:45:00

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date strDate = sdf.parse(valid_until);
            if (System.currentTimeMillis() < strDate.getTime()) {
                return true;
            }
            return false;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}
