package com.tiger.user.musicproject.Management;

import android.app.Application;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

public class GoogleGlobalCredential extends Application {
    private GoogleAccountCredential mCredential = null;
    private static final String KEY = "AIzaSyC96AxLHwohCVm9lQhJu5PVsV_hBjFRhNY";
    private static final String PACKAGENAME = "com.tiger.user.musicproject";
    private static final String SHA1 = "5F:F5:25:18:40:CE:15:1E:55:F3:50:31:D0:35:98:8C:55:9C:88:3E";

    public GoogleAccountCredential getmCredential() {
        return mCredential;
    }

    public void setmCredential(GoogleAccountCredential mCredential) {
        this.mCredential = mCredential;
    }

    public static String getKEY() {
        return KEY;
    }

    public static String getPACKAGENAME() {
        return PACKAGENAME;
    }

    public static String getSHA1() {
        return SHA1;
    }
}
