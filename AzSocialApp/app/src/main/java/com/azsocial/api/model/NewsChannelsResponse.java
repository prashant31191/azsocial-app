package com.azsocial.api.model;

import com.azsocial.api.model.ArticlesModel;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmList;
import io.realm.RealmObject;

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
