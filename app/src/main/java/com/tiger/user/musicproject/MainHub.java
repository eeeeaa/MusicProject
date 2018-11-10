package com.tiger.user.musicproject;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

public class MainHub extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,HomePage.OnDataPass{
    //**********SPOTIFY************//
    private static final String CLIENT_ID = "8941735733ef4a848d2cd0434fef264a";
    private static final String REDIRECT_URI = "com.tiger.user.musicproject://callback";
    private SpotifyAppRemote mSpotifyAppRemote;
    private String AUTH_TOKEN;
    public static Player mPlayer;
    private String recieved_data;
    //*****************************//



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_hub);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //*********Get Access Token*************
        AUTH_TOKEN = getIntent().getStringExtra(LoginActivity.AUTH_TOKEN);


    }

    //*******************Drawer UI stuffs****************//
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_hub, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fm = getSupportFragmentManager();

        if (id == R.id.nav_home_page) {
            Bundle args = new Bundle();
            args.putString("TOKEN", AUTH_TOKEN);
            HomePage home = new HomePage();
            home.setArguments(args);
            fm.beginTransaction().replace(R.id.content_frame,home).commit();

        } else if (id == R.id.nav_favorite_page) {
            fm.beginTransaction().replace(R.id.content_frame,new FavoritePage()).commit();

        } else if (id == R.id.nav_about) {
            fm.beginTransaction().replace(R.id.content_frame,new About()).commit();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //***************************************************//

    //***********Getting Data from Fragment Interface*********//
    public void onDataPass(String data) {
        Log.d("LOG ",data);
        recieved_data = data;
    }
    //********************************************************//

    //*******************Spotify stuffs********************//
    /*protected void onStart() {
        super.onStart();
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d("MainActivity", "Connected! Yay!");

                    }

                    public void onFailure(Throwable throwable) {
                        Log.e("MyActivity", throwable.getMessage(), throwable);

                    }
                });
    }

    private void connected(String album_id) {
        mSpotifyAppRemote.getPlayerApi().play("spotify:album:" + album_id);

    }

    @Override
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);

    }*/
    //*********************************************************//
}
