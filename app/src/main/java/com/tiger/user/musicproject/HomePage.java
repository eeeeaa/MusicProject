package com.tiger.user.musicproject;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.ProgressBar;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistItem;
import com.tiger.user.musicproject.Management.GoogleGlobalCredential;
import com.tiger.user.musicproject.Management.RecyclerViewInitializer;
import com.tiger.user.musicproject.Management.YoutubeCaller;
import com.tiger.user.musicproject.YoutubeLinkService.MainData;

import java.io.IOException;
import java.util.ArrayList;

public class HomePage extends Fragment {
    private RecyclerView homeView;
    private String TAG = "HUB";
    private YouTube youTube;
    private  YouTube.Search.List query;
    private static final String KEY = "AIzaSyC96AxLHwohCVm9lQhJu5PVsV_hBjFRhNY";
    private static final String PACKAGENAME = "com.tiger.user.musicproject";
    private static final String SHA1 = "5F:F5:25:18:40:CE:15:1E:55:F3:50:31:D0:35:98:8C:55:9C:88:3E";

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
                SharedPreferences prefs = getActivity().getSharedPreferences(MainHub.TOKEN_TAG,Context.MODE_PRIVATE);
                if(prefs != null){
                    String token = prefs.getString(MainHub.TOKEN_TAG,null);
                    Log.d(TAG, "TokenUser: " + token);
                    final ArrayList<PlaylistItem> outputList = youtubeCaller.GetVideoFromPlaylistID("RDEMd7yV4DVtFoa61a7qkl5eCg",30,token);
                    RecyclerViewInitializer.initRecyclerViewPlayList(getContext(),v,outputList);
                }
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
