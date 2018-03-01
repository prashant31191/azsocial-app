package com.azsocial.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.azsocial.App;
import com.azsocial.R;
import com.azsocial.fragments.BaseFragment;
import com.azsocial.fragments.FavouriteNewsListFragment;
import com.azsocial.fragments.HomeFragment;
import com.azsocial.fragments.NewsFragment;
import com.azsocial.fragments.OfflineNewsListFragment;
import com.azsocial.fragments.ProfileFragment;
import com.azsocial.fragments.SeachHeadLinesFragment;
import com.azsocial.fragments.SearchSourceFragment;
import com.azsocial.fragments.ShareFragment;
import com.azsocial.utils.FragmentHistory;
import com.azsocial.utils.Utils;
import com.azsocial.views.FragNavController;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements BaseFragment.FragmentNavigation, FragNavController.TransactionListener, FragNavController.RootFragmentListener {

    String TAG = "MainActivity";
    boolean isClickEnable = false;

    @BindView(R.id.content_frame)
    FrameLayout contentFrame;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private int[] mTabIconsSelected = {
            R.drawable.tab_home,
            R.drawable.tab_search,
            R.drawable.tab_share,
            R.drawable.tab_news,
            R.drawable.tab_profile};


    @BindArray(R.array.tab_name)
    String[] TABS;

    @BindView(R.id.bottom_tab_layout)
    TabLayout bottomTabLayout;

    private FragNavController mNavController;

    private FragmentHistory fragmentHistory;

    //for the video ads
    private RewardedVideoAd mRewardedVideoAd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_main);


            ButterKnife.bind(this);


            initToolbar();

            initTab();

            fragmentHistory = new FragmentHistory();


            mNavController = FragNavController.newBuilder(savedInstanceState, getSupportFragmentManager(), R.id.content_frame)
                    .transactionListener(this)
                    .rootFragmentListener(this, TABS.length)
                    .build();


            switchTab(0);

            bottomTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {

                    fragmentHistory.push(tab.getPosition());

                    switchTab(tab.getPosition());


                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                    mNavController.clearStack();

                    switchTab(tab.getPosition());


                }
            });

            RelativeLayout rlAds = findViewById(R.id.rlAds);
            App.setDisplayBanner(rlAds,MainActivity.this);


           /* AdView mAdView = findViewById(R.id.adViewTop);
            App.loadAdsBanner(mAdView);*/

            setupAds();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }



    private void initToolbar() {
        setSupportActionBar(toolbar);
    }

    private void initTab() {
        if (bottomTabLayout != null) {
            for (int i = 0; i < TABS.length; i++) {
                bottomTabLayout.addTab(bottomTabLayout.newTab());
                TabLayout.Tab tab = bottomTabLayout.getTabAt(i);
                if (tab != null)
                    tab.setCustomView(getTabView(i));
            }
        }
    }


    private View getTabView(int position) {
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.tab_item_bottom, null);
        ImageView icon = (ImageView) view.findViewById(R.id.tab_icon);
        icon.setImageDrawable(Utils.setDrawableSelector(MainActivity.this, mTabIconsSelected[position], mTabIconsSelected[position]));
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {

        super.onStop();
    }


    private void switchTab(int position) {
        mNavController.switchTab(position);


//        updateToolbarTitle(position);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }


        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {

        if (!mNavController.isRootFragment()) {
            mNavController.popFragment();
        } else {

            if (fragmentHistory.isEmpty()) {
                super.onBackPressed();
            } else {


                if (fragmentHistory.getStackSize() > 1) {

                    int position = fragmentHistory.popPrevious();

                    switchTab(position);

                    updateTabSelection(position);

                } else {

                    switchTab(0);

                    updateTabSelection(0);

                    fragmentHistory.emptyStack();
                }
            }

        }
    }


    private void updateTabSelection(int currentTab){

        for (int i = 0; i <  TABS.length; i++) {
            TabLayout.Tab selectedTab = bottomTabLayout.getTabAt(i);
            if(currentTab != i) {
                selectedTab.getCustomView().setSelected(false);
            }else{
                selectedTab.getCustomView().setSelected(true);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mNavController != null) {
            mNavController.onSaveInstanceState(outState);
        }
    }

    @Override
    public void pushFragment(Fragment fragment) {
        if (mNavController != null) {
            mNavController.pushFragment(fragment);
        }
    }


    @Override
    public void onTabTransaction(Fragment fragment, int index) {
        // If we have a backstack, show the back button
        if (getSupportActionBar() != null && mNavController != null) {
            updateToolbar();
        }
    }

    private void updateToolbar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(!mNavController.isRootFragment());
        getSupportActionBar().setDisplayShowHomeEnabled(!mNavController.isRootFragment());
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
    }


    @Override
    public void onFragmentTransaction(Fragment fragment, FragNavController.TransactionType transactionType) {
        //do fragmentty stuff. Maybe change title, I'm not going to tell you how to live your life
        // If we have a backstack, show the back button
        if (getSupportActionBar() != null && mNavController != null) {

            updateToolbar();

        }
    }

    @Override
    public Fragment getRootFragment(int index) {
        switch (index) {

            case FragNavController.TAB1:
                return new HomeFragment();
            case FragNavController.TAB2:
                return new SearchSourceFragment();
            case FragNavController.TAB3:
                return new SeachHeadLinesFragment();
            case FragNavController.TAB4:
                return new FavouriteNewsListFragment(); //NewsFragment
            case FragNavController.TAB5:
                return new OfflineNewsListFragment();
                //return new ProfileFragment();
        }
        throw new IllegalStateException("Need to send an index that we know");
    }


//    private void updateToolbarTitle(int position){
//
//
//        getSupportActionBar().setTitle(TABS[position]);
//
//    }


    public void updateToolbarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }



    private void setupAds()
    {
        try{
            // Initialize the Mobile Ads SDK.
            //MobileAds.initialize(this, App.APP_ID);

            mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
            mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
                @Override
                public void onRewardedVideoAdLoaded() {
                   App.showLog(TAG, "====onRewardedVideoAdLoaded====");
                    if (isClickEnable == false) {
                        isClickEnable = true;
                        showRewardedVideo();
                    }

                    loadRewardedVideoAd();


                }

                @Override
                public void onRewardedVideoAdOpened() {
                   App.showLog(TAG, "====onRewardedVideoAdOpened====");
                }

                @Override
                public void onRewardedVideoStarted() {
                   App.showLog(TAG, "====onRewardedVideoStarted====");
                }

                @Override
                public void onRewardedVideoAdClosed() {
                   App.showLog(TAG, "====onRewardedVideoAdClosed====");
                }

                @Override
                public void onRewarded(RewardItem rewardItem) {
                   App.showLog(TAG, "====onRewarded====");
                }

                @Override
                public void onRewardedVideoAdLeftApplication() {
                   App.showLog(TAG, "====onRewardedVideoAdLeftApplication====");
                }

                @Override
                public void onRewardedVideoAdFailedToLoad(int i) {
                   App.showLog(TAG, "====onRewardedVideoAdFailedToLoad====");

                }
            });

            loadRewardedVideoAd();
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private void loadRewardedVideoAd() {
        if (mRewardedVideoAd !=null && !mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.loadAd(App.ADS_APP_rwid_1, new AdRequest.Builder().build());
        }
    }

    private void showRewardedVideo() {
       App.showLog(TAG, "====showRewardedVideo====");

        if (mRewardedVideoAd !=null && mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        }
    }
}
