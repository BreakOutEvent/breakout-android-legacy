package org.break_out.breakout.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import org.break_out.breakout.R;
import org.break_out.breakout.manager.UserManager;
import org.break_out.breakout.model.User;
import org.break_out.breakout.ui.views.BOEditText;
import org.break_out.breakout.ui.views.BOFlatButton;
import org.break_out.breakout.util.BackgroundRunner;
import org.break_out.breakout.util.NotificationUtils;

import it.sephiroth.android.library.tooltip.Tooltip;

public class LoginRegisterActivity extends BackgroundImageActivity {

    private static final String TAG = "LoginRegisterActivity";

    private static final String RUNNER_LOGIN = "runner_login";
    private static final String RUNNER_REGISTER = "runner_registration";

    private static final String KEY_EMAIL = "key_email";
    private static final String KEY_PASSWORD = "key_password";
    private static final String KEY_LOGIN = "key_login";
    private static final String KEY_REGISTER = "key_register";
    private static final String KEY_SUCCESS = "key_success";

    private UserManager _userManager = null;

    private BOEditText _etEmail = null;
    private BOEditText _etPassword = null;
    private BOFlatButton _btLogin = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        _userManager = UserManager.getInstance(this);

        _etEmail = (BOEditText) findViewById(R.id.start_editText_email);
        _etPassword = (BOEditText) findViewById(R.id.start_editText_password);

        _btLogin = (BOFlatButton) findViewById(R.id.start_button_logIn);
        _btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });


        View vWhatIsBreakout = findViewById(R.id.tv_what_is_breakout);
        vWhatIsBreakout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginRegisterActivity.this, WebViewActivity.class);
                i.putExtra(WebViewActivity.KEY_URL, "http://www.break-out.org/worum-gehts/");
                i.putExtra(WebViewActivity.KEY_TITLE, getString(R.string.title_what_is_breakout));
                startActivity(i);
            }
        });

        // Restore instance state
        if(savedInstanceState != null) {
            _etEmail.setState(savedInstanceState.getSerializable(KEY_EMAIL));
            _etPassword.setState(savedInstanceState.getSerializable(KEY_PASSWORD));
            _btLogin.setState(savedInstanceState.getSerializable(KEY_LOGIN));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(KEY_EMAIL, _etEmail.getState());
        outState.putSerializable(KEY_PASSWORD, _etPassword.getState());
        outState.putSerializable(KEY_LOGIN, _btLogin.getState());
    }

    @Override
    protected void onPause() {
        super.onPause();
        BackgroundRunner.getRunner(RUNNER_LOGIN).removeListener();
        BackgroundRunner.getRunner(RUNNER_REGISTER).removeListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BackgroundRunner.getRunner(RUNNER_LOGIN).setListener(new LoginListener());
        BackgroundRunner.getRunner(RUNNER_REGISTER).setListener(new RegisterListener());
    }

    /**
     * Call code to register on the server.
     */
    private void register() {
        String email = _etEmail.getText();
        String password = _etPassword.getText();

        if(email.isEmpty() || password.isEmpty()) {
            showHint();
        } else {
            // Start registration
            _btLogin.setEnabled(false);

            BackgroundRunner runner = BackgroundRunner.getRunner(RUNNER_REGISTER);
            runner.setRunnable(new RegisterRunnable());

            Bundle params = new Bundle();
            params.putString(KEY_EMAIL, email);
            params.putString(KEY_PASSWORD, password);

            runner.execute(params);
        }
    }

    /**
     * Method called to log in the current user on the server.
     */
    private void login() {
        // Check if both fields are populated, show error otherwise
        String email = _etEmail.getText();
        String password = _etPassword.getText();

        if(email.isEmpty() || password.isEmpty()) {
            showHint();
        } else {
            // Start login
            _btLogin.setShowLoadingIndicator(true);

            BackgroundRunner runner = BackgroundRunner.getRunner(RUNNER_LOGIN);
            runner.setRunnable(new LoginRunnable());

            Bundle params = new Bundle();
            params.putString(KEY_EMAIL, email);
            params.putString(KEY_PASSWORD, password);

            runner.execute(params);
        }
    }

    private void showHint() {
        NotificationUtils.showTooltip(this, _etEmail, R.string.tooltip_username_password);
    }

    private class LoginRunnable implements BackgroundRunner.BackgroundRunnable {

        @Nullable
        @Override
        public Bundle run(@Nullable Bundle params) {
            Bundle result = new Bundle();

            if(params == null) {
                Log.e(TAG, "Could not get params in LoginRunnable");
                result.putBoolean(KEY_SUCCESS, false);
                return result;
            }

            // Get email and password
            String email = params.getString(KEY_EMAIL);
            String password = params.getString(KEY_PASSWORD);

            if(email == null || password == null) {
                Log.e(TAG, "Email or password are null");
                result.putBoolean(KEY_SUCCESS, false);
                return result;
            }

            // Create user with email and password
            User user = new User(email, password);

            // Log user in via OAuth
            boolean loginSuccessful = user.loginOnServerSync(getApplicationContext());

            if(!loginSuccessful) {
                result.putBoolean(KEY_SUCCESS, false);
                return result;
            }
            // Everything went well -> set user as current user in UserManager
            _userManager.setCurrentUser(user);

            result.putBoolean(KEY_SUCCESS, true);
            return result;
        }
    }

    private class LoginListener implements BackgroundRunner.BackgroundListener {

        @Override
        public void onResult(@Nullable Bundle result) {
            Boolean success = false;

            if(result != null) {
                success = result.getBoolean(KEY_SUCCESS, false);
            }

            if(success) {
                finish();
                return;
            } else {
                Log.e(TAG, "Login via OAuth failed");
                NotificationUtils.showInfoDialog(LoginRegisterActivity.this, getString(R.string.error), getString(R.string.login_failed));
            }

            _btLogin.setEnabled(true);
            _btLogin.setShowLoadingIndicator(false);
        }
    }

    private class RegisterRunnable implements BackgroundRunner.BackgroundRunnable {

        @Nullable
        @Override
        public Bundle run(@Nullable Bundle params) {
            Bundle result = new Bundle();

            if(params == null) {
                Log.e(TAG, "Could not get params in RegisterRunnable");
                result.putBoolean(KEY_SUCCESS, false);
                return result;
            }

            // Get email and password
            String email = params.getString(KEY_EMAIL);
            String password = params.getString(KEY_PASSWORD);

            if(email == null || password == null) {
                Log.e(TAG, "Email or password are null");
                result.putBoolean(KEY_SUCCESS, false);
                return result;
            }

            // Create user with email and password
            User user = new User(email, password);

            // Register user to the server and return result
            result.putBoolean(KEY_SUCCESS, user.registerOnServerSync(getApplication().getApplicationContext()));
            return result;
        }
    }

    private class RegisterListener implements BackgroundRunner.BackgroundListener {

        @Override
        public void onResult(@Nullable Bundle result) {
            Boolean success = false;

            if(result != null) {
                success = result.getBoolean(KEY_SUCCESS, false);
            }

            if(success) {
                NotificationUtils.showInfoDialog(LoginRegisterActivity.this, getString(R.string.registration_successful_title), getString(R.string.registration_successful_text));
            } else {
                Log.e(TAG, "Account could not be created on the server");
                NotificationUtils.showInfoDialog(LoginRegisterActivity.this, getString(R.string.error), getString(R.string.account_not_created));
            }

            _btLogin.setEnabled(true);
        }
    }
}
