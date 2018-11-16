package com.tiger.user.musicproject.Reference;

import android.app.ProgressDialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.tiger.user.musicproject.MainHub;
import com.tiger.user.musicproject.R;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
//****************FOR REFERENCE,CODE DOESN'T WORK!!***************************

public class PlayingMusic extends AppCompatActivity implements MediaPlayer.OnBufferingUpdateListener,MediaPlayer.OnCompletionListener {
    private ImageButton play_pause;
    private SeekBar seekBar;
    private TextView timer;

    private MediaPlayer mediaPlayer;
    private int mediaTimeLength;
    private int realTimeLength;
    final Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /////////////////// Music Player ////////////////////////

        play_pause = (ImageButton)findViewById(R.id.play_pause);
        seekBar = (SeekBar)findViewById(R.id.seekbar);
        seekBar.setMax(99); //0-99
        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(mediaPlayer.isPlaying()){
                    SeekBar seekBar = (SeekBar)view;
                    int playPosition = (mediaTimeLength/100)*seekBar.getProgress();
                    mediaPlayer.seekTo(playPosition);
                }
                return false;
            }
        });
        play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog mDialog = new ProgressDialog(PlayingMusic.this);
                AsyncTask<String,String,String> musicPlay = new AsyncTask<String, String, String>() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        mDialog.setMessage("Please Wait");
                        mDialog.show();
                    }

                    @Override
                    protected String doInBackground(String... strings) {
                        try {
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            mediaPlayer.setDataSource(strings[0]);
                            mediaPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return "";
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                        mediaTimeLength = mediaPlayer.getDuration();
                        realTimeLength = mediaTimeLength;
                        if(!mediaPlayer.isPlaying()){
                            mediaPlayer.start();
                            play_pause.setImageResource(R.drawable.ic_pause);
                        }else{
                            mediaPlayer.pause();
                            play_pause.setImageResource(R.drawable.ic_play);
                        }
                        UpdateSeekBar();
                        mDialog.dismiss();
                    }
                };
                musicPlay.execute("http://www.noiseaddicts.com/samples_1w72b820/2559.mp3");
            }
        });
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnBufferingUpdateListener(PlayingMusic.this);
        mediaPlayer.setOnCompletionListener(PlayingMusic.this);
        /////////////////////////////////////////////////////////
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
        seekBar.setSecondaryProgress(i);

    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        play_pause.setImageResource(R.drawable.ic_play);


    }

    public void UpdateSeekBar(){
        timer =(TextView)findViewById(R.id.textTimer);
        seekBar.setProgress((int)(((float)mediaPlayer.getCurrentPosition()/mediaTimeLength)*100));
        if(mediaPlayer.isPlaying()){
            Runnable updater = new Runnable() {
                @Override
                public void run() {
                    UpdateSeekBar();
                    realTimeLength -= 1000;
                    timer.setText(String.format("%d:%d",
                            TimeUnit.MILLISECONDS.toMinutes(realTimeLength),
                            (TimeUnit.MILLISECONDS.toSeconds(realTimeLength)
                                    -TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(realTimeLength))
                            )));
                }
            };
            handler.postDelayed(updater,1000);
        }
    }
}
