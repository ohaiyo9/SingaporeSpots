package id.ohaiyo.singaporespots;

import id.ohaiyo.singaporespots.util.AlertDialogManager;
import id.ohaiyo.singaporespots.util.ConnectionDetector;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AppEventsLogger;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.NonCachingTokenCachingStrategy;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;

public class MoreActivity extends Activity implements OnClickListener{
	
	private static final String SHARE_LINK = "https://play.google.com/store/apps/details?id=id.ohaiyo.singaporespots";
    private static final String SHARE_NAME = "Android app for various spots in Singapore";

    private boolean isResumed = false;
	private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
    
    static String PREFERENCE_NAME = "twitter_oauth";
    static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";
    
    static final String TWITTER_CALLBACK_URL = "oauth://t4j";
    static final String URL_TWITTER_AUTH = "auth_url";
    static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";
    
    private static Twitter twitter;
    private static RequestToken requestToken;
    
    private ConnectionDetector cd;
    AlertDialogManager alert = new AlertDialogManager();
    
    Button btnFBShare;
    Button btnTwShare;
    Button btnUpdStatus;
    Button btnLogoutTwitter;
    EditText etUpdate;
    TextView tvTwShare;
    TextView tvUpdate;
    TextView tvUserName;
    
    ProgressDialog pDialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
		setContentView(R.layout.activity_more);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		btnFBShare = (Button)findViewById(R.id.butFBShare);
		btnTwShare = (Button)findViewById(R.id.butTWShare);
		btnUpdStatus = (Button)findViewById(R.id.btnUpdateStatus);
		btnLogoutTwitter = (Button)findViewById(R.id.btnLogoutTwitter);
		etUpdate = (EditText)findViewById(R.id.etUpdateStatus);
		tvTwShare = (TextView)findViewById(R.id.tvTwShare);
		tvUpdate = (TextView)findViewById(R.id.tvUpdate);
		tvUserName = (TextView)findViewById(R.id.tvUserName);
		
		btnFBShare.setOnClickListener(this);
		btnTwShare.setOnClickListener(this);
		btnUpdStatus.setOnClickListener(this);
		btnLogoutTwitter.setOnClickListener(this);
		
		Session existingSession = Session.getActiveSession();
        if (existingSession == null || !existingSession.isOpened()) {
            AccessToken accessToken = AccessToken.createFromNativeLinkingIntent(getIntent());
            if (accessToken != null) {
                Session newSession = new Session.Builder(this).setTokenCachingStrategy(new NonCachingTokenCachingStrategy())
                        .build();
                newSession.open(accessToken, null);

                Session.setActiveSession(newSession);
            }
        }
        
        cd = new ConnectionDetector(getApplicationContext());
        
        if (!cd.isConnectingToInternet()) {
            alert.showAlertDialog(MoreActivity.this, "Internet Connection Error",
                    "Please connect to working Internet connection", false);
            return;
        }
         
        if(getResources().getString(R.string.tw_key).trim().length() == 0 || getResources().getString(R.string.tw_secret).trim().length() == 0){
            alert.showAlertDialog(MoreActivity.this, "Twitter oAuth tokens", "Please set your twitter oauth tokens first!", false);
            return;
        }
        
        if (!isTwitterLoggedInAlready()) {
            Uri uri = getIntent().getData();
            if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
                String verifier = uri
                        .getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);
     
                try {
                	twitter4j.auth.AccessToken accessToken = twitter.getOAuthAccessToken(
                            requestToken, verifier);
     
                    Editor e = LauncherActivity.prefs.edit();
     
                    e.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
                    e.putString(PREF_KEY_OAUTH_SECRET,
                            accessToken.getTokenSecret());
                    e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
                    e.commit();
     
                    Log.e("Twitter OAuth Token", "> " + accessToken.getToken());
     
                    btnTwShare.setVisibility(View.GONE);
                    tvTwShare.setVisibility(View.GONE);
     
                    tvUpdate.setVisibility(View.VISIBLE);
                    etUpdate.setVisibility(View.VISIBLE);
                    btnUpdStatus.setVisibility(View.VISIBLE);
                    btnLogoutTwitter.setVisibility(View.VISIBLE);
                     
                    long userID = accessToken.getUserId();
                    User user = twitter.showUser(userID);
                    String username = user.getName();
                     
                    tvUserName.setText(Html.fromHtml("<b>Welcome " + username + "</b>"));
                } catch (Exception e) {
                    Log.e("Twitter Login Error", "> " + e.getMessage());
                }
            }
        }
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.butFBShare:
			FacebookDialog.ShareDialogBuilder builder = new FacebookDialog.ShareDialogBuilder(this)
            .setLink(SHARE_LINK)
            .setName(SHARE_NAME);
		    if (builder.canPresent()) {
		        builder.build().present();
		    }
			break;
			
		case R.id.butTWShare:
			loginToTwitter();
			break;
			
		case R.id.btnUpdateStatus:
			String status = etUpdate.getText().toString();
			 
            if (status.trim().length() > 0) {
                new updateTwitterStatus().execute(status);
            } else {
                Toast.makeText(getApplicationContext(),
                        "Please enter status message", Toast.LENGTH_SHORT)
                        .show();
            }
			break;
		
		case R.id.btnLogoutTwitter:
			Editor e = LauncherActivity.prefs.edit();
		    e.remove(PREF_KEY_OAUTH_TOKEN);
		    e.remove(PREF_KEY_OAUTH_SECRET);
		    e.remove(PREF_KEY_TWITTER_LOGIN);
		    e.commit();
		 
		    btnLogoutTwitter.setVisibility(View.GONE);
		    btnUpdStatus.setVisibility(View.GONE);
		    etUpdate.setVisibility(View.GONE);
		    tvUpdate.setVisibility(View.GONE);
		    tvUserName.setText("");
		    tvUserName.setVisibility(View.GONE);
		 
		    btnTwShare.setVisibility(View.VISIBLE);
		    tvTwShare.setVisibility(View.VISIBLE);
			break;

		default:
			break;
		}
	}
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (isResumed) {
            if (exception != null && !(exception instanceof FacebookOperationCanceledException)) {
                Toast.makeText(this, R.string.login_error, Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }
	
	private void loginToTwitter() {
        if (!isTwitterLoggedInAlready()) {
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(getResources().getString(R.string.tw_key));
            builder.setOAuthConsumerSecret(getResources().getString(R.string.tw_secret));
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
            Toast.makeText(getApplicationContext(),
                    "Already Logged into twitter", Toast.LENGTH_LONG).show();
        }
    }
 
    private boolean isTwitterLoggedInAlready() {
        return LauncherActivity.prefs.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
    }
	
	@Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
        isResumed = true;
        
        AppEventsLogger.activateApp(this);
    }
	
	@Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
        isResumed = false;
    }
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }
    
    class updateTwitterStatus extends AsyncTask<String, String, String> {
    	 
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MoreActivity.this);
            pDialog.setMessage("Updating to twitter...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
     
        protected String doInBackground(String... args) {
            Log.d("Tweet Text", "> " + args[0]);
            String status = args[0];
            try {
                ConfigurationBuilder builder = new ConfigurationBuilder();
                builder.setOAuthConsumerKey(getResources().getString(R.string.tw_key));
                builder.setOAuthConsumerSecret(getResources().getString(R.string.tw_secret));
                 
                String access_token = LauncherActivity.prefs.getString(PREF_KEY_OAUTH_TOKEN, "");
                String access_token_secret = LauncherActivity.prefs.getString(PREF_KEY_OAUTH_SECRET, "");
                 
                twitter4j.auth.AccessToken accessToken = new twitter4j.auth.AccessToken(access_token, access_token_secret);
                Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);
                 
                twitter4j.Status response = twitter.updateStatus(status);
                 
                Log.d("Status", "> " + response.getText());
            } catch (TwitterException e) {
                Log.d("Twitter Update Error", e.getMessage());
            }
            return null;
        }
     
        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "Status tweeted successfully", Toast.LENGTH_SHORT)
                            .show();
                    etUpdate.setText("");
                }
            });
        }
     
    }

}
