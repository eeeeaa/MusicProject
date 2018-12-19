package com.tiger.user.musicproject.FavoriteService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FavoriteMain {
    private static Retrofit retrofit = null;
    public static Retrofit getRetrofit(){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        if(retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://fast-waters-85466.herokuapp.com")//change once uploaded
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}
