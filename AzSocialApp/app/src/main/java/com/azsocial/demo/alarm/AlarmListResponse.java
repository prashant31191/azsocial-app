package com.azsocial.demo.alarm;

import com.azsocial.demo.news.recycler.newsapi.ArticlesModel;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by prashant.chovatiya on 1/12/2018.
 */

public class AlarmListResponse implements Serializable {

    @SerializedName("status")
    public String status;

    @SerializedName("msg")
    public String msg;

    @SerializedName("data")
    public ArrayList<AlarmData> arrayListAlarmData;



}

