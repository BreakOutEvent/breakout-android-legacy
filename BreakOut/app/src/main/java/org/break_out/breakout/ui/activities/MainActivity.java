package org.break_out.breakout.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.break_out.breakout.R;
import org.break_out.breakout.manager.UserManager;
import org.break_out.breakout.model.User;
import org.break_out.breakout.ui.views.BOFlatButton;

public class MainActivity extends BOActivity {

    private UserManager _userManager = null;

    private DrawerLayout _drawerLayout = null;
    private TextView _tvDrawerTitle = null;
    private TextView _tvDrawerSubtitle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _userManager = UserManager.getInstance(this);

        // Set up action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp);

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        // Set up drawer
        _drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navView = (NavigationView) findViewById(R.id.navigation_view);
        navView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        _drawerLayout.closeDrawers();
                        return true;
                    }
                });

        // Set up header
        View headerView = navView.getHeaderView(0);
        _tvDrawerTitle = (TextView) headerView.findViewById(R.id.tv_title);
        _tvDrawerSubtitle = (TextView) headerView.findViewById(R.id.tv_subtitle);

        // Participate button
        BOFlatButton btParticipate = (BOFlatButton) findViewById(R.id.bt_participate);
        btParticipate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UserManager.getInstance(MainActivity.this).getCurrentUser().isAtLeast(User.Role.USER)) {
                    UserManager.getInstance(MainActivity.this).makeUserParticipant();
                } else {
                    UserManager.getInstance(MainActivity.this).loginOrRegisterUser();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        updateDrawer();
    }

    private void updateDrawer() {
        User user = _userManager.getCurrentUser();

        if(!user.getFirstName().equals("") || !user.getLastName().equals("")) {
            _tvDrawerTitle.setText(user.getFirstName() + " " + user.getLastName());
        } else {
            _tvDrawerTitle.setText(user.getRole().toString());
        }

        if(!user.getEmail().equals("")) {
            _tvDrawerSubtitle.setText(user.getEmail());
        } else {
            _tvDrawerSubtitle.setText(getString(R.string.not_registered_yet));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if(menuItem.getItemId() == android.R.id.home) {
            _drawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(menuItem);
    }
}