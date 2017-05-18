package org.break_out.breakout.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.break_out.breakout.R;
import org.break_out.breakout.manager.UserManager;
import org.break_out.breakout.model.User;
import org.break_out.breakout.ui.activities.LoginRegisterActivity;
import org.break_out.breakout.ui.activities.MainActivity;
import org.break_out.breakout.ui.views.BOFlatButton;

import java.util.Calendar;

/**
 * Created by Tino on 20.02.2016.
 */
public class EarlyBirdWelcomeFragment extends Fragment {

    public static final String TAG = "EarlyBirdWelcomeFr";

    private BOFlatButton _btParticipate = null;
    private TextView _tvHeadline;
    private TextView _tvContent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Init views
        View v = inflater.inflate(R.layout.fragment_early_bird_welcome, container, false);
        Log.d(TAG,"onCreateView");

        // Participate button
        _btParticipate = (BOFlatButton) v.findViewById(R.id.bt_participate);
        _btParticipate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UserManager.getInstance(getActivity()).getCurrentUsersRole() == User.Role.VISITOR) {
                    Intent intent = new Intent(getActivity(), LoginRegisterActivity.class);
                    startActivity(intent);
                }
            }
        });
        _tvHeadline = (TextView) v.findViewById(R.id.earlyBird_tv_headline);
        _tvContent = (TextView) v.findViewById(R.id.earlyBid_tv_content);

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
        setTextAccordingToTime();
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        User currUser = UserManager.getInstance(getContext()).getCurrentUser();

        // Set up button
        if(currUser.getRole() != User.Role.VISITOR) {
            _btParticipate.setVisibility(View.GONE);
            return;
        }
    }

    private void setTextAccordingToTime() {
        long currentTime = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        //set calendar wit time of event
        calendar.set(2017,5,28,9,0);
        Log.d(TAG,"current: "+currentTime+" event: "+calendar.getTimeInMillis());
        if(currentTime>=calendar.getTimeInMillis()) {
            _tvHeadline.setText(getString(R.string.earlyBird_running_title));
            _tvContent.setText(getString(R.string.earlyBird_running_text));
        } else {
            if(UserManager.getInstance(getContext()).getCurrentUser().getRole()!= User.Role.VISITOR){
                _tvContent.setText(getString(R.string.early_bird_welcome_text));
            }
        }
    }
}
