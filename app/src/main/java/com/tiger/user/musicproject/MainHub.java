package com.tiger.user.musicproject;

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
import android.widget.TextView;

import com.google.android.gms.auth.GoogleAuthException;
import com.tiger.user.musicproject.Management.GoogleGlobalCredential;

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
        getSupportActionBar().setTitle("");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        class Caller implements Runnable{
            private String Token;
            @Override
            public void run() {
                try {
                    GoogleGlobalCredential googleGlobalCredential = ((GoogleGlobalCredential) getApplicationContext());
                    Token = googleGlobalCredential.getmCredential().getToken();
                    String email = googleGlobalCredential.getmCredential().getSelectedAccountName();
                    SharedPreferences.Editor editor = getSharedPreferences(TOKEN_TAG,MODE_PRIVATE).edit();
                    editor.putString(TOKEN_TAG,Token);
                    editor.apply();
                    TextView username = (TextView) navigationView.findViewById(R.id.username_bar);
                    username.setText(email);
                    //Log.d(TAG, "Testing: " + Token);
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

        } else if(id == R.id.nav_playlist_1){
            fm.beginTransaction().replace(R.id.content_frame,new Playlist_1()).commit();

        }else if(id == R.id.nav_playlist_2){
            fm.beginTransaction().replace(R.id.content_frame,new Playlist_2()).commit();
        }else if(id == R.id.nav_search_page){
            fm.beginTransaction().replace(R.id.content_frame,new SearchPage()).commit();
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
