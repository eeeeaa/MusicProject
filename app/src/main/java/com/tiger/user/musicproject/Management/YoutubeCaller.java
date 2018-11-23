package com.tiger.user.musicproject.Management;

import android.content.Context;
import android.util.Log;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
    public boolean isFinish;

    private static String nextPageToken;
    private static String prevPageToken;

    public YoutubeCaller(Context context,String KEY,String PACKAGENAME,String SHA1){
        this.mContext = context;
        this.KEY = KEY;
        this.PACKAGENAME = PACKAGENAME;
        this.SHA1 = SHA1;
    }

    public static String getNextPageToken() {
        return nextPageToken;
    }
    public static String getPrevPageToken(){
        return prevPageToken;
    }

    private void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
        Log.d(TAG, "setNextPageToken: " + nextPageToken);
    }

    public void setPrevPageToken(String prevPageToken) {
        this.prevPageToken = prevPageToken;
        Log.d(TAG, "setPrevPageToken: " + prevPageToken);
    }

    public ArrayList<SearchResult> GetRandomVideoFromQuery (final String keywords,
                                                            final int MaxResult, final String Token,
                                                            final String next_pageToken, final String prev_pageToken){
        ArrayList<SearchResult> outputList = new ArrayList<SearchResult>();
        setNextPageToken(null);
        setPrevPageToken(null);
        class Query implements Runnable{
            volatile ArrayList<SearchResult> temp = new ArrayList<SearchResult>();
            @Override
            public void run() {
                setNextPageToken(null);
                setPrevPageToken(null);
                isFinish = false;
                youTube = new YouTube.Builder(new NetHttpTransport(),new JacksonFactory(),new HttpRequestInitializer(){

                    @Override
                    public void initialize(HttpRequest request) throws IOException {
                        request.getHeaders().set("X-Android-Package", PACKAGENAME);
                        request.getHeaders().set("X-Android-Cert",SHA1);
                    }
                }).setApplicationName("MusicProject").build();
                try {
                    query = youTube.search().list("id,snippet");
                    if(Token != null){
                        query.setOauthToken(Token);
                        Log.d(TAG, "Q Use Access Token");
                    }else {
                        query.setKey(KEY);
                        Log.d(TAG, "Q use Key");
                    }
                    if(next_pageToken!=null){
                        query.setPageToken(next_pageToken);
                    }else if(prev_pageToken != null){
                        query.setPageToken(next_pageToken);
                    }
                    query.setType("video");
                    query.setFields("prevPageToken,nextPageToken,items(id/kind,id/videoId,snippet/title,snippet/description,snippet/thumbnails/high/url,snippet/channelTitle)");
                    query.setQ(keywords);
                    query.setMaxResults((long)50);
                    SearchListResponse response = query.execute();
                    List<SearchResult> results = response.getItems();
                    setNextPageToken(response.getNextPageToken());
                    setPrevPageToken(response.getPrevPageToken());
                    if(response.getNextPageToken() == null){
                        setNextPageToken(null);
                    }
                    if(response.getPrevPageToken() == null){
                        setPrevPageToken(null);
                    }
                    Collections.shuffle(results);
                    List<SearchResult> new_results = results.subList(0,MaxResult-1);
                    for (int i = 0; i < new_results.size(); i++) {
                        temp.add(new_results.get(i));
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
            isFinish = true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return outputList;
    }

    public ArrayList<SearchResult> GetVideoFromQuery (final String keywords, final long MaxResult,
                                                      final String Token, final String next_pageToken,final String prev_pageToken){
        ArrayList<SearchResult> outputList = new ArrayList<SearchResult>();
        setNextPageToken(null);
        Log.d(TAG, "GetVideoFromQuery: "+ "set Next null");
        setPrevPageToken(null);
        Log.d(TAG, "GetVideoFromQuery: " + "set Next null");
        class Query implements Runnable{
            volatile ArrayList<SearchResult> temp = new ArrayList<SearchResult>();
            @Override
            public void run() {
                isFinish = false;
                youTube = new YouTube.Builder(new NetHttpTransport(),new JacksonFactory(),new HttpRequestInitializer(){

                    @Override
                    public void initialize(HttpRequest request) throws IOException {
                        request.getHeaders().set("X-Android-Package", PACKAGENAME);
                        request.getHeaders().set("X-Android-Cert",SHA1);
                    }
                }).setApplicationName("MusicProject").build();
                try {
                    query = youTube.search().list("id,snippet");
                    if(Token != null){
                        query.setOauthToken(Token);
                        Log.d(TAG, "Q Use Access Token");
                    }else {
                        query.setKey(KEY);
                        Log.d(TAG, "Q use Key");
                    }
                    if(next_pageToken!=null){
                        query.setPageToken(next_pageToken);
                    }else if(prev_pageToken != null){
                        query.setPageToken(prev_pageToken);
                    }
                    query.setType("video");
                    query.setFields("prevPageToken,nextPageToken,items(id/kind,id/videoId,snippet/title,snippet/description,snippet/thumbnails/high/url,snippet/channelTitle)");
                    query.setQ(keywords);
                    query.setMaxResults(MaxResult);
                    SearchListResponse response = query.execute();
                    setNextPageToken(response.getNextPageToken());
                    setPrevPageToken(response.getPrevPageToken());
                    if(response.getNextPageToken() == null){
                        setNextPageToken(null);
                    }
                    if(response.getPrevPageToken() == null){
                        setPrevPageToken(null);
                    }
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
            isFinish = true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return outputList;
    }
    public ArrayList<PlaylistItem> GetVideoFromPlaylistID(final String playlist_id, final long MaxResult, final String Token, final String next_pageToken,final String prev_pageToken){
        ArrayList<PlaylistItem> outputList = new ArrayList<PlaylistItem>();
        setNextPageToken(null);
        setPrevPageToken(null);
        class Query implements Runnable{
            volatile ArrayList<PlaylistItem> temp = new ArrayList<PlaylistItem>();
            @Override
            public void run() {
                isFinish = false;
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
                    if(Token != null){
                        playlistItem.setOauthToken(Token);
                        Log.d(TAG, "P Use Access Token");
                    }else {
                        playlistItem.setKey(KEY);
                        Log.d(TAG, "P use Key");
                    }
                    if(next_pageToken!=null){
                        playlistItem.setPageToken(next_pageToken);
                    }else if(prev_pageToken != null){
                        playlistItem.setPageToken(prev_pageToken);
                    }
                    playlistItem.setFields("prevPageToken,nextPageToken,items(snippet/resourceId/kind,snippet/resourceId/videoId," +
                            "snippet/title,snippet/description,snippet/thumbnails/high/url,snippet/channelTitle)");
                    playlistItem.setMaxResults(MaxResult);
                    PlaylistItemListResponse response = playlistItem.execute();
                    setNextPageToken(response.getNextPageToken());
                    setPrevPageToken(response.getPrevPageToken());
                    if(response.getNextPageToken() == null){
                        setNextPageToken(null);
                    }
                    if(response.getPrevPageToken() == null){
                        setPrevPageToken(null);
                    }
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
            isFinish =true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return outputList;
    }
}
