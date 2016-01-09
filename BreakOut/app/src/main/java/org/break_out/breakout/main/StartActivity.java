package org.break_out.breakout.main;

import android.app.ActionBar;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.break_out.breakout.R;

import java.net.URL;

public class StartActivity extends AppCompatActivity {
    private Context _context;
    private TextView _textView_about;
    private RelativeLayout _relativeLayout_container;

    private Button _button_login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        _context = this;

        _textView_about = (TextView) findViewById(R.id.start_textView_about);
        _relativeLayout_container = (RelativeLayout) findViewById(R.id.start_relativeLayout_mainWrapper);

    }
}
