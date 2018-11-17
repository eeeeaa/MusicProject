package com.tiger.user.musicproject.Model;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.api.services.youtube.model.PlaylistItem;
import com.squareup.picasso.Picasso;
import com.tiger.user.musicproject.R;
import com.tiger.user.musicproject.Test_activity;

import java.util.ArrayList;

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.ViewHolder>{
    private static final String TAG = "SongAdapter";
    public static final String VIDEO_ID = "VideoID";
    private ArrayList<PlaylistItem> songs;
    private Context mContext;

    public PlayListAdapter(ArrayList<PlaylistItem> songs, Context mContext) {
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
        Picasso.get().load(songs.get(i).getSnippet().getThumbnails().getHigh().getUrl()).into(viewHolder.song_image);
        viewHolder.song_title.setText(songs.get(i).getSnippet().getTitle());
        viewHolder.song_artist.setText(songs.get(i).getSnippet().getChannelTitle());
        Log.d(TAG, "onBindViewHolder: "+ songs.get(i).getSnippet().getChannelTitle());
        viewHolder.relative_songlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(mContext,songs.get(i).getSnippet().getTitle(),Toast.LENGTH_LONG).show();
                Intent intent = new Intent(mContext,Test_activity.class);
                intent.putExtra(VIDEO_ID,songs.get(i).getSnippet().getResourceId().getVideoId());
                mContext.startActivity(intent);
            }
        });
        /*Picasso.get().load(songs.get(i).album.images.get(0).url).into(viewHolder.song_image);
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
        });*/
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
