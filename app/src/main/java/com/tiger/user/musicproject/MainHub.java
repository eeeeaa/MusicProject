package com.tiger.user.musicproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.google.android.gms.auth.GoogleAuthException;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.tiger.user.musicproject.Management.GoogleGlobalCredential;
import com.tiger.user.musicproject.Management.YoutubeCaller;

import java.io.IOException;

// TODO: 11/11/2018 Set Home page to display when app start
public class MainHub extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,HomePage.OnDataPass{

   public static final String TOKEN_TAG = "AuthToken";
   private String TAG = "MainHub";

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
        class Caller implements Runnable{
            private String Token;
            @Override
            public void run() {
                try {
                    GoogleGlobalCredential googleGlobalCredential = ((GoogleGlobalCredential) getApplicationContext());
                    Token = googleGlobalCredential.getmCredential().getToken();
                    SharedPreferences.Editor editor = getSharedPreferences(TOKEN_TAG,MODE_PRIVATE).edit();
                    editor.putString(TOKEN_TAG,Token);
                    editor.apply();
                    Log.d(TAG, "Testing: " + Token);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (GoogleAuthException e) {
                    e.printStackTrace();
                }

            }
        }
        Caller caller = new Caller();
        Thread thread = new Thread(caller);
        thread.start();
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
            HomePage home = new HomePage();
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
        //recieved_data = data;
    }
    //********************************************************//
}
