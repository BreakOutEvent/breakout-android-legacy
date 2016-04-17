package org.break_out.breakout.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import org.break_out.breakout.R;
import org.break_out.breakout.manager.UserManager;
import org.break_out.breakout.model.User;
import org.break_out.breakout.ui.views.BODatePicker;
import org.break_out.breakout.ui.views.BOEditText;
import org.break_out.breakout.ui.views.BOFlatButton;
import org.break_out.breakout.ui.views.BOSpinner;
import org.break_out.breakout.util.ArrayUtils;
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
    private static final String KEY_TEAM_NAME = "key_team_name";
    private static final String KEY_EMAIL_PARTNER = "key_email_partner";
    private static final String KEY_CREATE_TEAM = "key_create_team";

    // Enter data
    private BOEditText _etFirstName = null;
    private BOEditText _etLastName = null;
    private BOSpinner _spGender = null;
    private BOSpinner _spTShirtSize = null;
    private BOEditText _etPhoneNumber = null;
    private BOEditText _etEmergencyNumber = null;
    private BODatePicker _dpBirthday = null;

    private BOFlatButton _btParticipate = null;

    // Select team
    private BOEditText _etTeamName = null;
    private BOEditText _etEmailPartner = null;
    private BOSpinner _spEventCity = null;

    private BOFlatButton _btCreateTeam = null;

    private Step _currentStep = Step.UNDEFINED;
    private View _vEnterData = null;
    private View _vSelectTeam = null;

    private enum Step {
        ENTER_DATA,
        SELECT_TEAM,
        UNDEFINED;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_become_participant);
        setHeaderImage(R.drawable.btn_camera_round);

        initViews();
        initHelpListeners();

        showStep(Step.ENTER_DATA, false);
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
        // Layouts for the steps
        _vEnterData = findViewById(R.id.ll_enter_data);
        _vSelectTeam = findViewById(R.id.ll_select_team);

        // Enter data
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

        // Select team
        _etTeamName = (BOEditText) findViewById(R.id.et_team_name);
        _etEmailPartner = (BOEditText) findViewById(R.id.et_email_partner);
        _spEventCity = (BOSpinner) findViewById(R.id.sp_event_city);

        _btCreateTeam = (BOFlatButton) findViewById(R.id.bt_create_team);
        _btCreateTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTeam();
            }
        });

        fillFieldsFromUser();
    }

    private void fillFieldsFromUser() {
        User currUser = UserManager.getInstance(this).getCurrentUser();

        _etFirstName.setText(currUser.getFirstName());
        _etLastName.setText(currUser.getLastName());
        _spGender.setSelectedPosition(ArrayUtils.getPositionOfString(this, R.array.gender_array, currUser.getGender()));
    }

    private void initHelpListeners() {
        _spGender.setRightDrawableOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationUtils.showInfoDialog(BecomeParticipantActivity.this, R.string.hint_gender, R.string.explanation_gender);
            }
        });

        _spTShirtSize.setRightDrawableOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationUtils.showInfoDialog(BecomeParticipantActivity.this, R.string.hint_t_shirt_size, R.string.explanation_t_shirt_size);
            }
        });

        _etPhoneNumber.setRightDrawableOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationUtils.showInfoDialog(BecomeParticipantActivity.this, R.string.hint_phone_number, R.string.explanation_phone_number);
            }
        });

        _etEmergencyNumber.setRightDrawableOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationUtils.showInfoDialog(BecomeParticipantActivity.this, R.string.hint_emergency_number, R.string.explanation_emergency_number);
            }
        });

        _dpBirthday.setRightDrawableOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationUtils.showInfoDialog(BecomeParticipantActivity.this, R.string.hint_birthday, R.string.explanation_birthday);
            }
        });

        _spEventCity.setRightDrawableOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationUtils.showInfoDialog(BecomeParticipantActivity.this, R.string.hint_event_city, R.string.explanation_event_city);
            }
        });
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if(savedInstanceState == null) {
            return;
        }

        // Enter data
        _etLastName.setState(savedInstanceState.getSerializable(KEY_LAST_NAME));
        _etFirstName.setState(savedInstanceState.getSerializable(KEY_FIRST_NAME));
        _spGender.setState(savedInstanceState.getSerializable(KEY_GENDER));
        _spTShirtSize.setState(savedInstanceState.getSerializable(KEY_T_SHIRT_SIZE));
        _etPhoneNumber.setState(savedInstanceState.getSerializable(KEY_PHONE_NUMBER));
        _etEmergencyNumber.setState(savedInstanceState.getSerializable(KEY_EMERGENCY_NUMBER));
        _dpBirthday.setState(savedInstanceState.getSerializable(KEY_BIRTHDAY));
        _btParticipate.setState(savedInstanceState.getSerializable(KEY_PARTICIPATE));

        // Select team
        _etTeamName.setState(savedInstanceState.getSerializable(KEY_TEAM_NAME));
        _etEmailPartner.setState(savedInstanceState.getSerializable(KEY_EMAIL_PARTNER));
        _btCreateTeam.setState(savedInstanceState.getSerializable(KEY_CREATE_TEAM));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Enter data
        outState.putSerializable(KEY_LAST_NAME, _etLastName.getState());
        outState.putSerializable(KEY_FIRST_NAME, _etFirstName.getState());
        outState.putSerializable(KEY_GENDER, _spGender.getState());
        outState.putSerializable(KEY_T_SHIRT_SIZE, _spTShirtSize.getState());
        outState.putSerializable(KEY_PHONE_NUMBER, _etPhoneNumber.getState());
        outState.putSerializable(KEY_EMERGENCY_NUMBER, _etEmergencyNumber.getState());
        outState.putSerializable(KEY_BIRTHDAY, _dpBirthday.getState());
        outState.putSerializable(KEY_PARTICIPATE, _btParticipate.getState());

        // Select team
        outState.putSerializable(KEY_TEAM_NAME, _etTeamName.getState());
        outState.putSerializable(KEY_EMAIL_PARTNER, _etEmailPartner.getState());
        outState.putSerializable(KEY_CREATE_TEAM, _btCreateTeam.getState());
    }

    /**
     * Shows one of the two steps <i>enter data</i> and <i>register team</i>
     * in this activity. You can turn on/off the fade animation when switching
     * between these steps.
     *
     * @param step The step to be shown
     * @param useFadeAnimation Whether to use a fade animation or not
     */
    private void showStep(final Step step, boolean useFadeAnimation) {
        if(step == _currentStep) {
            return;
        }

        setCloseButtonVisible(step == Step.ENTER_DATA);
        _currentStep = step;

        // No animation
        if(!useFadeAnimation) {
            _vEnterData.setVisibility(step == Step.ENTER_DATA ? View.VISIBLE : View.GONE);
            _vSelectTeam.setVisibility(step == Step.ENTER_DATA ? View.GONE : View.VISIBLE);
            return;
        }

        // With animation
        Animation animFadeOut = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);
        animFadeOut.setDuration(800);

        final Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
        animFadeIn.setDuration(800);

        animFadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(step == Step.ENTER_DATA) {
                    _vSelectTeam.setVisibility(View.GONE);

                    _vEnterData.setVisibility(View.VISIBLE);
                    _vEnterData.startAnimation(animFadeIn);
                } else {
                    _vEnterData.setVisibility(View.GONE);

                    _vSelectTeam.setVisibility(View.VISIBLE);
                    _vSelectTeam.startAnimation(animFadeIn);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        if(step == Step.ENTER_DATA) {
            _vSelectTeam.startAnimation(animFadeOut);
        } else {
            _vEnterData.startAnimation(animFadeOut);
        }
    }

    /**
     * This method will check if all mandatory inputs are given for
     * the current step.
     * If yes, this method will return true. Otherwise it will display a
     * tooltip respectively and return false.
     *
     * @return If the input is ok or not
     */
    private boolean checkInputAndShowHintIfNeeded() {
        if(_currentStep == Step.ENTER_DATA) {
            if(_etLastName.getText().equals("")) {
                NotificationUtils.showTooltip(this, _etLastName, R.string.tooltip_mandatory_field);
                return false;
            }

            if(_etFirstName.getText().equals("")) {
                NotificationUtils.showTooltip(this, _etFirstName, R.string.tooltip_mandatory_field);
                return false;
            }

            if(_spGender.getSelectedValue().equals("")) {
                NotificationUtils.showTooltip(this, _spGender, R.string.tooltip_mandatory_field);
                return false;
            }

            if(_spTShirtSize.getSelectedValue().equals("")) {
                NotificationUtils.showTooltip(this, _spTShirtSize, R.string.tooltip_mandatory_field);
                return false;
            }

            if(_etPhoneNumber.getText().equals("")) {
                NotificationUtils.showTooltip(this, _etPhoneNumber, R.string.tooltip_mandatory_field);
                return false;
            }

            if(!isValidPhoneNumber(_etPhoneNumber.getText())) {
                NotificationUtils.showTooltip(this, _etPhoneNumber, R.string.tooltip_no_valid_phone_number);
                return false;
            }

            if(_etEmergencyNumber.getText().equals("")) {
                NotificationUtils.showTooltip(this, _etEmergencyNumber, R.string.tooltip_mandatory_field);
                return false;
            }

            if(!isValidPhoneNumber(_etEmergencyNumber.getText())) {
                NotificationUtils.showTooltip(this, _etEmergencyNumber, R.string.tooltip_no_valid_phone_number);
                return false;
            }

            if(_dpBirthday.getSelectedDate() == null) {
                NotificationUtils.showTooltip(this, _dpBirthday, R.string.tooltip_mandatory_field);
                return false;
            }
        } else if(_currentStep == Step.SELECT_TEAM) {
            if(_etTeamName.getText().equals("")) {
                NotificationUtils.showTooltip(this, _etTeamName, R.string.tooltip_mandatory_field);
                return false;
            }

            if(_etEmailPartner.getText().equals("")) {
                NotificationUtils.showTooltip(this, _etEmailPartner, R.string.tooltip_mandatory_field);
                return false;
            }

            if(!isValidEmail(_etEmailPartner.getText())) {
                NotificationUtils.showTooltip(this, _etEmailPartner, R.string.tooltip_no_valid_email);
                return false;
            }

            if(_spEventCity.getSelectedValue().equals("")) {
                NotificationUtils.showTooltip(this, _spEventCity, R.string.tooltip_mandatory_field);
                return false;
            }
        }

        return true;
    }

    private void participate() {
        if(!checkInputAndShowHintIfNeeded()) {
            return;
        }

        String firstName = _etFirstName.getText();
        String lastName = _etLastName.getText();
        String gender = _spGender.getSelectedValue();
        String tShirtSize = _spTShirtSize.getSelectedValue();
        String phoneNumber = _etPhoneNumber.getText();
        String emergencyNumber = _etEmergencyNumber.getText();

        _btParticipate.setShowLoadingIndicator(true);

        // Set up background runner
        BackgroundRunner runner = BackgroundRunner.getRunner(RUNNER_PARTICIPATE);
        runner.setRunnable(new ParticipateRunnable());

        Bundle params = new Bundle();
        params.putString(KEY_FIRST_NAME, firstName);
        params.putString(KEY_LAST_NAME, lastName);
        params.putString(KEY_GENDER, gender);
        params.putString(KEY_T_SHIRT_SIZE, tShirtSize);
        params.putString(KEY_PHONE_NUMBER, phoneNumber);
        params.putString(KEY_EMERGENCY_NUMBER, emergencyNumber);

        // Start background runner
        runner.execute(params);
    }

    private void createTeam() {
        if(!checkInputAndShowHintIfNeeded()) {
            return;
        }

        // TODO
    }

    /**
     * Check if input is valid number (use for phoneNumber and emergencyNumber).
     *
     * @param number The phone number
     * @return True if valid, false otherwise
     */
    private boolean isValidPhoneNumber(String number) {
        return Patterns.PHONE.matcher(number).matches();
    }

    private boolean isValidEmail(String email) {
        return email != null && !email.equals("") && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
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
                showStep(Step.SELECT_TEAM, true);
                return;
            } else {
                Log.e(TAG, "Could not make user a participant");
                NotificationUtils.showInfoDialog(BecomeParticipantActivity.this, getString(R.string.error), getString(R.string.participate_failed));
            }

            _btParticipate.setShowLoadingIndicator(false);
        }
    }

    @Override
    public void onBackPressed() {
        // Selecting team is modal: The user has to finish it to continue
        if(_currentStep == Step.SELECT_TEAM) {
            return;
        }

        super.onBackPressed();
    }
}