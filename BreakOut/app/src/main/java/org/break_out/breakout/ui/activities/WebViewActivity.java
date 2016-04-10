package org.break_out.breakout.ui.activities;

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;

import org.break_out.breakout.R;
import org.break_out.breakout.util.DimenUtils;

/**
 * Created by Tino on 10.04.2016.
 */
public class WebViewActivity extends BOActivity {

    private static final String TAG = "WebViewActivity";

    public static final String KEY_URL = "key_url";
    public static final String KEY_TITLE = "key_title";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        Bundle extras = getIntent().getExtras();

        // Read parameters
        if(extras == null) {
            Log.e(TAG, "Could not open web view (parameters where null).");
            finish();
            return;
        }

        String url = extras.getString(KEY_URL);
        String title = extras.getString(KEY_TITLE);

        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.loadUrl(url);

        WebSettings settings = myWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowContentAccess(true);

        // Set up action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(title);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if(menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
