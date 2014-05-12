package com.twitterdev.parse_sample_app.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;

public class MainActivity extends ActionBarActivity {
    
    private String screen_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
        final SharedPreferences.Editor editor = settings.edit();

        // set full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // if the settings of the screen_name are not saved (prompt for a login)
        if (settings.getString("screen_name", null) == null){
            // Twitter Login
            ParseTwitterUtils.logIn(this, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException err) {
                    if (user == null) {
                        Log.d("MyApp", "Uh oh. The user cancelled the Twitter login.");
                    } else {
                        String screen_name = ParseTwitterUtils.getTwitter().getScreenName();
                        editor.putString("screen_name", screen_name);
                        editor.commit();
                        Log.d("MyApp", screen_name + " has signed in");
                        // Refresh
                        Intent myIntent = new Intent(getBaseContext(), MainActivity.class);
                        startActivity(myIntent);

                    }
                }
            });
        }
        setContentView(R.layout.activity_main);
        // set screen_name to a text view
        TextView txt_name = (TextView) findViewById(R.id.txt_name);
        txt_name.setText("Hello @" + settings.getString("screen_name", null));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.signout) {
            SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.clear();
            editor.commit();
            Intent myIntent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(myIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
