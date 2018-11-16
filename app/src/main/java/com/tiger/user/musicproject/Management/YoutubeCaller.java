package com.tiger.user.musicproject.Management;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class YoutubeCaller {
    private static String TAG = "YoutubeCaller";
    private YouTube youTube;
    private  YouTube.Search.List query;
    private YouTube.PlaylistItems.List playlistItem;
    private Context mContext;
    private static String KEY;
    private static String PACKAGENAME;
    private static String SHA1;
    public YoutubeCaller(Context context,String KEY,String PACKAGENAME,String SHA1){
        this.mContext = context;
        this.KEY = KEY;
        this.PACKAGENAME = PACKAGENAME;
        this.SHA1 = SHA1;
    }

    public ArrayList<SearchResult> GetVideoFromQuery (final String keywords, final long MaxResult){
        ArrayList<SearchResult> outputList = new ArrayList<SearchResult>();
        class Query implements Runnable{
            volatile ArrayList<SearchResult> temp = new ArrayList<SearchResult>();
            @Override
            public void run() {
                youTube = new YouTube.Builder(new NetHttpTransport(),new JacksonFactory(),new HttpRequestInitializer(){

                    @Override
                    public void initialize(HttpRequest request) throws IOException {
                        request.getHeaders().set("X-Android-Package", PACKAGENAME);
                        request.getHeaders().set("X-Android-Cert",SHA1);
                    }
                }).setApplicationName("MusicProject").build();
                try {
                    query = youTube.search().list("id,snippet");
                    query.setKey(KEY);
                    query.setType("video");
                    query.setFields("items(id/kind,id/videoId,snippet/title,snippet/description,snippet/thumbnails/high/url)");
                    query.setQ(keywords);
                    query.setMaxResults(MaxResult);
                    SearchListResponse response = query.execute();
                    List<SearchResult> results = response.getItems();
                    for (int i = 0; i < results.size(); i++) {
                        temp.add(results.get(i));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            public ArrayList<SearchResult> getResult(){
                return temp;
            }
        }
        Query query = new Query();
        Thread thread = new Thread(query);
        thread.start();
        try {
            thread.join();
            outputList = query.getResult();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(outputList.isEmpty()){
            Log.e(TAG, "GetVideoFromQuery: Null List!");
            return null;
        }else {
            return outputList;
        }
    }
    public ArrayList<PlaylistItem> GetVideoFromPlaylistID(final String playlist_id, final long MaxResult){
        ArrayList<PlaylistItem> outputList = new ArrayList<PlaylistItem>();
        class Query implements Runnable{
            volatile ArrayList<PlaylistItem> temp = new ArrayList<PlaylistItem>();
            @Override
            public void run() {
                youTube = new YouTube.Builder(new NetHttpTransport(),new JacksonFactory(),new HttpRequestInitializer(){

                    @Override
                    public void initialize(HttpRequest request) throws IOException {
                        request.getHeaders().set("X-Android-Package", PACKAGENAME);
                        request.getHeaders().set("X-Android-Cert",SHA1);
                    }
                }).setApplicationName("MusicProject").build();
                try {
                    playlistItem = youTube.playlistItems().list("id,snippet");
                    playlistItem.setPlaylistId(playlist_id);
                    playlistItem.setKey(KEY);
                    playlistItem.setFields("items(snippet/resourceId/kind,snippet/resourceId/videoId," +
                            "snippet/title,snippet/description,snippet/thumbnails/high/url)");
                    playlistItem.setMaxResults(MaxResult);
                    PlaylistItemListResponse response = playlistItem.execute();
                    List<PlaylistItem> results = response.getItems();
                    for (int i = 0; i < results.size(); i++) {
                        temp.add(results.get(i));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            public ArrayList<PlaylistItem> getResult(){
                return temp;
            }
        }
        Query query = new Query();
        Thread thread = new Thread(query);
        thread.start();
        try {
            thread.join();
            outputList = query.getResult();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(outputList.isEmpty()){
            return null;
        }else {
            return outputList;
        }
    }
}
