package org.break_out.breakout.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;

import okhttp3.Credentials;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Tino on 16.01.2016.
 */
public class User implements Serializable {

    private static final String TAG = "User";

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private Role _role = Role.VISITOR;

    // User information
    private long _remoteId = -1;
    private String _email = "";
    private String _password = "";
    private String _accessToken = "";

    // Participant information
    // TODO: Use enums where possible (and define them in a separate constants class)
    private String _gender = "";
    private String _firstName = "";
    private String _lastName = "";
    private String _emergencyNumber = "";
    private String _hometown = "";
    private String _phoneNumber = "";
    private String _tShirtSize = "";

    /**
     * Represents the role of a user.
     */
    public enum Role {
        VISITOR,
        USER,
        PARTICIPANT;

        public static Role fromString(String enumString) {
            try {
                return valueOf(enumString);
            } catch(Exception ex) {
                return VISITOR;
            }
        }
    }

    public User() {
        // Empty constructor
    }

    public User(String email, String password) {
        _email = (email != null ? email : "");
        _password = (password != null ? password : "");
    }

    public User(long remoteId, String email, String password) {
        _remoteId = (remoteId > -1 ? remoteId : -1);
        _email = (email != null ? email : "");
        _password = (password != null ? password : "");
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
     * Note that this method will return an empty String if the
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
     * Note that this method will return an empty String if the
     * user only has the role {@code VISITOR}.
     *
     * @return The password of the user or empty string if the user is {@code VISITOR}
     */
    public String getPassword() {
        return _password;
    }

    /**
     * Sets an OAuth access token to this user.
     *
     * @param accessToken The new access token
     */
    public void setAccessToken(String accessToken) {
        if(accessToken == null) {
            return;
        }

        _accessToken = accessToken;
    }

    /**
     * Returns the OAuth access token of this user.
     * Note that this method will return an empty String if the
     * user only has the role {@code VISITOR}.
     *
     * @return The OAuth access token of the user or empty string if the user is {@code VISITOR}
     */
    public String getAccessToken() {
        return _accessToken;
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
                return (_role == Role.USER || _role == Role.PARTICIPANT);
            case PARTICIPANT:
                return (_role == Role.PARTICIPANT);
            default:
                return false;
        }
    }

    public void setGender(String gender) {
        if(gender == null) {
            return;
        }

        _gender = gender;
    }

    public String getGender() {
        return _gender;
    }

    public void setFirstName(String firstName) {
        if(firstName == null) {
            return;
        }

        _firstName = firstName;
    }

    public String getFirstName() {
        return _firstName;
    }

    public void setLastName(String lastName) {
        if(lastName == null) {
            return;
        }

        _lastName = lastName;
    }

    public String getLastName() {
        return _lastName;
    }

    public void setEmergencyNumber(String emergencyNumber) {
        if(emergencyNumber == null) {
            return;
        }

        _emergencyNumber = emergencyNumber;
    }

    public String getEmergencyNumber() {
        return _emergencyNumber;
    }

    public void setHometown(String hometown) {
        if(hometown == null) {
            return;
        }

        _hometown = hometown;
    }

    public String getHometown() {
        return _hometown;
    }

    public void setPhoneNumber(String phoneNumber) {
        if(phoneNumber == null) {
            return;
        }

        _phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return _phoneNumber;
    }

    public void setTShirtSize(String tShirtSize) {
        if(tShirtSize == null) {
            return;
        }

        _tShirtSize = tShirtSize;
    }

    /**
     * Calling this method will start a synchronous network call registering
     * this user to the server. If successful, the {@link #_remoteId} of this
     * user will be changed to the ID returned by the server.
     *
     * @return True if the registration has been successful, false otherwise
     */
    public boolean registerOnServerSynchronously() {
        OkHttpClient client = new OkHttpClient();

        // Construct JSON for POST request
        String json = "{\n" +
                "  \"email\": \"" + _email + "\",\n" +
                "  \"password\": \"" + _password + "\"\n" +
                "}";

        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url("http://breakout-development.herokuapp.com/user/")
                .post(body)
                .build();

        try {
            Response response = client.newCall(request).execute();

            if(!response.isSuccessful()) {
                Log.e(TAG, "Registering user failed (response code " + response.code() + ")! Is the email in the correct format and the password long enough?");

                // TODO: Handle errors according to response code?
            } else {
                JSONObject jsonObj = new JSONObject(response.body().string());
                _remoteId = jsonObj.getLong("id");

                return true;
            }
        } catch(IOException e) {
            e.printStackTrace();
        } catch(JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Calling this method will start a synchronous network call trying to
     * log this user in on the server via OAuth. If successful, this method
     * will change the value of the {@link #_accessToken} of this user and set
     * its role to {@code USER}.
     *
     * @return True if the login has been successful, false otherwise
     */
    public boolean loginOnServerSynchronously() {
        OkHttpClient client = new OkHttpClient();

        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("breakout-development.herokuapp.com")
                .addPathSegment("oauth")
                .addPathSegment("token")
                .addQueryParameter("password", _password)
                .addQueryParameter("username", _email)
                .addQueryParameter("scope", "read write")
                .addQueryParameter("client_secret", "123456789")
                .addQueryParameter("client_id", "breakout_app")
                .addQueryParameter("grant_type", "password")
                .build();

        // A body is mandatory for every POST request -> use empty String as body
        RequestBody body = RequestBody.create(JSON, "");

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization", Credentials.basic("breakout_app", "123456789"))
                .build();

        try {
            Response response = client.newCall(request).execute();

            if(response.isSuccessful()) {
                // Get access token from JSON body and set it to this user
                JSONObject responseJson = new JSONObject(response.body().string());
                _accessToken = responseJson.getString("access_token");

                // Set role to user, as the user is now logged in
                _role = Role.USER;

                return true;
            } else {
                return false;
            }
        } catch(IOException e) {
            e.printStackTrace();
        } catch(JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public String toString() {
        return "(" + _remoteId + ")" + " " + _email + " - " + _role + " [" + _accessToken + "]";
    }
}
