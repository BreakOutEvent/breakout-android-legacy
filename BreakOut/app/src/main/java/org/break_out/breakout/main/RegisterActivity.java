package org.break_out.breakout.main;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.break_out.breakout.R;

public class RegisterActivity extends AppCompatActivity {
    private FragmentManager _manager;
    private FirstRegisterFragment _firstFragment;
    private SecondRegisterFragment _secondFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        _manager = getFragmentManager();
        _firstFragment = new FirstRegisterFragment();
        _secondFragment = new SecondRegisterFragment();
    }


    public static class FirstRegisterFragment extends Fragment {

    }

    public static class SecondRegisterFragment extends Fragment {

    }




}
