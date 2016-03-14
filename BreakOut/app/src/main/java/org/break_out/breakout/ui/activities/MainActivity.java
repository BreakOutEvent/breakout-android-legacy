package org.break_out.breakout.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.break_out.breakout.R;
import org.break_out.breakout.manager.UserManager;
import org.break_out.breakout.model.User;

public class MainActivity extends BOActivity {

    private Button _btLogout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final UserManager um = UserManager.getInstance(this);

        final TextView tvUserStatus = (TextView) findViewById(R.id.tv_user_status);
        tvUserStatus.setText(um.getCurrentUsersRole().toString());

        um.registerListener(new UserManager.UserDataChangedListener() {
            @Override
            public void userDataChanged() {
                tvUserStatus.setText(um.getCurrentUsersRole().toString());
                refreshButtonText();
            }
        });

        _btLogout = (Button) findViewById(R.id.bt_logout);
        _btLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(um.getCurrentUser().isAtLeast(User.Role.USER)) {
                    um.logOutCurrentUser();
                } else {
                    um.loginOrRegisterUser();
                }
            }
        });

        Button btParticipate = (Button) findViewById(R.id.bt_participate);
        btParticipate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                um.makeUserParticipant();
            }
        });

        refreshButtonText();
    }

    private void refreshButtonText() {
        _btLogout.setText(UserManager.getInstance(this).getCurrentUser().isAtLeast(User.Role.USER) ? "Logout" : "Login");
    }
}