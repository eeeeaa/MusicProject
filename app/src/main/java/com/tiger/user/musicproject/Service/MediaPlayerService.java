package com.tiger.user.musicproject.Service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.MediaSessionManager;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.tiger.user.musicproject.R;
import com.tiger.user.musicproject.Test_player;

import java.io.IOException;

public class MediaPlayerService extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener,
        AudioManager.OnAudioFocusChangeListener {
    private MediaPlayer mediaPlayer;
    private String mediaFile;
    private int resumePosition;
    private AudioManager audioManager;

    private final IBinder iBinder = new LocalBinder();

    Intent seekIntent;
    int mediaPosition;
    int mediaMax;
    private final Handler handler = new Handler();
    public static final String BROADCAST_ACTION = "com.tiger.user.musicproject.seekprogress";
    private boolean killingRunnable;

    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    //mediaPlayer section
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        stopMedia();
        stopSelf();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Log.d("MediaPlayer Error", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Log.d("MediaPlayer Error", "MEDIA ERROR SERVER DIED " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Log.d("MediaPlayer Error", "MEDIA ERROR UNKNOWN " + extra);
                break;
        }
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        playMedia();
        setupHandler();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        if(!mediaPlayer.isPlaying()){
            playMedia();
        }
    }

    //audioFocus section
    @Override
    public void onAudioFocusChange(int focusState) {
        switch (focusState) {
            case AudioManager.AUDIOFOCUS_GAIN:
                if (mediaPlayer == null) initMediaPlayer();
                else if (!mediaPlayer.isPlaying()) mediaPlayer.start();
                mediaPlayer.setVolume(1.0f, 1.0f);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                if (mediaPlayer.isPlaying()) mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if (mediaPlayer.isPlaying()) mediaPlayer.pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                if (mediaPlayer.isPlaying()) mediaPlayer.setVolume(0.1f, 0.1f);
                break;
        }
    }

    private boolean requestAudioFocus() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            return true;
        }
        return false;
    }

    private boolean removeAudioFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                audioManager.abandonAudioFocus(this);
    }

    public class LocalBinder extends Binder {
        public MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }

    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.reset();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(mediaFile);
        } catch (IOException e) {
            e.printStackTrace();
            stopSelf();
        }
        mediaPlayer.prepareAsync();
    }

    private void setupHandler() {
        handler.removeCallbacks(sendUpdatesToUI);
        killingRunnable = true;
        handler.postDelayed(sendUpdatesToUI, 1000);
    }

    private Runnable sendUpdatesToUI = new Runnable() {
        @Override
        public void run() {
            if(killingRunnable) {
                LogMediaPosition();
                handler.postDelayed(this, 1000);
            }else{
                return;
            }
        }
    };

    private void LogMediaPosition(){
        if(mediaPlayer.isPlaying()){
            mediaPosition = mediaPlayer.getCurrentPosition();
            mediaMax = mediaPlayer.getDuration();
            seekIntent.putExtra("counter",String.valueOf(mediaPosition));
            seekIntent.putExtra("mediamax",String.valueOf(mediaMax));
            sendBroadcast(seekIntent);
        }
    }

    private void updateSeekPos(Intent intent) {
        int seekPos = intent.getIntExtra("seekpos",0);
        if(mediaPlayer.isPlaying()){
            killingRunnable = false;
            handler.removeCallbacks(sendUpdatesToUI);
            mediaPlayer.seekTo(seekPos);
            setupHandler();
        }
    }

    private void playMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    private void stopMedia() {
        if (mediaPlayer == null) return;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    private void pauseMedia() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            resumePosition = mediaPlayer.getCurrentPosition();
        }
    }

    private void resumeMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(resumePosition);
            mediaPlayer.start();
        }
    }

    private void replayMedia() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            initMediaPlayer();
        }
    }

    //serviceLifeCycle section
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registerReceiverForSeekbarChange();
        registerReceiverForReplay();
        try {
            mediaFile = intent.getExtras().getString("media");
            registerReceiver();
            registerReceiverForDestroy();
            seekIntent = new Intent(BROADCAST_ACTION);
        } catch (NullPointerException e) {
            stopSelf();
        }

        if (requestAudioFocus() == false) {
            //Could not gain focus
            stopSelf();
        }

        if (mediaFile != null)
            initMediaPlayer();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(sendUpdatesToUI);
        unregisterReceiver(playNewAudio);
        unregisterReceiver(destroyService);
        unregisterReceiver(updateSeekBar);
        unregisterReceiver(replayPlayer);
        super.onDestroy();
        if (mediaPlayer != null) {
            stopMedia();
            mediaPlayer.release();
        }
        removeAudioFocus();
    }

    //broadcast section
    private BroadcastReceiver playNewAudio  = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }else if(!mediaPlayer.isPlaying()){
                mediaPlayer.start();
            }
        }
    };

    public void registerReceiver() {
        IntentFilter filter = new IntentFilter(Test_player.Broadcast_PLAY);
        registerReceiver(playNewAudio, filter);
    }

    private BroadcastReceiver destroyService  = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onDestroy();
        }
    };

    public void registerReceiverForDestroy() {
        IntentFilter filter = new IntentFilter(Test_player.Broadcast_DESTROY);
        registerReceiver(destroyService, filter);
    }

    private BroadcastReceiver updateSeekBar = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateSeekPos(intent);
        }

    };

    public void registerReceiverForSeekbarChange() {
        IntentFilter filter = new IntentFilter(Test_player.Broadcast_SEEKBAR);
        registerReceiver(updateSeekBar, filter);
    }

    public boolean isPlaying(){
        if(mediaPlayer.isPlaying()){
            return true;
        }else{
            return false;
        }
    }

    private BroadcastReceiver replayPlayer = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            replayMedia();
        }

    };

    public void registerReceiverForReplay() {
        IntentFilter filter = new IntentFilter(Test_player.Broadcast_REPLAY);
        registerReceiver(replayPlayer, filter);
    }

}
