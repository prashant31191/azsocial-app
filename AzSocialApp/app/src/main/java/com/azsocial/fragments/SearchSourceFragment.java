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
import com.azsocial.api.model.ArticlesModel;
import com.azsocial.api.model.NewsHeadlinesResponse;
import com.azsocial.fragments.sub.NewsDetailFragment;
import com.azsocial.utils.StringUtils;
import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.flurry.android.ads.FlurryAdInterstitial;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
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


public class SearchSourceFragment extends BaseFragment {

    String TAG = "SearchSourceFragment";

    @BindView(R.id.etSearch)
    EditText etSearch;

    @BindView(R.id.ivSearch)
    ImageView ivSearch;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.materialRefreshLayout)
    MaterialRefreshLayout materialRefreshLayout;


    @BindView(R.id.llNodata)
    LinearLayout llNodata;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.tvNodata)
    TextView tvNodata;

    DataListAdapter dataListAdapter;
    List<ArticlesModel> arrayListArticlesModel = new ArrayList<>();


    String strFrom = "", strData = "", category_id = "";
    int page = 1;
    FlurryAdInterstitial mFlurryAdInterstitial;
    String strSearchKeyword = "top news";
    String strTemp = "";

    ArticlesModel mArticlesModel;

    int fragCount;
    Activity mActivity;


    Request request;
    boolean isProgressVisible = false;

    public static SearchSourceFragment newInstance(int instance) {
        Bundle args = new Bundle();
        args.putInt(ARGS_INSTANCE, instance);
        SearchSourceFragment fragment = new SearchSourceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static SearchSourceFragment newInstance(Object object) {
        Bundle args = new Bundle();
        //args.putInt(ARGS_INSTANCE, instance);
        args.putSerializable(ARGS_INSTANCE, (Serializable) object);
        SearchSourceFragment fragment = new SearchSourceFragment();
        fragment.setArguments(args);
        return fragment;
    }


    public SearchSourceFragment() {
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
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        try {

            ButterKnife.bind(this, view);

            Bundle args = getArguments();


            if (args != null) {
                Object obj = (Object) args.getSerializable(ARGS_INSTANCE);

                if (obj instanceof Integer) {
                    fragCount = (int) obj;
                    //fragCount = args.getInt(ARGS_INSTANCE);
                }
                if (obj instanceof String) {
                    strSearchKeyword = (String) obj;
                    //fragCount = args.getInt(ARGS_INSTANCE);
                }
                if (obj instanceof ArticlesModel) {
                    mArticlesModel = (ArticlesModel) obj;
                    //fragCount = args.getInt(ARGS_INSTANCE);
                }

                if (mArticlesModel != null) {
                    App.showLog("====mArticlesModel====not null==");

                    if (StringUtils.isValidString(mArticlesModel.id) == true)
                        strSearchKeyword = mArticlesModel.id;

                }

                App.showLog("==fragCount==" + fragCount);
                App.showLog("==strSearchKeyword==" + strSearchKeyword);
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

            //((MainActivity) getActivity()).updateToolbarTitle((fragCount == 0) ? "Home" : "Sub Home " + fragCount);

            ((MainActivity) getActivity()).updateToolbarTitle(("Search news"));


            if (recyclerView != null) {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                //GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
                recyclerView.setLayoutManager(linearLayoutManager);
                //recyclerView.setHasFixedSize(true);
            }

            initialization();
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
                        //materialRefreshLayout.setLoadMore(false);
                        //App.setStopLoadingMaterialRefreshLayout(materialRefreshLayout);

                        asyncGetNewsList();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            if (strSearchKeyword != null) {
                etSearch.setText(strSearchKeyword);
            }

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
            page = 1;
            arrayListArticlesModel = new ArrayList<>();
            isProgressVisible = true;
            asyncGetNewsList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void asyncGetNewsList() {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);


            OkHttpClient httpClient = new OkHttpClient();
            //https://newsapi.org/v2/top-headlines?sources=bbc-news&apiKey=462f5f3ede2841408e9ef575919befe5
            String url = "https://newsapi.org/v2/everything?q=" + strSearchKeyword + "&apiKey=" + App.strNewsApiKey + "&page=" + page;

            if (request != null) {
                httpClient.newCall(request).cancel();
            }

            if (isProgressVisible == true) {
                isProgressVisible = false;
                progressBar.setVisibility(View.VISIBLE);
            }

            request = new Request.Builder()
                    .url(url)
                    .build();

            //httpClient.newCall(request).enqueue(new Callback() {
            App.getClient().newCall(request).enqueue(new Callback() {
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


                                        App.insertArticlesModelList(realm, topHeadLinesResponse.arrayListArticlesModel);
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
                            .placeholder(R.drawable.ic_az_news_reader)
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
