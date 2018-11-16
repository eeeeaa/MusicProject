package com.tiger.user.musicproject.YoutubeLinkService;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.annotations.SerializedName;
import com.tiger.user.musicproject.YoutubeLinkService.GetFileUrl;
import com.tiger.user.musicproject.YoutubeLinkService.MainData;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class YoutubeStreamer {
    private static String YOUTUBE_URL = "https://www.youtube.com/watch?v=";
    private static String TAG = "YoutubeStreamer";
    DataInterface mListener;
    public YoutubeStreamer(){
        super();
    }
    public void GetDirectLink(final String video_id){
        GetFileUrl getFileUrl = GetFileUrl.retrofit.create(GetFileUrl.class);
        Call<MainData> call = getFileUrl.Info(YOUTUBE_URL + video_id);
        Log.d(TAG, "GetDirectLink: " + YOUTUBE_URL + video_id);
        call.enqueue(new Callback<MainData>() {
            @Override
            public void onResponse(Call<MainData> call, Response<MainData> response) {
                mListener.responseData(response.body());
                Log.d(TAG, "onResponse: "+ response.code());
            }

            @Override
            public void onFailure(Call<MainData> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());

            }
        });
    }
    public void setOnDataListener(DataInterface listener) {
        mListener = listener;
    }

    public interface DataInterface {
        void responseData( MainData mainData);
    }
}
