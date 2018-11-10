package com.tiger.user.musicproject;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.tiger.user.musicproject.Reference.PlayingMusic;


public class Player extends Fragment {
    private static final String CLIENT_ID = "8941735733ef4a848d2cd0434fef264a";
    private static final String REDIRECT_URI = "com.tiger.user.musicproject://callback";
    private SpotifyAppRemote mSpotifyAppRemote;

    View v;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_player, container, false);
        ImageButton play_pause = (ImageButton) v.findViewById(R.id.play_pause);

        final Bundle stream_id = getArguments();
        if (stream_id != null) {
            final String stream = stream_id.getString("Song_ID");
            play_pause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mSpotifyAppRemote.isConnected()) {
                        PlayMusic(stream);
                    }
                }
            });
        }
        return v;
    }

    private void PlayMusic(String mus_id) {
        Log.d("FinalURL","spotify:album:" + mus_id);
        mSpotifyAppRemote.getPlayerApi().play("spotify:album:" + mus_id);

    }
    @Override
    public void onStart() {
        super.onStart();
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();
        SpotifyAppRemote.connect(getContext(), connectionParams,
                new Connector.ConnectionListener() {

                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d("Player", "Connected!");

                    }

                    public void onFailure(Throwable throwable) {
                        Log.e("Player", throwable.getMessage(), throwable);

                    }
                });
    }

    @Override
    public void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);

    }

}
