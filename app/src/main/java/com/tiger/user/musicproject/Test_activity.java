package com.tiger.user.musicproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tiger.user.musicproject.Model.PlayListAdapter;
import com.tiger.user.musicproject.Model.SongAdapter;
import com.tiger.user.musicproject.YoutubeLinkService.GetFileUrl;
import com.tiger.user.musicproject.YoutubeLinkService.MainData;
import com.tiger.user.musicproject.YoutubeLinkService.YoutubeStreamer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Test_activity extends AppCompatActivity {
    private static String YOUTUBE_URL = "https://www.youtube.com/watch?v=";
    private static String TAG= "Test_Activity";
    private String video_id;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_activity);
        progressDialog = new ProgressDialog(Test_activity.this);
        progressDialog.setMessage("Loading URL...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        final Intent data = getIntent();
        Button button = (Button)findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(data != null){
                    String output = "none";
                    video_id = data.getStringExtra(SongAdapter.VIDEO_ID);
                    TextView id = (TextView)findViewById(R.id.content_2);
                    id.setText(video_id);
                    class Loader implements Runnable{
                        volatile String output;
                        @Override
                        public void run() {
                            GetFileUrl getFileUrl = GetFileUrl.retrofit.create(GetFileUrl.class);
                            Call<MainData> call = getFileUrl.Info(YOUTUBE_URL + video_id);
                            Log.d(TAG, "GetDirectLink: " + YOUTUBE_URL + video_id);
                            call.enqueue(new Callback<MainData>() {
                                @Override
                                public void onResponse(Call<MainData> call, Response<MainData> response) {
                                    output = response.body().info.getFormat().get(0).getUrl();
                                    class updateUI implements Runnable{
                                        String output;
                                        public updateUI(String output){
                                            this.output = output;
                                        }
                                        @Override
                                        public void run() {
                                            TextView content = (TextView)findViewById(R.id.content_1);
                                            content.setText(output);
                                        }
                                    }
                                    Handler handler = new Handler();
                                    handler.post(new updateUI(output));
                                    Log.d(TAG, "onResponse: "+ response.code());
                                }

                                @Override
                                public void onFailure(Call<MainData> call, Throwable t) {
                                    Log.d(TAG, "onFailure: " + t.getMessage());

                                }
                            });
                        }
                    }
                    Thread thread = new Thread(new Loader());
                    thread.start();
                }
            }
        });
    }
}
