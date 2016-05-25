package org.break_out.breakout.manager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;

import org.break_out.breakout.ui.activities.LoginRegisterActivity;
import org.break_out.breakout.model.User;
import org.break_out.breakout.util.BackgroundRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Tino on 16.01.2016.
 */
public class UserManager {

    private static final String TAG = "UserManager";

    private static final String PREF_KEY = "pref_key_user";

    private static final String KEY_REMOTE_ID = "key_id";
    private static final String KEY_TEAM_ID = "key_team_id";
    private static final String KEY_EVENT_ID = "key_event-id";
    private static final String KEY_ROLE = "key_role";
    private static final String KEY_EMAIL = "key_email";
    private static final String KEY_PASSWORD = "key_password";
    private static final String KEY_ACCESS_TOKEN = "key_access_token";

    private static final String KEY_FIRST_NAME = "key_first_name";
    private static final String KEY_LAST_NAME = "key_last_name";

    private static final String KEY_EMERGENCY_NUMBER = "key_emergency_number";
    private static final String KEY_HOMETOWN = "key_hometown";
    private static final String KEY_PHONE_NUMBER = "key_phone_number";
    private static final String KEY_T_SHIRT_SIZE = "key_t_shirt_size";
    private static final String KEY_GENDER = "key_gender";
    private static final String KEY_EVENT_CITY = "key_event_city";

    private static final String KEY_NEW_USER = "key_new_user";
    private static final String KEY_UPDATE_SUCCESS = "key_update_success";

    private static final String RUNNER_UPDATE_ON_SERVER = "runner_update_on_server";
    private static final String RUNNER_UPDATE_FROM_SERVER = "runner_update_from_server";

    private static UserManager _instance;

    private Context _context;

    private User _currUser = new User();

    private List<UserDataChangedListener> _dataChangedListeners = new ArrayList<UserDataChangedListener>();

    public interface UserDataChangedListener {
        public void onUserDataChanged();
    }

    public interface UserUpdateListener {
        public void userUpdated();
        public void updateFailed();
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

    public OkHttpClient getOAuthClient() {
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder().addHeader("Authorization", "Bearer " + _currUser.getAccessToken()).build();
                return chain.proceed(newRequest);
            }
        };

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.interceptors().add(interceptor);

        return builder.build();
    }

    /**
     * <p>
     * Register a listener to get notified whenever the user data
     * changes.
     * </p>
     *
     * <p>
     * It is a good practice to register such a listener in an activity's {@code onStart(...)}
     * method and unregister it in the {@code onPause(...)} method by calling
     * {@link #unregisterListener(UserDataChangedListener)}.
     * </p>
     *
     * @param listener The listener to be called
     */
    public void registerListener(UserDataChangedListener listener) {
        if(listener != null && !_dataChangedListeners.contains(listener)) {
            _dataChangedListeners.add(listener);
        }
    }

    public void unregisterListener(UserDataChangedListener listener) {
        if(listener != null && _dataChangedListeners.contains(listener)) {
            _dataChangedListeners.remove(listener);
        }
    }

    /**
     * Calls all user data changed listeners
     * <b>on the UI Thread</b>.
     */
    private void notifyDataChangedListeners() {
        Handler mainHandler = new Handler(_context.getMainLooper());

        for(final UserDataChangedListener l : _dataChangedListeners) {
            if(l != null) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        l.onUserDataChanged();
                    }
                });
            }
        }
    }

    /**
     * Set the currently logged in user.
     * Notice: You should never need to call this method! It will
     * only be called by the {@link LoginRegisterActivity}.
     *
     * @param user The currently logged in user
     */
    public void setCurrentUser(User user) {
        _currUser = user;
        Log.d(TAG,"curUser set with teamID: "+_currUser.getTeamId());
        saveCurrUserToPrefs();

        notifyDataChangedListeners();
    }

    public void updateFromServer(final Context c,@Nullable final UserUpdateListener listener) {
        // Set up runner
        BackgroundRunner runner = BackgroundRunner.getRunner(RUNNER_UPDATE_FROM_SERVER);

        runner.setRunnable(new BackgroundRunner.BackgroundRunnable() {
            @Nullable
            @Override
            public Bundle run(@Nullable Bundle params) {
                Bundle result = new Bundle();

                result.putBoolean(KEY_UPDATE_SUCCESS, _currUser.updateFromServerSync(c));

                return result;
            }
        });

        runner.setListener(new BackgroundRunner.BackgroundListener() {
            @Override
            public void onResult(@Nullable Bundle result) {
                if(result != null) {
                    boolean success = result.getBoolean(KEY_UPDATE_SUCCESS, false);

                    if(success) {
                        if(listener != null) {
                            listener.userUpdated();
                        }
                        setCurrentUser(_currUser);
                        return;
                    }
                }

                if(listener != null) {
                    listener.updateFailed();
                }
            }
        });

        runner.execute();
    }

    /**
     * <p>
     * Calling this method will result in updating the current user on the server.
     * If this could be done successfully, the current user in the UserManager will also
     * be updated. Set a listener in the parameters to get notified about the result of
     * the update process.
     * </p>
     * <p>
     * Note that you can <b>not</b> update a user's role using this method. If you want to do so,
     * you should e.g. consider calling {@link #loginOrRegisterUser()}.
     * </p>
     *
     * @param newUserData A user object filled with the new data (access token, role and remote id etc. will be set automatically)
     * @param listener The listener for the update process
     */
    public void updateUserOnServer(final User newUserData, final UserUpdateListener listener) {
        if(newUserData == null || listener == null) {
            Log.e(TAG, "Neither the new user data nor the listener shall be null. Stopped update process.");
            return;
        }

        // Setup new user
        User newUser = new User(newUserData);
        User currentUser = getCurrentUser();
        newUser.setAccessToken(currentUser.getAccessToken());
        newUser.setRole(currentUser.getRole());
        newUser.setRemoteId(currentUser.getRemoteId());

        // Set up runner
        BackgroundRunner runner = BackgroundRunner.getRunner(RUNNER_UPDATE_ON_SERVER);

        runner.setRunnable(new BackgroundRunner.BackgroundRunnable() {
            @Nullable
            @Override
            public Bundle run(@Nullable Bundle params) {
                Bundle result = new Bundle();

                if(params == null) {
                    result.putBoolean(KEY_UPDATE_SUCCESS, false);
                    return result;
                }

                User newUser = (User) params.getSerializable(KEY_NEW_USER);
                if(newUser == null) {
                    result.putBoolean(KEY_UPDATE_SUCCESS, false);
                    return result;
                }

                result.putSerializable(KEY_NEW_USER, newUser);
                result.putBoolean(KEY_UPDATE_SUCCESS, newUser.updateOnServerSync());

                return result;
            }
        });

        runner.setListener(new BackgroundRunner.BackgroundListener() {
            @Override
            public void onResult(@Nullable Bundle result) {
                if(result != null) {
                    boolean success = result.getBoolean(KEY_UPDATE_SUCCESS, false);
                    User newUser = (User) result.getSerializable(KEY_NEW_USER);

                    if(success && newUser != null) {
                        listener.userUpdated();
                        setCurrentUser(newUser);

                        return;
                    }
                }

                listener.updateFailed();
            }
        });

        Bundle params = new Bundle();
        params.putSerializable(KEY_NEW_USER, newUser);
        runner.execute(params);
    }

    /**
     * Opens the {@link LoginRegisterActivity} and lets the user either
     * login or create an account. To stay updated about the status of
     * the user, register a listener by calling {@link #registerListener(UserDataChangedListener)}.
     */
    public void loginOrRegisterUser() {
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
        setCurrentUser(new User());
    }

    /**
     * Returns a copy of the currently logged in user.
     * If there is no user logged in, this method will return
     * an empty instance of {@link User} with the role {@code VISITOR}.
     *
     * @return The current user
     */
    public User getCurrentUser() {
        return new User(_currUser);
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
     * Stores the current user to the SharedPreferences.
     * Note that there can always be only one user stored in the
     * preferences.
     */
    private void saveCurrUserToPrefs() {
        SharedPreferences sharedPref = _context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putLong(KEY_REMOTE_ID, _currUser.getRemoteId());
        editor.putInt(KEY_TEAM_ID,_currUser.getTeamId());
        editor.putInt(KEY_EVENT_ID,_currUser.getEventId());
        editor.putString(KEY_ROLE, _currUser.getRole().toString());
        editor.putString(KEY_EMAIL, _currUser.getEmail());
        editor.putString(KEY_PASSWORD, _currUser.getPassword());
        editor.putString(KEY_ACCESS_TOKEN, _currUser.getAccessToken());

        editor.putString(KEY_FIRST_NAME, _currUser.getFirstName());
        editor.putString(KEY_LAST_NAME, _currUser.getLastName());

        editor.putString(KEY_EMERGENCY_NUMBER, _currUser.getEmergencyNumber());
        editor.putString(KEY_HOMETOWN, _currUser.getHometown());
        editor.putString(KEY_PHONE_NUMBER, _currUser.getPhoneNumber());
        editor.putString(KEY_T_SHIRT_SIZE, _currUser.getTShirtSize());
        editor.putString(KEY_GENDER, _currUser.getGender());
        editor.putString(KEY_EVENT_CITY, _currUser.getEventCity());

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

        // Get values from prefs
        long remoteId = sharedPref.getLong(KEY_REMOTE_ID, -1);
        int teamId = sharedPref.getInt(KEY_TEAM_ID,-1);
        int eventId = sharedPref.getInt(KEY_EVENT_ID,-1);
        User.Role role = User.Role.fromString(sharedPref.getString(KEY_ROLE, ""));
        String email = sharedPref.getString(KEY_EMAIL, "");
        String password = sharedPref.getString(KEY_PASSWORD, "");
        String accessToken = sharedPref.getString(KEY_ACCESS_TOKEN, "");

        String firstName = sharedPref.getString(KEY_FIRST_NAME, "");
        String lastName = sharedPref.getString(KEY_LAST_NAME, "");

        String emergencyNumber = sharedPref.getString(KEY_EMERGENCY_NUMBER, "");
        String hometown = sharedPref.getString(KEY_HOMETOWN, "");
        String phoneNumber = sharedPref.getString(KEY_PHONE_NUMBER, "");
        String tShirtSize = sharedPref.getString(KEY_T_SHIRT_SIZE, "");
        String gender = sharedPref.getString(KEY_GENDER, "");
        String eventCity = sharedPref.getString(KEY_EVENT_CITY, "");

        // Create user
        User user = new User();
        user.setRole(role);

        if(role == User.Role.VISITOR) {
            _currUser = user;
            return;
        }

        // User specific information
        user.setRemoteId(remoteId);
        user.setEmail(email);
        user.setPassword(password);
        user.setAccessToken(accessToken);

        user.setFirstName(firstName);
        user.setLastName(lastName);

        if(role == User.Role.USER) {
            _currUser = user;
            return;
        }

        // Participant specific information
        user.setEmergencyNumber(emergencyNumber);
        user.setHometown(hometown);
        user.setPhoneNumber(phoneNumber);
        user.setTShirtSize(tShirtSize);
        user.setGender(gender);
        user.setEventCity(eventCity);
        user.setEventId(eventId);
        user.setTeamId(teamId);

        _currUser = user;
    }


}
