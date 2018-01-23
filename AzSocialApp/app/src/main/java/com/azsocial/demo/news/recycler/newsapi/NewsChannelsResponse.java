package com.azsocial.demo.news.recycler.newsapi;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by prashant.chovatiya on 1/12/2018.
 */

public class NewsChannelsResponse implements Serializable {

    @SerializedName("status")
    public String status;

    @SerializedName("sources")
    public ArrayList<ArticlesModel> arrayListArticlesModel;

}
