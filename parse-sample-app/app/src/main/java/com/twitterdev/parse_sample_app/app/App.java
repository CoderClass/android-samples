package com.twitterdev.parse_sample_app.app;

/**
 * Created by gjones on 5/11/14.
 */
import android.app.Application;
import com.parse.Parse;
import com.parse.ParseTwitterUtils;

public class App extends Application {

    @Override public void onCreate() {
        super.onCreate();
        // Initialize Parse
        Parse.initialize(this, Constants.PARSE_APP_ID, Constants.PARSE_CLIENT_KEY);
        // Set Twitter Consumer Key and Secret
        ParseTwitterUtils.initialize(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
    }
}