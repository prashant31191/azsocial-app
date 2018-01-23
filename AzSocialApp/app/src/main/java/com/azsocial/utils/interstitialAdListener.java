package com.azsocial.utils;

import com.flurry.android.ads.FlurryAdErrorType;
import com.flurry.android.ads.FlurryAdInterstitial;
import com.flurry.android.ads.FlurryAdInterstitialListener;

/**
 * Created by prashant.chovatiya on 1/20/2018.
 */

public class interstitialAdListener implements FlurryAdInterstitialListener {

    //This method will be called when the user has clicked on the ad.
    @Override
    public void onClicked(FlurryAdInterstitial adInterstitial) {
    }


    //This method will be called when the ad has been received from the server
    @Override
    public void onFetched(FlurryAdInterstitial adInterstitial) {
    }

    //This method will be called when ad is successfully rendered.
    @Override
    public void onRendered(FlurryAdInterstitial adInterstitial) {

    }

    @Override
    public void onDisplay(FlurryAdInterstitial flurryAdInterstitial) {

    }

    //Invoked when an ad is closed
    @Override
    public void onClose(FlurryAdInterstitial adInterstitial) {

    }

    //This method will be called when the user is leaving the application after following
    //events associated with the current Ad in the provided Ad Space name.
    @Override
    public void onAppExit(FlurryAdInterstitial adInterstitial) {

    }

    //This method is present only in case the ad served is a video clip and adspace is marked as client side rewarded, or rewarded.
    //Make sure to not destroy the ad object in onStop as it will prevent this callback form fireing
    @Override
    public void onVideoCompleted(FlurryAdInterstitial adInterstitial) {

    }
    //This method will be called when fetch, render or click failed.
    @Override
    public void onError(FlurryAdInterstitial adInterstitial, FlurryAdErrorType adErrorType, int errorCode) {

    }
}