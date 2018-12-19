package com.tiger.user.musicproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.services.youtube.model.SearchResult;
import com.tiger.user.musicproject.Management.GoogleGlobalCredential;
import com.tiger.user.musicproject.Management.RecyclerViewInitializer;
import com.tiger.user.musicproject.Management.YoutubeCaller;
import com.tiger.user.musicproject.Model.SongAdapter;

import java.util.ArrayList;

public class SearchPage extends Fragment{
    View v;
    private String TAG = "SearchPage";
    private SearchView searchView;
    ProgressBar progressBar;
    RecyclerViewInitializer recyclerViewInitializer;
    YoutubeCaller youtubeCaller;
    SwipeRefreshLayout swipeRefreshLayout;
    private String query;

    /////////////////////MENU///////////////////////
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem item = menu.findItem(R.id.search_view);
        searchView = new SearchView(((AppCompatActivity) getActivity()).getSupportActionBar().getThemedContext());
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        MenuItemCompat.setActionView(item, searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit: " + query);
                swipeRefreshLayout.setEnabled(true);
                setQuery(query);
                new loadingList().execute();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                SongAdapter adapter = recyclerViewInitializer.getSongAdapter();
                swipeRefreshLayout.setEnabled(false);
                if(adapter != null){
                    adapter.clear();
                }
                return false;
            }
        });
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SongAdapter adapter = recyclerViewInitializer.getSongAdapter();
        switch (item.getItemId()) {
            case R.id.menu_forward:
                if(adapter!=null) {
                    if (YoutubeCaller.getNextPageToken() != null && !adapter.isEmpty()) {
                        new SearchPage.updateList_next().execute();
                    }
                }
                return true;

            case R.id.menu_backward:
                if(adapter!=null) {
                    if (YoutubeCaller.getPrevPageToken() != null && !adapter.isEmpty()) {
                        new SearchPage.updateList_prev().execute();
                    }
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
    ////////////////////////////////////////////////////////

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.search_page,container,false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Search");
        progressBar = (ProgressBar)v.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
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
        swipeRefreshLayout.setEnabled(false);
        return v;
    }

    ///////////////////LOADING DATA//////////////////////

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
                Toast.makeText(getContext(),"Connection Error",Toast.LENGTH_SHORT).show();
            }else {
                recyclerViewInitializer.initRecyclerViewQuery(getContext(),v,items);
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
                Toast.makeText(getContext(),"Connection Error",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getContext(),"Connection Error",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getContext(),"Connection Error",Toast.LENGTH_SHORT).show();
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
    //////////////////////////////////////////////////////
}
