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
    public User.Role getCurrentUserRole() {
        return _currUser.getRole();
    }

}
