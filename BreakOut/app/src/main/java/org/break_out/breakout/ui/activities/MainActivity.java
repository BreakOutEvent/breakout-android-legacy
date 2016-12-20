package org.break_out.breakout.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.break_out.breakout.R;
import org.break_out.breakout.manager.MediaManager;
import org.break_out.breakout.manager.UserManager;
import org.break_out.breakout.model.BOMedia;
import org.break_out.breakout.model.User;
import org.break_out.breakout.ui.fragments.AllPostsFragment;
import org.break_out.breakout.ui.fragments.EarlyBirdWelcomeFragment;
import org.break_out.breakout.ui.fragments.HelpFragment;
import org.break_out.breakout.ui.fragments.ProfileFragment;

public class MainActivity extends BOActivity implements UserManager.UserDataChangedListener {
    private static final String TAG = "MainActivity";

    private UserManager _userManager = null;

    private DrawerLayout _drawerLayout = null;

    private TextView _tvDrawerTitle = null;
    private TextView _tvDrawerSubtitle = null;
    private ImageView _ivProfileImage = null;
    private NavigationView _navView;

    private Fragment _currentFragment = null;
    private boolean _firstFragment = true;
    private ProfileFragment.ProfileFragmentListener _profileListener = null;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startTrackingService(getApplicationContext());

        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);

        _userManager = UserManager.getInstance(this);

        // Set up drawer
        _drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        _navView = (NavigationView) findViewById(R.id.navigation_view);
        _navView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        _drawerLayout.closeDrawers();

                        switch(menuItem.getItemId()) {
                            case R.id.post:
                                if(UserManager.getInstance(getApplicationContext()).getCurrentUsersRole()== User.Role.VISITOR) {
                                    Toast.makeText(getApplicationContext(),getApplicationContext().getString(R.string.toast_login_first),Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(getApplicationContext(),LoginRegisterActivity.class));
                                } else {
                                    startActivity(new Intent(getApplicationContext(), PostScreenActivity.class));
                                }
                                break;
                            case R.id.all_posts:
                                setCurrentFragment(new AllPostsFragment());
                                break;
                            case R.id.map:
                                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                                break;
                            case R.id.help:
                                setCurrentFragment(new HelpFragment());
                        }
                        return true;
                    }
                });




        // Set navigation drawer up header
        View headerView = _navView.getHeaderView(0);
        _tvDrawerTitle = (TextView) headerView.findViewById(R.id.tv_title);
        _tvDrawerSubtitle = (TextView) headerView.findViewById(R.id.tv_subtitle);
        _ivProfileImage = (ImageView) headerView.findViewById(R.id.profile_image);
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
        getSupportFragmentManager().popBackStack();
        setCurrentFragment(_currentFragment);

        //load user data
        _userManager.updateFromServer(this, new UserManager.UserUpdateListener() {
            @Override
            public void userUpdated() {
                User curUser = _userManager.getCurrentUser();
                if(curUser != null) {
                    if(curUser.getProfileImage()!=null) {
                        if(curUser.getProfileImage().isDownloaded()) {
                            Log.d(TAG,"image downloaded");
                            MediaManager.getInstance().setSizedImage(curUser.getProfileImage(),_ivProfileImage, BOMedia.SIZE.MEDIUM,true);

                        } else {
                            MediaManager.loadMediaFromServer(curUser.getProfileImage(),_ivProfileImage, BOMedia.SIZE.MEDIUM);
                        }
                    }
                }
                startTrackingService(getApplicationContext());
            }

            @Override
            public void updateFailed() {

            }
        });
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d(TAG,"onPrepareOptionsMenu called. Role : "+UserManager.getInstance(this).getCurrentUser().getRole());

        if(UserManager.getInstance(this).getCurrentUser().isAtLeast(User.Role.PARTICIPANT)) {
            menu.findItem(R.id.post).setEnabled(true);

        }
        return super.onPrepareOptionsMenu(menu);
    }

    public void openDrawer() {
        closeKeyboard();
        _drawerLayout.openDrawer(GravityCompat.START);
    }

    private void updateMenu() {
        Menu menu = _navView.getMenu();
        MenuItem postItem = menu.findItem(R.id.post);
        if(UserManager.getInstance(this).getCurrentUser().isAtLeast(User.Role.PARTICIPANT)) {
            postItem.setEnabled(true);
        }
    }

    public void setCurrentFragment(@Nullable Fragment fragment) {
        if(fragment != null) {
            if(fragment instanceof ProfileFragment) {
                _profileListener = new ProfileListener();
                ((ProfileFragment) fragment).registerListener(_profileListener);
            }

            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_placeholder, fragment, "NewFragmentTag");
            if(_firstFragment) {
                _firstFragment = false;

            } else {
                ft.addToBackStack("NewFragmentTag");

            }
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
        updateMenu();

        if(!user.getFirstName().equals("") || !user.getLastName().equals("")) {
            _tvDrawerTitle.setText(user.getFirstName() + " " + user.getLastName());
        } else {
            _tvDrawerTitle.setText(user.getRole().toString());
        }

        if(user.getProfileImage()!=null) {
            if(!user.getProfileImage().isDownloaded()) {
                MediaManager.loadMediaFromServer(user.getProfileImage(),_ivProfileImage, BOMedia.SIZE.MEDIUM);
            } else {
                MediaManager.decodeSampledBitmapFromFile(user.getProfileImage(),100,100);
            }

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
            case R.id.post:
                startActivityForResult(new Intent(this, PostScreenActivity.class), 0);
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