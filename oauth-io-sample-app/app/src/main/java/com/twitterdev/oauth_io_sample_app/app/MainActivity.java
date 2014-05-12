package com.twitterdev.oauth_io_sample_app.app;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import io.oauth.OAuth;
import io.oauth.OAuthCallback;
import io.oauth.OAuthData;

public class MainActivity extends Activity implements OAuthCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final OAuth oauth = new OAuth(this);
        oauth.initialize(Constants.OAUTH_IO_KEY);
        oauth.popup("twitter", MainActivity.this);


        setContentView(R.layout.activity_main);
    }

    public void onFinished(OAuthData data) {
        Log.v("MyApp", data.provider);
    }




}
