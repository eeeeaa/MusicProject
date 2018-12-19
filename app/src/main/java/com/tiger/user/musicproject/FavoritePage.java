package com.tiger.user.musicproject;

import android.content.Context;
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

import com.tiger.user.musicproject.FavoriteService.FavoriteCommand;
import com.tiger.user.musicproject.FavoriteService.FavoriteMain;
import com.tiger.user.musicproject.FavoriteService.FavoriteModel;
import com.tiger.user.musicproject.Management.CustomToast;
import com.tiger.user.musicproject.Management.RecyclerViewInitializer;
import com.tiger.user.musicproject.Model.FavoriteAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoritePage extends Fragment {
    private static String TAG = "FavoritePage";
    View v;
    ProgressBar progressBar;
    RecyclerViewInitializer recyclerViewInitializer;
    SwipeRefreshLayout swipeRefreshLayout;

    /////////////////////////////////////////
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v= inflater.inflate(R.layout.favorite_page,container,false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.favorite);
        progressBar = (ProgressBar)v.findViewById(R.id.progressBar);
        recyclerViewInitializer = new RecyclerViewInitializer();
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
    public class loadingList extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            ArrayList<FavoriteModel> favoriteModels = new ArrayList<FavoriteModel>();
            //loading list of video
            class GetAll implements Runnable{
                private volatile ArrayList<FavoriteModel> tempList = new ArrayList<FavoriteModel>();
                @Override
                public void run() {
                    FavoriteCommand favoriteCommand = FavoriteMain.getRetrofit().create(FavoriteCommand.class);
                    Call<List<FavoriteModel>> call = favoriteCommand.getallVIdeo();
                    call.enqueue(new Callback<List<FavoriteModel>>() {
                        @Override
                        public void onResponse(Call<List<FavoriteModel>> call, Response<List<FavoriteModel>> response) {
                            for(FavoriteModel item : response.body()){
                                Log.d(TAG, "GetonResponse: " + item.videoId);
                                tempList.add(item);
                                Log.d(TAG, "GetonResponse_2: " + tempList.get(0).videoName);
                            }
                            progressBar.setVisibility(View.GONE);
                            if(tempList == null || tempList.isEmpty()){
                                Log.d(TAG, "onPostExecute: ");
                                CustomToast.makeText(getContext(),"Connection Error or Empty list!",Toast.LENGTH_SHORT);
                            }else {
                                recyclerViewInitializer.initRecyclerViewFavorite(getContext(),v,tempList);
                                FavoriteAdapter adapter = recyclerViewInitializer.getFavoriteAdapter();
                            }
                            swipeRefreshLayout.setRefreshing(false);
                        }

                        @Override
                        public void onFailure(Call<List<FavoriteModel>> call, Throwable t) {

                        }
                    });
                }
            }
            GetAll getAll = new GetAll();
            Thread thread = new Thread(getAll);
            thread.start();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }
    }
    public class refreshList extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            class getAll implements Runnable{
                volatile ArrayList<FavoriteModel> tempList = new ArrayList<FavoriteModel>();
                @Override
                public void run() {
                    FavoriteCommand favoriteCommand = FavoriteMain.getRetrofit().create(FavoriteCommand.class);
                    Call<List<FavoriteModel>> call = favoriteCommand.getallVIdeo();
                    call.enqueue(new Callback<List<FavoriteModel>>() {
                        @Override
                        public void onResponse(Call<List<FavoriteModel>> call, Response<List<FavoriteModel>> response) {
                            for(FavoriteModel item : response.body()){
                                Log.d(TAG, "RefreshonResponse: " + item.videoId);
                                tempList.add(item);
                            }
                            if(tempList == null || tempList.isEmpty()){
                                CustomToast.makeText(getContext(),"Connection Error",Toast.LENGTH_SHORT);
                            }else {
                                FavoriteAdapter adapter = recyclerViewInitializer.getFavoriteAdapter();
                                if(adapter != null) {
                                    adapter.clear();
                                    adapter.addAll(tempList);
                                }else {
                                    new loadingList().execute();
                                }
                            }
                            swipeRefreshLayout.setRefreshing(false);
                        }

                        @Override
                        public void onFailure(Call<List<FavoriteModel>> call, Throwable t) {

                        }
                    });
                }
                public ArrayList<FavoriteModel> getTempList(){
                    return tempList;
                }
            }
            getAll getAll = new getAll();
            Thread thread = new Thread(getAll);
            thread.start();
            return null;
        }
    }

}
