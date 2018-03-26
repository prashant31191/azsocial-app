package com.azsocial.demo.news.recycler;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.azsocial.App;
import com.azsocial.R;
import com.azsocial.api.model.ArticlesModel;
import com.azsocial.utils.AppFlags;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActNewsDashboard extends AppCompatActivity {

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.ivPhoto)
    ImageView ivPhoto;

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsing_toolbar;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tvTitle)
    TextView tvTitle;

    @BindView(R.id.tvDetail)
    TextView tvDetail;

    @BindView(R.id.tvLink)
    TextView tvLink;

    String strFrom = "", strData = "", category_id = "";
    ArticlesModel articlesModel;

    String strCountry = "ae,ar,at,au,be,bg,br,ca,ch,cn,co,cu,cz,de,eg,fr,gb,gr,hk,hu,id,ie,il,in,it,jp,kr,lt,lv,ma,mx,my,ng,nl,no,nz,ph,pl,pt,ro,rs,ru,sa,sa,se,sg,si,sk,th,tr,tw,ua,us,ve,za";
    String strLanguages = "ar,en,zh,de,es,fr,he,it,nl,no,pt,ru,se,ud";
    String strCategory = "business,entertainment,general,health,science,sports,technology";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.act_news_detail);
            ButterKnife.bind(this);

            setSupportActionBar(toolbar);

            getIntentData();
            initialization();
/* if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }*/
            toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_black_24dp);
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private void getIntentData() {
        Bundle bundle;
        if (getIntent() != null && getIntent().getExtras() != null) {
            bundle = getIntent().getExtras();
            if (bundle.getString(AppFlags.tagFrom) != null) {
                strFrom = bundle.getString(AppFlags.tagFrom);
            }


            if (bundle.getString(AppFlags.tagData) != null) {
                strData = bundle.getString(AppFlags.tagData);
            }

            if (bundle.getString(AppFlags.tagCatId) != null) {
                category_id = bundle.getString(AppFlags.tagCatId);
            }
            if (bundle.getSerializable(AppFlags.tagArticlesModel) != null) {
                articlesModel = (ArticlesModel)bundle.getSerializable(AppFlags.tagArticlesModel);
            }
        }

        App.showLog("====strFrom===" + strFrom);
        App.showLog("===strData====" + strData);
        App.showLog("===category_id====" + category_id);

    }

    private void initialization() {
        try {

            if(articlesModel !=null){

                collapsing_toolbar.setTitle(articlesModel.title);

                tvTitle.setText(articlesModel.title);
                tvDetail.setText(articlesModel.description);
                tvLink.setText(articlesModel.url);

                if (articlesModel.urlToImage != null && articlesModel.urlToImage.length() > 1) {
                    ivPhoto.setVisibility(View.VISIBLE);

                    Picasso.with(this)
                            .load(articlesModel.urlToImage)
                            .placeholder(R.color.clr_divider)
                            .error(R.color.white)
                            .fit()
                            .centerCrop()
                            .into(ivPhoto);

                } else {
                    ivPhoto.setVisibility(View.GONE);
                }
            }



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
