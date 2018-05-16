package com.azsocial.api;


import com.azsocial.api.model.NewsChannelsResponse;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface ApiService {



    @Multipart
    @POST("{path}")
    Call<NewsChannelsResponse> getSourceList(
            @Path("path") String path,
            @PartMap() Map<String, RequestBody> partMap
    );


    @GET("{path}")
    Call<NewsChannelsResponse> getSourceList2(
            @Path("path") String path,
            @Query("apiKey") String apiKey,
            @Query("page") String page

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