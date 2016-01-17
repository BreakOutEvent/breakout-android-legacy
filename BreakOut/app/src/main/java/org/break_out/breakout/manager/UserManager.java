package org.break_out.breakout.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import org.break_out.breakout.model.User;

/**
 * Created by Tino on 16.01.2016.
 */
public class UserManager {

    public static final int REQUEST_CODE_LOGIN = 0;
    public static final int REQUEST_CODE_BECOME_MEMBER = 1;

    public static final String KEY_USER = "key_user";

    private static UserManager _instance;

    private Activity _activity;

    private User _currUser = new User();

    private UserUpgradeListener _listener = null;

    public interface UserUpgradeListener {
        public void upgradeSuccessful();
        public void upgradeFailed();
    }

    private UserManager(Activity activity) {
        _activity = activity;

        _currUser = User.loadFromPrefs(_activity);
    }

    /**
     * Returns an instance of the UserManager.
     *
     * @param activity The context (e.g., an Activity)
     * @return An instance of the UserManager
     */
    public static UserManager getInstance(Activity activity) {
        if(_instance == null) {
            _instance = new UserManager(activity);
        }

        return _instance;
    }

    /**
     * Set the currently logged in user.
     *
     * @param user The currently logged in user
     */
    private void setCurrentUser(User user) {
        _currUser = user;
        user.saveToPrefs(_activity);
    }

    private void sendLoginRegisterIntent() {
        //Intent intent = new Intent(this, LoginRegisterActivity.class);
        //_activity.startActivityForResult(intent, REQUEST_CODE_LOGIN);
    }

    private void sendBecomeTeamMemberIntent() {
        //Intent intent = new Intent(this, BecomeTeamMemberActivity.class);
        //_activity.startActivityForResult(intent, REQUEST_CODE_BECOME_MEMBER);
    }

    /**
     * Upgrades the current user to the given role.<br />
     * This method will launch an Activity which will handle the
     * registration of the user. If everything went fine and the user
     * could be upgraded, this method call the listener's {@link UserUpgradeListener#upgradeSuccessful()}
     * method. If anything went wrong (e.g., the user cancelled the registration process) or the user
     * already own this or a higher role, the listener's {@link UserUpgradeListener#upgradeFailed()} method
     * will be called.
     *
     * @param role The role the current user should be upgraded to (has to be higher than the current one)
     * @param listener The listener for the result of the upgrade
     */
    public void upgradeCurrentUser(User.Role role, UserUpgradeListener listener) {
        _listener = listener;

        // No such upgrade is possible if the user already has this or a higher role
        if(_currUser.isAtLeast(role)) {
            callListener(false);
            return;
        }

        if(role == User.Role.USER) {
            sendLoginRegisterIntent();
        } else if(role == User.Role.TEAM_MEMBER) {

            // Only users can become team members
            if(getCurrentUsersRole() != User.Role.USER) {
                callListener(false);
                return;
            }

            sendBecomeTeamMemberIntent();
        }
    }

    /**
     * Logs out the current user.
     * Afterwards, the current user will be an empty dummy user
     * with the role {@code VISITOR}.<br />
     * You should think about rebuilding the UI after logging out the
     * user.
     */
    public void logOutCurrentUser() {
        _currUser = new User();
        _currUser.saveToPrefs(_activity);
    }

    /**
     * Returns the currently logged in user.
     * If there is no user logged in, this method will return
     * an empty instance of {@link User} with the role {@code VISITOR}.
     *
     * @return The current user
     */
    public User getCurrentUser() {
        return _currUser;
    }

    /**
     * Returns the role of the currently logged in user.
     * If no user is logged in, this method will return the
     * role {@code VISITOR}.
     *
     * @return The role of the currently logged in user
     */
    public User.Role getCurrentUsersRole() {
        return _currUser.getRole();
    }

    /**
     * This method will call the upgrade listener, if it is not null.
     * Depending on the boolean passed as a parameter, the corresponding
     * method of the listener will be called.
     *
     * @param loginSuccessful If the login was successful or not
     */
    private void callListener(boolean loginSuccessful) {
        if(_listener == null) {
            return;
        }

        if(loginSuccessful) {
            _listener.upgradeSuccessful();
        } else {
            _listener.upgradeFailed();
        }

        _listener = null;
    }

    /**
     * This method will be called by the {@link org.break_out.breakout.BOActivity}, when
     * the login/register Activity is done.
     *
     * @param resultCode The data from the login/register Activity
     */
    public void loginActivityDone(int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            User user = (User) data.getSerializableExtra(KEY_USER);
            setCurrentUser(user);
            callListener(true);
        } else {
            callListener(false);
        }
    }

}
