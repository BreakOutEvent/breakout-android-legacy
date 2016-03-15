package org.break_out.breakout.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Patterns;
import android.view.View;

import org.break_out.breakout.R;
import org.break_out.breakout.manager.UserManager;
import org.break_out.breakout.model.User;
import org.break_out.breakout.ui.views.BODatePicker;
import org.break_out.breakout.ui.views.BOEditText;
import org.break_out.breakout.ui.views.BOFlatButton;
import org.break_out.breakout.ui.views.BOSpinner;
import org.break_out.breakout.util.BackgroundRunner;
import org.break_out.breakout.util.NotificationUtils;

import java.util.Calendar;

public class BecomeParticipantActivity extends BackgroundImageActivity {

    private static final String TAG = "BecomeParticipantAct";

    private static final String RUNNER_PARTICIPATE = "runner_participate";

    private static final String KEY_LAST_NAME = "key_last_name";
    private static final String KEY_FIRST_NAME = "key_first_name";
    private static final String KEY_GENDER = "key_gender";
    private static final String KEY_T_SHIRT_SIZE = "key_t_shirt_size";
    private static final String KEY_PHONE_NUMBER = "key_phone_number";
    private static final String KEY_BIRTHDAY = "key_birthday";
    private static final String KEY_EMERGENCY_NUMBER = "key_emergency_number";
    private static final String KEY_PARTICIPATE = "key_participate";
    private static final String KEY_SUCCESS = "key_success";

    private BOEditText _etFirstName = null;
    private BOEditText _etLastName = null;
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

    @Override
    protected void onResume() {
        super.onResume();
        BackgroundRunner.getRunner(RUNNER_PARTICIPATE).setListener(new ParticipateListener());
    }

    @Override
    protected void onPause() {
        super.onPause();
        BackgroundRunner.getRunner(RUNNER_PARTICIPATE).removeListener();
    }

    private void initViews() {
        _etLastName = (BOEditText) findViewById(R.id.et_last_name);
        _etFirstName = (BOEditText) findViewById(R.id.et_first_name);
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
        _spGender.setState(savedInstanceState.getSerializable(KEY_GENDER));
        _spTShirtSize.setState(savedInstanceState.getSerializable(KEY_T_SHIRT_SIZE));
        _etPhoneNumber.setState(savedInstanceState.getSerializable(KEY_PHONE_NUMBER));
        _etEmergencyNumber.setState(savedInstanceState.getSerializable(KEY_EMERGENCY_NUMBER));
        _dpBirthday.setState(savedInstanceState.getSerializable(KEY_BIRTHDAY));
        _btParticipate.setState(savedInstanceState.getSerializable(KEY_PARTICIPATE));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(KEY_LAST_NAME, _etLastName.getState());
        outState.putSerializable(KEY_FIRST_NAME, _etFirstName.getState());
        outState.putSerializable(KEY_GENDER, _spGender.getState());
        outState.putSerializable(KEY_T_SHIRT_SIZE, _spTShirtSize.getState());
        outState.putSerializable(KEY_PHONE_NUMBER, _etPhoneNumber.getState());
        outState.putSerializable(KEY_EMERGENCY_NUMBER, _etEmergencyNumber.getState());
        outState.putSerializable(KEY_BIRTHDAY, _dpBirthday.getState());
        outState.putSerializable(KEY_PARTICIPATE, _btParticipate.getState());
    }

    private void participate() {
        String firstName = _etFirstName.getText();
        String lastName = _etLastName.getText();
        String gender = _spGender.getSelectedValue();
        String tShirtSize = _spTShirtSize.getSelectedValue();
        String phoneNumber = _etPhoneNumber.getText();
        String emergencyNumber = _etEmergencyNumber.getText();

        if(!isValidPhoneNumber(phoneNumber)) {
            // TODO: Handle error
        } else if(!isValidPhoneNumber(emergencyNumber)) {
            // TODO: Handle error
        } else {
            _btParticipate.setShowLoadingIndicator(true);

            BackgroundRunner runner = BackgroundRunner.getRunner(RUNNER_PARTICIPATE);
            runner.setRunnable(new ParticipateRunnable());

            Bundle params = new Bundle();
            params.putString(KEY_FIRST_NAME, firstName);
            params.putString(KEY_LAST_NAME, lastName);
            params.putString(KEY_GENDER, gender);
            params.putString(KEY_T_SHIRT_SIZE, tShirtSize);
            params.putString(KEY_PHONE_NUMBER, phoneNumber);
            params.putString(KEY_EMERGENCY_NUMBER, emergencyNumber);

            runner.execute(params);
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
        return Patterns.PHONE.matcher(number).matches();
    }

    private class ParticipateRunnable implements BackgroundRunner.BackgroundRunnable {

        @Nullable
        @Override
        public Bundle run(@Nullable Bundle params) {
            UserManager userManager = UserManager.getInstance(BecomeParticipantActivity.this);
            Bundle result = new Bundle();

            if(params == null) {
                Log.e(TAG, "Could not get params in ParticipateRunnable (bundle was null)");
                result.putBoolean(KEY_SUCCESS, false);
                return result;
            }

            User currUserCopy = userManager.getCurrentUser();

            currUserCopy.setFirstName(params.getString(KEY_FIRST_NAME));
            currUserCopy.setLastName(params.getString(KEY_LAST_NAME));
            currUserCopy.setTShirtSize(params.getString(KEY_T_SHIRT_SIZE));
            currUserCopy.setGender(params.getString(KEY_GENDER));
            currUserCopy.setPhoneNumber(params.getString(KEY_PHONE_NUMBER));
            currUserCopy.setEmergencyNumber(params.getString(KEY_EMERGENCY_NUMBER));
            currUserCopy.setBirthday((Calendar) params.getSerializable(KEY_BIRTHDAY));

            // Run update on server
            boolean success = currUserCopy.updateOnServerSync();

            if(!success) {
                result.putBoolean(KEY_SUCCESS, false);
                return result;
            }

            Log.d(TAG, "User is now a participant");

            // Everything went well -> set user as current user in UserManager
            userManager.setCurrentUser(currUserCopy);

            result.putBoolean(KEY_SUCCESS, true);
            return result;
        }
    }

    private class ParticipateListener implements BackgroundRunner.BackgroundListener {

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
                Log.e(TAG, "Could not make user a participant");
                NotificationUtils.showInfoDialog(BecomeParticipantActivity.this, getString(R.string.error), getString(R.string.participate_failed));
            }

            _btParticipate.setShowLoadingIndicator(false);
        }
    }
}