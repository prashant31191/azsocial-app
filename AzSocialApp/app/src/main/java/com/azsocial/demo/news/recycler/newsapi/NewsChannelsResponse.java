package com.azsocial.demo.news.recycler.newsapi;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Created by prashant.chovatiya on 1/12/2018.
 */

public class NewsChannelsResponse  extends RealmObject implements Serializable {

    @SerializedName("status")
    public String status;

    @SerializedName("sources")
    public RealmList<ArticlesModel> arrayListArticlesModel;

/*
    @PrimaryKey
    @Index*/
}
