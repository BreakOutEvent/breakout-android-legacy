package org.break_out.breakout.ui.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.break_out.breakout.R;
import org.break_out.breakout.manager.UserManager;

public class MainActivity extends BOActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final UserManager um = UserManager.getInstance(this);

        final TextView tvUserStatus = (TextView) findViewById(R.id.tv_user_status);
        tvUserStatus.setText(um.getCurrentUsersRole().toString());

        um.loginOrRegisterUser();

        um.registerListener(new UserManager.UserDataChangedListener() {
            @Override
            public void userDataChanged() {
                tvUserStatus.setText(um.getCurrentUsersRole().toString());
            }
        });

        Button btLogout = (Button) findViewById(R.id.bt_logout);
        btLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserManager.getInstance(MainActivity.this).logOutCurrentUser();
            }
        });
    }
}