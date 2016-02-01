package org.break_out.breakout.manager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import org.break_out.breakout.ui.activities.BecomeParticipantActivity;
import org.break_out.breakout.ui.activities.LoginRegisterActivity;
import org.break_out.breakout.model.User;
import org.break_out.breakout.ui.activities.BOActivity;

/**
 * Created by Tino on 16.01.2016.
 */
public class UserManager {

    public static final int REQUEST_CODE_LOGIN = 0;
    public static final int REQUEST_CODE_BECOME_MEMBER = 1;

    public static final String PREF_KEY = "pref_key_user";

    private static final String KEY_REMOTE_ID = "key_id";
    private static final String KEY_ROLE = "key_role";
    private static final String KEY_EMAIL = "key_email";
    private static final String KEY_PASSWORD = "key_password";
    private static final String KEY_ACCESS_TOKEN = "key_access_token";

    public static final String KEY_USER = "key_user";

    private static UserManager _instance;

    private Context _context;

    private User _currUser = new User();

    private LoginRegisterListener _listener = null;

    public interface LoginRegisterListener {
        public void loginRegisterSuccessful();
        public void loginRegisterFailed();
    }

    private UserManager(Context context) {
        _context = context;
        loadCurrUserFromPrefs();
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
     * Notice: You should never need to call this method! It will
     * only be called by the {@link LoginRegisterActivity} or the
     * {@link BecomeParticipantActivity}.
     *
     * @param user The currently logged in user
     */
    public void setCurrentUser(User user) {
        _currUser = user;
        saveCurrUserToPrefs();
    }

    /**
     * Opens the {@link LoginRegisterActivity} and lets the user either
     * login or create an account. The listener will be called, when the
     * process is finished.
     *
     * @param listener The listener for the login/register process
     */
    public void loginOrRegisterUser(LoginRegisterListener listener) {
        _listener = listener;

        Intent intent = new Intent(_context, LoginRegisterActivity.class);
        _context.startActivity(intent);
    }

    /**
     * Logs out the current user.
     * Afterwards, the current user will be an empty dummy user
     * with the role {@code VISITOR}.<br />
     * You should consider rebuilding the UI after logging out the
     * user.
     */
    public void logOutCurrentUser() {
        _currUser = new User();
        saveCurrUserToPrefs();
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
     * @param loginRegistrationSuccessful If the login/registration was successful or not
     */
    private void callLoginRegisterListener(boolean loginRegistrationSuccessful) {
        if(_listener == null) {
            return;
        }

        if(loginRegistrationSuccessful) {
            _listener.loginRegisterSuccessful();
        } else {
            _listener.loginRegisterFailed();
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
        callLoginRegisterListener(success);
    }

    /**
     * Stores the current user to the SharedPreferences.
     * Note that there can always be only one user stored in the
     * preferences.
     */
    private void saveCurrUserToPrefs() {
        SharedPreferences sharedPref = _context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putLong(KEY_REMOTE_ID, _currUser.getRemoteId());
        editor.putString(KEY_ROLE, _currUser.getRole().toString());
        editor.putString(KEY_EMAIL, _currUser.getEmail());
        editor.putString(KEY_PASSWORD, _currUser.getPassword());
        editor.putString(KEY_ACCESS_TOKEN, _currUser.getAccessToken());

        editor.commit();
    }

    /**
     * This method will restore the user from the SharedPreferences.
     * If no user had been stored, this method will set an empty User
     * with the role {@code VISITOR}.
     *
     * @return The stored user
     */
    private void loadCurrUserFromPrefs() {
        SharedPreferences sharedPref = _context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);

        long remoteId = sharedPref.getLong(KEY_REMOTE_ID, -1);
        User.Role role = User.Role.fromString(sharedPref.getString(KEY_ROLE, ""));
        String email = sharedPref.getString(KEY_EMAIL, "");
        String password = sharedPref.getString(KEY_PASSWORD, "");
        String accessToken = sharedPref.getString(KEY_ACCESS_TOKEN, "");

        User user = new User();
        user.setRole(role);

        if(role == User.Role.VISITOR) {
            _currUser = user;
            return;
        }

        user.setRemoteId(remoteId);
        user.setEmail(email);
        user.setPassword(password);
        user.setAccessToken(accessToken);

        if(role == User.Role.USER) {
            _currUser = user;
            return;
        }

        // TODO: Set attributes that are only available for team members
    }

}
