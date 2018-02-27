package com.azsocial.demo.news.recycler.newsapi;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by prashant.chovatiya on 1/12/2018.
 */

//extends RealmObject
public class TopHeadLinesResponse   implements Serializable {

    @SerializedName("status")
    public String status;
/*
    @SerializedName("totalResults")
    public String totalResults;

    @SerializedName("articles")
    public RealmList<ArticlesModel> arrayListArticlesModel;*/

}
