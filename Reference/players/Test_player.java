package com.tiger.user.musicproject;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.tiger.user.musicproject.Management.BlurTransformation;
import com.tiger.user.musicproject.Management.ColorManagement;

public class Test_player extends Fragment {
    View v;
    ImageButton bar_play;
    TextView bar_title;
    TextView bar_artist;
    SlidingUpPanelLayout mLayout;
    ImageView bar_cover_art;
    ProgressBar bar_progress;

    ImageButton full_play;
    TextView full_title;
    TextView full_artist;
    ImageView full_bg;

    RelativeLayout player_header;
    RelativeLayout player_main;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.player_bar,container,false);

        bar_play = (ImageButton)v.findViewById(R.id.play_header_button);
        bar_title = (TextView)v.findViewById(R.id.player_header_title);
        bar_artist = (TextView)v.findViewById(R.id.player_header_artist);
        bar_cover_art = (ImageView)v.findViewById(R.id.bar_cover_art);
        bar_progress = (ProgressBar)v.findViewById(R.id.bar_progress);
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(),"font/roboto_regular.ttf");
        bar_title.setTypeface(typeface);
        bar_artist.setTypeface(typeface);
        bar_title.setSelected(true);
        bar_artist.setSelected(true);

        full_play = (ImageButton) v.findViewById(R.id.play_full_button);
        full_title = (TextView) v.findViewById(R.id.player_full_title);
        full_artist = (TextView) v.findViewById(R.id.player_full_artist);
        full_bg = (ImageView)v.findViewById(R.id.play_full_cover_art);
        full_title.setTypeface(typeface);
        full_artist.setTypeface(typeface);
        full_title.setSelected(true);
        full_artist.setSelected(true);

        player_header = (RelativeLayout)v.findViewById(R.id.player_header);
        player_main = (RelativeLayout)v.findViewById(R.id.player_main);

        mLayout = ((MainHub)getActivity()).getmLayout();
        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                bar_play.setAlpha(1-slideOffset);
                bar_title.setAlpha(1-slideOffset);
                bar_artist.setAlpha(1-slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if(newState.equals(SlidingUpPanelLayout.PanelState.EXPANDED)&&
                        previousState.equals(SlidingUpPanelLayout.PanelState.COLLAPSED)){
                    bar_title.setVisibility(panel.GONE);
                    bar_artist.setVisibility(panel.GONE);
                    bar_play.setVisibility(panel.GONE);
                    mLayout.setDragView(panel);


                }

                else {

                    bar_title.setVisibility(panel.VISIBLE);
                    bar_artist.setVisibility(panel.VISIBLE);
                    bar_play.setVisibility(panel.VISIBLE);
                }
            }
        });
        init();
        return v;
    }
    public void init(){
        Bundle data = getArguments();
        if(data != null){
            String title = data.getString("song_title");
            String artist = data.getString("song_artist");
            String img_url = data.getString("img_url");
            bar_title.setText(title);
            full_title.setText(title);
            bar_artist.setText(artist);
            full_artist.setText(artist);
            bar_progress.setVisibility(View.VISIBLE);
            ////////////////////////////////////////////////
            int imageDimension = (int) getResources().getDimension(R.dimen.bar_image_size);
            Target target = new Target() {
                @Override
                public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                    bar_progress.setVisibility(View.GONE);
                    assert bar_cover_art != null;
                    bar_cover_art.setImageBitmap(bitmap);
                    Palette.from(bitmap)
                            .generate(new Palette.PaletteAsyncListener() {
                                @Override
                                public void onGenerated(Palette palette) {
                                    Palette.Swatch textSwatch = palette.getVibrantSwatch();
                                    if (textSwatch == null) {
                                        int color = ColorManagement.getDominantColorFast(bitmap);
                                        GradientDrawable gd = (GradientDrawable) player_header.getBackground().getCurrent();
                                        int[] colors = {color,R.color.colorPrimary};
                                        gd.setColors(colors);
                                        player_main.setBackgroundColor(color);
                                        return;
                                    }
                                    GradientDrawable gd = (GradientDrawable) player_header.getBackground().getCurrent();
                                    int[] colors = {textSwatch.getRgb(),R.color.colorPrimary};
                                    gd.setColors(colors);
                                    player_main.setBackgroundColor(textSwatch.getRgb());
                                }
                            });
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    bar_progress.setVisibility(View.GONE);
                    bar_cover_art.setImageResource(R.drawable.placeholder);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };
            bar_cover_art.setTag(target);
            Picasso.get().load(img_url).resize(imageDimension,imageDimension).centerCrop().into(target);
            Picasso.get().load(img_url).fit().centerCrop()
                    .into(full_bg, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            full_bg.setImageResource(R.drawable.placeholder);
                        }
                    });
            //////////////////////////////////////////////////
        }
    }
    public void findVideo(){
        //find video
    }
    public void addVideo(){
        //add video
    }
    public void removeVideo(){
        //remove video

    }
}
