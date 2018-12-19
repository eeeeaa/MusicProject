package com.tiger.user.musicproject.Model;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.tiger.user.musicproject.FavoriteService.FavoriteModel;
import com.tiger.user.musicproject.MainHub;
import com.tiger.user.musicproject.R;
import com.tiger.user.musicproject.Test_player;

import java.util.ArrayList;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder>{
    private static final String TAG = "SongAdapter";
    private ArrayList<FavoriteModel> songs;
    private Context mContext;
    public FavoriteAdapter(ArrayList<FavoriteModel> songs, Context mContext) {
        this.songs = songs;
        this.mContext = mContext;
    }
    @NonNull
    @Override
    public FavoriteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_musiclist,viewGroup,false);
        FavoriteAdapter.ViewHolder viewHolder = new FavoriteAdapter.ViewHolder(view);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        //Log.d(TAG, "onBindViewHolder: called");
        viewHolder.progressBar.setVisibility(View.VISIBLE);
        Picasso.get().load(songs.get(i).thumbnail)
                .fit().centerCrop().into(viewHolder.song_image, new Callback() {
            @Override
            public void onSuccess() {
                viewHolder.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                viewHolder.progressBar.setVisibility(View.GONE);
                viewHolder.song_image.setImageResource(R.drawable.placeholder);
            }
        });
        Typeface typeface1 = Typeface.createFromAsset(mContext.getAssets(),"font/roboto_regular.ttf");
        Typeface typeface2 = Typeface.createFromAsset(mContext.getAssets(),"font/roboto_light.ttf");
        viewHolder.song_title.setText(songs.get(i).videoName);
        viewHolder.song_artist.setText("");
        viewHolder.song_title.setTypeface(typeface1);
        viewHolder.song_artist.setTypeface(typeface2);
        viewHolder.relative_songlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainHub)mContext).showPanel();
                FragmentManager fm = ((AppCompatActivity)mContext).getSupportFragmentManager();
                Bundle data = new Bundle();
                data.putString("song_title",songs.get(i).videoName);
                data.putString("song_artist","");
                data.putString("img_url",songs.get(i).thumbnail);
                //search result item
                data.putString(SongAdapter.VIDEO_ID,songs.get(i).videoId);
                Test_player player = new Test_player();
                player.setArguments(data);
                fm.beginTransaction().replace(R.id.player_frame_drag,player).commit();

            }
        });

    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView song_image;
        TextView song_title;
        TextView song_artist;
        CardView relative_songlist;
        ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            song_image = itemView.findViewById(R.id.song_image);
            song_title = itemView.findViewById(R.id.song_title);
            song_artist = itemView.findViewById(R.id.song_artist);
            relative_songlist = (CardView) itemView.findViewById(R.id.relative_songlist);
            progressBar = (ProgressBar)itemView.findViewById(R.id.song_progressBar);
        }
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    ///////////////////////////////////////////////


    public void clear() {
        songs.clear();
        notifyDataSetChanged();
    }
    public void addAll(ArrayList<FavoriteModel> list) {
        songs.addAll(list);
        notifyDataSetChanged();
    }
    public boolean isEmpty(){
        if(songs.isEmpty()){
            return true;
        }
        return false;
    }


    //////////////////////////////////////////////
}
