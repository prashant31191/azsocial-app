package com.azsocial.demo.alarm;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by prashant.chovatiya on 1/30/2018.
 */

public class AlarmData implements Serializable {

    @SerializedName("event_id")
    public String event_id;

    @SerializedName("title")
    public String title;

    @SerializedName("category")
    public String category;

    @SerializedName("category_image")
    public String category_image;

    @SerializedName("event_start")
    public String event_start;

    @SerializedName("event_end")
    public String event_end;

    @SerializedName("stop_repeat")
    public String stop_repeat;

    @SerializedName("event_alert")
    public String event_alert;

    @SerializedName("event_repeat")
    public String event_repeat;

}