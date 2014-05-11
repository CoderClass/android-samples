package com.twitterdev.hello_world.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.extractors.TokenExtractor20Impl;
import org.scribe.extractors.TokenExtractorImpl;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import android.app.ActionBar;


public class LoggedIn extends ActionBarActivity {

    private final OAuthService s = new ServiceBuilder()
            .provider(TwitterApi.SSL.class)
            .apiKey(Constants.API_KEY)
            .apiSecret(Constants.API_SECRET)
            .callback(Constants.CALLBACKURL)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.home_main);

        final SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);

        try {
            String user_data = new getTweets().execute().get();
            Log.v("user", user_data);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private class getTweets extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
            Token newAccessToken = new Token(settings.getString("accessToken", null), settings.getString("accessSecret", null));
            final OAuthRequest request = new OAuthRequest(Verb.GET,"https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=twitterdev");
            s.signRequest(newAccessToken, request);
            Response response = request.send();
            final Activity ac = LoggedIn.this;
            String body = response.getBody();
            try {
                JSONArray data_list = new JSONArray(body);
                final ArrayList<String> items = new ArrayList<String>();
                int len = data_list.length();
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < len; i++) {
                    JSONObject item = data_list.getJSONObject(i);
                    try {
                        String tweet = item.get("text").toString();
                        items.add(tweet);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                final ListView listView = (ListView) findViewById(R.id.list);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String[] array = items.toArray(new String[items.size()]);
                        TweetAdapter a = new TweetAdapter(ac, array);
                        listView.setAdapter(a);

                    }


                });

            }
            catch (JSONException e) {
                e.printStackTrace();
            }

            return body;

        }
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.logout:
                SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.clear();
                editor.commit();
                Intent myIntent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(myIntent);
                break;
            case R.id.refresh:
                Intent refresh = new Intent(getBaseContext(), LoggedIn.class);
                startActivity(refresh);
                break;
            case R.id.fork:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/twitterdev")));
                break;
            case R.id.help:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://dev.twitter.com")));
                break;
        }
        return true;

    };

    // provide user details


}