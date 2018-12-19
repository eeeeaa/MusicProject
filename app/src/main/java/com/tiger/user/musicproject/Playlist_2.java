package com.tiger.user.musicproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.api.services.youtube.model.PlaylistItem;
import com.tiger.user.musicproject.Management.GoogleGlobalCredential;
import com.tiger.user.musicproject.Management.RecyclerViewInitializer;
import com.tiger.user.musicproject.Management.YoutubeCaller;

import java.util.ArrayList;


public class Playlist_2 extends Playlist_1 {



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_playlist_2, container, false);
        TAG = this.getClass().getName();
        progressBar = (ProgressBar)v.findViewById(R.id.progressBar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Playlist 2");
        playlist_id = "PLZPJVH6oXcDw_Pd-ukNc9kFyqYeJMz1Mq";
        youtubeCaller = new YoutubeCaller(getContext(),GoogleGlobalCredential.getKEY(),
                GoogleGlobalCredential.getPACKAGENAME(),GoogleGlobalCredential.getSHA1());
        recyclerViewInitializer = new RecyclerViewInitializer();
        swipeRefreshLayout = (SwipeRefreshLayout)v.findViewById(R.id.swipeContainer);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            swipeRefreshLayout.setProgressViewOffset(false, 0,((MainHub)((AppCompatActivity)getActivity())).getSupportActionBar().getHeight());
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Playlist_2.updateList().execute();
            }
        });
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.colorPrimaryDark));
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        new Playlist_2.loadingList().execute();
        return v;
    }

}
