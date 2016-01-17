package org.break_out.breakout.main;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

import java.net.URL;

/**
 * Created by Maximilian Duehr on 07.01.2016.
 */
public class BreakOutWebView extends WebView {
    private static final String TAG = "BreakoutWebView";
    private URL url;

    public BreakOutWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BreakOutWebView(Context context,AttributeSet attrs) {
        super(context,attrs);
    }

    public BreakOutWebView(Context context) {
        super(context);
    }
}
