package org.break_out.breakout.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import org.break_out.breakout.R;
import org.break_out.breakout.manager.UserManager;
import org.break_out.breakout.model.User;
import org.break_out.breakout.sync.BOSyncController;
import org.break_out.breakout.sync.model.Posting;
import org.break_out.breakout.ui.fragments.EarlyBirdWelcomeFragment;
import org.break_out.breakout.ui.fragments.ProfileFragment;

public class MainActivity extends BOActivity implements UserManager.UserDataChangedListener {

    private UserManager _userManager = null;

    private DrawerLayout _drawerLayout = null;

    private TextView _tvDrawerTitle = null;
    private TextView _tvDrawerSubtitle = null;

    private Fragment _currentFragment = null;
    private ProfileFragment.ProfileFragmentListener _profileListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);

        _userManager = UserManager.getInstance(this);
        _userManager.updateFromServer(null);

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
                if(_userManager.getCurrentUser().isAtLeast(User.Role.USER)) {
                    setCurrentFragment(new ProfileFragment());
                    _drawerLayout.closeDrawers();
                }
            }
        });

        _currentFragment = new EarlyBirdWelcomeFragment();
        setCurrentFragment(_currentFragment);
    }

    @Override
    protected void onStart() {
        super.onStart();

        updateDrawer();
        _userManager.registerListener(this);

        // Register fragment listener(s)
        if(_currentFragment instanceof ProfileFragment) {
            _profileListener = new ProfileListener();
            ((ProfileFragment) _currentFragment).registerListener(_profileListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        _userManager.unregisterListener(this);

        // Unregister fragment listener(s)
        if(_currentFragment instanceof ProfileFragment) {
            ((ProfileFragment) _currentFragment).unregisterListener(_profileListener);
            _profileListener = null;
        }
    }

    public void openDrawer() {
        closeKeyboard();
        _drawerLayout.openDrawer(GravityCompat.START);
    }

    public void setCurrentFragment(@Nullable Fragment fragment) {
        if(fragment != null) {
            if(fragment instanceof ProfileFragment) {
                _profileListener = new ProfileListener();
                ((ProfileFragment) fragment).registerListener(_profileListener);
            }

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
                openDrawer();
                break;
            case R.id.action_ok:
                setCurrentFragment(new EarlyBirdWelcomeFragment());
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onUserDataChanged() {
        updateDrawer();
    }

    private class ProfileListener implements ProfileFragment.ProfileFragmentListener {

        @Override
        public void onDone() {
            setCurrentFragment(new EarlyBirdWelcomeFragment());
            closeKeyboard();
        }
    }
}