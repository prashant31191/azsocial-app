package com.azsocial.api.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by prashant.chovatiya on 2/21/2018.
 */

public class CommonResponse {

    @SerializedName("status")
    public String status;

    @SerializedName("msg")
    public String msg;
}
