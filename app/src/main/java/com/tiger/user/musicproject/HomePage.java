package com.tiger.user.musicproject;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistItem;
import com.tiger.user.musicproject.Management.RecyclerViewInitializer;
import com.tiger.user.musicproject.Management.YoutubeCaller;

import java.util.ArrayList;

public class HomePage extends Fragment {
    private RecyclerView homeView;
    private String TAG = "HUB";
    private YouTube youTube;
    private  YouTube.Search.List query;
    public static final String KEY = "AIzaSyC96AxLHwohCVm9lQhJu5PVsV_hBjFRhNY";
    public static final String PACKAGENAME = "com.tiger.user.musicproject";
    public static final String SHA1 = "5F:F5:25:18:40:CE:15:1E:55:F3:50:31:D0:35:98:8C:55:9C:88:3E";

    View v;
    OnDataPass dataPasser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.home_page, container, false);
        Button getList = (Button)v.findViewById(R.id.getlist);
        ProgressBar progressBar = (ProgressBar)v.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        getList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YoutubeCaller youtubeCaller = new YoutubeCaller(getContext(),KEY,PACKAGENAME,SHA1);
                final ArrayList<PlaylistItem> outputList = youtubeCaller.GetVideoFromPlaylistID("RDEMd7yV4DVtFoa61a7qkl5eCg",30);
                RecyclerViewInitializer.initRecyclerViewPlayList(getContext(),v,outputList);
            }
        });

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
}
