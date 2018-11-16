package com.tiger.user.musicproject.Reference;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.tiger.user.musicproject.MainHub;
import com.tiger.user.musicproject.Management.CredentialsHandler;
import com.tiger.user.musicproject.R;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends Activity {
    private static final int REQUEST_CODE = 1337;
    public static final String CLIENT_ID = "8941735733ef4a848d2cd0434fef264a";
    private static final String REDIRECT_URI = "com.tiger.user.musicproject://callback";
    public static final String AUTH_TOKEN = "AUTH_TOKEN";
    private static final String TAG = "Spotify " + LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);

        builder.setScopes(new String[]{"streaming","playlist-read-private","user-read-recently-played","user-read-currently-playing",
        "playlist-read-collaborative","user-library-read","app-remote-control","user-read-playback-state","user-top-read"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    // Handle successful response
                    CredentialsHandler.setToken(this, response.getAccessToken(), response.getExpiresIn(), TimeUnit.SECONDS);
                    Intent success = new Intent(LoginActivity.this,MainHub.class);
                    success.putExtra(AUTH_TOKEN, response.getAccessToken());
                    Log.d("Send",response.getAccessToken());
                    startActivity(success);
                    //destroy();
                    break;
                // Auth flow returned an error
                case ERROR:
                    Log.e(TAG,"Auth error: " + response.getError());
                    // Handle error response
                    break;

                // Most likely auth flow was cancelled
                default:
                    Log.d(TAG,"Auth result: " + response.getType());
                    // Handle other cases
            }
        }
    }
    public void destroy(){
        LoginActivity.this.finish();
    }
}
