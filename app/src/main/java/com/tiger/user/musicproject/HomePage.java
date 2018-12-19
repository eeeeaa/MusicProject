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

import com.google.api.services.youtube.model.SearchResult;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.tiger.user.musicproject.Management.CustomToast;
import com.tiger.user.musicproject.Management.GoogleGlobalCredential;
import com.tiger.user.musicproject.Management.RecyclerViewInitializer;
import com.tiger.user.musicproject.Management.YoutubeCaller;
import com.tiger.user.musicproject.Model.SongAdapter;

import java.util.ArrayList;

public class HomePage extends Fragment {
    private String TAG = "HUB";

    View v;
    OnDataPass dataPasser;
    ProgressBar progressBar;
    RecyclerViewInitializer recyclerViewInitializer;
    YoutubeCaller youtubeCaller;
    SwipeRefreshLayout swipeRefreshLayout;
    String query = null;

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


    public void setQuery(String query) {
        this.query = query;
    }

    public String getQuery() {
        if(query != null){
            return query;
        }else {
            return null;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.home_page, container, false);
        setQuery("Synthwave");
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.home_page);
        progressBar = (ProgressBar)v.findViewById(R.id.progressBar);
        recyclerViewInitializer = new RecyclerViewInitializer();
        youtubeCaller = new YoutubeCaller(getContext(),GoogleGlobalCredential.getKEY(),
                GoogleGlobalCredential.getPACKAGENAME(),GoogleGlobalCredential.getSHA1());
        swipeRefreshLayout = (SwipeRefreshLayout)v.findViewById(R.id.swipeContainer);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            swipeRefreshLayout.setProgressViewOffset(false, 0,((MainHub)((AppCompatActivity)getActivity())).getSupportActionBar().getHeight());
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new refreshList().execute();
            }
        });
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.colorPrimaryDark));
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        new loadingList().execute();
        return v;
    }

    //////////////LOAD DATA///////////////////
    public class loadingList extends AsyncTask<ArrayList<SearchResult>, Void, ArrayList<SearchResult>> {
        @Override
        protected ArrayList<SearchResult> doInBackground(ArrayList<SearchResult>... arrayLists) {
            SharedPreferences prefs = getActivity().getSharedPreferences(MainHub.TOKEN_TAG,Context.MODE_PRIVATE);
            if(prefs != null){
                String token = prefs.getString(MainHub.TOKEN_TAG,null);
                //Log.d(TAG, "TokenUser: " + token);
                final ArrayList<SearchResult> outputList = youtubeCaller.GetVideoFromQuery(getQuery(),30,token,null,null);
                return outputList;
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<SearchResult> items) {
            super.onPostExecute(items);
            progressBar.setVisibility(View.GONE);
            if(items == null || items.isEmpty()){
                CustomToast.makeText(getContext(),"Connection Error",Toast.LENGTH_SHORT);
            }else {
                recyclerViewInitializer.initRecyclerViewQuery(getContext(),v,items);
                SongAdapter adapter = recyclerViewInitializer.getSongAdapter();
            }
            swipeRefreshLayout.setRefreshing(false);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

    }
    public class refreshList extends AsyncTask<ArrayList<SearchResult>, Void, ArrayList<SearchResult>> {
        @Override
        protected ArrayList<SearchResult> doInBackground(ArrayList<SearchResult>... arrayLists) {
            SharedPreferences prefs = getActivity().getSharedPreferences(MainHub.TOKEN_TAG,Context.MODE_PRIVATE);
            if(prefs != null){
                String token = prefs.getString(MainHub.TOKEN_TAG,null);
                //Log.d(TAG, "TokenUser: " + token);
                final ArrayList<SearchResult> outputList = youtubeCaller.GetVideoFromQuery(getQuery(),30,token,null,null);
                return outputList;
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<SearchResult> items) {
            super.onPostExecute(items);
            if(items == null || items.isEmpty()){
                CustomToast.makeText(getContext(),"Connection Error",Toast.LENGTH_SHORT);
            }else {
                SongAdapter adapter = recyclerViewInitializer.getSongAdapter();
                if(adapter != null) {
                    adapter.clear();
                    adapter.addAll(items);
                }else {
                    new loadingList().execute();
                }
            }
            swipeRefreshLayout.setRefreshing(false);

        }
    }
    public class updateList_next extends AsyncTask<ArrayList<SearchResult>, Void, ArrayList<SearchResult>> {
        @Override
        protected ArrayList<SearchResult> doInBackground(ArrayList<SearchResult>... arrayLists) {
            SharedPreferences prefs = getActivity().getSharedPreferences(MainHub.TOKEN_TAG,Context.MODE_PRIVATE);
            if(prefs != null){
                String token = prefs.getString(MainHub.TOKEN_TAG,null);
                //Log.d(TAG, "TokenUser: " + token);
                final ArrayList<SearchResult> outputList = youtubeCaller.GetVideoFromQuery(getQuery(),30,token,YoutubeCaller.getNextPageToken(),null);
                return outputList;
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<SearchResult> items) {
            super.onPostExecute(items);
            progressBar.setVisibility(View.GONE);
            if(items == null || items.isEmpty()){
                CustomToast.makeText(getContext(),"Connection Error",Toast.LENGTH_SHORT);
            }else {
                SongAdapter adapter = recyclerViewInitializer.getSongAdapter();
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
            SongAdapter adapter = recyclerViewInitializer.getSongAdapter();
            if(adapter != null){
                adapter.clear();
            }
            progressBar.setVisibility(View.VISIBLE);
        }
    }
    public class updateList_prev extends AsyncTask<ArrayList<SearchResult>, Void, ArrayList<SearchResult>> {
        @Override
        protected ArrayList<SearchResult> doInBackground(ArrayList<SearchResult>... arrayLists) {
            SharedPreferences prefs = getActivity().getSharedPreferences(MainHub.TOKEN_TAG,Context.MODE_PRIVATE);
            if(prefs != null){
                String token = prefs.getString(MainHub.TOKEN_TAG,null);
                //Log.d(TAG, "TokenUser: " + token);
                final ArrayList<SearchResult> outputList = youtubeCaller.GetVideoFromQuery(getQuery(),30,token,null,YoutubeCaller.getPrevPageToken());
                return outputList;
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<SearchResult> items) {
            super.onPostExecute(items);
            progressBar.setVisibility(View.GONE);
            if(items == null || items.isEmpty()){
                CustomToast.makeText(getContext(),"Connection Error",Toast.LENGTH_SHORT);
            }else {
                SongAdapter adapter = recyclerViewInitializer.getSongAdapter();
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
            SongAdapter adapter = recyclerViewInitializer.getSongAdapter();
            if(adapter != null){
                adapter.clear();
            }
            progressBar.setVisibility(View.VISIBLE);
        }
    }
    ////////////////////////////////////////////////////////

    ////////////Passing Data/////////////////
    public interface OnDataPass {
        public void onDataPass(String data);
    }
    public void passData(String data) {

        dataPasser.onDataPass(data);
    }
    public void onAttach(Context context) {
        super.onAttach(context);
        dataPasser = (OnDataPass) context;
    }
    /////////////////////////////////////////

}
