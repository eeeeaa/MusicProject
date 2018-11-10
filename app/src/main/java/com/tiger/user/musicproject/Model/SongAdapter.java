package com.tiger.user.musicproject.Model;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.squareup.picasso.Picasso;
import com.tiger.user.musicproject.HomePage;
import com.tiger.user.musicproject.Player;
import com.tiger.user.musicproject.R;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.models.Track;

// TODO: 11/11/2018 Since you cant play a single track,might have to change to album list instead 
public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder>{
    private static final String TAG = "SongAdapter";
    private ArrayList<Track> songs;
    private Context mContext;

    public SongAdapter(ArrayList<Track> songs, Context mContext) {
        this.songs = songs;
        this.mContext = mContext;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_musiclist,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        Log.d(TAG, "onBindViewHolder: called");
        Picasso.get().load(songs.get(i).album.images.get(0).url).into(viewHolder.song_image);
        viewHolder.song_title.setText(songs.get(i).album.name);
        viewHolder.song_artist.setText(songs.get(i).artists.get(0).name);
        viewHolder.relative_songlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: "+ songs.get(i).name);
                //Toast.makeText(mContext,songs.get(i).album.name,Toast.LENGTH_SHORT).show();
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                Bundle data = new Bundle();
                data.putString("Song_ID",songs.get(i).album.id);
                Log.d(TAG, "Song_ID "+data.getString("Song_ID"));
                Player player = new Player();
                player.setArguments(data);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.player_frame,player).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView song_image;
        TextView song_title;
        TextView song_artist;
        RelativeLayout relative_songlist;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            song_image = itemView.findViewById(R.id.song_image);
            song_title = itemView.findViewById(R.id.song_title);
            song_artist = itemView.findViewById(R.id.song_artist);
            relative_songlist = itemView.findViewById(R.id.relative_songlist);
        }
    }
}
