package org.break_out.breakout.main;

import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.break_out.breakout.R;

public class RegisterActivity extends AppCompatActivity {
    private FragmentManager _manager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }


}
