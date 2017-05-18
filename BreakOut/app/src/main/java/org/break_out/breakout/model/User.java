package org.break_out.breakout.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.break_out.breakout.R;
import org.break_out.breakout.manager.MediaManager;
import org.break_out.breakout.manager.UserManager;
import org.break_out.breakout.secrets.BOSecrets;
import org.break_out.breakout.util.URLUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;

import okhttp3.Credentials;
import okhttp3.FormBody;
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
    private static final String FORM_URL_ENCODED = "application/x-www-form-urlencoded";

    private static final String NULL = "null";

    // FIXME: Handle participants without a team correctly!
    private Role _role = Role.VISITOR;

    // User information
    private long _remoteId = -1;
    private int _teamID = -1;
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
    private Calendar _birthday = null;
    private String _eventCity = "";
    private int _eventId = -1;

    private BOMedia _profileImage;

    /**
     * Represents the role of a user.
     */
    public enum Role {
        VISITOR,
        USER,
        PARTICIPANT_WITHOUT_TEAM,
        PARTICIPANT, SPONSOR;

        public static Role fromString(String enumString) {
            try {
                return valueOf(enumString);
            } catch (Exception ex) {
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

    public User(User original) {
        setEmail(original.getEmail());
        setPassword(original.getPassword());
        setFirstName(original.getFirstName());
        setLastName(original.getLastName());
        setRemoteId(original.getRemoteId());
        setTeamId(original.getTeamId());
        setRole(original.getRole());
        setHometown(original.getHometown());
        setAccessToken(original.getAccessToken());
        setPhoneNumber(original.getPhoneNumber());
        setEmergencyNumber(original.getEmergencyNumber());
        setGender(original.getGender());
        setTShirtSize(original.getTShirtSize());
        setEventCity(original.getEventCity());
        setEventId(original.getEventId());
        setProfileImage(original.getProfileImage());

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

    public int getTeamId() {
        return _teamID;
    }

    public void setTeamId(int teamId) {
        _teamID = teamId;
    }

    /**
     * Sets an email to this user.
     *
     * @param email The new email
     */
    public void setEmail(String email) {
        _email = email != null ? email : "";
    }

    /**
     * Returns the email of this user.
     * Note that this method will return an empty String if the
     * user only has the role {@code VISITOR}.
     *
     * @return The email of the user or empty string if the user is {@code VISITOR}
     */
    public
    @NonNull
    String getEmail() {
        return _email;
    }

    /**
     * Sets a password to this user.
     *
     * @param password The new password
     */
    public void setPassword(String password) {
        _password = password != null ? password : "";
    }

    /**
     * Returns the password of this user.
     * Note that this method will return an empty String if the
     * user only has the role {@code VISITOR}.
     *
     * @return The password of the user or empty string if the user is {@code VISITOR}
     */
    public
    @NonNull
    String getPassword() {
        return _password;
    }

    /**
     * Sets an OAuth access token to this user.
     *
     * @param accessToken The new access token
     */
    public void setAccessToken(String accessToken) {
        _accessToken = accessToken != null ? accessToken : "";
    }

    /**
     * Returns the OAuth access token of this user.
     * Note that this method will return an empty String if the
     * user only has the role {@code VISITOR}.
     *
     * @return The OAuth access token of the user or empty string if the user is {@code VISITOR}
     */
    public
    @NonNull
    String getAccessToken() {
        return _accessToken;
    }

    /**
     * Sets the role of this user.
     *
     * @param role The new role of the user
     */
    public void setRole(Role role) {
        _role = role != null ? role : Role.VISITOR;
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
        switch (role) {
            case VISITOR:
                return true;
            case USER:
                return (_role == Role.USER || _role == Role.PARTICIPANT_WITHOUT_TEAM || _role == Role.PARTICIPANT);
            case PARTICIPANT:
                return (_role == Role.PARTICIPANT);
            default:
                return false;
        }
    }

    public void setGender(@Nullable String gender) {
        _gender = (gender != null ? gender : "");
    }

    public
    @NonNull
    String getGender() {
        return _gender;
    }

    public void setFirstName(@Nullable String firstName) {
        _firstName = firstName != null ? firstName : "";
    }

    public
    @NonNull
    String getFirstName() {
        return _firstName;
    }

    public void setLastName(@Nullable String lastName) {
        _lastName = lastName != null ? lastName : "";
    }

    public
    @NonNull
    String getLastName() {
        return _lastName;
    }

    public void setEmergencyNumber(@Nullable String emergencyNumber) {
        _emergencyNumber = emergencyNumber != null ? emergencyNumber : "";
    }

    public
    @NonNull
    String getEmergencyNumber() {
        return _emergencyNumber;
    }

    public void setHometown(@Nullable String hometown) {
        _hometown = hometown != null ? hometown : "";
    }

    public
    @NonNull
    String getHometown() {
        return _hometown;
    }

    public void setEventId(int id) {
        _eventId = id;
        Log.d(TAG, "set event id to: " + _eventId);
    }

    public int getEventId() {
        return _eventId;
    }

    public void setPhoneNumber(@Nullable String phoneNumber) {
        _phoneNumber = phoneNumber != null ? phoneNumber : "";
    }

    public
    @NonNull
    String getPhoneNumber() {
        return _phoneNumber;
    }

    public void setTShirtSize(@Nullable String tShirtSize) {
        _tShirtSize = tShirtSize != null ? tShirtSize : "";
    }

    public
    @NonNull
    String getTShirtSize() {
        return _tShirtSize;
    }

    public void setBirthday(@Nullable Calendar birthday) {
        _birthday = birthday;
    }

    public
    @Nullable
    Calendar getBirthday() {
        return _birthday;
    }

    public void setEventCity(@Nullable String eventCity) {
        _eventCity = eventCity != null ? eventCity : "";
    }

    public
    @NonNull
    String getEventCity() {
        return _eventCity;
    }

    public void setProfileImage(BOMedia media) {
        _profileImage = media;
    }

    @Nullable
    public BOMedia getProfileImage() {
        return _profileImage;
    }

    /**
     * Calling this method will start a synchronous network call registering
     * this user to the server. If successful, the {@link #_remoteId} of this
     * user will be changed to the ID returned by the server.
     *
     * @return True if the registration has been successful, false otherwise
     */
    public boolean registerOnServerSync(Context c) {
        OkHttpClient client = new OkHttpClient();

        // Construct JSON for POST request
        String json = "{\n" +
                "  \"email\": \"" + _email + "\",\n" +
                "  \"password\": \"" + _password + "\"\n" +
                "}";

        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(URLUtils.getBaseUrl(c) + "/user/")
                .post(body)
                .build();

        try {
            Response response = client.newCall(request).execute();

            if (!response.isSuccessful()) {
                Log.e(TAG, "Registering user failed (response code " + response.code() + ")! Possible reasons: Email format, email already existing, password too short.");
                Log.e(TAG, "Response: " + response.body().string());

                // TODO: Handle errors according to response code?
            } else {
                JSONObject jsonObj = new JSONObject(response.body().string());
                _remoteId = jsonObj.getLong("id");

                return true;
            }
        } catch (IOException | JSONException e) {
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
    public boolean loginOnServerSync(Context c) {
        OkHttpClient client = new OkHttpClient();
        Log.d(TAG, "loginOnServerSync");
        Log.d(TAG, "set password: " + _password);

        // Build URL
        String scheme = "https";
        int port = Integer.parseInt(URLUtils.getPort());
        if (c.getSharedPreferences(c.getString(R.string.PREFERENCES_GLOBAL), Context.MODE_PRIVATE).getBoolean(c.getString(R.string.PREFERENCE_IS_TEST), true)) {
            scheme = "http";
        }
        HttpUrl.Builder loginUrlBuilder = new HttpUrl.Builder()
                .scheme(scheme)
                .host(URLUtils.getBaseUrlWithoutProtocol(c))
                .addPathSegment("oauth")
                .addPathSegment("token");
        if (c.getSharedPreferences(c.getString(R.string.PREFERENCES_GLOBAL), Context.MODE_PRIVATE).getBoolean(c.getString(R.string.PREFERENCE_IS_TEST), true)) {
            loginUrlBuilder.port(port);
        }
        HttpUrl loginUrl = loginUrlBuilder.build();


        // Build x-www-form-urlencoded body
        RequestBody body = new FormBody.Builder()
                .add("password", _password)
                .add("username", _email)
                .add("scope", "read write")
                .add("grant_type", "password")
                .build();

        Request loginRequest = new Request.Builder()
                .url(loginUrl)
                .post(body)
                .addHeader("Authorization", Credentials.basic("breakout_app", new BOSecrets().getClientSecret(c)))
                .addHeader("Content-Type", FORM_URL_ENCODED)
                .build();
        try {
            Response loginResponse = client.newCall(loginRequest).execute();

            String response = loginResponse.body().string();
            Log.d(TAG, "login response: " + response);
            if (!loginResponse.isSuccessful()) {
                Log.e(TAG, loginResponse.body().string());
                return false;
            }

            // Get access token from JSON body and set it to this user
            JSONObject loginResponseJson = new JSONObject(response);
            loginResponse.body().close();
            Log.d(TAG, "login :\n" + response);
            BOMedia profilePic = null;
            if (!loginResponseJson.isNull("profilePic")) {
                profilePic = BOMedia.sizedMediaFromJSON(c, loginResponseJson.getJSONObject("profilePic"), BOMedia.SIZE.MEDIUM);
                Log.d(TAG, "profile pic : " + profilePic.getUrl());
                setProfileImage(profilePic);
            }

            _accessToken = loginResponseJson.getString("access_token");
            _role = Role.USER;

            if (getProfileImage() != null) {
                if (!getProfileImage().isDownloaded()) {
                    MediaManager.loadMediaFromServer(getProfileImage(), null, BOMedia.SIZE.MEDIUM);
                }
            }

            Log.d(TAG, loginResponseJson.toString());
            Log.d(TAG, "OAuth access token: " + _accessToken);

            boolean updateSuccessful = updateFromServerSync(c);
            if (!updateSuccessful) {
                Log.e(TAG, "Login process failed: Could not finish due to error while updating user.");
                return false;
            }

            return true;
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        } catch (JSONException e) {
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
    public boolean updateFromServerSync(Context c) {
        if (_role == Role.VISITOR) {
            Log.e(TAG, "Could not update user because it does not have an account");
            return false;
        }

        OkHttpClient client = new OkHttpClient();

        HttpUrl updateUrl = HttpUrl.parse(URLUtils.getBaseUrl(c) + "/me/");

        // Get remote ID from server
        Request updateRequest = new Request.Builder()
                .url(updateUrl)
                .get()
                .addHeader("Authorization", "Bearer " + _accessToken)
                .build();

        try {
            Response updateResponse = client.newCall(updateRequest).execute();

            if (!updateResponse.isSuccessful()) {
                Log.e(TAG, "Could not update the user from the server (" + updateResponse.code() + ")");
                loginOnServerSync(c);
                return false;
            }

            JSONObject responseObj = new JSONObject(updateResponse.body().string());
            updateResponse.body().close();

            Log.d(TAG, "response object: " + responseObj.toString());


            // Get values
            long remoteId = ((responseObj.has("id") && !responseObj.isNull("id")) ? responseObj.getLong("id") : -1);

            String firstName = ((responseObj.has("firstname") && !responseObj.isNull("firstname")) ? responseObj.getString("firstname") : null);
            String lastName = ((responseObj.has("lastname") && !responseObj.isNull("lastname")) ? responseObj.getString("lastname") : null);
            String email = ((responseObj.has("email") && !responseObj.isNull("email")) ? responseObj.getString("email") : null);
            String gender = ((responseObj.has("gender") && !responseObj.isNull("gender")) ? responseObj.getString("gender") : null);

            String emergencyNumber = null;
            String phoneNumber = null;
            String tShirtSize = null;
            String hometown = null;
            int teamId = -1;
            int eventId = -1;
            BOMedia profileImage = UserManager.getInstance(c).getCurrentUser().getProfileImage();

            // Get participant values
            if (!responseObj.isNull("participant")) {
                JSONObject participantObj = responseObj.getJSONObject("participant");

                // The user is a participant
                emergencyNumber = ((participantObj.has("emergencynumber") && !participantObj.isNull("emergencynumber")) ? participantObj.getString("emergencynumber") : null);
                phoneNumber = ((participantObj.has("phonenumber") && !participantObj.isNull("phonenumber")) ? participantObj.getString("phonenumber") : null);
                tShirtSize = ((participantObj.has("tshirtsize") && !participantObj.isNull("tshirtsize")) ? participantObj.getString("tshirtsize") : null);
                hometown = ((participantObj.has("hometown") && !participantObj.isNull("hometown")) ? participantObj.getString("hometown") : null);
                teamId = ((participantObj.has("teamId") && !participantObj.isNull("teamId")) ? participantObj.getInt("teamId") : -1);
                eventId = ((participantObj.has("eventId") && !participantObj.isNull("eventId")) ? participantObj.getInt("eventId") : -1);


                // FIXME: Differentiate between participants with/without a team!
                _role = Role.PARTICIPANT;
            } else {
                _role = Role.USER;
            }

            if (!responseObj.isNull("profilePic")) {
                profileImage = BOMedia.sizedMediaFromJSON(c, responseObj.getJSONObject("profilePic"), BOMedia.SIZE.MEDIUM);
            }

            // Set user values
            _remoteId = (remoteId >= 0 ? remoteId : -1);
            _firstName = (firstName != null ? firstName : "");
            _lastName = (lastName != null ? lastName : "");
            _email = (email != null ? email : "");
            _gender = (gender != null ? gender : "");

            // Set participant values
            _emergencyNumber = (emergencyNumber != null ? emergencyNumber : "");
            _phoneNumber = (phoneNumber != null ? phoneNumber : "");
            _tShirtSize = (tShirtSize != null ? tShirtSize : "");
            _hometown = (hometown != null ? hometown : "");
            setTeamId(teamId);
            setEventId(eventId);
            _profileImage = profileImage;

            UserManager.getInstance(c).setCurrentUser(this);

            return true;
        } catch (IOException | JSONException e) {
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
    public boolean updateOnServerSync(Context c) {
        if (_role == Role.VISITOR) {
            Log.e(TAG, "Could not update user because it is a " + Role.VISITOR.toString());
            return false;
        }

        OkHttpClient client = new OkHttpClient();

        // Build URL
        HttpUrl updateUrl = HttpUrl.parse(URLUtils.getBaseUrl(c) + "/user/" + _remoteId + "/");

        // Build body
        JSONObject userJSON = toJSON();

        if (userJSON == null) {
            Log.e(TAG, "Could not create a valid JSON object for this user. The update has been cancelled.");
            return false;
        }

        RequestBody updateBody = RequestBody.create(JSON, userJSON.toString());

        Request updateRequest = new Request.Builder()
                .url(updateUrl)
                .put(updateBody)
                .addHeader("Authorization", "Bearer " + _accessToken)
                .build();

        try {
            Response updateResponse = client.newCall(updateRequest).execute();

            if (!updateResponse.isSuccessful()) {
                Log.e(TAG, updateResponse.body().string());
                Log.e(TAG, "The network call for updating the user was not successful (" + updateResponse.code() + ")");
                return false;
            }

            JSONObject responseJson = new JSONObject(updateResponse.body().string());
            if (responseJson.get("participant") != null && notNullOrEmpty(responseJson.get("participant").toString())) {
                // FIXME: Differentiate between participants with/without a team!
                _role = Role.PARTICIPANT;
            } else {
                _role = Role.USER;
            }

            Log.d(TAG, "User has been updated");

            return true;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * This method will return a JSON object representation of this user.
     *
     * @return JSON representation of this user or null if an error occurred
     */
    private JSONObject toJSON() {
        JSONObject userObj = new JSONObject();

        try {
            userObj.put("email", _email);
            userObj.put("firstname", notNullOrEmpty(_firstName) ? _firstName : "");
            userObj.put("lastname", notNullOrEmpty(_lastName) ? _lastName : "");
            userObj.put("gender", notNullOrEmpty(_gender) ? _gender : "");

            // Note: The password cannot be changed!

            if (notNullOrEmpty(_emergencyNumber) || notNullOrEmpty(_hometown) || notNullOrEmpty(_phoneNumber) || notNullOrEmpty(_tShirtSize)) {
                JSONObject participantObject = new JSONObject();

                participantObject.put("emergencynumber", notNullOrEmpty(_emergencyNumber) ? _emergencyNumber : "");
                participantObject.put("hometown", notNullOrEmpty(_hometown) ? _hometown : "");
                participantObject.put("phonenumber", notNullOrEmpty(_phoneNumber) ? _phoneNumber : "");
                participantObject.put("tshirtsize", notNullOrEmpty(_tShirtSize) ? _tShirtSize : "");

                // Add participant object to user object
                userObj.put("participant", participantObject);
            }

            return userObj;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private boolean notNullOrEmpty(String text) {
        return (text != null && !text.equals(NULL) && !text.equals(""));
    }

    @Override
    public String toString() {
        return "(" + _remoteId + ")" + " " + _email + " - " + _role + " [" + _accessToken + "]";
    }
}
