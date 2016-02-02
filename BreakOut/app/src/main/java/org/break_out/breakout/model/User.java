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

    // Optional user information
    private String _firstName = "";
    private String _lastName = "";

    // Participant information
    private String _gender = "";
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

    public String getTShirtSize() {
        return _tShirtSize;
    }

    /**
     * Calling this method will start a synchronous network call registering
     * this user to the server. If successful, the {@link #_remoteId} of this
     * user will be changed to the ID returned by the server.
     *
     * @return True if the registration has been successful, false otherwise
     */
    public boolean registerOnServerSync() {
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
                Log.e(TAG, "Registering user failed (response code " + response.code() + ")! Possible reasons: Email format, email already existing, password too short.");

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
    public boolean loginOnServerSync() {
        OkHttpClient client = new OkHttpClient();

        // Build URL
        HttpUrl loginUrl = new HttpUrl.Builder()
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
        RequestBody emptyBody = RequestBody.create(JSON, "");

        Request loginRequest = new Request.Builder()
                .url(loginUrl)
                .post(emptyBody)
                .addHeader("Authorization", Credentials.basic("breakout_app", "123456789"))
                .build();

        try {
            Response loginResponse = client.newCall(loginRequest).execute();

            if(!loginResponse.isSuccessful()) {
                return false;
            }

            // Get access token from JSON body and set it to this user
            JSONObject loginResponseJson = new JSONObject(loginResponse.body().string());
            loginResponse.body().close();

            _accessToken = loginResponseJson.getString("access_token");
            _role = Role.USER;

            Log.d(TAG, "OAuth access token: " + _accessToken);

            boolean updateSuccessful = updateFromServerSync();
            if(!updateSuccessful) {
                Log.e(TAG, "Login process failed: Could not finish due to error while updating user.");
                return false;
            }

            return true;
        } catch(IOException e) {
            Log.e(TAG, e.getMessage());
        } catch(JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Calling this method will overwrite all data saved in this
     * user with the date of this user on the server. Local changes
     * that have not been updated on the server yet will be lost!
     * This method will run synchronously.
     *
     * @return If the update was successful or not
     */
    public boolean updateFromServerSync() {
        if(_role == Role.VISITOR) {
            Log.e(TAG, "Could not update user because it does not have an account.");
            return false;
        }

        OkHttpClient client = new OkHttpClient();

        HttpUrl updateUrl = HttpUrl.parse("https://breakout-development.herokuapp.com/me/");

        // Get remote ID from server
        Request updateRequest = new Request.Builder()
                .url(updateUrl)
                .get()
                .addHeader("Authorization", "Bearer " + _accessToken)
                .build();

        try {
            Response updateResponse = client.newCall(updateRequest).execute();

            if(!updateResponse.isSuccessful()) {
                Log.e(TAG, "Could not update the user from the server (" + updateResponse.code() + ").");
                return false;
            }

            JSONObject responseObj = new JSONObject(updateResponse.body().string());
            updateResponse.body().close();

            // Get values
            long remoteId = (responseObj.has("id") ? responseObj.getLong("id") : -1);
            String firstName = (responseObj.has("firstname") ? responseObj.getString("firstname") : null);
            String lastName = (responseObj.has("lastname") ? responseObj.getString("lastname") : null);
            String email = (responseObj.has("email") ? responseObj.getString("email") : null);
            String gender = (responseObj.has("gender") ? responseObj.getString("gender") : null);

            String emergencyNumber = null;
            String phoneNumber = null;
            String tShirtSize = null;
            String hometown = null;

            // Get participant values
            if(!responseObj.isNull("participant")) {
                JSONObject participantObj = responseObj.getJSONObject("participant");

                // The user is a participant
                emergencyNumber = (participantObj.has("emergencynumber") ? participantObj.getString("emergencynumber") : null);
                phoneNumber = (participantObj.has("phonenumber") ? participantObj.getString("phonenumber") : null);
                tShirtSize = (participantObj.has("tshirtsize") ? participantObj.getString("tshirtsize") : null);
                hometown = (participantObj.has("hometown") ? participantObj.getString("hometown") : null);

                _role = Role.PARTICIPANT;
            } else {
                _role  = Role.USER;
            }

            // Set user values
            _remoteId= (remoteId >= 0 ? remoteId : -1);
            _firstName = (firstName != null ? firstName : "");
            _lastName = (lastName != null ? lastName : "");
            _email = (email != null ? email : "");
            _gender = (gender != null ? gender : "");

            // Set participant values
            _emergencyNumber = (emergencyNumber != null ? emergencyNumber : "");
            _phoneNumber = (phoneNumber != null ? phoneNumber : "");
            _tShirtSize = (tShirtSize != null ? tShirtSize : "");
            _hometown = (hometown != null ? hometown : "");

            return true;
        } catch(IOException e) {
            e.printStackTrace();
        } catch(JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * This method will upload this user to the server and update
     * its information online. The data on the server will be overwritten.
     *
     * @return If the update was successful or not
     */
    public boolean updateOnServerSync() {
        if(_role == Role.VISITOR) {
            Log.e(TAG, "Could not update user because it does not have an account.");
            return false;
        }

        OkHttpClient client = new OkHttpClient();

        // Build URL
        HttpUrl updateUrl = HttpUrl.parse("https://breakout-development.herokuapp.com/user/" + _remoteId + "/");

        // Build body
        JSONObject userJSON = toJSON();

        if(userJSON == null) {
            Log.e(TAG, "Could not create a valid JSON object for this user. The update has been cancelled.");
            return false;
        }

        RequestBody updateBody = RequestBody.create(JSON, userJSON.toString());

        Log.d(TAG, toJSON().toString());

        Request updateRequest = new Request.Builder()
                .url(updateUrl)
                .put(updateBody)
                .addHeader("Authorization", "Bearer " + _accessToken)
                .build();

        try {
            Response updateResponse = client.newCall(updateRequest).execute();

            if(!updateResponse.isSuccessful()) {
                Log.e(TAG, "The network call for updating the user was not successful (" + updateResponse.code() + ").");
                return false;
            }

            Log.d(TAG, "User has been updated.");

            return true;

        } catch(IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * This method will return a JSON object representation of this user.
     *
     * @return JSON representation of this user or null if an error occured
     */
    private JSONObject toJSON() {
        JSONObject userObj = new JSONObject();

        try {
            userObj.put("email", _email);
            userObj.put("firstname", _firstName);
            userObj.put("lastname", _lastName);
            userObj.put("gender", _gender);

            // Note: The password cannot be changed!

            if(_role == Role.PARTICIPANT) {
                JSONObject participantObject = new JSONObject();

                participantObject.put("emergencynumber", _emergencyNumber);
                participantObject.put("hometown", _hometown);
                participantObject.put("phonenumber", _phoneNumber);
                participantObject.put("tshirtsize", _tShirtSize);

                // Add participant object to user object
                userObj.put("participant", participantObject);
            }

            return userObj;
        } catch(JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public String toString() {
        return "(" + _remoteId + ")" + " " + _email + " - " + _role + " [" + _accessToken + "]";
    }
}
