package org.break_out.breakout.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import org.break_out.breakout.R;

import java.util.ArrayList;

public class BecomeParticipantActivity extends BackgroundImageActivity {

    private static final String TAG = "BecomeParticipantActivity";

    private ScrollView _scrollView_credentials;

    private ImageView _ivChosenImage;
    private EditText _etFirstName;
    private EditText _etLastName;
    private EditText _etEmail;
    private EditText _etPassword;
    private EditText _etPasswordRepeat;
    private EditText _etTShirtSize;
    private EditText _etCity;
    private EditText _etPhoneNumber;
    private EditText _etEmergencyNumber;
    private Button _btRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_become_participant);

        initViews();
    }

    /**
     * instantiate views and set needed listener(s)
     */
    private void initViews() {
        /*
        _ivChosenImage = (ImageView) findViewById(R.id.register_imageView_addProfileImage);
        _scrollView_credentials = (ScrollView) findViewById(R.id.register_scrollView_credentials);
        _etFirstName = (EditText) findViewById(R.id.register_editText_firstname);
        _etLastName = (EditText) findViewById(R.id.register_editText_lastname);
        _etEmail = (EditText) findViewById(R.id.register_editText_email);
        _etPassword = (EditText) findViewById(R.id.register_editText_password);
        _etPasswordRepeat = (EditText) findViewById(R.id.register_editText_passwordRepeat);
        _etTShirtSize = (EditText) findViewById(R.id.register_editText_shirtSize);
        _etCity = (EditText) findViewById(R.id.register_editText_city);
        _etPhoneNumber = (EditText) findViewById(R.id.register_editText_phoneNumber);
        _etEmergencyNumber = (EditText) findViewById(R.id.register_editText_emergencyNumber);
        _btRegister = (Button) findViewById(R.id.register_button_register);

        _etEmergencyNumber.setOnEditorActionListener(new OnUserInputFinishedListener());
        _btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<InputError> inputErrors = checkInput();
                if(inputErrors.isEmpty()) {
                    //user input correct, handle
                } else {
                    //user input incorrect/missing something, handle
                    Toast.makeText(getApplicationContext(), "Errors!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        */
    }

    /**
     * scroll to the end of the scrollView to show register button
     */
    private void scrollToEnd() {
        _scrollView_credentials.fullScroll(ScrollView.FOCUS_DOWN);
    }

    /**
     * check if input is email
     * @return
     */
    private boolean isValidEmail(String mail) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches();
    }

    /**
     * check if input is valid number(use for phoneNumber and emergencyNumber)
     * @param number
     * @return
     */
    private boolean isValidPhoneNumber(String number) {
        return Patterns.PHONE.matcher("").matches();
    }

    /**
     * check if input of password and passwordrepeat are equal
     * @return
     */
    private boolean doPasswordsMatch(String p1, String p2) {
        return(p1.equals(p2));
    }

    /**
     * check the input for errors
     * @return ArrayList containing InputError objects that reference the errors
     */
    private ArrayList<InputError> checkInput() {
        String firstname = getInputText(_etFirstName);
        String lastname = getInputText(_etLastName);
        String email = getInputText(_etEmail);
        String password = getInputText(_etPassword);
        String passwordRepeat = getInputText(_etPasswordRepeat);
        String shirtSize = getInputText(_etTShirtSize);
        String city = getInputText(_etCity);
        String phoneNumber = getInputText(_etPhoneNumber);
        String emergencyNumber = getInputText(_etEmergencyNumber);
        ArrayList<InputError> result = new ArrayList<>();

        if(firstname.isEmpty()) {
            addError(result,InputError.FIRSTNAME);
        }
        if(lastname.isEmpty()) {
            addError(result,InputError.LASTNAME);
        }
        if(email.isEmpty() || !isValidEmail(email)) {
            addError(result,InputError.EMAIL);
        }
        if(password.isEmpty()){
            addError(result,InputError.PASSWORD);
        }
        if(passwordRepeat.isEmpty()||!doPasswordsMatch(password, passwordRepeat)) {
            addError(result,InputError.PASSWORDREPEAT);
        }
        if(shirtSize.isEmpty()) {
            addError(result,InputError.SHIRTSIZE);
        }
        if(city.isEmpty()) {
            addError(result,InputError.CITY);
        }
        if(phoneNumber.isEmpty()) {
            addError(result,InputError.PHONENUMBER);
        }
        if(emergencyNumber.isEmpty()) {
            addError(result,InputError.EMERGENCYNUMBER);
        }
        return result;
    }

    /**
     * add error to list
     * @param list
     * @param error
     */
    private void addError(ArrayList<InputError> list,InputError error) {
        if(!list.contains(error)) {
            list.add(error);
        }
    }

    /**
     * get input from EditText
     * @param editText
     * @return
     */
    private String getInputText(EditText editText) {
        return editText.getText().toString();
    }



    //Detect if the user has entered the credential and scroll down to the end of the list if yes
    private class OnUserInputFinishedListener implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

            hideKeyboard();
            scrollToEnd();
            return true;
        }

        /**
         * hide the soft input keyboard
         */
        private void hideKeyboard() {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(_etEmergencyNumber.getWindowToken(), 0);
        }
    }

    /**
     * enum to identify errors
     */
    private enum InputError {
        FIRSTNAME,LASTNAME,EMAIL,PASSWORD,PASSWORDREPEAT,SHIRTSIZE,CITY,PHONENUMBER,EMERGENCYNUMBER;
    }
}