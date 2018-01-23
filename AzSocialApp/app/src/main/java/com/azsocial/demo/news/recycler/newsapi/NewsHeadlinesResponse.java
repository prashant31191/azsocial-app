package com.azsocial.demo.news.recycler.newsapi;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by prashant.chovatiya on 1/12/2018.
 */

public class NewsHeadlinesResponse implements Serializable {

    @SerializedName("totalResults")
    public String totalResults;

    @SerializedName("articles")
    public ArrayList<ArticlesModel> arrayListArticlesModel;

}
