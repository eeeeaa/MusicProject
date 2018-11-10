package com.tiger.user.musicproject;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.tiger.user.musicproject.Management.CredentialsHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class HomePage extends Fragment {
    private RecyclerView homeView;
    private String AUTH_TOKEN;
    private Button test;
    public static SpotifyService spotifyService;
    private String TAG = "HUB";

    View v;
    OnDataPass dataPasser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.home_page, container, false);
        homeView = (RecyclerView)v.findViewById(R.id.tracks_view);
        final TextView test = (TextView)v.findViewById(R.id.textView2);
        Bundle data = getArguments();
        if(data != null){
            AUTH_TOKEN = data.getString("TOKEN");
            Log.d("TOKEN",AUTH_TOKEN);
            setServiceAPI();
            Map<String,Object> options = new HashMap<String,Object>();
            options.put("limit",20);
            final ArrayList<Track> tracks = new ArrayList<Track>();
            spotifyService.getTopTracks(options, new Callback<Pager<Track>>() {
                @Override
                public void success(Pager<Track> trackPager, Response response) {
                    for(Track track:trackPager.items){
                        Log.d("Track name",track.name);
                        Log.d("Track album",track.album.id);
                        test.append(track.name+"\n");
                        tracks.add(track);
                    }
                    passData(tracks.get(0).album.id);
                    Log.d("Track ID",tracks.get(0).album.id);
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
        }
        return v;
    }
    ///////////////////////////////////////////
    public interface OnDataPass {
        public void onDataPass(String data);
    }
    public void passData(String data) {

        dataPasser.onDataPass(data);
    }
    //////////////////////////////////////////
    public void onAttach(Context context) {
        super.onAttach(context);
        dataPasser = (OnDataPass) context;
    }
    private void setServiceAPI(){
        Log.d(TAG, "Setting Spotify API Service");
        SpotifyApi api = new SpotifyApi();
        api.setAccessToken(CredentialsHandler.getToken(getContext()));
        spotifyService = api.getService();
    }

}
