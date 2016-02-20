package org.break_out.breakout.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.break_out.breakout.R;

/**
 * Created by Tino on 20.02.2016.
 */
public class EarlyBirdWelcomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_early_bird_welcome, container, false);
    }

}
