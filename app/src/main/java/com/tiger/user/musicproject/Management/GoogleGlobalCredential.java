package com.tiger.user.musicproject.Management;

import android.app.Application;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

public class GoogleGlobalCredential extends Application {
    private GoogleAccountCredential mCredential = null;

    public GoogleAccountCredential getmCredential() {
        return mCredential;
    }

    public void setmCredential(GoogleAccountCredential mCredential) {
        this.mCredential = mCredential;
    }
}
