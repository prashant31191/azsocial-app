package com.azsocial.utils;


import com.azsocial.App;
import com.flurry.android.ads.FlurryAdErrorType;
import com.flurry.android.ads.FlurryAdInterstitial;
import com.flurry.android.ads.FlurryAdInterstitialListener;

/**
 * Created by prashant.chovatiya on 1/20/2018.
 */

public class AdsUtils {

    public static String mAdSpaceName = "203152";//"INTERSTITIAL_ADSPACE";

    public static FlurryAdInterstitialListener interstitialAdListener;

    public static void initListener()
    {
        final String strTag1= "FlurryAdInterstitialListener";
     interstitialAdListener = new FlurryAdInterstitialListener() {

        @Override
        public void onFetched(FlurryAdInterstitial adInterstitial) {
            App.showLog("======onFetched=="+strTag1);
            adInterstitial.displayAd();
        }

        @Override
        public void onRendered(FlurryAdInterstitial flurryAdInterstitial) {
            App.showLog("======onRendered=="+strTag1);

        }

        @Override
        public void onDisplay(FlurryAdInterstitial flurryAdInterstitial) {
            App.showLog("======onDisplay=="+strTag1);
        }

        @Override
        public void onClose(FlurryAdInterstitial flurryAdInterstitial) {
            App.showLog("======onClose=="+strTag1);
        }

        @Override
        public void onAppExit(FlurryAdInterstitial flurryAdInterstitial) {
            App.showLog("======onAppExit=="+strTag1);
        }

        @Override
        public void onClicked(FlurryAdInterstitial flurryAdInterstitial) {
            App.showLog("======onClicked=="+strTag1);
        }

        @Override
        public void onVideoCompleted(FlurryAdInterstitial flurryAdInterstitial) {
            App.showLog("======onVideoCompleted=="+strTag1);
        }

        @Override
        public void onError(FlurryAdInterstitial adInterstitial, FlurryAdErrorType adErrorType, int errorCode) {
            App.showLog("======onError=="+strTag1);
            adInterstitial.destroy();
        }
        //..
        //the remainder of listener callbacks
    };
}
}
