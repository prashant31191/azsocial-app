package com.azsocial.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.azsocial.App;
import com.azsocial.R;
import com.azsocial.activities.MainActivity;
import com.azsocial.api.model.ArticlesModel;
import com.azsocial.api.model.FilterModel;
import com.azsocial.fragments.sub.NewsDetailFragment;
import com.azsocial.utils.StringUtils;
import com.azsocial.utils.SwipeHelper;
import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;


public class OfflineNewsListFragment extends BaseFragment {

    String TAG = "OfflineNewsListFragment";

    @BindView(R.id.etSearch)
    EditText etSearch;

    @BindView(R.id.ivSearch)
    ImageView ivSearch;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.materialRefreshLayout)
    MaterialRefreshLayout materialRefreshLayout;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;


    @BindView(R.id.llNodata)
    LinearLayout llNodata;

    @BindView(R.id.tvNodata)
    TextView tvNodata;


    DataListAdapter dataListAdapter;
    List<ArticlesModel> arrayListArticlesModel = new ArrayList<>();



    String strSearchKeyword = "";
    String strTemp = "";
    String strSourceId = "bbc-news";
    String strSourceName = "Offline news";
    ArticlesModel mArticlesModel;
    FilterModel mFilterModel;
    Activity mActivity;
    View mView;


    Realm realm;
    private Paint p = new Paint();

    public static OfflineNewsListFragment newInstance(int instance) {
        Bundle args = new Bundle();
        args.putInt(ARGS_INSTANCE, instance);
        OfflineNewsListFragment fragment = new OfflineNewsListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static OfflineNewsListFragment newInstance(Object object) {
        Bundle args = new Bundle();
        //args.putInt(ARGS_INSTANCE, instance);
        args.putSerializable(ARGS_INSTANCE, (Serializable) object);
        OfflineNewsListFragment fragment = new OfflineNewsListFragment();
        fragment.setArguments(args);
        return fragment;
    }


    public OfflineNewsListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mActivity = getActivity();
        if(mView == null)
            mView = inflater.inflate(R.layout.fragment_search, container, false);

        try {

            ButterKnife.bind(this, mView);

            Bundle args = getArguments();


            if (args != null) {
                Object obj = (Object) args.getSerializable(ARGS_INSTANCE);

                if (obj instanceof String) {
                    strSourceId = (String) obj;
                    //fragCount = args.getInt(ARGS_INSTANCE);
                }
                if (obj instanceof ArticlesModel) {
                    mArticlesModel = (ArticlesModel) obj;
                }
                if (obj instanceof FilterModel) {
                    mFilterModel = (FilterModel) obj;
                }

                if (mArticlesModel != null) {
                    App.showLog("====mArticlesModel====not null==");

                    if (StringUtils.isValidString(mArticlesModel.id) == true)
                        strSourceId = mArticlesModel.id;

                    if (StringUtils.isValidString(mArticlesModel.name) == true)
                        strSourceName = mArticlesModel.name;
                }


                App.showLog("==strSourceId==" + strSourceId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {

            ((MainActivity) getActivity()).updateToolbarTitle(("Offline news"));




            if(arrayListArticlesModel !=null && arrayListArticlesModel.size() > 0)
            {

            }
            else {
                initialization();
                arrayListArticlesModel = new ArrayList<>();

                realm = Realm.getInstance(App.getRealmConfiguration());

                setStaticData(true);
                setSendDataAnalytics();
                setSearchViews();
                initSwipe();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void setSendDataAnalytics() {
        try {


            FirebaseAnalytics mFirebaseAnalytics;
            FirebaseAuth mFirebaseAuth;

            // Initialize FirebaseAuth
            mFirebaseAuth = FirebaseAuth.getInstance();
            // Obtain the FirebaseAnalytics instance.
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());

            App.showLog(TAG, "---FirebaseAnalytics.Event.SELECT_CONTENT------");
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

            mFirebaseAnalytics.setCurrentScreen(getActivity(), TAG, "setSendDataAnalytics");
        } catch (Exception e) {
            App.showLog(TAG, "---setSendDataAnalytics-Error send analytics--");
            e.printStackTrace();
        }
    }

    private void setSearchViews() {
        try {

            ivSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    strSearchKeyword = etSearch.getText().toString().trim();

                    if (strTemp.equalsIgnoreCase(strSearchKeyword)) {
                        return;
                    }

                    if (strSearchKeyword.length() > 0) {
                        App.hideSoftKeyboardMy(getActivity());
                        strTemp = strSearchKeyword;
                        performSearch();
                    } else {
                        App.hideSoftKeyboardMy(getActivity());
                        App.showSnackBar(etSearch, "Please enter search text.");

                        setStaticData(true);
                    }
                }
            });

            etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        ivSearch.performClick();
                        return true;
                    }
                    return false;
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void performSearch() {
        try {

            arrayListArticlesModel = new ArrayList<>();
            arrayListArticlesModel = App.getSearchFromAllOfflineNews(realm, strSearchKeyword,false);



            if (arrayListArticlesModel != null) {
                App.showLog("======search data list notify=====");
                App.showLog("======arrayListArticlesModel===size=="+arrayListArticlesModel.size());
                dataListAdapter = new DataListAdapter(mActivity, arrayListArticlesModel);
                recyclerView.setAdapter(dataListAdapter);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                dataListAdapter.notifyDataSetChanged();

                if(arrayListArticlesModel.size() < 1)
                    llNodata.setVisibility(View.VISIBLE);
                else
                    llNodata.setVisibility(View.GONE);



            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    private void initialization() {
        try {

            progressBar.setVisibility(View.GONE);
            App.setStopLoadingMaterialRefreshLayout(materialRefreshLayout);

            materialRefreshLayout.setIsOverLay(true);
            materialRefreshLayout.setWaveShow(true);
            materialRefreshLayout.setWaveColor(0x55ffffff);
            materialRefreshLayout.setLoadMore(false);

            materialRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
                @Override
                public void onRefresh(final MaterialRefreshLayout materialRefreshLayout) {
                    try {
                        progressBar.setVisibility(View.GONE);
                        App.setStopLoadingMaterialRefreshLayout(materialRefreshLayout);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                    try {
                        progressBar.setVisibility(View.GONE);
                        App.setStopLoadingMaterialRefreshLayout(materialRefreshLayout);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            if (recyclerView != null) {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                //GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
                recyclerView.setLayoutManager(linearLayoutManager);
                //recyclerView.setHasFixedSize(true);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setStaticData(boolean isSetAdapter) {
        try {


            App.showLog("=======setStaticData===");


            arrayListArticlesModel = App.fetchArticlesModelList(realm);


            if (arrayListArticlesModel != null && arrayListArticlesModel.size() > 0) {

                llNodata.setVisibility(View.GONE);


                App.showLog("======set adapter=DataListAdapter==");

                if (dataListAdapter == null  || isSetAdapter == true) {
                    dataListAdapter = new DataListAdapter(mActivity, arrayListArticlesModel);
                    recyclerView.setAdapter(dataListAdapter);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    dataListAdapter.notifyDataSetChanged();
                } else {
                    dataListAdapter.notifyDataSetChanged();
                }

            } else {
                llNodata.setVisibility(View.VISIBLE);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    Dialog dialogRemoveFromNews;

    private void initSwipe() {
        try {
            SwipeHelper swipeHelper = new SwipeHelper(getActivity(), recyclerView) {
                @Override
                public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                    underlayButtons.add(new SwipeHelper.UnderlayButton(
                            " Delete ",
                            0,
                            Color.parseColor("#6e8bad"),
                            new SwipeHelper.UnderlayButtonClickListener() {
                                @Override
                                public void onClick(final int pos) {
                                    // TODO: onDelete

                                    App.showLog("=Delete====pos==" + pos);

                                    if (dataListAdapter != null) {
                                        dataListAdapter.removeItem(pos);
                                    }
                                    /*{
                                        if(dialogRemoveFromNews !=null && dialogRemoveFromNews.isShowing())
                                        {
                                            dialogRemoveFromNews.dismiss();
                                        }

                                        dialogRemoveFromNews = new Dialog(getActivity());
                                        // Include dialogRemoveFromNews.xml file
                                        // Include dialogRemoveFromNews.xml file
                                        dialogRemoveFromNews.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
                                        dialogRemoveFromNews.getWindow().setBackgroundDrawableResource(R.drawable.prograss_bg); //temp removed
                                        dialogRemoveFromNews.requestWindowFeature(Window.FEATURE_NO_TITLE);

                                        dialogRemoveFromNews.setContentView(R.layout.popup_exit);
                                        dialogRemoveFromNews.setCancelable(false);

                                        // set values for custom dialogRemoveFromNews components - text, image and button
                                        // set values for custom dialogRemoveFromNews components - text, image and button
                                        TextView tvExitMessage = (TextView) dialogRemoveFromNews.findViewById(R.id.tvMessage);
                                        TextView tvCancel = (TextView) dialogRemoveFromNews.findViewById(R.id.tvCancel);
                                        TextView tvOK = (TextView) dialogRemoveFromNews.findViewById(R.id.tvOk);

                                        String strAlertMessage = "Are you sure you want to delete this news?";
                                        String strYes = "DELETE";
                                        String strNo = "CANCEL";


                                        App.showLog("==al-msg====strAlertMessage====" + strAlertMessage);
                                        App.showLog("==al-0=====strYes===" + strYes);
                                        App.showLog("==al-1====strNo====" + strNo);

                                        tvExitMessage.setText(strAlertMessage);
                                        tvCancel.setText(strNo);
                                        tvOK.setText(strYes);


                                        dialogRemoveFromNews.show();

                                        tvCancel.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dialogRemoveFromNews.dismiss();
                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        dataListAdapter.notifyDataSetChanged();
                                                    }
                                                }, 200);
                                            }
                                        });

                                        tvOK.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dialogRemoveFromNews.dismiss();

                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        dataListAdapter.removeItem(pos);
                                                    }
                                                }, 200);

                                            }
                                        });
                                    }*/
                                }
                            }
                    ));

                   /* underlayButtons.add(new SwipeHelper.UnderlayButton(
                            "Transfer",
                            0,
                            Color.parseColor("#FF9502"),
                            new SwipeHelper.UnderlayButtonClickListener() {
                                @Override
                                public void onClick(int pos) {
                                    // TODO: OnTransfer
                                    App.showLog("=Transfer====pos=="+pos);
                                }
                            }
                    ));*/
                    underlayButtons.add(new SwipeHelper.UnderlayButton(
                            " Share ",
                            0,
                            Color.parseColor("#0e3f77"),
                            new SwipeHelper.UnderlayButtonClickListener() {
                                @Override
                                public void onClick(int pos) {
                                    // TODO: OnUnshare
                                    App.showLog("=share====pos==" + pos);


                                    if (arrayListArticlesModel != null && arrayListArticlesModel.get(pos) != null) {
                                        String strShareData =
                                                arrayListArticlesModel.get(pos).title + " \n \n" +
                                                        arrayListArticlesModel.get(pos).description + " \n \n" +
                                                        arrayListArticlesModel.get(pos).url;

                                        Intent sendIntent = new Intent();
                                        sendIntent.setAction(Intent.ACTION_SEND);
                                        sendIntent.putExtra(Intent.EXTRA_TEXT, strShareData);
                                        sendIntent.setType("text/plain");
                                        startActivity(sendIntent);
                                    }

                                }
                            }
                    ));
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initSwipe2() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                try {
                    final int position = viewHolder.getAdapterPosition();

                    if (direction == ItemTouchHelper.LEFT && dataListAdapter != null) {
                        //11


                        if (dialogRemoveFromNews != null && dialogRemoveFromNews.isShowing()) {
                            dialogRemoveFromNews.dismiss();
                        }

                        dialogRemoveFromNews = new Dialog(getActivity());
                        // Include dialogRemoveFromNews.xml file
                        // Include dialogRemoveFromNews.xml file
                        dialogRemoveFromNews.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
                        dialogRemoveFromNews.getWindow().setBackgroundDrawableResource(R.drawable.prograss_bg); //temp removed
                        dialogRemoveFromNews.requestWindowFeature(Window.FEATURE_NO_TITLE);

                        dialogRemoveFromNews.setContentView(R.layout.popup_exit);
                        dialogRemoveFromNews.setCancelable(false);

                        // set values for custom dialogRemoveFromNews components - text, image and button
                        // set values for custom dialogRemoveFromNews components - text, image and button
                        TextView tvExitMessage = (TextView) dialogRemoveFromNews.findViewById(R.id.tvMessage);
                        TextView tvCancel = (TextView) dialogRemoveFromNews.findViewById(R.id.tvCancel);
                        TextView tvOK = (TextView) dialogRemoveFromNews.findViewById(R.id.tvOk);

                        String strAlertMessage = "Are you sure you want to delete this news?";
                        String strYes = "DELETE";
                        String strNo = "CANCEL";


                        App.showLog("==al-msg====strAlertMessage====" + strAlertMessage);
                        App.showLog("==al-0=====strYes===" + strYes);
                        App.showLog("==al-1====strNo====" + strNo);

                        tvExitMessage.setText(strAlertMessage);
                        tvCancel.setText(strNo);
                        tvOK.setText(strYes);


                        dialogRemoveFromNews.show();

                        tvCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogRemoveFromNews.dismiss();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        dataListAdapter.notifyDataSetChanged();
                                    }
                                }, 200);
                            }
                        });

                        tvOK.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogRemoveFromNews.dismiss();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        dataListAdapter.removeItem(position);
                                    }
                                }, 200);

                            }
                        });


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX < 0) {


                        /*p.setColor(Color.RED);
                        c.drawRect(background,p);*/

                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());


                        p.setColor(Color.RED);
                        p.setStyle(Paint.Style.FILL); //fill the background with blue color
                        //+ App.convertDpToPixel(5,ActDashboard.this)
                        c.drawRect(background.left, background.top, background.right, background.bottom, p);

                        p.setColor(Color.WHITE);
                        p.setTextSize(App.convertDpToPixel(20, getActivity()));
                        //p.setColor(Color.RED);

                        //c.drawText("Delete", background.centerX(), background.centerY(), p);
                        c.drawText("      Delete", background.left, background.centerY(), p);

                        //versionViewHolder.tvRowTitle.setTypeface(App.getFont_Regular());

                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }


    public class DataListAdapter extends RecyclerView.Adapter<DataListAdapter.VersionViewHolder> {
        List<ArticlesModel> mArrListmPEArticleModel;
        Context mContext;


        public DataListAdapter(Context context, List<ArticlesModel> arrList) {
            mArrListmPEArticleModel = arrList;
            mContext = context;
        }

        @Override
        public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_articles_list, viewGroup, false);
            VersionViewHolder viewHolder = new VersionViewHolder(view);
            return viewHolder;
        }


        @Override
        public void onBindViewHolder(final VersionViewHolder versionViewHolder, final int i) {
            try {
                ArticlesModel mPEArticleModel = mArrListmPEArticleModel.get(i);


                if (i % 5 == 0) {
                    if (versionViewHolder.rlAds != null) {
                        App.setDisplayBanner(versionViewHolder.rlAds, mContext);
                    }
                } else {
                    if (versionViewHolder.rlAds != null) {
                        versionViewHolder.rlAds.setVisibility(View.GONE);
                    }
                }

                versionViewHolder.tvTitle.setText(mPEArticleModel.title);
                versionViewHolder.tvDate.setText(mPEArticleModel.publishedAt);
                versionViewHolder.tvTime.setText(mPEArticleModel.author);

                versionViewHolder.tvDetail.setText("#" + i + " " + mPEArticleModel.description);
                versionViewHolder.tvLink.setText(mPEArticleModel.url);

                if (mPEArticleModel.urlToImage != null && mPEArticleModel.urlToImage.length() > 1) {
                    versionViewHolder.ivPhoto.setVisibility(View.VISIBLE);

                    Picasso.with(mContext)
                            .load(mPEArticleModel.urlToImage)
                            .placeholder(R.color.clr_divider)
                            .error(R.color.white)
                            .fit()
                            .centerCrop()
                            .into(versionViewHolder.ivPhoto);

                } else {
                    versionViewHolder.ivPhoto.setVisibility(View.GONE);
                }
                if (mPEArticleModel.name.equalsIgnoreCase("1")) {
                    versionViewHolder.ivFavourite.setSelected(true);
                } else {
                    versionViewHolder.ivFavourite.setSelected(false);
                }


                versionViewHolder.ivFavourite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mArrListmPEArticleModel.get(i).name.equalsIgnoreCase("1")) {
                            mArrListmPEArticleModel.get(i).name = "0";
                            if (mArrListmPEArticleModel.get(i) != null && mArrListmPEArticleModel.get(i).name != null) {

                            }
                        } else {
                            mArrListmPEArticleModel.get(i).name = "1";
                        }

                        dataListAdapter.notifyDataSetChanged();
                    }
                });

                versionViewHolder.cvItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (mArrListmPEArticleModel.get(i) != null && mFragmentNavigation != null) {
                            mFragmentNavigation.pushFragment(NewsDetailFragment.newInstance((Object) mArrListmPEArticleModel.get(i)));
                        }

                    }
                });


                // Set the view to fade in
                // setFadeAnimation(versionViewHolder.cvItem);
                setAnimation(versionViewHolder.cvItem, i);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * Here is the key method to apply the animation
         */
        // Allows to remember the last item shown on screen
        private int lastPosition = -1;

        private void setAnimation(View viewToAnimate, int position) {
            // If the bound view wasn't previously displayed on screen, it's animated
            if (position > lastPosition) {
                Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_up);
                viewToAnimate.startAnimation(animation);
                lastPosition = position;
            }
        }



        @Override
        public int getItemCount() {
            return mArrListmPEArticleModel.size();
        }


        public void removeItem(int position) {
            try {

                App.removeFromOfflineNews(realm, mArrListmPEArticleModel.get(position));

                mArrListmPEArticleModel.remove(position);
                notifyItemRemoved(position);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }


        class VersionViewHolder extends RecyclerView.ViewHolder {


            TextView tvTitle, tvDate, tvTime, tvDetail, tvLink;
            ImageView ivPhoto, ivFavourite;
            RelativeLayout rlMain;
            RelativeLayout rlAds;
            CardView cvItem;


            public VersionViewHolder(View itemView) {
                super(itemView);


                cvItem = (CardView) itemView.findViewById(R.id.cvItem);
                rlMain = (RelativeLayout) itemView.findViewById(R.id.rlMain);
                rlAds = (RelativeLayout) itemView.findViewById(R.id.rlAds);
                tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
                tvDate = (TextView) itemView.findViewById(R.id.tvDate);
                tvTime = (TextView) itemView.findViewById(R.id.tvTime);
                tvDetail = (TextView) itemView.findViewById(R.id.tvDetail);
                tvLink = (TextView) itemView.findViewById(R.id.tvLink);

                ivPhoto = (ImageView) itemView.findViewById(R.id.ivPhoto);
                ivFavourite = (ImageView) itemView.findViewById(R.id.ivFavourite);
            }

        }
    }


}
