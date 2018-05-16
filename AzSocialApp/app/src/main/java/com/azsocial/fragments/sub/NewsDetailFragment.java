package com.azsocial.fragments.sub;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.azsocial.App;
import com.azsocial.R;
import com.azsocial.activities.MainActivity;
import com.azsocial.api.model.ArticlesModel;
import com.azsocial.fragments.BaseFragment;
import com.azsocial.utils.StringUtils;
import com.azsocial.utils.TouchImageView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;


public class NewsDetailFragment extends BaseFragment {


    String TAG = "NewsDetailFragment";
    Activity mActivity;

    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvDate)
    TextView tvDate;
    @BindView(R.id.tvTime)
    TextView tvTime;
    @BindView(R.id.tvDetail)
    TextView tvDetail;
    @BindView(R.id.tvLink)
    TextView tvLink;

    @BindView(R.id.ivPhoto)
    TouchImageView ivPhoto;
    @BindView(R.id.ivClose)
    ImageView ivClose;
    @BindView(R.id.ivPhoto2)
    ImageView ivPhoto2;
    @BindView(R.id.ivFavourite)
    ImageView ivFavourite;

    @BindView(R.id.rlMain)
    RelativeLayout rlMain;
    @BindView(R.id.rlImageFullView)
    RelativeLayout rlImageFullView;
    @BindView(R.id.nsvData)
    NestedScrollView nsvData;

    @BindView(R.id.fabFavourite)
    FloatingActionButton fabFavourite;

    ArticlesModel mArticlesModel;
    Realm realm;

    public static NewsDetailFragment newInstance(Object object) {
        Bundle args = new Bundle();
        //args.putInt(ARGS_INSTANCE, instance);
        args.putSerializable(ARGS_INSTANCE, (Serializable) object);
        NewsDetailFragment fragment = new NewsDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }


    public NewsDetailFragment() {
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
        View view = inflater.inflate(R.layout.fragment_sub_news_detail, container, false);
        try {

            ButterKnife.bind(this, view);

            Bundle args = getArguments();
            if (args != null) {
                Object obj = (Object) args.getSerializable(ARGS_INSTANCE);

                if (obj instanceof ArticlesModel) {
                    mArticlesModel = (ArticlesModel) obj;
                    //fragCount = args.getInt(ARGS_INSTANCE);
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
        try {
            if (mArticlesModel != null) {
                if (StringUtils.isValidString(mArticlesModel.title) == true) {
                    ((MainActivity) getActivity()).updateToolbarTitle((mArticlesModel.title));
                } else {
                    ((MainActivity) getActivity()).updateToolbarTitle(("News detail"));
                }

                setStaticData(true);
            } else {
                ((MainActivity) getActivity()).updateToolbarTitle(("News detail"));
            }

            setSendDataAnalytics();
        } catch (Exception e) {
            App.showLog(TAG, "---onViewCreated--");
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


    private void setStaticData(boolean isSetAdapter) {
        try {


            realm = Realm.getInstance(App.getRealmConfiguration());

            App.showLog("=======setStaticData===");

            tvTitle.setText(mArticlesModel.title);
            tvDate.setText(mArticlesModel.publishedAt);
            tvTime.setText(mArticlesModel.author);

            tvDetail.setText(mArticlesModel.description);
            tvLink.setText(mArticlesModel.url);

            if (mArticlesModel.urlToImage != null && mArticlesModel.urlToImage.length() > 1) {
                ivPhoto.setVisibility(View.VISIBLE);

                Picasso.with(mActivity)
                        .load(mArticlesModel.urlToImage)
                        .placeholder(R.drawable.ic_az_news_reader)
                        .error(R.color.white)
                        .into(ivPhoto);

            } else {
                ivPhoto.setVisibility(View.GONE);
            }

            if (mArticlesModel.urlToImage != null && mArticlesModel.urlToImage.length() > 1) {
                ivPhoto2.setVisibility(View.VISIBLE);

                Picasso.with(mActivity)
                        .load(mArticlesModel.urlToImage)
                        .placeholder(R.drawable.ic_az_news_reader)
                        .error(R.color.white)
                        .into(ivPhoto2);

            } else {
                ivPhoto2.setVisibility(View.GONE);
            }


            fabFavourite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
             /*   if (searchView != null) {
                    searchView.setIconifiedByDefault(true);
                    searchView.requestFocus();
                }*/

                    if (view.isSelected() == true) {
                        App.showLog("=====fav added----Remove==");
                        if (realm != null) {
                            view.setSelected(false);
                            fabFavourite.setImageResource(R.drawable.ic_star_border_black_24dp);
                            App.removeFromFavouriteNews(realm, mArticlesModel);
                        }
                    } else {
                        App.showLog("=====fav not added----Add==");

                        if (realm != null) {
                            view.setSelected(true);
                            fabFavourite.setImageResource(R.drawable.ic_star_black_24dp);
                            App.addToFavouriteNews(realm, mArticlesModel);
                        }
                    }
                }
            });


            if (mArticlesModel.favourite.equalsIgnoreCase("1")) {
                fabFavourite.setSelected(true);
                fabFavourite.setImageResource(R.drawable.ic_star_black_24dp);
            }
            else
            {
                fabFavourite.setSelected(false);
                fabFavourite.setImageResource(R.drawable.ic_star_border_black_24dp);
            }


            ivPhoto2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setFullView(true);
                }
            });
            ivClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setFullView(false);
                }
            });

            setFullView(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setFullView(boolean isOpen) {
        try {
            if (isOpen == true) {
                rlImageFullView.setVisibility(View.VISIBLE);
                nsvData.setVisibility(View.GONE);
            } else {
                rlImageFullView.setVisibility(View.GONE);
                nsvData.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
