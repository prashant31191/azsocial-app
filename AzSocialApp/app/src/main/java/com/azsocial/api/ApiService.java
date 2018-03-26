package com.azsocial.api;


import com.azsocial.api.model.NewsChannelsResponse;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;


public interface ApiService {



    @Multipart
    @POST("{path}")
    Call<NewsChannelsResponse> getSourceList(
            @Path("path") String path,
            @PartMap() Map<String, RequestBody> partMap
    );


/*

    @Multipart
    @POST("rest/{op}")
    Call<UserProfileDataResponse> setUserProfilePhoto(
            @Path("op") String op,
            @Part("photo\"; filename=\"myfile.jpg\" ") RequestBody filePhoto,
            @PartMap() Map<String, RequestBody> partMap
    );

*/


}