package org.break_out.breakout.manager;

import android.content.Context;

import org.break_out.breakout.model.User;

/**
 * Created by Tino on 16.01.2016.
 */
public class UserManager {

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

        _currUser = User.loadFromPrefs(_context);
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
        // No such upgrade is possible, if the user already has this or a higher role
        if(_currUser.isAtLeast(role)) {
            listener.upgradeFailed();
            return;
        }

        _listener = listener;

        // TODO: Start register Activity, get results and call listener
        // Don't forget to save the new user to the prefs!
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

}
