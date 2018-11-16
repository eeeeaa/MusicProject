package com.tiger.user.musicproject.Reference;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.tiger.user.musicproject.Management.CredentialsHandler;
import com.tiger.user.musicproject.Model.SongAdapter;
import com.tiger.user.musicproject.R;

import java.util.ArrayList;
import java.util.HashMap;
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
        final ProgressBar progressBar = (ProgressBar)v.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
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
                        progressBar.setVisibility(View.GONE);
                        Log.d("Track name",track.name);
                        Log.d("Track album",track.album.id);
                        tracks.add(track);
                    }
                    //passData(tracks.get(0).album.id);
                    Log.d("Track ID",tracks.get(0).album.id);
                    initRecyclerView(getContext(),tracks);
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });

        }
        return v;
    }
    ////////////Passing Data/////////////////
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

    //////////////Spotify///////////////////
    private void setServiceAPI(){
        Log.d(TAG, "Setting Spotify API Service");
        SpotifyApi api = new SpotifyApi();
        api.setAccessToken(CredentialsHandler.getToken(getContext()));
        spotifyService = api.getService();
    }
    ///////////////////////////////////////

    /////////////////RecyclerView Initialize/////////////
    private void initRecyclerView(Context context,ArrayList<Track> tracks){
        Log.d(TAG, "initRecyclerView: started");
        RecyclerView recyclerView = v.findViewById(R.id.tracks_view);
        SongAdapter songAdapter = new SongAdapter(tracks,context);
        recyclerView.setAdapter(songAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }
}
