package org.break_out.breakout.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.break_out.breakout.R;

import java.util.ArrayList;

public class BecomeTeamMemberActivity extends AppCompatActivity {
    private static final String TAG = "BecomeTeamMemberActivity";
    private ScrollView _scrollView_credentials;

    private ImageView _imageView_chosenImage;
    private EditText _editText_firstname;
    private EditText _editText_lastname;
    private EditText _editText_email;
    private EditText _editText_password;
    private EditText _editText_passwordRepeat;
    private EditText _editText_shirtSize;
    private EditText _editText_city;
    private EditText _editText_phoneNumber;
    private EditText _editText_emergencyNumber;
    private Button _button_register;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initViews();
    }

    /**
     * instantiate views and set neededl listener(s)
     */
    private void initViews() {
        _imageView_chosenImage = (ImageView) findViewById(R.id.register_imageView_addProfileImage);
        _scrollView_credentials = (ScrollView) findViewById(R.id.register_scrollView_credentials);
        _editText_firstname = (EditText) findViewById(R.id.register_editText_firstname);
        _editText_lastname = (EditText) findViewById(R.id.register_editText_lastname);
        _editText_email = (EditText) findViewById(R.id.register_editText_email);
        _editText_password = (EditText) findViewById(R.id.register_editText_password);
        _editText_passwordRepeat = (EditText) findViewById(R.id.register_editText_passwordRepeat);
        _editText_shirtSize = (EditText) findViewById(R.id.register_editText_shirtSize);
        _editText_city = (EditText) findViewById(R.id.register_editText_city);
        _editText_phoneNumber = (EditText) findViewById(R.id.register_editText_phoneNumber);
        _editText_emergencyNumber = (EditText) findViewById(R.id.register_editText_emergencyNumber);
        _button_register = (Button) findViewById(R.id.register_button_register);

        _editText_emergencyNumber.setOnEditorActionListener(new OnUserInputFinishedListener());
        _button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<InputError> inputErrors = checkInput();
                if(inputErrors.isEmpty()) {
                    //user input correct, handle
                } else {
                    //user input incorrect/missing something, handle
                    Toast.makeText(getApplicationContext(),"Errors!",Toast.LENGTH_SHORT).show();
                }
            }
        });
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
        String firstname = getInputText(_editText_firstname);
        String lastname = getInputText(_editText_lastname);
        String email = getInputText(_editText_email);
        String password = getInputText(_editText_password);
        String passwordRepeat = getInputText(_editText_passwordRepeat);
        String shirtSize = getInputText(_editText_shirtSize);
        String city = getInputText(_editText_city);
        String phoneNumber = getInputText(_editText_phoneNumber);
        String emergencyNumber = getInputText(_editText_emergencyNumber);
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
            imm.hideSoftInputFromWindow(_editText_emergencyNumber.getWindowToken(), 0);
        }
    }

    /**
     * enum to identify errors
     */
    private enum InputError {
        FIRSTNAME,LASTNAME,EMAIL,PASSWORD,PASSWORDREPEAT,SHIRTSIZE,CITY,PHONENUMBER,EMERGENCYNUMBER;
    }
}