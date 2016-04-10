package org.break_out.breakout.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.break_out.breakout.R;
import org.break_out.breakout.manager.UserManager;
import org.break_out.breakout.model.User;
import org.break_out.breakout.ui.views.BOFlatButton;
import org.break_out.breakout.util.DimenUtils;

/**
 * Created by Tino on 20.02.2016.
 */
public class EarlyBirdWelcomeFragment extends Fragment {

    private BOFlatButton _btParticipate = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_early_bird_welcome, container, false);

        _btParticipate = (BOFlatButton) v.findViewById(R.id.bt_participate);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Set up participate/find team button
        if(UserManager.getInstance(getActivity()).getCurrentUser().isAtLeast(User.Role.USER)) {
            _btParticipate.setText(getString(R.string.button_participate));
        }
    }
}
