package org.break_out.breakout.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.break_out.breakout.R;
import org.break_out.breakout.manager.UserManager;
import org.break_out.breakout.model.User;
import org.break_out.breakout.ui.views.BOEditText;

/**
 * Created by Tino on 13.04.2016.
 */
public class ProfileFragment extends Fragment implements UserManager.UserDataChangedListener {

    public static final String TAG = "ProfileFragment";

    private UserManager _userManager = null;

    private BOEditText _etFirstName = null;
    private BOEditText _etLastName = null;
    private BOEditText _etEmail = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _userManager = UserManager.getInstance(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Enable custom options menu for this fragment
        setHasOptionsMenu(true);

        // Init views
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        _etFirstName = (BOEditText) v.findViewById(R.id.et_first_name);
        _etLastName = (BOEditText) v.findViewById(R.id.et_last_name);
        _etEmail = (BOEditText) v.findViewById(R.id.et_email);

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

    private void updateUserData() {
        User user = _userManager.getCurrentUser();

        _etFirstName.setText(user.getFirstName());
        _etLastName.setText(user.getLastName());
        _etEmail.setText(user.getEmail());
    }

    @Override
    public void userDataChanged() {
        updateUserData();
    }

    public void startUpdatingUserOnServer() {

    }
}
