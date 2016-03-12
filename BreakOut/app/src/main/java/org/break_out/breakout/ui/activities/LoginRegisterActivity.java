package org.break_out.breakout.ui.activities;

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

public class LoginRegisterActivity extends BackgroundImageActivity {

    private static final String TAG = "LoginRegisterActivity";

    private static final String RUNNER_LOGIN = "runner_login";
    private static final String RUNNER_REGISTER = "runner_registration";

    private static final String KEY_EMAIL = "key_email";
    private static final String KEY_PASSWORD = "key_password";
    private static final String KEY_LOGIN = "key_login";
    private static final String KEY_REGISTER = "key_register";

    private UserManager _userManager = null;

    private View _rlHintWrapper = null;
    private BOEditText _etEmail = null;
    private BOEditText _etPassword = null;
    private BOFlatButton _btLogin = null;
    private BOFlatButton _btRegister = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        _userManager = UserManager.getInstance(this);

        _rlHintWrapper = findViewById(R.id.popup_wrapper);

        _etEmail = (BOEditText) findViewById(R.id.start_editText_email);
        _etEmail.addTextChangedListener(new LoginRegisterTextWatcher());

        _etPassword = (BOEditText) findViewById(R.id.start_editText_password);
        _etPassword.addTextChangedListener(new LoginRegisterTextWatcher());

        _btLogin = (BOFlatButton) findViewById(R.id.start_button_logIn);
        _btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        _btRegister = (BOFlatButton) findViewById(R.id.start_button_register);
        _btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        // Restore instance state
        if(savedInstanceState != null) {
            _etEmail.setState(savedInstanceState.getSerializable(KEY_EMAIL));
            _etPassword.setState(savedInstanceState.getSerializable(KEY_PASSWORD));
            _btLogin.setState(savedInstanceState.getSerializable(KEY_LOGIN));
            _btRegister.setState(savedInstanceState.getSerializable(KEY_REGISTER));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(KEY_EMAIL, _etEmail.getState());
        outState.putSerializable(KEY_PASSWORD, _etPassword.getState());
        outState.putSerializable(KEY_LOGIN, _btLogin.getState());
        outState.putSerializable(KEY_REGISTER, _btRegister.getState());
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
            _btRegister.setShowLoadingIndicator(true);
            _btLogin.setEnabled(false);

            BackgroundRunner runner = BackgroundRunner.getRunner(RUNNER_REGISTER);
            runner.setRunnable(new RegisterRunnable());
            runner.execute(email, password);
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
            _btRegister.setEnabled(false);

            BackgroundRunner runner = BackgroundRunner.getRunner(RUNNER_LOGIN);
            runner.setRunnable(new LoginRunnable());
            runner.execute(email, password);
        }
    }

    private void checkIfEnoughInput() {
        String email = _etEmail.getText();
        String password = _etPassword.getText();

        if(!email.isEmpty() && !password.isEmpty()) {
            hideHint();
        }
    }

    private void showHint() {
        _rlHintWrapper.setVisibility(View.VISIBLE);
    }

    private void hideHint() {
        _rlHintWrapper.setVisibility(View.GONE);
    }

    private class LoginRegisterTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            checkIfEnoughInput();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    private class LoginRunnable implements BackgroundRunner.BackgroundRunnable {

        @Nullable
        @Override
        public Object run(@Nullable Object... params) {
            if(params == null || params.length != 2) {
                Log.e(TAG, "Could not get the correct params");
                return false;
            }

            String email = (String) params[0];
            String password = (String) params[1];

            if(email == null || password == null) {
                Log.e(TAG, "Email or password are null.");
                return false;
            }

            // Create user with email and password
            User user = new User(email, password);

            // Log user in via OAuth
            boolean loginSuccessful = user.loginOnServerSync();

            if(!loginSuccessful) {
                return false;
            }

            Log.d(TAG, "User remote ID: " + user.getRemoteId());

            // Everything went well -> set user as current user in UserManager
            _userManager.setCurrentUser(user);

            return true;
        }
    }

    private class LoginListener implements BackgroundRunner.BackgroundListener {

        @Override
        public void onResult(@Nullable Object result) {
            try {
                Boolean success = (Boolean) result;

                if(success != null && success) {
                    finish();
                    return;
                } else {
                    Log.e(TAG, "Login via OAuth failed.");
                    NotificationUtils.showInfoDialog(LoginRegisterActivity.this, getString(R.string.error), getString(R.string.login_failed));
                }
            } catch(ClassCastException e) {
                e.printStackTrace();
            }

            _btLogin.setEnabled(true);
            _btRegister.setEnabled(true);
            _btLogin.setShowLoadingIndicator(false);
        }
    }

    private class RegisterRunnable implements BackgroundRunner.BackgroundRunnable {

        @Nullable
        @Override
        public Object run(@Nullable Object... params) {
            // Get email and password
            if(params == null || params.length != 2) {
                Log.e(TAG, "Could not get the correct params");
                return false;
            }

            String email = (String) params[0];
            String password = (String) params[1];

            if(email == null || password == null) {
                Log.e(TAG, "Email or password are null.");
                return null;
            }

            // Create user with email and password
            User user = new User(email, password);

            // Register user to the server and return result
            return user.registerOnServerSync();
        }
    }

    private class RegisterListener implements BackgroundRunner.BackgroundListener {

        @Override
        public void onResult(@Nullable Object result) {
            try {
                Boolean success = (Boolean) result;

                if(success != null && success) {
                    NotificationUtils.showInfoDialog(LoginRegisterActivity.this, getString(R.string.registration_successful_title), getString(R.string.registration_successful_text));
                } else {
                    Log.e(TAG, "Account could not be created on the server.");
                    NotificationUtils.showInfoDialog(LoginRegisterActivity.this, getString(R.string.error), getString(R.string.account_not_created));
                }
            } catch(ClassCastException e) {
                e.printStackTrace();
            }

            _btLogin.setEnabled(true);
            _btRegister.setEnabled(true);
            _btRegister.setShowLoadingIndicator(false);
        }
    }
}
