package org.break_out.breakout;

import android.os.Bundle;

import com.flurry.android.FlurryAgent;

public class MainActivity extends BOActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}