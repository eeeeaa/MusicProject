package com.tiger.user.musicproject.Management;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.SearchResult;
import com.tiger.user.musicproject.Model.PlayListAdapter;
import com.tiger.user.musicproject.Model.SongAdapter;
import com.tiger.user.musicproject.R;

import java.util.ArrayList;

public class RecyclerViewInitializer {
    private static String TAG = "RecyclerViewInit";
    private SongAdapter songAdapter;
    private PlayListAdapter playListAdapter;

    public void initRecyclerViewQuery(Context context,View v,ArrayList<SearchResult> songs){
        RecyclerView recyclerView = v.findViewById(R.id.tracks_view);
        songAdapter = new SongAdapter(songs,context);
        recyclerView.setAdapter(songAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
    }
    public void initRecyclerViewPlayList(Context context,View v,ArrayList<PlaylistItem> songs){
        RecyclerView recyclerView = v.findViewById(R.id.tracks_view);
        playListAdapter = new PlayListAdapter(songs,context);
        recyclerView.setAdapter(playListAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    public PlayListAdapter getPlayListAdapter() {
        return playListAdapter;
    }

    public SongAdapter getSongAdapter() {
        return songAdapter;
    }
}
