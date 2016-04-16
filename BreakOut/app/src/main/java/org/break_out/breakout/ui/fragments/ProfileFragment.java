package org.break_out.breakout.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.break_out.breakout.R;
import org.break_out.breakout.manager.UserManager;
import org.break_out.breakout.model.User;
import org.break_out.breakout.ui.activities.MainActivity;
import org.break_out.breakout.ui.views.BOEditText;
import org.break_out.breakout.ui.views.BOSpinner;
import org.break_out.breakout.util.ArrayUtils;

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
    private BOEditText _etPhoneNumber = null;
    private BOEditText _etEmergencyNumber = null;

    private BOSpinner _spGender = null;
    private BOSpinner _spTShirtSize = null;
    private BOSpinner _spEventCity = null;

    private List<ProfileFragmentListener> _listeners = new ArrayList<ProfileFragmentListener>();

    public interface ProfileFragmentListener {
        public void onSaved();
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
        _etPhoneNumber = (BOEditText) v.findViewById(R.id.et_phone_number);
        _etEmergencyNumber = (BOEditText) v.findViewById(R.id.et_emergency_number);
        _spGender = (BOSpinner) v.findViewById(R.id.sp_gender);
        _spTShirtSize = (BOSpinner) v.findViewById(R.id.sp_t_shirt_size);
        _spEventCity = (BOSpinner) v.findViewById(R.id.sp_event_city);

        // Init toolbar
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = getActivity();

                if(!(activity instanceof MainActivity)) {
                    Log.e(TAG, "Parent activity must be MainActivity.");
                    return;
                }

                MainActivity mainActivity = (MainActivity) activity;
                mainActivity.openDrawer();
            }
        });

        View saveButton = toolbar.findViewById(R.id.iv_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Starting update...", Toast.LENGTH_SHORT).show();
                _userManager.updateUserOnServer(getUserFromData(), new UserManager.UserUpdateOnServerListener() {
                    @Override
                    public void userUpdated() {
                        for(ProfileFragmentListener l : _listeners) {
                            if(l != null) {
                                l.onSaved();
                            }
                        }
                    }

                    @Override
                    public void updateFailed() {
                        Toast.makeText(getContext(), "Update failed.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

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

        updateUserData();
        _userManager.registerListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        _userManager.unregisterListener(this);
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

    private User getUserFromData() {
        User user = new User();

        user.setFirstName(_etFirstName.getText());
        user.setLastName(_etLastName.getText());
        user.setEmail(_etEmail.getText());
        user.setPhoneNumber(_etPhoneNumber.getText());
        user.setEmergencyNumber(_etEmergencyNumber.getText());

        user.setGender(_spGender.getSelectedValue());

        // TODO: some data still missing

        return user;
    }

    private void updateUserData() {
        User user = _userManager.getCurrentUser();

        _etFirstName.setText(user.getFirstName());
        _etLastName.setText(user.getLastName());
        _etEmail.setText(user.getEmail());
        _etPhoneNumber.setText(user.getPhoneNumber());
        _etEmergencyNumber.setText(user.getEmergencyNumber());

        _spGender.setSelectedPosition(ArrayUtils.getPositionOfString(getContext(), R.array.gender_array, user.getGender()));

        // TODO: some data still missing
    }

    @Override
    public void userDataChanged() {
        updateUserData();
    }
}
