package com.azsocial.fragments.sub;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
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
import com.azsocial.api.model.ArticlesModel;
import com.azsocial.api.model.FilterModel;
import com.azsocial.api.model.NewsChannelsResponse;
import com.azsocial.fragments.BaseFragment;
import com.azsocial.utils.StringUtils;
import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class SourceFilterFragment extends BaseFragment {

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


    int page = 1;
    FilterModel mFilterModel;


    Activity mActivity;

    public static SourceFilterFragment newInstance(int instance) {
        Bundle args = new Bundle();
        args.putInt(ARGS_INSTANCE, instance);
        SourceFilterFragment fragment = new SourceFilterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static SourceFilterFragment newInstance(Object object) {
        Bundle args = new Bundle();
        //args.putInt(ARGS_INSTANCE, instance);
        args.putSerializable(ARGS_INSTANCE, (Serializable) object);
        SourceFilterFragment fragment = new SourceFilterFragment();
        fragment.setArguments(args);
        return fragment;
    }


    public SourceFilterFragment() {
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
        View view = inflater.inflate(R.layout.fragment_sub_topheadline, container, false);

        try {

            ButterKnife.bind(this, view);

            Bundle args = getArguments();


            if (args != null) {
                Object obj = (Object) args.getSerializable(ARGS_INSTANCE);

                if (obj instanceof FilterModel) {
                    mFilterModel = (FilterModel) obj;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mFilterModel != null) {
            ((MainActivity) getActivity()).updateToolbarTitle(("Filter " + StringUtils.setString(mFilterModel.strFilterKey) + " (" + StringUtils.setString(mFilterModel.strFilterValue) + ")"));

        } else {
            ((MainActivity) getActivity()).updateToolbarTitle(("Filter source"));
        }
        {

        }

        if (recyclerView != null) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
            recyclerView.setLayoutManager(gridLayoutManager);
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
            materialRefreshLayout.setLoadMore(false);

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


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void asyncGetNewsList() {
        try {

            OkHttpClient httpClient = new OkHttpClient();

            String url = "https://newsapi.org/v2/sources?apiKey=" + App.strNewsApiKey + "&" + mFilterModel.strFilterKey + "=" + mFilterModel.strFilterValue + "&page=" + page;

            Request request = new Request.Builder()
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
                                    NewsChannelsResponse newsChannelsResponse = gson.fromJson(result.toString(), NewsChannelsResponse.class);
                                    App.setStopLoadingMaterialRefreshLayout(materialRefreshLayout);
                                    if (newsChannelsResponse != null && newsChannelsResponse.arrayListArticlesModel != null) {
                                        //arrayListArticlesModel = newsChannelsResponse.arrayListArticlesModel;
                                        if (page == 1) {
                                            arrayListArticlesModel = newsChannelsResponse.arrayListArticlesModel;
                                        } else {
                                            arrayListArticlesModel.addAll(newsChannelsResponse.arrayListArticlesModel);
                                        }
                                        page = page + 1;
                                        setStaticData(false);
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
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_source_list, viewGroup, false);
            VersionViewHolder viewHolder = new VersionViewHolder(view);
            return viewHolder;
        }


        @Override
        public void onBindViewHolder(final VersionViewHolder versionViewHolder, final int i) {
            try {
                ArticlesModel mPEArticleModel = mArrListmPEArticleModel.get(i);

                versionViewHolder.tvTitle.setText(mPEArticleModel.name);
                versionViewHolder.tvDate.setText(mPEArticleModel.country);
                versionViewHolder.tvTime.setText(mPEArticleModel.language);

                versionViewHolder.tvDetail.setText(mPEArticleModel.id + "\n" + mPEArticleModel.category);
                versionViewHolder.tvLink.setText(mPEArticleModel.url);

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

                        if (mFragmentNavigation != null && mArrListmPEArticleModel.get(i) != null) {
                            //mFragmentNavigation.pushFragment(NewsFragment.newInstance(fragCount + 1));
                            //mFragmentNavigation.pushFragment(TopHeadLinesFragment.newInstance(fragCount + 1));
                            mFragmentNavigation.pushFragment(TopHeadLinesFragment.newInstance((Object) mArrListmPEArticleModel.get(i)));
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
            ImageView ivFavourite;
            RelativeLayout rlMain;
            CardView cvItem;


            public VersionViewHolder(View itemView) {
                super(itemView);


                cvItem = (CardView) itemView.findViewById(R.id.cvItem);
                cvItem = (CardView) itemView.findViewById(R.id.cvItem);
                rlMain = (RelativeLayout) itemView.findViewById(R.id.rlMain);
                tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
                tvDate = (TextView) itemView.findViewById(R.id.tvDate);
                tvTime = (TextView) itemView.findViewById(R.id.tvTime);
                tvDetail = (TextView) itemView.findViewById(R.id.tvDetail);
                tvLink = (TextView) itemView.findViewById(R.id.tvLink);

                ivFavourite = (ImageView) itemView.findViewById(R.id.ivFavourite);
            }

        }
    }


}
