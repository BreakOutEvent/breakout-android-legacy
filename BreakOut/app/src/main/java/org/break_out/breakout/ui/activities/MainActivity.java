package org.break_out.breakout.ui.activities;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.break_out.breakout.R;
import org.break_out.breakout.manager.UserManager;
import org.break_out.breakout.model.User;
import org.break_out.breakout.ui.fragments.EarlyBirdWelcomeFragment;
import org.break_out.breakout.ui.fragments.ProfileFragment;

public class MainActivity extends BOActivity implements UserManager.UserDataChangedListener {

    private UserManager _userManager = null;

    private Toolbar _toolbar = null;
    private ActionBar _actionbar = null;
    private DrawerLayout _drawerLayout = null;

    private TextView _tvDrawerTitle = null;
    private TextView _tvDrawerSubtitle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);

        _userManager = UserManager.getInstance(this);

        // Set up actionbar
        _toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(_toolbar);

        _actionbar = getSupportActionBar();

        setToolbarTransparent(true);

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

        // Set navigation drawer up header
        View headerView = navView.getHeaderView(0);
        _tvDrawerTitle = (TextView) headerView.findViewById(R.id.tv_title);
        _tvDrawerSubtitle = (TextView) headerView.findViewById(R.id.tv_subtitle);
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurrentFragment(new ProfileFragment());
                setToolbarTransparent(false);
                _drawerLayout.closeDrawers();
            }
        });

        setCurrentFragment(new EarlyBirdWelcomeFragment());
    }

    @Override
    protected void onStart() {
        super.onStart();

        updateDrawer();
        _userManager.registerListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        _userManager.unregisterListener(this);
    }

    public void setCurrentFragment(@Nullable Fragment fragment) {
        if(fragment != null) {
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_placeholder, fragment, "NewFragmentTag");
            ft.addToBackStack("NewFragmentTag");
            ft.commit();
            return;
        }

        Fragment currFragment = getSupportFragmentManager().findFragmentByTag("NewFragmentTag");
        if(currFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(currFragment).commit();
        }
    }

    private void setToolbarTransparent(boolean transparent) {
        // Enable/disable title and home icon
        if(_actionbar != null) {
            _actionbar.setDisplayShowTitleEnabled(!transparent);
            _actionbar.setDisplayShowHomeEnabled(true);
        }

        // Burger icon
        _toolbar.setNavigationIcon(transparent ? R.drawable.ic_menu_black_24dp : R.drawable.ic_menu_white_24dp);

        // Background color
        _actionbar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, transparent ? android.R.color.transparent : R.color.colorPrimary)));

        // Transparency
        _toolbar.setAlpha(transparent ? 0.5f : 1.0f);

        // Set fragment below or behind toolbar
        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        p.addRule(RelativeLayout.BELOW, transparent ? 0 : R.id.toolbar);
        findViewById(R.id.fragment_placeholder).setLayoutParams(p);
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
        switch(menuItem.getItemId()) {
            case android.R.id.home:
                _drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.action_ok:
                setCurrentFragment(new EarlyBirdWelcomeFragment());
                setToolbarTransparent(true);
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void userDataChanged() {
        updateDrawer();
    }
}