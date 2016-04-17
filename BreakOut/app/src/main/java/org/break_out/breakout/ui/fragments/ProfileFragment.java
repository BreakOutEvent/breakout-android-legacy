package org.break_out.breakout.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.break_out.breakout.R;
import org.break_out.breakout.manager.UserManager;
import org.break_out.breakout.model.User;
import org.break_out.breakout.ui.views.BOEditText;
import org.break_out.breakout.ui.views.BOSpinner;
import org.break_out.breakout.util.ArrayUtils;
import org.break_out.breakout.util.NotificationUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tino on 13.04.2016.
 */
public class ProfileFragment extends Fragment implements UserManager.UserDataChangedListener {

    public static final String TAG = "ProfileFragment";

    private UserManager _userManager = null;

    private BOEditText _etFirstName = null;
    private BOEditText _etLastName = null;
    private BOEditText _etEmail = null;
    private BOEditText _etPassword = null;
    private BOEditText _etPhoneNumber = null;
    private BOEditText _etEmergencyNumber = null;
    private BOEditText _etHometown = null;

    private BOSpinner _spGender = null;
    private BOSpinner _spTShirtSize = null;
    private BOSpinner _spEventCity = null;

    private View _vEventInformation = null;
    private View _vEventInformationDivider = null;
    private View _vSaveButton = null;
    private ProgressBar _pbLoadingIndicator = null;

    private List<ProfileFragmentListener> _listeners = new ArrayList<ProfileFragmentListener>();

    public interface ProfileFragmentListener {
        public void onDone();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _userManager = UserManager.getInstance(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Enable custom options menu for this fragment
        setHasOptionsMenu(true);

        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        // Init views
        _etFirstName = (BOEditText) v.findViewById(R.id.et_first_name);
        _etLastName = (BOEditText) v.findViewById(R.id.et_last_name);
        _etEmail = (BOEditText) v.findViewById(R.id.et_email);
        _etPassword = (BOEditText) v.findViewById(R.id.et_password);
        _etPhoneNumber = (BOEditText) v.findViewById(R.id.et_phone_number);
        _etEmergencyNumber = (BOEditText) v.findViewById(R.id.et_emergency_number);
        _etHometown = (BOEditText) v.findViewById(R.id.et_hometown);

        _spGender = (BOSpinner) v.findViewById(R.id.sp_gender);
        _spTShirtSize = (BOSpinner) v.findViewById(R.id.sp_t_shirt_size);
        _spEventCity = (BOSpinner) v.findViewById(R.id.sp_event_city);

        _vEventInformation = v.findViewById(R.id.ll_event_information);
        _vEventInformationDivider = v.findViewById(R.id.v_event_information_divider);

        // Init toolbar
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_profile));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!hasUnsavedChanges()) {
                    notifyListenersDone();
                    return;
                }

                NotificationUtils.showPositiveNegativeDialog(getContext(), R.string.save, R.string.explanation_changes_will_be_lost, android.R.string.yes, android.R.string.no, new NotificationUtils.PositiveNegativeListener() {
                    @Override
                    public void onPositiveClicked() {
                        notifyListenersDone();
                    }

                    @Override
                    public void onNegativeClicked() {

                    }
                });
            }
        });

        // Loading indicator
        _pbLoadingIndicator = (ProgressBar) v.findViewById(R.id.pb_loading_indicator);
        _pbLoadingIndicator.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getContext(), R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);

        // Save button
        _vSaveButton = toolbar.findViewById(R.id.iv_save);
        _vSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!hasUnsavedChanges()) {
                    notifyListenersDone();
                    return;
                }

                setShowLoadingIndicator(true);

                _userManager.updateUserOnServer(getUserFromData(), new UserManager.UserUpdateOnServerListener() {
                    @Override
                    public void userUpdated() {
                        notifyListenersDone();
                        setShowLoadingIndicator(false);
                    }

                    @Override
                    public void updateFailed() {
                        setShowLoadingIndicator(false);
                        Toast.makeText(getContext(), "Update failed.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        setShowLoadingIndicator(false);

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_early_bird_welcome, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onStart() {
        super.onStart();

        refreshUserData();
        _userManager.registerListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        _userManager.unregisterListener(this);
    }

    /**
     * Call all registered fragment listeners' onDone method.
     */
    private void notifyListenersDone() {
        for(ProfileFragmentListener l : _listeners) {
            if(l != null) {
                l.onDone();
            }
        }
    }

    /**
     * This method will check if the user made changes on his/her data.
     *
     * @return True if the user changed some of his/her data, false otherwise
     */
    private boolean hasUnsavedChanges() {
        User currUser = _userManager.getCurrentUser();

        if(!_etFirstName.getText().equals(currUser.getFirstName())) {
            return true;
        }

        if(!_etLastName.getText().equals(currUser.getLastName())) {
            return true;
        }

        if(!_etPassword.getText().equals("")) {
            return true;
        }

        if(!_etPhoneNumber.getText().equals(currUser.getPhoneNumber())) {
            return true;
        }

        if(!_etEmergencyNumber.getText().equals(currUser.getEmergencyNumber())) {
            return true;
        }

        if(!_etHometown.getText().equals(currUser.getHometown())) {
            return true;
        }

        if(!_spGender.getSelectedValue().equals(currUser.getGender())) {
            return true;
        }

        if(!_spTShirtSize.getSelectedValue().equals(currUser.getTShirtSize())) {
            return true;
        }

        if(!_spEventCity.getSelectedValue().equals(currUser.getEventCity())) {
            return true;
        }

        return false;
    }

    /**
     * Set the visibility of the loading indicator in the top right corner
     * of the toolbar.
     *
     * @param show Whether to show the indicator or not
     */
    private void setShowLoadingIndicator(boolean show) {
        _pbLoadingIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
        _vSaveButton.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    public void registerListener(ProfileFragmentListener listener) {
        if(!_listeners.contains(listener)) {
            _listeners.add(listener);
        }
    }

    public void unregisterListener(ProfileFragmentListener listener) {
        if(_listeners.contains(listener)) {
            _listeners.remove(listener);
        }
    }

    /**
     * Build a user object from all the data entered in this fragment.
     *
     * @return A user object with the data entered (the role, access token and remote ID will NOT be set)
     */
    private User getUserFromData() {
        User user = new User();

        if(_userManager.getCurrentUser().isAtLeast(User.Role.USER)) {
            user.setFirstName(_etFirstName.getText());
            user.setLastName(_etLastName.getText());
            user.setEmail(_etEmail.getText());
            user.setGender(_spGender.getSelectedValue());
        }

        if(_userManager.getCurrentUser().isAtLeast(User.Role.PARTICIPANT)) {
            user.setPhoneNumber(_etPhoneNumber.getText());
            user.setEmergencyNumber(_etEmergencyNumber.getText());
            user.setHometown(_etHometown.getText());
            user.setTShirtSize(_spTShirtSize.getSelectedValue());
            user.setEventCity(_spEventCity.getSelectedValue());
        }

        // TODO: Is password changeable? -> No

        return user;
    }

    /**
     * Overwrites all fields with the data stored in the app's current user.
     */
    private void refreshUserData() {
        User user = _userManager.getCurrentUser();

        if(user.isAtLeast(User.Role.USER)) {
            _etFirstName.setText(user.getFirstName());
            _etLastName.setText(user.getLastName());
            _etEmail.setText(user.getEmail());
            _spGender.setSelectedPosition(ArrayUtils.getPositionOfString(getContext(), R.array.gender_array, user.getGender()));
        }

        if(user.isAtLeast(User.Role.PARTICIPANT)) {
            _etPhoneNumber.setText(user.getPhoneNumber());
            _etEmergencyNumber.setText(user.getEmergencyNumber());
            _etHometown.setText(user.getHometown());
            _spTShirtSize.setSelectedPosition(ArrayUtils.getPositionOfString(getContext(), R.array.t_shirt_size_array, user.getTShirtSize()));
            _spEventCity.setSelectedPosition(ArrayUtils.getPositionOfString(getContext(), R.array.event_city_array, user.getEventCity()));

            _vEventInformation.setVisibility(View.VISIBLE);
            _vEventInformationDivider.setVisibility(View.VISIBLE);
        } else {
            _vEventInformation.setVisibility(View.GONE);
            _vEventInformationDivider.setVisibility(View.GONE);
        }

        // TODO: Is password changeable? -> No
    }

    @Override
    public void onUserDataChanged() {
        refreshUserData();
    }
}
