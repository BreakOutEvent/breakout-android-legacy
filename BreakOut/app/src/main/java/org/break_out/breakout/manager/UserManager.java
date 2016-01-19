package org.break_out.breakout.manager;

import android.content.Context;
import android.content.Intent;

import org.break_out.breakout.ui.activities.BecomeTeamMemberActivity;
import org.break_out.breakout.ui.activities.LoginRegisterActivity;
import org.break_out.breakout.model.User;
import org.break_out.breakout.ui.activities.BOActivity;

/**
 * Created by Tino on 16.01.2016.
 */
public class UserManager {

    public static final int REQUEST_CODE_LOGIN = 0;
    public static final int REQUEST_CODE_BECOME_MEMBER = 1;

    public static final String KEY_USER = "key_user";

    private static UserManager _instance;

    private Context _context;

    private User _currUser = new User();

    private UserUpgradeListener _listener = null;

    public interface UserUpgradeListener {
        public void upgradeSuccessful();
        public void upgradeFailed();
    }

    private UserManager(Context context) {
        _context = context;

        _currUser = User.loadFromPrefs(context);
    }

    /**
     * Returns an instance of the UserManager.
     *
     * @param context The context (e.g., an Activity)
     * @return An instance of the UserManager
     */
    public static UserManager getInstance(Context context) {
        if(_instance == null) {
            _instance = new UserManager(context);
        }

        return _instance;
    }

    /**
     * Set the currently logged in user.
     *
     * @param user The currently logged in user
     */
    public void setCurrentUser(User user) {
        _currUser = user;
        user.saveToPrefs(_context);
    }

    /**
     * Opens the {@link LoginRegisterActivity} and lets the user either
     * login or create an account. The listener will be called, when the
     * process is finished.
     *
     * @param listener The listener for the login/register process
     */
    public void loginOrRegisterUser(UserUpgradeListener listener) {
        _listener = listener;

        Intent intent = new Intent(_context, LoginRegisterActivity.class);
        _context.startActivity(intent);
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
        _currUser.saveToPrefs(_context);
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
     * This method will be called by the {@link BOActivity}, when
     * the login/register Activity is done.
     *
     * @param success If the login/register process was successful
     */
    public void loginRegisterDone(boolean success) {
        callListener(success);
    }

}
