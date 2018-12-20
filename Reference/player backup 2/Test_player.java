package com.tiger.user.musicproject;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.tiger.user.musicproject.FavoriteService.FavoriteCommand;
import com.tiger.user.musicproject.FavoriteService.FavoriteMain;
import com.tiger.user.musicproject.FavoriteService.FavoriteModel;
import com.tiger.user.musicproject.Management.BlurTransformation;
import com.tiger.user.musicproject.Management.ColorManagement;
import com.tiger.user.musicproject.Management.CustomToast;
import com.tiger.user.musicproject.Model.SongAdapter;
import com.tiger.user.musicproject.Service.MediaPlayerService;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class Test_player extends Fragment implements SeekBar.OnSeekBarChangeListener{
    private static String TAG = "Player";
    View v;
    ImageButton bar_play;
    TextView bar_title;
    TextView bar_artist;
    SlidingUpPanelLayout mLayout;
    ImageView bar_cover_art;
    ProgressBar bar_progress;
    ProgressBar bar_play_progress;

    TextView full_title;
    TextView full_artist;
    ImageView full_bg;
    ImageButton favorite_button;
    TextView full_currentTime;
    ImageButton full_replay_button;

    RelativeLayout player_header;
    RelativeLayout player_main;

    MediaPlayer mediaPlayer;
    String urlForPlayer;
    String constant = "https://www.youtube.com/watch?v=";
    String tempUrl;
    boolean pauseState;
    boolean checked;
    boolean CheckBox;
    private MediaPlayerService player;
    boolean serviceBound = false;
    String titleForDisplay;

    private SeekBar full_seekBar;
    private int seekMax;
    private static int songEnded = 0;
    boolean mBroadcastIsRegistered;
    Intent intentSeekBar;

    public static final String Broadcast_PLAY = "com.tiger.user.musicproject.playPauseAudio";
    public static final String Broadcast_DESTROY = "com.tiger.user.musicproject.destroy";
    public static final String Broadcast_SEEKBAR = "com.tiger.user.musicproject.seekBar";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.player_bar,container,false);

        bar_play = (ImageButton)v.findViewById(R.id.play_header_button);
        bar_play.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                pauseResume();
            }
        });
        bar_title = (TextView)v.findViewById(R.id.player_header_title);
        bar_artist = (TextView)v.findViewById(R.id.player_header_artist);
        bar_cover_art = (ImageView)v.findViewById(R.id.bar_cover_art);
        bar_progress = (ProgressBar)v.findViewById(R.id.bar_progress);
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(),"font/roboto_regular.ttf");
        bar_title.setTypeface(typeface);
        bar_artist.setTypeface(typeface);
        bar_title.setSelected(true);
        bar_artist.setSelected(true);
        bar_play_progress = (ProgressBar)v.findViewById(R.id.bar_play_progress);
        bar_play_progress.setVisibility(View.GONE);


        bar_play.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                pauseResume();
            }
        });

        full_title = (TextView) v.findViewById(R.id.player_full_title);
        full_artist = (TextView) v.findViewById(R.id.player_full_artist);
        full_bg = (ImageView)v.findViewById(R.id.play_full_cover_art);
        full_title.setTypeface(typeface);
        full_artist.setTypeface(typeface);
        full_title.setSelected(true);
        full_artist.setSelected(true);
        //seek bar
        full_seekBar = (SeekBar)v.findViewById(R.id.player_full_seekBar);
        full_seekBar.setEnabled(false);
        //Current time
        full_currentTime = (TextView)v.findViewById(R.id.player_full_current_time);
        //replay
        full_replay_button = (ImageButton)v.findViewById(R.id.player_full_replay_button);

        player_header = (RelativeLayout)v.findViewById(R.id.player_header);
        player_main = (RelativeLayout)v.findViewById(R.id.player_main);

        favorite_button = (ImageButton)v.findViewById(R.id.favorite_button);

        mLayout = ((MainHub)getActivity()).getmLayout();
        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                bar_play.setAlpha(1-slideOffset);
                bar_title.setAlpha(1-slideOffset);
                bar_artist.setAlpha(1-slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if(newState.equals(SlidingUpPanelLayout.PanelState.EXPANDED)&&
                        previousState.equals(SlidingUpPanelLayout.PanelState.COLLAPSED)){
                    bar_title.setVisibility(panel.GONE);
                    bar_artist.setVisibility(panel.GONE);
                    bar_play.setVisibility(panel.GONE);
                    mLayout.setDragView(panel);


                }

                else {

                    bar_title.setVisibility(panel.VISIBLE);
                    bar_artist.setVisibility(panel.VISIBLE);
                    bar_play.setVisibility(panel.VISIBLE);
                }
            }
        });
        init();
        setListerners();
        registerReceiver();
        return v;
    }

    //////////////////// Main Player ///////////////////////////
    public static class Data{
        public List<Format> formats;
    }

    public static class Format{
        public String url;
    }

    public static class InputMp3{
        public Data info;
    }

    public interface FileInterface{
        @GET("api/info")
        Call<InputMp3> Info(@Query("url") String url);
    }

    public void init(){
        Bundle data = getArguments();
        if(data != null) {

            final String title = data.getString("song_title");
            String artist = data.getString("song_artist");
            final String img_url = data.getString("img_url");
            final String url = data.getString(SongAdapter.VIDEO_ID);
            findVideo(url,title,img_url);
            favorite_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            FavoriteCommand favoriteCommand = FavoriteMain.getRetrofit().create(FavoriteCommand.class);
                            Call<FavoriteModel> call = favoriteCommand.findVideo(url);
                            call.enqueue(new retrofit2.Callback<FavoriteModel>() {
                                @Override
                                public void onResponse(Call<FavoriteModel> call, Response<FavoriteModel> response) {
                                    Log.d(TAG, "FindonResponse: "+ response.code());
                                    Log.d(TAG, "onResponse: " + response.raw());
                                    if(response.isSuccessful()) {
                                        if (response.body().videoId == null) {
                                            addVideo(title, img_url, url);
                                        } else {
                                            removeVideo(url);
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<FavoriteModel> call, Throwable t) {
                                    Log.d(TAG, "FindonFailure: " + t.getMessage());

                                }
                            });
                        }
                    });
                    thread.start();
                }
            });
            tempUrl = constant + url;
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            bar_play.setImageResource(R.color.transparent);
                            bar_play.setEnabled(false);
                            bar_play_progress.setVisibility(View.VISIBLE);
                        }
                    });
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("https://thawing-woodland-43310.herokuapp.com")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build(); //building a retrofit, calling this URL
                    FileInterface fileInterface = retrofit.create(FileInterface.class);
                    String str;
                    try {
                        Call<InputMp3> call = fileInterface.Info(tempUrl);
                        Data temp = call.execute().body().info;
                        Format temp2 = temp.formats.get(0);
                        str = temp2.url;
                        class StrUpdate implements Runnable {
                            String str;

                            StrUpdate(String str) {
                                this.str = str;
                            }

                            public void run() {
                                urlForPlayer = str;
                                titleForDisplay = title;
                                playAudio(urlForPlayer);
                            }
                        }
                        Handler h = new Handler(Looper.getMainLooper());
                        h.post(new StrUpdate(str)); //allows the thread to loop
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
            bar_title.setText(title);
            full_title.setText(title);
            bar_artist.setText(artist);
            full_artist.setText(artist);
            bar_progress.setVisibility(View.VISIBLE);

            ///////////////Color Generation////////////////////
            int imageDimension = (int) getResources().getDimension(R.dimen.bar_image_size);
            Target target = new Target() {
                @Override
                public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                    bar_progress.setVisibility(View.GONE);
                    assert bar_cover_art != null;
                    bar_cover_art.setImageBitmap(bitmap);
                    Palette.from(bitmap)
                            .generate(new Palette.PaletteAsyncListener() {
                                @Override
                                public void onGenerated(Palette palette) {
                                    Palette.Swatch textSwatch = palette.getVibrantSwatch();
                                    if (textSwatch == null) {
                                        int color = ColorManagement.getDominantColorFast(bitmap);
                                        GradientDrawable gd = (GradientDrawable) player_header.getBackground().getCurrent();
                                        int[] colors = {color, R.color.colorPrimary};
                                        gd.setColors(colors);
                                        player_main.setBackgroundColor(color);
                                        return;
                                    }
                                    GradientDrawable gd = (GradientDrawable) player_header.getBackground().getCurrent();
                                    int[] colors = {textSwatch.getRgb(), R.color.colorPrimary};
                                    gd.setColors(colors);
                                    player_main.setBackgroundColor(textSwatch.getRgb());
                                }
                            });
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    bar_progress.setVisibility(View.GONE);
                    bar_cover_art.setImageResource(R.drawable.placeholder);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };
            bar_cover_art.setTag(target);
            Picasso.get().load(img_url).resize(imageDimension, imageDimension).centerCrop().into(target);
            Picasso.get().load(img_url).fit().centerCrop()
                    .into(full_bg, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            full_bg.setImageResource(R.drawable.placeholder);
                        }
                    });
            //////////////////////////////////////////////////
        }
    }

    //////////////////////// Service Connections //////////////////////
    @Override
    public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
        if(fromUser){
            int seekPos = sb.getProgress();
            intentSeekBar.putExtra("seekpos",seekPos);
            getActivity().sendBroadcast(intentSeekBar);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bar_play_progress.setVisibility(View.GONE);
            bar_play.setEnabled(true);
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            player = binder.getService();
            serviceBound = true;
            CustomToast.makeTextNormal(getActivity(),""+titleForDisplay+" is playing",Toast.LENGTH_LONG);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    private void playAudio(String media) {
        if (!serviceBound) {
            bar_play.setImageResource(R.drawable.ic_pause);
            Intent playerIntent = new Intent(getActivity(), MediaPlayerService.class);
            playerIntent.putExtra("media", media);
            getActivity().startService(playerIntent);
            getActivity().bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private void pauseResume(){
        if(player.isPlaying()){
            bar_play.setImageResource(R.drawable.ic_play_no_circle);
        }else{
            bar_play.setImageResource(R.drawable.ic_pause);
        }
        Intent intent = new Intent(Broadcast_PLAY);
        getActivity().sendBroadcast(intent);
        Log.d("broadCastIntent","sent");
    }

    private void destroyService(){
        Intent intent = new Intent(Broadcast_DESTROY);
        getActivity().sendBroadcast(intent);
    }

    private BroadcastReceiver broadcastReceiver  = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent serviceIntent) {
            updateUI(serviceIntent);
        }
    };

    private void updateUI(Intent serviceIntent){
        String counter = serviceIntent.getStringExtra("counter");
        String mediamax = serviceIntent.getStringExtra("mediamax");
        int seekProgress = Integer.parseInt(counter);
        seekMax = Integer.parseInt(mediamax);
        full_seekBar.setMax(seekMax);
        full_seekBar.setProgress(seekProgress);
    }

    public void registerReceiver() {
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(MediaPlayerService.BROADCAST_ACTION));
        mBroadcastIsRegistered = true;
    }

    private void setListerners(){
        full_seekBar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("ServiceState", serviceBound);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        destroyService();
        if(mBroadcastIsRegistered) {
            getActivity().unregisterReceiver(broadcastReceiver);
        }
        super.onDestroy();
    }
    ///////////////////////////////////////////////////


    ///////////////// Favorite functions /////////////////////
    public void findVideo(final String video_id,final String videoName,final String thumbnail){
        Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            FavoriteCommand favoriteCommand = FavoriteMain.getRetrofit().create(FavoriteCommand.class);
                            Call<FavoriteModel> call = favoriteCommand.findVideo(video_id);
                            call.enqueue(new retrofit2.Callback<FavoriteModel>() {
                                @Override
                                public void onResponse(Call<FavoriteModel> call, Response<FavoriteModel> response) {
                                    Log.d(TAG, "FindonResponse: "+ response.code());
                                    Log.d(TAG, "onResponse: " + response.raw());
                                    if(response.isSuccessful()) {
                                        if (response.body().videoId == null) {
                                            favorite_button.setImageResource(R.drawable.ic_favorite_off);
                                        } else {
                                            favorite_button.setImageResource(R.drawable.ic_favorite_on);
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<FavoriteModel> call, Throwable t) {
                                    Log.d(TAG, "FindonFailure: " + t.getMessage());

                                }
                            });
                        }
                    });
                    thread.start();
    }
    public void addVideo(final String videoName, final String thumbnail, final String video_id){
        //add video
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                FavoriteCommand favoriteCommand = FavoriteMain.getRetrofit().create(FavoriteCommand.class);
                Call<ResponseBody> call = favoriteCommand.addVideo(videoName, thumbnail, video_id);
                call.enqueue(new retrofit2.Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.d(TAG, "AddonResponse: "+ response.code());
                        favorite_button.setImageResource(R.drawable.ic_favorite_on);
                        CustomToast.makeTextNormal(getActivity(),"video added",Toast.LENGTH_SHORT);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d(TAG, "AddonFailure: " + t.getMessage());
                    }
                });
            }
        });
        thread.start();

    }
    public void removeVideo(final String video_id){
        //remove video
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                FavoriteCommand favoriteCommand = FavoriteMain.getRetrofit().create(FavoriteCommand.class);
                Call<ResponseBody> call = favoriteCommand.removeVideo(video_id);
                call.enqueue(new retrofit2.Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.d(TAG, "RevonResponse: "+ response.code());
                        favorite_button.setImageResource(R.drawable.ic_favorite_off);
                        CustomToast.makeTextNormal(getActivity(),"video removed",Toast.LENGTH_SHORT);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d(TAG, "RevonFailure: " + t.getMessage());
                    }
                });

            }
        });
        thread.start();
    }
    ///////////////////////////////////////////////////////
}
