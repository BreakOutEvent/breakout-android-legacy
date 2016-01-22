package org.break_out.breakout.ui.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.break_out.breakout.R;
import org.break_out.breakout.manager.UserManager;
import org.break_out.breakout.model.User;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginRegisterActivity extends BOActivity {

    private static final String TAG = "LoginRegiserActivity";

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String BASE_URL = "http://breakout-development.herokuapp.com";

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
     * Method called to login.
     */
    private void login() {
        // check if both fields are populated, show error otherwise
        String input_email = _etEmail.getText().toString();
        String input_password = _etPassword.getText().toString();
        if(input_email.isEmpty() || input_password.isEmpty()) {
            showHint();
        } else {
            // TODO: Start login
        }
    }

    /**
     * Call code to register online.
     */
    private void register() {
        String email = _etEmail.getText().toString();
        String password = _etPassword.getText().toString();

        if(email.isEmpty() || password.isEmpty()) {
            showHint();
        } else {
            // Start registration
            new RegisterTask().execute(email, password);
        }
    }

    private void checkIfEnoughInput() {
        String input_email = _etEmail.getText().toString();
        String input_password = _etPassword.getText().toString();
        if(!input_email.isEmpty()&&!input_password.isEmpty()) {
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
     * as the current user in the {@link UserManager}.<br />
     * This task expects two Strings as parameters when executing:
     * the user's email and the desired password.
     */
    private class RegisterTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {

            // Get email and password
            if(params.length != 2) {
                Log.e(TAG, "The RegisterTask needs 2 arguments (email and password). Given: " + params.length +  ".");
                return false;
            }

            String email = params[0];
            String password = params[1];

            if(email == null || password == null) {
                Log.e(TAG, "Email or password are null.");
                return false;
            }

            // Create user with email and password
            User user = new User(email, password);

            // Register user to the server
            boolean registerSuccess = user.registerOnServerSynchronously();

            if(!registerSuccess) {
                Log.e(TAG, "Account could not be created on the server.");

                // TODO: Handle registration error (and retry login?)

                return false;
            }

            // Log user in via OAuth
            boolean loginSuccess = user.loginOnServerSynchronously();

            if(!loginSuccess) {
                Log.e(TAG, "Account has been created but login via OAuth failed.");

                // TODO: Handle login error (and retry login?)

                return false;
            }

            // Everything went well -> set user as current user in UserManager
            _userManager.setCurrentUser(user);

            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);

            _loginRegisterSuccessful = success;

            if(success) {
                finish();
            } else {
                // TODO: Handle error (retry registration or finish Activity with an error)
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        _userManager.loginRegisterDone(_loginRegisterSuccessful);
    }
}
