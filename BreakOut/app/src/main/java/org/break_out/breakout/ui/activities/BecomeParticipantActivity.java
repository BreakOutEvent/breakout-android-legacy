package org.break_out.breakout.ui.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;

import org.break_out.breakout.R;
import org.break_out.breakout.ui.views.BODatePicker;
import org.break_out.breakout.ui.views.BOEditText;
import org.break_out.breakout.ui.views.BOFlatButton;
import org.break_out.breakout.ui.views.BOSpinner;

public class BecomeParticipantActivity extends BackgroundImageActivity {

    private static final String TAG = "BecomeParticipantActivity";

    private static final String KEY_LAST_NAME = "last_name";
    private static final String KEY_FIRST_NAME = "first_name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_T_SHIRT_SIZE = "t_shirt_size";
    private static final String KEY_PHONE_NUMBER = "phone_number";
    private static final String KEY_BIRTHDAY = "birthday";
    private static final String KEY_EMERGENCY_NUMBER = "emergency_number";

    private BOEditText _etFirstName = null;
    private BOEditText _etLastName = null;
    private BOEditText _etEmail = null;
    private BOSpinner _spGender = null;
    private BOSpinner _spTShirtSize = null;
    private BOEditText _etPhoneNumber = null;
    private BOEditText _etEmergencyNumber = null;
    private BODatePicker _dpBirthday = null;

    private BOFlatButton _btParticipate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_become_participant);
        setHeaderImage(R.drawable.btn_camera_round);

        initViews();
    }

    private void initViews() {
        _etLastName = (BOEditText) findViewById(R.id.et_last_name);
        _etFirstName = (BOEditText) findViewById(R.id.et_first_name);
        _etEmail = (BOEditText) findViewById(R.id.et_email);
        _spGender = (BOSpinner) findViewById(R.id.sp_gender);
        _spTShirtSize = (BOSpinner) findViewById(R.id.sp_t_shirt_size);
        _etPhoneNumber = (BOEditText) findViewById(R.id.et_phone_number);
        _etEmergencyNumber = (BOEditText) findViewById(R.id.et_emergency_number);
        _dpBirthday = (BODatePicker) findViewById(R.id.dp_birthday);

        _btParticipate = (BOFlatButton) findViewById(R.id.bt_participate);
        _btParticipate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                participate();
            }
        });
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if(savedInstanceState == null) {
            return;
        }

        _etLastName.setState(savedInstanceState.getSerializable(KEY_LAST_NAME));
        _etFirstName.setState(savedInstanceState.getSerializable(KEY_FIRST_NAME));
        _etEmail.setState(savedInstanceState.getSerializable(KEY_EMAIL));
        _spGender.setState(savedInstanceState.getSerializable(KEY_GENDER));
        _spTShirtSize.setState(savedInstanceState.getSerializable(KEY_T_SHIRT_SIZE));
        _etPhoneNumber.setState(savedInstanceState.getSerializable(KEY_PHONE_NUMBER));
        _etEmergencyNumber.setState(savedInstanceState.getSerializable(KEY_EMERGENCY_NUMBER));
        _dpBirthday.setState(savedInstanceState.getSerializable(KEY_BIRTHDAY));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(KEY_LAST_NAME, _etLastName.getState());
        outState.putSerializable(KEY_FIRST_NAME, _etFirstName.getState());
        outState.putSerializable(KEY_EMAIL, _etEmail.getState());
        outState.putSerializable(KEY_GENDER, _spGender.getState());
        outState.putSerializable(KEY_T_SHIRT_SIZE, _spTShirtSize.getState());
        outState.putSerializable(KEY_PHONE_NUMBER, _etPhoneNumber.getState());
        outState.putSerializable(KEY_EMERGENCY_NUMBER, _etEmergencyNumber.getState());
        outState.putSerializable(KEY_BIRTHDAY, _dpBirthday.getState());
    }

    private void participate() {
        new ParticipateTask().execute();
        if(!isValidEmail(_etEmail.getText())) {
            // TODO: Handle error
        } else if(!isValidPhoneNumber(_etPhoneNumber.getText())) {
            // TODO: Handle error
        } else if(!isValidPhoneNumber(_etEmergencyNumber.getText())) {
            // TODO: Handle error
        } else {
            new ParticipateTask().execute();
        }
    }

    /**
     * Check if input is email
     * @return
     */
    private boolean isValidEmail(String mail) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches();
    }

    /**
     * Check if input is valid number(use for phoneNumber and emergencyNumber)
     * @param number
     * @return
     */
    private boolean isValidPhoneNumber(String number) {
        return Patterns.PHONE.matcher("").matches();
    }

    private class ParticipateTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            _btParticipate.setShowLoadingIndicator(true);
        }

        @Override
        protected Void doInBackground(String... params) {
            // TODO

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            _btParticipate.setShowLoadingIndicator(false);
        }
    }
}