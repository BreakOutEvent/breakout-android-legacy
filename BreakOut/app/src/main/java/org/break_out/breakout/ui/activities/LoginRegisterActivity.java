package org.break_out.breakout.ui.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.break_out.breakout.R;
import org.break_out.breakout.manager.UserManager;
import org.break_out.breakout.model.User;

import okhttp3.MediaType;

public class LoginRegisterActivity extends BOActivity {

    private static final String TAG = "LoginRegiserActivity";

    // TODO: Relocate to a network helper class (and to the app secrets)
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String BASE_URL = "http://breakout-development.herokuapp.com";

    /**
     * Global state of success regarding the login/registration
     * process for this Activity.
     */
    private boolean _loginRegisterSuccessful = false;

    private UserManager _userManager = null;

    private TextView _tvAbout;
    private RelativeLayout _rlHintWrapper;
    private EditText _etEmail;
    private EditText _etPassword;
    private Button _btLogin;
    private Button _btRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        _userManager = UserManager.getInstance(this);

        _tvAbout = (TextView) findViewById(R.id.start_textView_about);
        _rlHintWrapper = (RelativeLayout) findViewById(R.id.start_relativeLayout_popupWrapper);

        _etEmail = (EditText) findViewById(R.id.start_editText_email);
        _etPassword = (EditText) findViewById(R.id.start_editText_password);
        _btLogin = (Button) findViewById(R.id.start_button_logIn);
        _btRegister = (Button) findViewById(R.id.start_button_register);

        _etEmail.addTextChangedListener(new LoginRegisterTextWatcher());
        _etPassword.addTextChangedListener(new LoginRegisterTextWatcher());

        _btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        _btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    /**
     * Call code to register on the server.
     */
    private void register() {
        String email = _etEmail.getText().toString();
        String password = _etPassword.getText().toString();

        if(email.isEmpty() || password.isEmpty()) {
            showHint();
        } else {
            // Start registration
            new LoginRegisterTask(true).execute(email, password);
        }
    }

    /**
     * Method called to log in the current user on the server.
     */
    private void login() {
        // Check if both fields are populated, show error otherwise
        String email = _etEmail.getText().toString();
        String password = _etPassword.getText().toString();

        if(email.isEmpty() || password.isEmpty()) {
            showHint();
        } else {
            // Start login
            new LoginRegisterTask(false).execute(email, password);
        }
    }

    private void checkIfEnoughInput() {
        String email = _etEmail.getText().toString();
        String password = _etPassword.getText().toString();

        if(!email.isEmpty() && !password.isEmpty()) {
            hideHint();
        }
    }

    private void showHint() {
        _rlHintWrapper.setVisibility(View.VISIBLE);
    }

    private void hideHint() {
        _rlHintWrapper.setVisibility(View.INVISIBLE);
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

    /**
     * This AsyncTask will register a user on the server and set it
     * as the current user in the {@link UserManager}, if {@code registerBeforeLogin}
     * is set to {@code true}. Otherwise it will skip the registration and
     * directly log in the user.<br />
     * This task expects two Strings as parameters when executing:
     * the user's email and the desired password.
     */
    private class LoginRegisterTask extends AsyncTask<String, Void, Void> {

        /**
         * Set to true to register the user before logging in
         */
        boolean register = false;

        public LoginRegisterTask(boolean registerBeforeLogin) {
            register = registerBeforeLogin;
        }

        @Override
        protected Void doInBackground(String... params) {

            // Get email and password
            if(params.length != 2) {
                Log.e(TAG, "The LoginRegisterTask needs 2 arguments (email and password). Given: " + params.length + ".");
                return null;
            }

            String email = params[0];
            String password = params[1];

            if(email == null || password == null) {
                Log.e(TAG, "Email or password are null.");
                return null;
            }

            // Create user with email and password
            User user = new User(email, password);

            if(register) {
                // Register user to the server
                boolean registerSuccess = user.registerOnServerSync();

                if(!registerSuccess) {
                    Log.e(TAG, "Account could not be created on the server.");

                    // TODO: Handle registration error (and retry login?)

                    return null;
                }
            }

            // Log user in via OAuth
            boolean loginSuccess = user.loginOnServerSync();

            if(!loginSuccess) {
                Log.e(TAG, "Login via OAuth failed.");

                // TODO: Handle login error (and retry login?)

                return null;
            }

            // TODO: Set remote ID of the user before saving in the UserManager

            // Everything went well -> set user as current user in UserManager
            _userManager.setCurrentUser(user);

            _loginRegisterSuccessful = true;

            finish();

            return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        _userManager.loginRegisterDone(_loginRegisterSuccessful);
    }
}
