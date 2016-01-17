package org.break_out.breakout.ui;

import android.os.Bundle;

import org.break_out.breakout.R;
import org.break_out.breakout.ui.BOActivity;

public class MainActivity extends BOActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}