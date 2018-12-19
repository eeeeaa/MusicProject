package com.tiger.user.musicproject.Management;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.SearchResult;
import com.tiger.user.musicproject.FavoriteService.FavoriteModel;
import com.tiger.user.musicproject.MainHub;
import com.tiger.user.musicproject.Model.FavoriteAdapter;
import com.tiger.user.musicproject.Model.PlayListAdapter;
import com.tiger.user.musicproject.Model.SongAdapter;
import com.tiger.user.musicproject.R;

import java.util.ArrayList;

public class RecyclerViewInitializer {
    private static String TAG = "RecyclerViewInit";
    private SongAdapter songAdapter;
    private PlayListAdapter playListAdapter;
    private FavoriteAdapter favoriteAdapter;
    private static boolean scroll_down;

    public void initRecyclerViewQuery(final Context context, View v, ArrayList<SearchResult> songs){
        RecyclerView recyclerView = v.findViewById(R.id.tracks_view);
        songAdapter = new SongAdapter(songs,context);
        recyclerView.setAdapter(songAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (scroll_down) {
                    ((MainHub)context).getSupportActionBar().hide();
                } else {
                    ((MainHub)context).getSupportActionBar().show();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 70) {
                    //scroll down
                    scroll_down = true;

                } else if (dy < -5) {
                    //scroll up
                    scroll_down = false;
                }
            }
        });
    }
    public void initRecyclerViewPlayList(final Context context, View v, ArrayList<PlaylistItem> songs){
        RecyclerView recyclerView = v.findViewById(R.id.tracks_view);
        playListAdapter = new PlayListAdapter(songs,context);
        recyclerView.setAdapter(playListAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (scroll_down) {
                    ((MainHub)context).getSupportActionBar().hide();
                } else {
                    ((MainHub)context).getSupportActionBar().show();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 70) {
                    //scroll down
                    scroll_down = true;

                } else if (dy < -5) {
                    //scroll up
                    scroll_down = false;
                }
            }
        });
    }

    public void initRecyclerViewFavorite(final Context context, View v, ArrayList<FavoriteModel> songs){
        RecyclerView recyclerView = v.findViewById(R.id.tracks_view);
        favoriteAdapter = new FavoriteAdapter(songs,context);
        recyclerView.setAdapter(favoriteAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (scroll_down) {
                    ((MainHub)context).getSupportActionBar().hide();
                } else {
                    ((MainHub)context).getSupportActionBar().show();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 70) {
                    //scroll down
                    scroll_down = true;

                } else if (dy < -5) {
                    //scroll up
                    scroll_down = false;
                }
            }
        });
    }

    public PlayListAdapter getPlayListAdapter() {
        return playListAdapter;
    }

    public SongAdapter getSongAdapter() {
        return songAdapter;
    }

    public FavoriteAdapter getFavoriteAdapter(){
        return favoriteAdapter;
    }
}
