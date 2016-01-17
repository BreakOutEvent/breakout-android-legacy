package org.break_out.breakout.ui;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.break_out.breakout.ui.BOActivity;
import org.break_out.breakout.R;

public class LoginRegisterActivity extends BOActivity {
    private Context _context;
    private TextView _textView_about;
    private RelativeLayout _relativeLayout_container;
    private RelativeLayout _relativeLayout_hintWrapper;

    private Button _button_login;
    private Button _button_register;
    private EditText _editText_email;
    private EditText _editText_password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        _context = this;

        _textView_about = (TextView) findViewById(R.id.start_textView_about);
        _relativeLayout_container = (RelativeLayout) findViewById(R.id.start_relativeLayout_mainWrapper);
        _relativeLayout_hintWrapper = (RelativeLayout) findViewById(R.id.start_relativeLayout_popupWrapper);
        _button_login = (Button) findViewById(R.id.start_button_logIn);
        _button_register = (Button) findViewById(R.id.start_button_register);
        _editText_email = (EditText) findViewById(R.id.start_editText_email);
        _editText_password = (EditText) findViewById(R.id.start_editText_password);

        _editText_email.addTextChangedListener(new BOTextWatcher());
        _editText_password.addTextChangedListener(new BOTextWatcher());

        _button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logIn();
            }
        });

        _button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    /**
     * Method called to login
     */
    private void logIn() {
        //LogIn method
        //check if both fields are populated, show error otherwise
        String input_email = _editText_email.getText().toString();
        String input_password = _editText_password.getText().toString();
        if(input_email.isEmpty() || input_password.isEmpty()) {
            showHint();
        } else {
            //Start login
        }
    }

    /**
     * call code to register online
     */
    private void register() {
        String input_email = _editText_email.getText().toString();
        String input_password = _editText_password.getText().toString();

        if(input_email.isEmpty() || input_password.isEmpty()) {
            showHint();
        } else {
            //Start register
        }
    }

    private void checkIfEnoughInput() {
        String input_email = _editText_email.getText().toString();
        String input_password = _editText_password.getText().toString();
        if(!input_email.isEmpty()&&!input_password.isEmpty()) {
            hideHint();
        }
    }

    private void showHint() {
        _relativeLayout_hintWrapper.setVisibility(View.VISIBLE);

    }

    private void hideHint() {
        _relativeLayout_hintWrapper.setVisibility(View.INVISIBLE);
    }

    private class BOTextWatcher implements TextWatcher {

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
}
