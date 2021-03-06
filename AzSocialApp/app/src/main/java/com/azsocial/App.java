package com.azsocial;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.multidex.MultiDex;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.azsocial.api.ApiService;
import com.azsocial.demo.alarm.AlarmManagerBroadcastReceiver;
import com.azsocial.api.model.ArticlesModel;
import com.azsocial.utils.AdsUtils;
import com.azsocial.utils.SharePrefrences;
import com.cjj.MaterialRefreshLayout;
import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.fabric.sdk.android.Fabric;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by prashant.chovatiya on 1/12/2018.
 */

public class App extends Application {

    private static App mInstance;
    private static final String TAG = App.class.getSimpleName();

    // fullscreen
    public static boolean blnFullscreenActvitity = false;

    public static String strBaseHostUrl = "https://newsapi.org/v2/";

    public static String ADS_APP_MAIN_ACC_ID = "ca-app-pub-5194219019183767~3956858209";

    public static String ADS_APP_bnr_1 = "ca-app-pub-5194219019183767/3453816390";
    public static String ADS_APP_bnr_2 = "ca-app-pub-5194219019183767/3262244705";
    public static String ADS_APP_bnr_3 = "ca-app-pub-5194219019183767/9444509678";
    public static String ADS_APP_bnr_4 = "ca-app-pub-5194219019183767/9448379100";
    public static String ADS_APP_bnr_5 = "ca-app-pub-5194219019183767/9252937987";

    public static String ADS_APP_int_1 = "ca-app-pub-5194219019183767/2687529633";
    public static String ADS_APP_int_2 = "ca-app-pub-5194219019183767/2882970757";

    public static String ADS_APP_rwid_1 = "ca-app-pub-5194219019183767/6243631263";
    public static String ADS_APP_rwid_2 = "ca-app-pub-5194219019183767/8678222914";


    // runing
    /*public static String ADS_APP_MAIN_ACC_ID = "ca-app-pub-5194219019183767~4506849787";
    public static String ADS_APP_bnr_1 = "ca-app-pub-5194219019183767/2052076832";
*/



/*
//Live - 7878
    public static String ADS_APP_MAIN_ACC_ID = "ca-app-pub-4346653435295459~2016282424";
    public static String ADS_APP_bnr_1 = "ca-app-pub-4346653435295459/4925872012";

//Testing - we wer
 public static String ADS_APP_MAIN_ACC_ID = "ca-app-pub-5194219019183767~4506849787";
    public static String ADS_APP_bnr_1 = "ca-app-pub-5194219019183767/2052076832";

*/


    public static String FlurryApiKey = "JYQHHN34N2H6P6KR48RD";
    public static String PREF_NAME = "azsocial_app";
    public static String DB_NAME = "azsocial.db";
    /* public static String DB_PATH = "/data/data/" + "com.azsocial" + "/databases/";*/

    // app folder name
    public static String APP_FOLDERNAME = ".azsocial";
    public static String strPrevTime = "";
    public static String strNewsApiKey = App.getNewsApiKey();

    //for the realm database encryption and decryption key
    public static String RealmEncryptionKey = "f263575e7b00a977a8e915feb9bfb2f992b2b8f11eaaaaaaa46523132131689465413132132165469487987987643545465464abbbbbccdddffff111222333";
    public static RealmConfiguration realmConfiguration;

    // class for the share pref keys and valyes get set
    public static SharePrefrences sharePrefrences;

    // for the app context
    public static Context mContext;

    // application on create methode for the create and int base values
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        try {
            MultiDex.install(this);
            mContext = getApplicationContext();
            sharePrefrences = new SharePrefrences(App.this);
            MobileAds.initialize(this, App.ADS_APP_MAIN_ACC_ID);

            Realm.init(this);
            Fabric.with(this, new Crashlytics());
            realmConfiguration = getRealmConfiguration();

        /*    new FlurryAgent.Builder()
                    .withLogEnabled(true)
                    .withCaptureUncaughtExceptions(true)
                    .withContinueSessionMillis(10)
                    .withLogLevel(VERBOSE)
                    .build(this, FLURRY_API_KEY);*/

            new FlurryAgent.Builder()
                    .withLogEnabled(true)
                    .withCaptureUncaughtExceptions(true)
                    .build(this, FlurryApiKey);

            AdsUtils.initListener();
            createAppFolder();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }


    // FOR THE DATABASE

    public static String getNewsApiKey() {
        String strKey[] =
                {
                        "3f7d2ffdeae948ae9e60ce5fd7d22743",
                        "462f5f3ede2841408e9ef575919befe5",
                        "515d94b5c06e40db842c0dac3397c955",
                        "3f7d2ffdeae948ae9e60ce5fd7d22743",
                        "462f5f3ede2841408e9ef575919befe5",
                        "515d94b5c06e40db842c0dac3397c955",
                        "3f7d2ffdeae948ae9e60ce5fd7d22743",
                        "462f5f3ede2841408e9ef575919befe5",
                        "515d94b5c06e40db842c0dac3397c955",
                        "3f7d2ffdeae948ae9e60ce5fd7d22743",
                        "462f5f3ede2841408e9ef575919befe5",
                        "515d94b5c06e40db842c0dac3397c955"
                };


        return getRandomFromStringArr(strKey);

    }

    public static String getRandomBannerId() {
        String strArr[] =
                {
                        App.ADS_APP_bnr_1,
                        App.ADS_APP_bnr_2,
                        App.ADS_APP_bnr_3,
                        App.ADS_APP_bnr_4,
                        App.ADS_APP_bnr_5,


                        App.ADS_APP_bnr_1,
                        App.ADS_APP_bnr_2,
                        App.ADS_APP_bnr_3,
                        App.ADS_APP_bnr_4,
                        App.ADS_APP_bnr_5,

                        App.ADS_APP_bnr_1,
                        App.ADS_APP_bnr_2,
                        App.ADS_APP_bnr_3,
                        App.ADS_APP_bnr_4,
                        App.ADS_APP_bnr_5

                };


        //return App.ADS_APP_bnr_1;
        return getRandomFromStringArr(strArr);

    }


    public static String getRandomFromStringArr(String[] strArr) {
        String strRandom = strArr[new Random().nextInt(strArr.length)];
        return strRandom;
    }


    public static RealmConfiguration getRealmConfiguration() {
        if (realmConfiguration != null) {
            return realmConfiguration;
        } else {
/*

            realmConfiguration = new RealmConfiguration.Builder()
                    .encryptionKey(App.getEncryptRawKey())
                    .build();
*/


            realmConfiguration = new RealmConfiguration.Builder()
                    .deleteRealmIfMigrationNeeded()
                    .encryptionKey(App.getEncryptRawKey())
                    .build();


            return realmConfiguration;
        }
    }


    // for the encrypt Encrypt
    public static byte[] getEncryptRawKey() {

        try {
            /*byte[] bytes64Key = App.RealmEncryptionKey.getBytes("UTF-8");
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            sr.setSeed(bytes64Key);
            kgen.init(128, sr);
            SecretKey skey = kgen.generateKey();
            byte[] raw = skey.getEncoded();*/

            byte[] key = new BigInteger(App.RealmEncryptionKey, 16).toByteArray();
            return key;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static boolean checkDbFileIsExist() {
        try {
            App.showLog("=======checkDbFileIsExist=====");


            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + "download.realm");
            if (file.exists()) {
                //Do something
                return true;
            } else {
                // Do something else.
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteFile() {
        App.showLog("=======deleteFile=====");

        try {

            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + "download.realm");
            if (file.exists()) {
                //Do something
                file.delete();

                return true;
            } else {
                // Do something else.
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public static boolean renameFile(File from, File to) {
        return from.getParentFile().exists() && from.exists() && from.renameTo(to);
    }


    public static void showSnackBar(View view, String strMessage) {
        Snackbar snackbar = Snackbar.make(view, strMessage, Snackbar.LENGTH_SHORT);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.BLACK);
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    public static void showSnackBarLong(View view, String strMessage) {
        Snackbar.make(view, strMessage, Snackbar.LENGTH_LONG).show();
    }


    public static void showLog(String strMessage) {
        App.showLog("==App==", "--strMessage--" + strMessage);
    }


    public static void showLogApi(String strMessage) {
        //Log.v("==App==", "--strMessage--" + strMessage);
        App.showLog("==App==", "--API-MESSAGE--" + strMessage);

        //  appendLogApi("c_api", strMessage);
    }

    public static void showLogApi(String strOP, String strMessage) {
        App.showLog("==App=strOP=" + strOP, "--strMessage--" + strMessage);
//        System.out.println("--API-strOP--" + strOP);
        //      System.out.println("--API-MESSAGE--" + strMessage);

        // appendLogApi(strOP + "_c_api", strMessage);
    }
/*

    public static void showLogApiRespose(String op, Response response) {
        //Log.w("=op==>" + op, "response==>");
        String strResponse = new Gson().toJson(response.body());
        App.showLog("=op==>" + op, "response==>" + strResponse);
        // appendLogApi(op + "_r_api", strResponse);
    }
*/


    public static void showLogResponce(String strTag, String strResponse) {
        App.showLog("==App==strTag==" + strTag, "--strResponse--" + strResponse);
        //appendLogApi(strTag + "_r_api", strResponse);
    }

    public static void appendLogApi(String fileName, String text) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyMM_dd_HHmmss");
            String currentDateandTime = sdf.format(new Date());

            String sdCardPath = sdCardPath = Environment.getExternalStorageDirectory().toString();

            File logFile = new File(sdCardPath, "/" + App.APP_FOLDERNAME + "/AppLog2/" + fileName + "_" + currentDateandTime + "_lg.txt");
            if (!logFile.exists()) {
                try {
                    logFile.createNewFile();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        } catch (Exception e) {

            Log.e(TAG, e.getMessage(), e);
        }
    }


    public static void showLog(String strTag, String strMessage) {
        Log.v("==App==strTag==" + strTag, "--strMessage--" + strMessage);
    }


    public static void setTaskBarColored(Activity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        }
    }


    public static boolean isInternetAvail(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

    public static boolean isInternetAvailWithMessage(Context context, View view) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        App.showSnackBar(view, mContext.getString(R.string.strNetError));
        return false;
    }


    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp      A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }


    public static String getOnlyDigits(String s) {
        Pattern pattern = Pattern.compile("[^0-9]");
        Matcher matcher = pattern.matcher(s);
        String number = matcher.replaceAll("");
        return number;
    }


    public static String getOnlyStrings(String s) {
        Pattern pattern = Pattern.compile("[^a-z A-Z]");
        Matcher matcher = pattern.matcher(s);
        String number = matcher.replaceAll("");
        return number;
    }


    public static String getOnlyAlfaNumeric(String s) {
        Pattern pattern = Pattern.compile("[^a-zA-Z0-9]");
        Matcher matcher = pattern.matcher(s);
        String number = matcher.replaceAll(" ");
        return number;
    }


    public void hideKeyBoard(View view) {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void hideSoftKeyboardMy(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @SuppressLint("NewApi")
    public static void myStartActivity(Activity activity, Intent intent) {
        activity.startActivity(intent);

    }


    public static void myFinishActivityRefresh(Activity activity, Intent intent) {
        try {
            activity.finish();
            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void myFinishActivity(Activity activity) {
        activity.finish();
    }


    public static void expand(final View v) {

        try {
            //v.measure(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT); //WRAP_CONTENT
            v.measure(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            final int targetHeight = v.getMeasuredHeight();

            // Older versions of android (pre API 21) cancel animations for views with a height of 0.
            v.getLayoutParams().height = 1;
            v.setVisibility(View.VISIBLE);
            Animation a = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    v.getLayoutParams().height = interpolatedTime == 1
                            //? WindowManager.LayoutParams.WRAP_CONTENT //WRAP_CONTENT
                            ? WindowManager.LayoutParams.MATCH_PARENT //WRAP_CONTENT
                            : (int) (targetHeight * interpolatedTime);
                    v.requestLayout();
                }

                @Override
                public boolean willChangeBounds() {
                    return true;
                }
            };

            // 1dp/ms
            a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
            v.startAnimation(a);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void expandWRAP_CONTENT(final View v) {
        try {
            //v.measure(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT); //WRAP_CONTENT
            v.measure(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            final int targetHeight = v.getMeasuredHeight();

            // Older versions of android (pre API 21) cancel animations for views with a height of 0.
            v.getLayoutParams().height = 1;
            v.setVisibility(View.VISIBLE);
            Animation a = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    v.getLayoutParams().height = interpolatedTime == 1
                            //? WindowManager.LayoutParams.WRAP_CONTENT //WRAP_CONTENT
                            ? WindowManager.LayoutParams.MATCH_PARENT //WRAP_CONTENT
                            : (int) (targetHeight * interpolatedTime);
                    v.requestLayout();
                }

                @Override
                public boolean willChangeBounds() {
                    return true;
                }
            };

            // 1dp/ms
            a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
            v.startAnimation(a);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void collapse(final View v) {
        try {
            final int initialHeight = v.getMeasuredHeight();

            Animation a = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    if (interpolatedTime == 1) {
                        v.setVisibility(View.GONE);
                    } else {
                        v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                        v.requestLayout();
                    }
                }

                @Override
                public boolean willChangeBounds() {
                    return true;
                }
            };

            // 1dp/ms
            a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
            v.startAnimation(a);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setStopLoadingMaterialRefreshLayout(MaterialRefreshLayout materialRefreshLayout) {
        if (materialRefreshLayout != null) {
            // refresh complete
            materialRefreshLayout.finishRefresh();
            // load more refresh complete
            materialRefreshLayout.finishRefreshLoadMore();
        }
    }

    private void createAppFolder() {
        try {
            String sdCardPath = Environment.getExternalStorageDirectory().toString();
            //File file2 = new File(sdCardPath + "/" + App.APP_FOLDERNAME + "");
            File file2 = new File(sdCardPath + "/" + App.APP_FOLDERNAME + "/AppLog2");
            if (!file2.exists()) {
                if (!file2.mkdirs()) {
                    System.out.println("==Create Directory " + App.APP_FOLDERNAME + "====");
                } else {
                    System.out.println("==No--1Create Directory " + App.APP_FOLDERNAME + "====");
                }
            } else {
                System.out.println("== already created---No--2Create Directory " + App.APP_FOLDERNAME + "====");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // APi call


    public static OkHttpClient getClient() {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            SSLContext sslContext = null;

            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

        return new OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory,(X509TrustManager) trustAllCerts[0])
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .connectTimeout(2, TimeUnit.MINUTES)
                .readTimeout(2, TimeUnit.MINUTES)
                .build();
        } catch (Exception e) {
            e.printStackTrace();
        return new OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.MINUTES)
                .readTimeout(2, TimeUnit.MINUTES)
                .build();
        }
    }


    public static Retrofit getRetrofitBuilder() {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();


        return new Retrofit.Builder()
                .baseUrl(App.strBaseHostUrl)
                .client(getClient()) // it's optional for adding client
                .addConverterFactory(GsonConverterFactory.create(gson))
                //.addConverterFactory(GsonConverterFactory.create())
                //  .client(SelfSigningClientBuilder.getUnsafeOkHttpClient())
                .build();
    }

    public static ApiService getRetrofitApiService() {
        return getRetrofitBuilder().create(ApiService.class);
    }

    public static RequestBody createPartFromString(String value) {
        return RequestBody.create(MediaType.parse("multipart/form-data"), value);
    }

    public static RequestBody createPartFromFile(File file) {
        return RequestBody.create(MediaType.parse("multipart/form-data"), file);
    }

    static HashMap<String, RequestBody> hashMapData = new HashMap<>();

    public static void setDataHashmap() {
        hashMapData = new HashMap<>();
    }

    public static HashMap<String, RequestBody> getDataHashmap() {
        return hashMapData;
    }

    public static void putDataHashmap(String key, String value) {
        //HashMap<String, RequestBody> hashMap = getDataHashmap();
        RequestBody r_data = App.createPartFromString(value);
        getDataHashmap().put(key, r_data);
    }


    public static AlarmManagerBroadcastReceiver alarm;

    public static void startAlarmServices(Context context) {
        if (alarm == null) {
            alarm = new AlarmManagerBroadcastReceiver();
        }
        if (alarm != null) {
            alarm.CancelAlarm(context);
            alarm.SetAlarm(context);
        } else {
            Toast.makeText(context, "Alarm is null", Toast.LENGTH_SHORT).show();
        }

    }

    public static void stopUpdateLocation(Context context) {
        if (alarm == null) {
            alarm = new AlarmManagerBroadcastReceiver();
        }
        if (alarm != null) {
            alarm.CancelAlarm(context);
        } else {
            Toast.makeText(context, "Alarm is null", Toast.LENGTH_SHORT).show();
        }
    }


    // for the database save and get
/*

    public static void insertGsonResponseWallpaperList(Realm realm, GsonResponseWallpaperList gsonResponseWallpaperList) {
        try {
            App.showLog("========insertWallpaper=====");


            realm.beginTransaction();
            GsonResponseWallpaperList articlesModel = realm.copyToRealm(gsonResponseWallpaperList);
            realm.commitTransaction();

            getDataWallpaper(realm);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                realm.commitTransaction();
            }
            catch (Exception e2)
            {e2.printStackTrace();}
        }

    }

    public static void getGsonResponseWallpaperList(Realm realm) {
        try {
            App.showLog("========getDataWallpaper=====");
            ArrayList<GsonResponseWallpaperList> arrGsonResponseWallpaperList = new ArrayList<>();

            RealmResults<GsonResponseWallpaperList> arrDLocationModel = realm.where(GsonResponseWallpaperList.class).findAll();
            App.sLog("===arrDLocationModel==" + arrDLocationModel);
            List<GsonResponseWallpaperList> gsonResponseWallpaperList = arrDLocationModel;
            arrGsonResponseWallpaperList = new ArrayList<GsonResponseWallpaperList>(gsonResponseWallpaperList);

            for (int k = 0; k < arrGsonResponseWallpaperList.size(); k++) {
                App.showL(k + "===arrGsonResponseWallpaperList=name=" + arrGsonResponseWallpaperList.get(k).filename);

                App.sLog(k + "===arrGsonResponseWallpaperList=size=" + arrGsonResponseWallpaperList.get(k).arrayListJsonImageModel.size());
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
*/

    public static void insertArticlesModelList(Realm realm, List<ArticlesModel> listArticlesModel) {
        try {
            App.showLog("========insertArticlesModelList=====");
            if (listArticlesModel != null) {

                if (realm.isInTransaction() == false) {
                    realm.beginTransaction();
                }
                //Collection<ArticlesModel> articlesModel = realm.copyToRealm(listArticlesModel);
                Collection<ArticlesModel> articlesModel = realm.copyToRealmOrUpdate(listArticlesModel);
                realm.commitTransaction();

                //getDataDashboard();

            } else {
                App.showLog("===insertArticlesModelList ==null==no insert database==");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static List<ArticlesModel> fetchArticlesModelList(Realm realm) {
        try {
            App.showLog("========fetchArticlesModelList=====");

            RealmResults<ArticlesModel> resultsList = realm.where(ArticlesModel.class).findAll();

            // RealmResults<YourModel> results = yourList.where().findAllSorted("date", Sort.Desending);

            App.showLog("===arrDLocationModel==" + resultsList);
            List<ArticlesModel> listArticlesModel = resultsList;
            listArticlesModel = new ArrayList<ArticlesModel>(listArticlesModel);

            if (listArticlesModel != null) {
                App.showLog("====listArticlesModel===" + listArticlesModel.size());
                return listArticlesModel;
            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    //Upate for like &  unlike

    public static void addToFavouriteNews(Realm realm, ArticlesModel mArticlesModel) {
        try {
            App.showLog("========insert-set-as-favourite=====");


            realm.beginTransaction();
            mArticlesModel.favourite = "1";
            // ArticlesModel articlesModel = realm.copyToRealm(mArticlesModel);
            ArticlesModel articlesModel = realm.copyToRealmOrUpdate(mArticlesModel);
            realm.commitTransaction();

            //getAllFavouriteOfflineNews(realm);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                realm.commitTransaction();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    public static void removeFromFavouriteNews(Realm realm, ArticlesModel mArticlesModel) {
        try {
            App.showLog("========insert-set-as-favourite=====");


            realm.beginTransaction();
            mArticlesModel.favourite = "0";
            // ArticlesModel articlesModel = realm.copyToRealm(mArticlesModel);
            ArticlesModel articlesModel = realm.copyToRealmOrUpdate(mArticlesModel);
            realm.commitTransaction();

            //getAllFavouriteOfflineNews(realm);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                realm.commitTransaction();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    public static void removeFromOfflineNews(Realm realm, ArticlesModel articlesModel) {
        try {

            App.showLog("========insert-set-as-favourite=====");
            realm.beginTransaction();

            RealmResults<ArticlesModel> arrDLocationModel;
            if (articlesModel.description != null && articlesModel.description.length() > 0) {
                arrDLocationModel = realm.where(ArticlesModel.class)
                        .beginGroup()
                        .equalTo("description", articlesModel.description)
                        // .equalTo("Exchange",mArticlesModel.Exchange)
                        .endGroup()
                        .findAll();
            } else {
                arrDLocationModel = realm.where(ArticlesModel.class)
                        .beginGroup()
                        .equalTo("description", articlesModel.description)
                        .endGroup()
                        .findAll();

            }

           /* RealmResults<StocklistItemListResponse> arrDLocationModel = realm.where(StocklistItemListResponse.class)
                    .beginGroup()
                    .equalTo("Symbol", mArticlesModel.Symbol)
                    .equalTo("Exchange",mArticlesModel.Exchange)
                    .or()
                    .equalTo("Exchange","")
                    *//*.or()
                    .contains("name", "Jo")*//*
                    .endGroup()

                    .findAll();*/

            arrDLocationModel.deleteAllFromRealm();
            realm.commitTransaction();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<ArticlesModel> getSearchFromAllOfflineNews(Realm realm, String keyword, boolean isFavourite) {
        try {
            App.showLog("========getDataWallpaper=====");
            List<ArticlesModel> arrListArticlesModel = new ArrayList<>();
            RealmResults<ArticlesModel> arrDLocationModel;
            if (isFavourite == true) {

                arrDLocationModel = realm.where(ArticlesModel.class)
                        .beginGroup()
                        .equalTo("favourite", "1")
                        .contains("title", keyword, Case.INSENSITIVE)
                        .or()
                        .contains("description", keyword)
                        .endGroup()
                        .findAll();

                App.showLog("===arrDLocationModel====isFavourite==" + arrDLocationModel);
            } else {
                arrDLocationModel = realm.where(ArticlesModel.class)
                        .beginGroup()
                        .contains("title", keyword, Case.INSENSITIVE)
                        .or()
                        .contains("description", keyword)
                        .endGroup()
                        .findAll();

                App.showLog("===arrDLocationModel====isOffline==" + arrDLocationModel);
            }

            List<ArticlesModel> gsonResponseWallpaperList = arrDLocationModel;
            arrListArticlesModel = new ArrayList<ArticlesModel>(gsonResponseWallpaperList);

            /*for (int k = 0; k < arrListArticlesModel.size(); k++) {
                App.showLog(k + "===ArticlesModel=title=" + arrListArticlesModel.get(k).title);
                App.showLog(k + "===ArticlesModel=description=" + arrListArticlesModel.get(k).description);
                App.showLog(k + "===ArticlesModel=favourite=" + arrListArticlesModel.get(k).favourite);
            }*/

            return arrListArticlesModel;


        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<ArticlesModel> getAllFavouriteOfflineNews(Realm realm) {
        try {
            App.showLog("========getDataWallpaper=====");
            List<ArticlesModel> arrListArticlesModel = new ArrayList<>();

            RealmResults<ArticlesModel> arrDLocationModel = realm.where(ArticlesModel.class)
                    .beginGroup()
                    .equalTo("favourite", "1")
                    .endGroup()
                    .findAll();
            App.showLog("===arrDLocationModel==" + arrDLocationModel);
            List<ArticlesModel> gsonResponseWallpaperList = arrDLocationModel;
            arrListArticlesModel = new ArrayList<ArticlesModel>(gsonResponseWallpaperList);

           /* for (int k = 0; k < arrListArticlesModel.size(); k++) {
                App.showLog(k + "===ArticlesModel=title=" + arrListArticlesModel.get(k).title);
                App.showLog(k + "===ArticlesModel=description=" + arrListArticlesModel.get(k).description);
                App.showLog(k + "===ArticlesModel=favourite=" + arrListArticlesModel.get(k).favourite);
            }*/

            return arrListArticlesModel;


        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    // for the ads banner loading
    public static void loadAdsBanner(AdView mAdView) {
        try {

            String testDeviceId = "777FA88B7778C3830777A58A1777F777";
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice(testDeviceId)

                    .build();
          /*  mAdView.setAdSize(AdSize.BANNER);
            mAdView.setAdUnitId(App.getAdsBannerUnitId());
*/

            //AdRequest.Builder.addTestDevice("C93FA88BA058C383097DA58A1211F595");

            mAdView.loadAd(adRequest);

            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    // Code to be executed when an ad finishes loading.
                    App.showLog("==AdListener=====onAdLoaded=====");
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    // Code to be executed when an ad request fails.
                    App.showLog("==AdListener=====onAdFailedToLoad====errorCode=" + errorCode);
                }

                @Override
                public void onAdOpened() {
                    // Code to be executed when an ad opens an overlay that
                    // covers the screen.
                    App.showLog("==AdListener=====onAdOpened=====");
                }

                @Override
                public void onAdLeftApplication() {
                    // Code to be executed when the user has left the app.
                    App.showLog("==AdListener=====onAdLeftApplication=====");
                }

                @Override
                public void onAdClosed() {
                    // Code to be executed when when the user is about to return
                    // to the app after tapping on an ad.
                    App.showLog("==AdListener=====onAdClosed=====");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void setDisplayBanner(final RelativeLayout rlAds, final Context context) {


        //String deviceid = tm.getDeviceId();

        //  rlAds = (RelativeLayout) findViewById(R.id.rlAds);
        rlAds.setVisibility(View.VISIBLE);
        AdView mAdView = new AdView(context);
        mAdView.setAdSize(AdSize.BANNER);
        mAdView.setAdUnitId(App.getRandomBannerId());

        // Add the AdView to the view hierarchy. The view will have no size
        // until the ad is loaded.
        rlAds.addView(mAdView);

        // Create an ad request. Check logcat output for the hashed device ID to
        // get test ads on a physical device.
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("77788250222518CCDA7DE777504EE777")
                .build();
        //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
        //.addTestDevice(deviceid).build();

        // Start loading the ad in the background.
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.i("Ads", "onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                Log.i("Ads", "onAdFailedToLoad");
                // rlAds.setVisibility(View.GONE);
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Log.i("Ads", "onAdOpened");
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                Log.i("Ads", "onAdLeftApplication");
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
                Log.i("Ads", "onAdClosed");
            }
        });
    }
}
