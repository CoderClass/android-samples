package com.twitterdev.hello_world.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import java.util.concurrent.ExecutionException;


public class MainActivity extends ActionBarActivity {
    // make some declarations


    private final OAuthService s = new ServiceBuilder()
            .provider(TwitterApi.SSL.class)
            .apiKey(Constants.API_KEY)
            .apiSecret(Constants.API_SECRET)
            .callback(Constants.CALLBACKURL)
            .build();

    private static Token requestToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // define the context view in /res/layout/..
        setContentView(R.layout.activity_main);
        // full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // set the button
        ImageButton loginButton = (ImageButton) findViewById(R.id.button);
        // ad View.OnClickListener "click"
        loginButton.setOnClickListener(click);

        ImageView imgV = (ImageView) findViewById(R.id.imageView);
        imgV.setScaleType(ImageView.ScaleType.FIT_XY);
        final Uri uri = this.getIntent().getData();
        SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
        final SharedPreferences.Editor editor = settings.edit();



    }

    View.OnClickListener click = new View.OnClickListener() {
        // handle onclick
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(), "Login", Toast.LENGTH_LONG).show();
            String authUrl = null;
            try {
                authUrl = new MainActivity.authUrl().execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl)));


        }
    };

    private class authUrl extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            requestToken = s.getRequestToken();
            final String authorizationUrl = s.getAuthorizationUrl(requestToken);
            return authorizationUrl;
        }

    }

    private class OauthEnd extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
            final SharedPreferences.Editor editor = settings.edit();

            final String verifier = params[0];
            final Verifier v = new Verifier(verifier);
            Log.v("R", requestToken.getRawResponse());
            // Setup storage for access token
            final Token accessToken = s.getAccessToken(requestToken,v);

            editor.putString("accessToken", accessToken.getToken());
            editor.putString("accessSecret", accessToken.getSecret());
            String androidId = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID);

            editor.putString("androidId", androidId);
            editor.commit();
            return null;
        }
    }

    private class getUser extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String...params) {
            SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);

            Token newAccessToken = new Token(settings.getString("accessToken", null), settings.getString("accessSecret", null));
            final OAuthRequest request = new OAuthRequest(Verb.GET,"https://api.twitter.com/1.1/account/verify_credentials.json");
            s.signRequest(newAccessToken, request);
            Response response = request.send();
            String body = response.getBody();
            return body;

        }

        @Override
        protected void onPostExecute(String result){
            try {
                JSONObject myJson = new JSONObject(result);
                String name = myJson.optString("screen_name");
                Log.v("name", name);
                // set the text to show my name
                TextView text = (TextView) findViewById(R.id.text1);

                // set info
                SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("screen_name", name);
                editor.commit();


                text.setText("Howdy @" + name);
                // hide the login button
                ImageButton loginButton = (ImageButton) findViewById(R.id.button);
                loginButton.setVisibility(View.GONE);

            } catch (JSONException e) {
                Log.e("JSONObject", e.getMessage());
            }
            Log.v("body", result);
        }
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        // setup shared secret
        Toast.makeText(getApplicationContext(), "Resuming", Toast.LENGTH_LONG).show();
        Log.v("RESUME", "TRUE");
        final Uri uri = this.getIntent().getData();
        SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);

        if (settings.getString("accessToken", null) != null && settings.getString("accessSecret", null) != null){
            // settings have been found make request
            new getUser().execute();
            Intent myIntent = new Intent(getBaseContext(), LoggedIn.class);
            startActivity(myIntent);

        } else {
            // if shared settings are not set / check whether the uri is valid to do an OAuth Dance
            if (uri != null && uri.toString().startsWith(Constants.CALLBACKURL)) {
                Log.v("onResume", "callback is valid");
                Log.v("uri", uri.toString());
                String verifier = uri.getQueryParameter("oauth_verifier");
                new OauthEnd().execute(verifier);
                new getUser().execute();
                Intent myIntent = new Intent(getBaseContext(), LoggedIn.class);
                startActivity(myIntent);
            }
        }

        // set the context view etc
        setContentView(R.layout.activity_main);
        ImageButton loginButton = (ImageButton) findViewById(R.id.button);
        loginButton.setOnClickListener(click);
        ImageView imgV = (ImageView) findViewById(R.id.imageView);
        imgV.setScaleType(ImageView.ScaleType.FIT_XY);


    }


}
