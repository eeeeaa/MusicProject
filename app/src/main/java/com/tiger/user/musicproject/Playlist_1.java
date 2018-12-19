package com.tiger.user.musicproject;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.api.services.youtube.model.PlaylistItem;
import com.tiger.user.musicproject.Management.CustomToast;
import com.tiger.user.musicproject.Management.GoogleGlobalCredential;
import com.tiger.user.musicproject.Management.RecyclerViewInitializer;
import com.tiger.user.musicproject.Management.YoutubeCaller;
import com.tiger.user.musicproject.Model.PlayListAdapter;

import java.util.ArrayList;

public class Playlist_1 extends Fragment {
    String TAG = "Playlist1";
    String playlist_id;
    View v;
    ProgressBar progressBar;
    YoutubeCaller youtubeCaller;
    RecyclerViewInitializer recyclerViewInitializer;
    SwipeRefreshLayout swipeRefreshLayout;

    /////////////////////MENU///////////////////////
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_general, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_forward:
                if(YoutubeCaller.getNextPageToken() != null){
                    new updateList_next().execute();
                }
                return true;

            case R.id.menu_backward:
                if(YoutubeCaller.getPrevPageToken() != null){
                    new updateList_prev().execute();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    ////////////////////////////////////////////////////////

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_playlist_1, container, false);
        progressBar = (ProgressBar)v.findViewById(R.id.progressBar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Playlist 1");
        playlist_id = "PL3Xkcd7UEoUNP_NKOCDU0IyOpzT5hKsbD";
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
                new updateList().execute();
            }
        });
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.colorPrimaryDark));
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        new Playlist_1.loadingList().execute();
        return v;
    }

    public class loadingList extends AsyncTask<ArrayList<PlaylistItem>, Void, ArrayList<PlaylistItem>> {
        @Override
        protected ArrayList<PlaylistItem> doInBackground(ArrayList<PlaylistItem>... arrayLists) {

            SharedPreferences prefs = getActivity().getSharedPreferences(MainHub.TOKEN_TAG,Context.MODE_PRIVATE);
            if(prefs != null){
                String token = prefs.getString(MainHub.TOKEN_TAG,null);
                //Log.d(TAG, "TokenUser: " + token);
                final ArrayList<PlaylistItem> outputList = youtubeCaller.GetVideoFromPlaylistID(playlist_id,30,token,null,null);
                return outputList;
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<PlaylistItem> items) {
            super.onPostExecute(items);
            progressBar.setVisibility(View.GONE);
            if(items == null || items.isEmpty()){
                CustomToast.makeText(getContext(),"Connection Error",Toast.LENGTH_SHORT);
            }else {
                recyclerViewInitializer.initRecyclerViewPlayList(getContext(),v,items);
            }
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

    }

    public class updateList extends AsyncTask<ArrayList<PlaylistItem>, Void, ArrayList<PlaylistItem>> {
        @Override
        protected ArrayList<PlaylistItem> doInBackground(ArrayList<PlaylistItem>... arrayLists) {

            SharedPreferences prefs = getActivity().getSharedPreferences(MainHub.TOKEN_TAG,Context.MODE_PRIVATE);
            if(prefs != null){
                String token = prefs.getString(MainHub.TOKEN_TAG,null);
                //Log.d(TAG, "TokenUser: " + token);
                final ArrayList<PlaylistItem> outputList = youtubeCaller.GetVideoFromPlaylistID(playlist_id,30,token,null,null);
                return outputList;
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<PlaylistItem> items) {
            super.onPostExecute(items);
            if(items == null || items.isEmpty()){
                CustomToast.makeText(getContext(),"Connection Error",Toast.LENGTH_SHORT);
            }else {

                PlayListAdapter adapter = recyclerViewInitializer.getPlayListAdapter();
                if(adapter != null) {
                    adapter.clear();
                    adapter.addAll(items);
                }else {
                    new loadingList().execute();
                }
            }
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

    }

    public class updateList_next extends AsyncTask<ArrayList<PlaylistItem>, Void, ArrayList<PlaylistItem>> {
        @Override
        protected ArrayList<PlaylistItem> doInBackground(ArrayList<PlaylistItem>... arrayLists) {

            SharedPreferences prefs = getActivity().getSharedPreferences(MainHub.TOKEN_TAG,Context.MODE_PRIVATE);
            if(prefs != null){
                String token = prefs.getString(MainHub.TOKEN_TAG,null);
                //Log.d(TAG, "TokenUser: " + token);
                final ArrayList<PlaylistItem> outputList = youtubeCaller.GetVideoFromPlaylistID(playlist_id,30,token,YoutubeCaller.getNextPageToken(),null);
                return outputList;
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<PlaylistItem> items) {
            super.onPostExecute(items);
            if(items == null || items.isEmpty()){
                CustomToast.makeText(getContext(),"Connection Error",Toast.LENGTH_SHORT);
            }else {

                PlayListAdapter adapter = recyclerViewInitializer.getPlayListAdapter();
                if(adapter != null) {
                    adapter.clear();
                    adapter.addAll(items);
                }else {
                    new loadingList().execute();
                }
            }
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

    }

    public class updateList_prev extends AsyncTask<ArrayList<PlaylistItem>, Void, ArrayList<PlaylistItem>> {
        @Override
        protected ArrayList<PlaylistItem> doInBackground(ArrayList<PlaylistItem>... arrayLists) {

            SharedPreferences prefs = getActivity().getSharedPreferences(MainHub.TOKEN_TAG,Context.MODE_PRIVATE);
            if(prefs != null){
                String token = prefs.getString(MainHub.TOKEN_TAG,null);
                //Log.d(TAG, "TokenUser: " + token);
                final ArrayList<PlaylistItem> outputList = youtubeCaller.GetVideoFromPlaylistID(playlist_id,30,token,null,YoutubeCaller.getPrevPageToken());
                return outputList;
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<PlaylistItem> items) {
            super.onPostExecute(items);
            if(items == null || items.isEmpty()){
                CustomToast.makeText(getContext(),"Connection Error",Toast.LENGTH_SHORT);
            }else {

                PlayListAdapter adapter = recyclerViewInitializer.getPlayListAdapter();
                if(adapter != null) {
                    adapter.clear();
                    adapter.addAll(items);
                }else {
                    new loadingList().execute();
                }
            }
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

    }
}
