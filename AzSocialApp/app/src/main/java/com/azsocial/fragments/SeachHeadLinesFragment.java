package com.azsocial.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
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
import com.azsocial.demo.news.recycler.newsapi.NewsHeadlinesResponse;
import com.azsocial.fragments.sub.NewsDetailFragment;
import com.azsocial.utils.StringUtils;
import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class SeachHeadLinesFragment extends BaseFragment {

    String TAG = "SeachHeadLinesFragment";

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


    @BindView(R.id.llFilter)
    LinearLayout llFilter;

    @BindView(R.id.rlFilter)
    RelativeLayout rlFilter;


    @BindView(R.id.recyclerViewCountry)
    RecyclerView recyclerViewCountry;

    @BindView(R.id.recyclerViewLanguage)
    RecyclerView recyclerViewLanguage;

    @BindView(R.id.recyclerViewCategory)
    RecyclerView recyclerViewCategory;

    DataListAdapter dataListAdapter;
    List<ArticlesModel> arrayListArticlesModel = new ArrayList<>();


    int page = 1;
    String strSourceId = "bbc-news";
    String strSourceName = "Top headlines";
    ArticlesModel  mArticlesModel;
    FilterModel  mFilterModel;
    Activity mActivity;


    String strCountryCodes = "ae,ar,at,au,be,bg,br,ca,ch,cn,co,cu,cz,de,eg,fr,gb,gr,hk,hu,id,ie,il,in,it,jp,kr,lt,lv,ma,mx,my,ng,nl,no,nz,ph,pl,pt,ro,rs,ru,sa,se,sg,si,sk,th,tr,tw,ua,us,ve,za";
    String strLanguagesCodes = "ar,de,en,es,fr,he,it,nl,no,pt,ru,se,ud,zh";
    String strcategoryCodes = "business,entertainment,general,health,science,sports,technology";


    public static SeachHeadLinesFragment newInstance(int instance) {
        Bundle args = new Bundle();
        args.putInt(ARGS_INSTANCE, instance);
        SeachHeadLinesFragment fragment = new SeachHeadLinesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static SeachHeadLinesFragment newInstance(Object object) {
        Bundle args = new Bundle();
        //args.putInt(ARGS_INSTANCE, instance);
        args.putSerializable(ARGS_INSTANCE, (Serializable) object);
        SeachHeadLinesFragment fragment = new SeachHeadLinesFragment();
        fragment.setArguments(args);
        return fragment;
    }


    public SeachHeadLinesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
                
                if(mArticlesModel !=null)
                {
                    App.showLog("====mArticlesModel====not null==");

                    if(StringUtils.isValidString(mArticlesModel.id) == true)
                        strSourceId = mArticlesModel.id;

                    if(StringUtils.isValidString(mArticlesModel.name) == true)
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
                ((MainActivity) getActivity()).updateToolbarTitle(("Top headline news"));
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

            if (recyclerViewCountry != null && recyclerViewLanguage != null && recyclerViewCategory != null) {
                setStaticListHorizontalList();
            }

            initialization();

            rlFilter.setSelected(false);
            setFilterVisibility();

            if (dataListAdapter == null) {
                page = 1;
                arrayListArticlesModel = new ArrayList<>();
                asyncGetNewsList();
            } else {
                progressBar.setVisibility(View.GONE);
                setStaticData(true);

            }
         /*  initialization();
          asyncGetNewsList();*/

        }
        catch (Exception e)
        {
            e.printStackTrace();
            setSendDataAnalytics();
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


            materialRefreshLayout.setIsOverLay(true);
            materialRefreshLayout.setWaveShow(true);
            materialRefreshLayout.setWaveColor(0x55ffffff);
            materialRefreshLayout.setLoadMore(true);

            materialRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
                @Override
                public void onRefresh(final MaterialRefreshLayout materialRefreshLayout) {
                    try {
                        page = 1;
                        arrayListArticlesModel = new ArrayList<>();
                        asyncGetNewsList();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                    try {
                        asyncGetNewsList();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


            rlFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (rlFilter.isSelected() == true) {
                        rlFilter.setSelected(false);
                        setFilterVisibility();
                    } else {
                        rlFilter.setSelected(true);
                        setFilterVisibility();
                    }
                }
            });


            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (dy > 0) {
                        // Scrolling up
                        if (rlFilter.isSelected() == true) {
                            rlFilter.setSelected(false);
                            setFilterVisibility();
                        }
                    } /*else {
                        // Scrolling down
                        if(rlFilter.isSelected() == true)
                        {
                            rlFilter.setSelected(false);
                            setFilterVisibility();
                        }
                    }*/
                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    /*if (newState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                        // Do something
                    } else if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                        // Do something
                    } else {
                        // Do something
                    }*/
                }
            });



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setStaticListHorizontalList() {
        try {

            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            LinearLayoutManager layoutManager3 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

            recyclerViewCountry.setLayoutManager(layoutManager);
            recyclerViewLanguage.setLayoutManager(layoutManager2);
            recyclerViewCategory.setLayoutManager(layoutManager3);

            String[] strCountry = strCountryCodes.split(",");
            String[] strLanguage = strLanguagesCodes.split(",");
            String[] strCategory = strcategoryCodes.split(",");


            if (strCountry != null && strLanguage != null && strCategory != null) {

                ArrayList<String> arrayListCountry = new ArrayList(Arrays.asList(strCountry));
                ArrayList<String> arrayListLanguages = new ArrayList(Arrays.asList(strLanguage));
                ArrayList<String> arrayListCategory = new ArrayList(Arrays.asList(strCategory));

                CommonListAdapter adapterCountry = new CommonListAdapter(getActivity(), "0", arrayListCountry);
                recyclerViewCountry.setAdapter(adapterCountry);

                CommonListAdapter adapterLanguages = new CommonListAdapter(getActivity(), "1", arrayListLanguages);
                recyclerViewLanguage.setAdapter(adapterLanguages);

                CommonListAdapter adapterCategory = new CommonListAdapter(getActivity(), "2", arrayListCategory);
                recyclerViewCategory.setAdapter(adapterCategory);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setFilterVisibility() {
        try {

            if (rlFilter.isSelected() == true) {
                llFilter.setVisibility(View.VISIBLE);
            } else {
                llFilter.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    public class CommonListAdapter extends RecyclerView.Adapter<CommonListAdapter.VersionViewHolder> {
        ArrayList<String> mArrList;
        Context mContext;
        String strType = "0"; // 0=country , 1 = Language , 2= Category


        public CommonListAdapter(Context context, String type, ArrayList<String> arrList) {
            mArrList = arrList;
            mContext = context;
            strType = type;
        }

        @Override
        public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_cntr_lang_list, viewGroup, false);
            VersionViewHolder viewHolder = new VersionViewHolder(view);
            return viewHolder;
        }


        @Override
        public void onBindViewHolder(final VersionViewHolder versionViewHolder, final int i) {
            try {

                String strData = mArrList.get(i);


                versionViewHolder.tvTitleOval.setText(strData);
                versionViewHolder.tvTitleRect.setText(strData);


                versionViewHolder.rlMainOval.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        if (mFragmentNavigation != null && mArrList.get(i) != null) {

                            FilterModel filterModel = new FilterModel();
                            filterModel.strFilterValue = (String) mArrList.get(i);

                            if (strType.equalsIgnoreCase("0")) {
                                filterModel.strFilterKey = "country";
                            } else {
                                filterModel.strFilterKey = "language";
                            }
                            filterTopHeadlineList(filterModel);

                        }

                    }
                });
                versionViewHolder.rlMainRect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (mFragmentNavigation != null && mArrList.get(i) != null) {

                            FilterModel filterModel = new FilterModel();
                            filterModel.strFilterValue = (String) mArrList.get(i);
                            filterModel.strFilterKey = "category";

                            filterTopHeadlineList(filterModel);

                           // mFragmentNavigation.pushFragment(SourceFilterFragment.newInstance((Object) filterModel));
                        }

                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return mArrList.size();
        }


        class VersionViewHolder extends RecyclerView.ViewHolder {


            TextView tvTitleOval, tvTitleRect;
            RelativeLayout rlMainOval, rlMainRect;


            public VersionViewHolder(View itemView) {
                super(itemView);

                rlMainOval = (RelativeLayout) itemView.findViewById(R.id.rlMainOval);
                rlMainRect = (RelativeLayout) itemView.findViewById(R.id.rlMainRect);

                tvTitleOval = (TextView) itemView.findViewById(R.id.tvTitleOval);
                tvTitleRect = (TextView) itemView.findViewById(R.id.tvTitleRect);

                if (strType.equalsIgnoreCase("2")) {
                    rlMainRect.setVisibility(View.VISIBLE);
                    rlMainOval.setVisibility(View.GONE);
                } else {
                    rlMainRect.setVisibility(View.GONE);
                    rlMainOval.setVisibility(View.VISIBLE);
                }
            }

        }
    }

    private void  filterTopHeadlineList(FilterModel filterModel){
        try{
            if(mFragmentNavigation !=null)
                mFragmentNavigation.pushFragment(SeachHeadLinesFragment.newInstance((Object) filterModel));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }




    private void asyncGetNewsList() {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);


            OkHttpClient httpClient = new OkHttpClient();

            String url = "https://newsapi.org/v2/top-headlines?sources=" + strSourceId + "&apiKey=" + App.strNewsApiKey + "&page=" + page;

            if(mFilterModel !=null  && StringUtils.isValidString(mFilterModel.strFilterValue))
            {
                url ="https://newsapi.org/v2/top-headlines?"+ mFilterModel.strFilterKey +"=" + mFilterModel.strFilterValue + "&apiKey=" + App.strNewsApiKey + "&page=" + page;
            }


            Request request = new Request.Builder()
                    .url(url)
                    .build();

            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    App.showLog("error in getting response using async okhttp call");
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                progressBar.setVisibility(View.GONE);
                                App.setStopLoadingMaterialRefreshLayout(materialRefreshLayout);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                                progressBar.setVisibility(View.GONE);
                                App.setStopLoadingMaterialRefreshLayout(materialRefreshLayout);
                            }
                        }
                    });
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    final ResponseBody responseBody = response.body();

                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (!response.isSuccessful()) {
                                    throw new IOException("Error response " + response);
                                }

                                String result = responseBody.string();
                                App.showLog("=result==" + result);
                                progressBar.setVisibility(View.GONE);
                                if (result != null) {
                                    App.showLog("==result==" + result.toString());

                                    Gson gson = new GsonBuilder().create();
                                    NewsHeadlinesResponse topHeadLinesResponse = gson.fromJson(result.toString(), NewsHeadlinesResponse.class);
                                    App.setStopLoadingMaterialRefreshLayout(materialRefreshLayout);
                                    if (topHeadLinesResponse != null && topHeadLinesResponse.arrayListArticlesModel != null) {
                                        //arrayListArticlesModel = topHeadLinesResponse.arrayListArticlesModel;
                                        if (page == 1) {
                                            arrayListArticlesModel = topHeadLinesResponse.arrayListArticlesModel;
                                        } else {
                                            arrayListArticlesModel.addAll(topHeadLinesResponse.arrayListArticlesModel);
                                        }
                                        page = page + 1;
                                        setStaticData(false);

                                        Realm realm;
                                        realm = Realm.getInstance(App.getRealmConfiguration());

                                        App.insertArticlesModelList(realm,topHeadLinesResponse.arrayListArticlesModel);

                                    } else {
                                        materialRefreshLayout.setLoadMore(false);
                                    }
                                }

                            } catch (Exception e1) {
                                progressBar.setVisibility(View.GONE);
                                App.setStopLoadingMaterialRefreshLayout(materialRefreshLayout);
                                e1.printStackTrace();
                            }
                        }
                    });


                }
            });


        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            App.setStopLoadingMaterialRefreshLayout(materialRefreshLayout);
            e.printStackTrace();

        }
    }


    private void setStaticData(boolean isSetAdapter) {
        try {


            App.showLog("=======setStaticData===");

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

                versionViewHolder.tvDetail.setText(mPEArticleModel.description);
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

                      /*
                        Intent intent= new Intent(mActivity,ActNewsDetail.class);
                        intent.putExtra(AppFlags.tagArticlesModel,mArrListmPEArticleModel.get(i));
                        mActivity.startActivity(intent);
                        */

                        if (mArrListmPEArticleModel.get(i) !=null && mFragmentNavigation != null) {
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
