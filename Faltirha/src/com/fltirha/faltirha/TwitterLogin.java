package com.fltirha.faltirha;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class TwitterLogin extends Activity {

	static String TWITTER_CONSUMER_KEY = "sKYQnJdkNKVWcYlluSfQ";
    static String TWITTER_CONSUMER_SECRET = "3D97jKPfqvQZyeB1tmxovREG5f6xBbo21vkib4";
    
    // Preference Constants
    static String PREFERENCE_NAME = "twitter_oauth";
    static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";
 
    static final String TWITTER_CALLBACK_URL = "oauth://t4jsample";
 
    // Twitter oauth urls
    static final String URL_TWITTER_AUTH = "auth_url";
    static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";
 
    private static Twitter twitter;
    private static RequestToken requestToken;
 
    // Shared Preferences
    private static SharedPreferences mSharedPreferences;
 
    // Internet Connection detector
    private ConnectionDetector cd;
    AlertDialogManager alert = new AlertDialogManager(); 
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.twitterlogin);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        cd = new ConnectionDetector(getApplicationContext());
 
        // Check if Internet present
        if (!cd.isConnectingToInternet()) {
            // Internet Connection is not present
            alert.showAlertDialog(TwitterLogin.this, "خطأ",
                    "مشكلة في اتصال الانترنت", false);
            // stop executing code by return
            
        }
 
   
        // Shared Preferences 
        mSharedPreferences = getApplicationContext().getSharedPreferences(
                "MyPref", 0);
        
             //
        if (isTwitterLoggedInAlready())
    	{
        	final Intent target = new Intent(this, MainActivity.class);
        	target.addFlags(target.FLAG_ACTIVITY_SINGLE_TOP);
        	target.addFlags(target.FLAG_ACTIVITY_CLEAR_TOP);
        	target.addFlags(target.FLAG_ACTIVITY_NEW_TASK);
        	
        	
            startActivity(target);
            finish();
               return;} 
        
        
       // actionlistener 
		final Button b=(Button) findViewById(R.id.button1);
		b.setOnClickListener(new OnClickListener(){
			public void onClick(View v)
			{ 
				 loginToTwitter();
			}
		});

		 if (!isTwitterLoggedInAlready()) {
		        Uri uri = getIntent().getData();
		        if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
		            // oAuth verifier
		            String verifier = uri
		                    .getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);

		            try {
		                // Get the access token
		                AccessToken accessToken = twitter.getOAuthAccessToken(
		                        requestToken, verifier);

		                // Shared Preferences
		                Editor e = mSharedPreferences.edit();

		                // After getting access token, access token secret
		                // store them in application preferences
		                e.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
		                e.putString(PREF_KEY_OAUTH_SECRET,
		                        accessToken.getTokenSecret());
		                // Store login status - true
		                e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
		                e.commit(); // save changes

		                Log.e("Twitter OAuth Token", "> " + accessToken.getToken());

		                // Hide login button
		                final Intent target = new Intent(this, MainActivity.class);
		                  startActivity(target);

		                // Getting user details from twitter
		                // For now i am getting his name only
		                long userID = accessToken.getUserId();
		                User user = twitter.showUser(userID);

		            } catch (Exception e) {
		                // Check log for login errors
		                Log.e("خطأ", "> " + e.getMessage());
		            }
		        }
		    }

		}

	 private void loginToTwitter() {
	        // Check if already logged in
	        if (!isTwitterLoggedInAlready()) {
	            ConfigurationBuilder builder = new ConfigurationBuilder();
	            builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
	            builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
	            Configuration configuration = builder.build();
	            TwitterFactory factory = new TwitterFactory(configuration);
	            twitter = factory.getInstance();

	            try {
	                requestToken = twitter
	                        .getOAuthRequestToken(TWITTER_CALLBACK_URL);
	                this.startActivity(new Intent(Intent.ACTION_VIEW, Uri
	                        .parse(requestToken.getAuthenticationURL())));
	            } catch (TwitterException e) {
	                e.printStackTrace();
	            }
	        } else {

	            final Intent target = new Intent(this, MainActivity.class);
	            startActivity(target);
	               return;

	        }
	    }

	    /**
	     * Check user already logged in your application using twitter Login flag is
	     * fetched from Shared Preferences
	     * */
	    private boolean isTwitterLoggedInAlready() {
	        // return twitter login status from Shared Preferences
	        return mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
	    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}



}

