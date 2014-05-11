
Thanks to the awesome folks who contiune to contribute and work on twitter4j. 

To get started please create a Constants.java file within the main src folder:

```
package com.twitterdev.twitter4j_sample_app.app;

/**
 * Created by @gpj
 */
public final class Constants {
    public static final String API_KEY = "1ATgfadfsdsdasdsadwq23Sg";
    public static final String API_SECRET = "oASQwjAnNnMMzWd2d2343fefsdf3O4";
    public static final String PREFS_NAME = "hello_world_app";
    public static final String CALLBACKURL = "app://twitter-dev";

    public static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    public static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    public static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLoggedIn";

    // Twitter oauth urls
    public static final String URL_TWITTER_AUTH = "auth_url";
    public static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    public static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";

}

```