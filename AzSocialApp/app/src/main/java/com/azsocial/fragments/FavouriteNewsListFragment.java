package com.azsocial.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.azsocial.demo.news.recycler.newsapi.ArticlesModel;
import com.azsocial.demo.news.recycler.newsapi.FilterModel;
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


public class FavouriteNewsListFragment extends BaseFragment {

    String TAG = "FavouriteNewsListFragment";

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


    int page = 1;
    String strSearchKeyword = "";
    String strTemp = "";
    String strSourceId = "bbc-news";
    String strSourceName = "Favourite news";
    ArticlesModel mArticlesModel;
    FilterModel mFilterModel;
    Activity mActivity;
    View mView;

    Realm realm;


    public static FavouriteNewsListFragment newInstance(int instance) {
        Bundle args = new Bundle();
        args.putInt(ARGS_INSTANCE, instance);
        FavouriteNewsListFragment fragment = new FavouriteNewsListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static FavouriteNewsListFragment newInstance(Object object) {
        Bundle args = new Bundle();
        //args.putInt(ARGS_INSTANCE, instance);
        args.putSerializable(ARGS_INSTANCE, (Serializable) object);
        FavouriteNewsListFragment fragment = new FavouriteNewsListFragment();
        fragment.setArguments(args);
        return fragment;
    }


    public FavouriteNewsListFragment() {
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

            ((MainActivity) getActivity()).updateToolbarTitle(("Favourite news"));



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

            /*initialization();
            arrayListArticlesModel = new ArrayList<>();

            setStaticData(true);
            setSendDataAnalytics();

            initSwipe();*/
        } catch (Exception e) {
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
            arrayListArticlesModel = App.getSearchFromAllOfflineNews(realm, strSearchKeyword,true);



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


    private void initSwipe()
    {
        try{
            SwipeHelper swipeHelper = new SwipeHelper(getActivity(), recyclerView) {
                @Override
                public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                    underlayButtons.add(new SwipeHelper.UnderlayButton(
                            " Un-favourite ",
                            0,
                            Color.parseColor("#6e8bad"),
                            new SwipeHelper.UnderlayButtonClickListener() {
                                @Override
                                public void onClick(int pos) {
                                    // TODO: onDelete

                                    App.showLog("=Delete====pos=="+pos);

                                    if(dataListAdapter !=null)
                                    {
                                        dataListAdapter.removeItem(pos);
                                    }
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
                                    App.showLog("=share====pos=="+pos);


                                    if(arrayListArticlesModel !=null && arrayListArticlesModel.get(pos) !=null)
                                    {
                                        String strShareData =
                                                arrayListArticlesModel.get(pos).title +" \n \n"+
                                                arrayListArticlesModel.get(pos).description +" \n \n"+
                                                arrayListArticlesModel.get(pos).url ;

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
        }
        catch (Exception e)
        {
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


            if (recyclerView != null) {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                //GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
                recyclerView.setLayoutManager(linearLayoutManager);
                //recyclerView.setHasFixedSize(true);
            }

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



        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setStaticData(boolean isSetAdapter) {
        try {


            App.showLog("=======setStaticData===");
            arrayListArticlesModel = App.getAllFavouriteOfflineNews(realm);


            if (arrayListArticlesModel != null && arrayListArticlesModel.size() > 0) {

                llNodata.setVisibility(View.GONE);


                App.showLog("======set adapter=DataListAdapter==page=" + page);

                if (dataListAdapter == null || page <= 2 || isSetAdapter == true) {
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

                versionViewHolder.tvTitle.setText(mPEArticleModel.title);
                versionViewHolder.tvDate.setText(mPEArticleModel.publishedAt);
                versionViewHolder.tvTime.setText(mPEArticleModel.author);

                versionViewHolder.tvDetail.setText("#"+i+" "+mPEArticleModel.description);
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


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return mArrListmPEArticleModel.size();
        }


        public void removeItem(int position) {
            try {
                App.removeFromFavouriteNews(realm, mArrListmPEArticleModel.get(position));

                mArrListmPEArticleModel.remove(position);
                notifyItemRemoved(position);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }


        class VersionViewHolder extends RecyclerView.ViewHolder {


            TextView tvTitle, tvDate, tvTime, tvDetail, tvLink;
            ImageView ivPhoto, ivFavourite;
            RelativeLayout rlMain;
            CardView cvItem;


            public VersionViewHolder(View itemView) {
                super(itemView);


                cvItem = (CardView) itemView.findViewById(R.id.cvItem);
                rlMain = (RelativeLayout) itemView.findViewById(R.id.rlMain);
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
