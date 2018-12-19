package com.tiger.user.musicproject.FavoriteService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

//TODO change the url to favorite server once uploaded
public interface FavoriteCommand {

    @GET("/addVideo")
    Call<ResponseBody> addVideo(@Query("videoName") String uri,
                                 @Query("URLthumbnail")String thumbnail_url,
                                 @Query("videoId")String videoId);
    @GET("/removeVideo")
    Call<ResponseBody> removeVideo(@Query("videoId")String videoId);

    @GET("/findVideo")
    Call<FavoriteModel> findVideo(@Query("videoId")String videoId);

    @GET("/getallVideo")
    Call<List<FavoriteModel>> getallVIdeo();
}
