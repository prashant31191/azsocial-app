package com.azsocial.demo.news.recycler.newsapi;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Created by prashant.chovatiya on 1/12/2018.
 */

public class ArticlesModel extends RealmObject  implements Serializable {

    @SerializedName("author")
    public String author="";

    @SerializedName("title")
    public String title="";


    @PrimaryKey
    @Index
    @SerializedName("description") // user for news list, chanel name,
    public String description="";

    @SerializedName("url") // user for news list, chanel name,
    public String url="";

    @SerializedName("urlToImage")
    public String urlToImage="";

    @SerializedName("publishedAt")
    public String publishedAt="";


    //channnel list

    @SerializedName("id")
    public String id="";

    @SerializedName("name")
    public String name="";

    @SerializedName("category")
    public String category="";

    @SerializedName("language")
    public String language="";

    @SerializedName("country")
    public String country="";

}
