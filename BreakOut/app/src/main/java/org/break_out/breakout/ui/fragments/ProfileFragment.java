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
import org.break_out.breakout.ui.views.BOFlatButton;

/**
 * Created by Tino on 13.04.2016.
 */
public class ProfileFragment extends Fragment {

    public static final String TAG = "ProfileFragment";

    private TextView _tvFirstName = null;
    private TextView _tvLastName = null;
    private TextView _tvEmail = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Enable custom options menu for this fragment
        setHasOptionsMenu(true);

        // Init views
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        _tvFirstName = (TextView) v.findViewById(R.id.tv_first_name);
        _tvLastName = (TextView) v.findViewById(R.id.tv_last_name);
        _tvEmail = (TextView) v.findViewById(R.id.tv_email);

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

        UserManager um = UserManager.getInstance(getContext());
        User user = um.getCurrentUser();

        _tvFirstName.setText(user.getFirstName());
        _tvLastName.setText(user.getLastName());
        _tvEmail.setText(user.getEmail());
    }
}
