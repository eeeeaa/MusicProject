package com.tiger.user.musicproject.Management;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.SearchResult;
import com.tiger.user.musicproject.Model.PlayListAdapter;
import com.tiger.user.musicproject.Model.SongAdapter;
import com.tiger.user.musicproject.R;

import java.util.ArrayList;

public class RecyclerViewInitializer {

    public static void initRecyclerViewQuery(Context context,View v,ArrayList<SearchResult> songs){
        RecyclerView recyclerView = v.findViewById(R.id.tracks_view);
        SongAdapter songAdapter = new SongAdapter(songs,context);
        recyclerView.setAdapter(songAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }
    public static void initRecyclerViewPlayList(Context context,View v,ArrayList<PlaylistItem> songs){
        RecyclerView recyclerView = v.findViewById(R.id.tracks_view);
        PlayListAdapter playListAdapter = new PlayListAdapter(songs,context);
        recyclerView.setAdapter(playListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }
}
