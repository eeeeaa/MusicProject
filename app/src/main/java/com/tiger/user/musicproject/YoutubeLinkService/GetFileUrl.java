package com.tiger.user.musicproject.YoutubeLinkService;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetFileUrl{
    @GET("/api/info")
    Call<MainData> Info(@Query("url") String uri);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://thawing-woodland-43310.herokuapp.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}