package org.break_out.breakout.model;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.Serializable;

/**
 * Created by Tino on 16.01.2016.
 */
public class User implements Serializable {

    public static final String PREF_KEY = "pref_key_user";

    private static final String KEY_REMOTE_ID = "key_id";
    private static final String KEY_ROLE = "key_role";
    private static final String KEY_EMAIL = "key_email";
    private static final String KEY_PASSWORD = "key_password";

    private long _remoteId = -1;
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
        _email = (email != null ? email : "");
        _password = (password != null ? password : "");

        _role = Role.USER;
    }

    public User(long remoteId, String email, String password) {
        _remoteId = (remoteId > -1 ? remoteId : -1);
        _email = (email != null ? email : "");
        _password = (password != null ? password : "");

        _role = Role.USER;
    }

    /**
     * Set the ID assigned by the server.
     *
     * @param remoteId The ID of this user on the server
     */
    public void setRemoteId(long remoteId) {
        _remoteId = remoteId;
    }

    /**
     * Returns the global ID of this user as assigned
     * by the server.
     *
     * @return The remote ID or -1 if no ID has been assigned
     */
    public long getRemoteId() {
        return _remoteId;
    }

    /**
     * Sets an email to this user.
     *
     * @param email The new email
     */
    public void setEmail(String email) {
        if(email == null) {
            return;
        }

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
        if(password == null) {
            return;
        }

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
        if(role == null) {
            return;
        }

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

        editor.putLong(KEY_REMOTE_ID, _remoteId);
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

        long remoteId = sharedPref.getLong(KEY_REMOTE_ID, -1);
        Role role = Role.fromString(sharedPref.getString(KEY_ROLE, ""));
        String email = sharedPref.getString(KEY_EMAIL, "");
        String password = sharedPref.getString(KEY_PASSWORD, "");

        User user = new User();
        user.setRole(role);

        if(role == Role.VISITOR) {
            return user;
        }

        user.setRemoteId(remoteId);
        user.setEmail(email);
        user.setPassword(password);

        if(role == Role.USER) {
            return user;
        }

        // TODO: Set attributes that are only available for team members
        return user;
    }

    @Override
    public String toString() {
        return "(" + _remoteId + ")" + " " + _email + " - " + _role;
    }
}
