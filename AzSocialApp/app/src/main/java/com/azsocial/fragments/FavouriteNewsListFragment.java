package com.azsocial.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.materialRefreshLayout)
    MaterialRefreshLayout materialRefreshLayout;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.rlFilter)
    RelativeLayout rlFilter;

    @BindView(R.id.vLine)
    View vLine;

    @BindView(R.id.llNodata)
    LinearLayout llNodata;

    @BindView(R.id.tvNodata)
    TextView tvNodata;


    DataListAdapter dataListAdapter;
    List<ArticlesModel> arrayListArticlesModel = new ArrayList<>();


    int page = 1;
    String strSourceId = "bbc-news";
    String strSourceName = "Favourite news";
    ArticlesModel mArticlesModel;
    FilterModel mFilterModel;
    Activity mActivity;


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
        View view = inflater.inflate(R.layout.fragment_search_headline, container, false);

        try {

            ButterKnife.bind(this, view);

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


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            if (StringUtils.isValidString(strSourceName) == true) {
                ((MainActivity) getActivity()).updateToolbarTitle((strSourceName));
            } else {
                ((MainActivity) getActivity()).updateToolbarTitle(("Favourite news"));
            }

            if (mFilterModel != null && StringUtils.isValidString(mFilterModel.strFilterKey)) {
                App.showLog("====mFilterModel====not null==");
                {
                    ((MainActivity) getActivity()).updateToolbarTitle(("Top headline " + mFilterModel.strFilterKey + "(" + mFilterModel.strFilterValue + ")"));
                }
            }

            if (recyclerView != null) {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                //GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
                recyclerView.setLayoutManager(linearLayoutManager);
                //recyclerView.setHasFixedSize(true);
            }

            initialization();
            arrayListArticlesModel = new ArrayList<>();

            setStaticData(true);
            setSendDataAnalytics();

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
            rlFilter.setVisibility(View.GONE);
            vLine.setVisibility(View.GONE);
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



        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setStaticData(boolean isSetAdapter) {
        try {


            App.showLog("=======setStaticData===");

            Realm realm;
            realm = Realm.getInstance(App.getRealmConfiguration());

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
