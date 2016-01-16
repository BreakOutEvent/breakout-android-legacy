package org.break_out.breakout.model;

import android.content.Context;
import android.content.SharedPreferences;

import org.break_out.breakout.R;

/**
 * Created by Tino on 16.01.2016.
 */
public class User {

    public static final String PREF_KEY = "pref_key_user";

    public static final String KEY_ROLE = "key_role";
    public static final String KEY_EMAIL = "key_email";
    public static final String KEY_PASSWORD = "key_password";

    private String _email = "";
    private String _password = "";

    private Role _role = Role.VISITOR;

    /**
     * Represents the role of a user.
     */
    public enum Role {
        VISITOR,
        USER,
        TEAM_MEMBER;

        public static Role fromString(String enumString) {
            try {
                return valueOf(enumString);
            } catch(Exception ex) {
                return VISITOR;
            }
        }
    }

    public User() {

    }

    public User(String email, String password) {
        _email = email;
        _password = password;

        _role = Role.USER;
    }

    /**
     * Sets an email to this user.
     *
     * @param email The new email
     */
    public void setEmail(String email) {
        _email = email;
    }

    /**
     * Returns the email of this user.
     * Note that this method will return an empty String, if the
     * user only has the role {@code VISITOR}.
     *
     * @return The email of the user or empty string if the user is {@code VISITOR}
     */
    public String getEmail() {
        return _email;
    }

    /**
     * Sets a password to this user.
     *
     * @param password The new password
     */
    public void setPassword(String password) {
        _password = password;
    }

    /**
     * Returns the password of this user.
     * Note that this method will return an empty String, if the
     * user only has the role {@code VISITOR}.
     *
     * @return The password of the user or empty string if the user is {@code VISITOR}
     */
    public String getPassword() {
        return _password;
    }

    /**
     * Sets the role of this user.
     *
     * @param role The new role of the user
     */
    public void setRole(Role role) {
        _role = role;
    }

    /**
     * Returns the role of this user.
     *
     * @return The role of the user
     */
    public Role getRole() {
        return _role;
    }

    /**
     * Use this method to check if this user has at least
     * a specific role.
     *
     * @param role The role the user should have at least
     * @return True if the user's role is at least the role given, false otherwise
     */
    public boolean isAtLeast(Role role) {
        switch(role) {
            case VISITOR:
                return true;
            case USER:
                return (_role == Role.USER || _role == Role.TEAM_MEMBER);
            case TEAM_MEMBER:
                return (_role == Role.TEAM_MEMBER);
            default:
                return false;
        }
    }

    /**
     * Stores this user to the SharedPreferences.
     * Note that there can always be only one user stored in the
     * preferences.
     *
     * @param context A context
     */
    public void saveToPrefs(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(KEY_ROLE, _role.toString());
        editor.putString(KEY_EMAIL, _email);
        editor.putString(KEY_PASSWORD, _password);

        editor.commit();
    }

    /**
     * This method will restore the user from the SharedPreferences.
     * If no user had been stored, this method will return an empty User
     * with the role {@code VISITOR}.
     *
     * @param context A context
     * @return The stored user
     */
    public static User loadFromPrefs(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);

        Role role = Role.fromString(sharedPref.getString(KEY_ROLE, ""));
        String email = sharedPref.getString(KEY_EMAIL, "");
        String password = sharedPref.getString(KEY_PASSWORD, "");

        User user = new User();
        user.setRole(role);

        if(role == Role.VISITOR) {
            return user;
        }

        user.setEmail(email);
        user.setPassword(password);

        if(role == Role.USER) {
            return user;
        }

        // TODO: Set attributes that are only available for team members
        return user;
    }
}
