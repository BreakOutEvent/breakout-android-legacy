package org.break_out.breakout.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import org.break_out.breakout.R;

public class RegisterActivity extends FragmentActivity {
    private static final String TAG = "RegisterActivity";
    private FragmentManager _manager;
    private RelativeLayout _relativeLayout_fragmentHolder;
    private FirstRegisterFragment _firstFragment;
    private SecondRegisterFragment _secondFragment;
    private ViewPager _viewPager;
    private BORegisterFragmentAdapter _adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        _manager = getSupportFragmentManager();
        _adapter = new BORegisterFragmentAdapter(_manager);
        _relativeLayout_fragmentHolder = (RelativeLayout) findViewById(R.id.register_relativeLayout_fragmentHolder);
        _viewPager = (ViewPager) findViewById(R.id.register_viewPager);
        _viewPager.setAdapter(_adapter);
        _firstFragment = new FirstRegisterFragment();
        _secondFragment = new SecondRegisterFragment();

        _viewPager.setCurrentItem(0);
        Log.d(TAG,"onCreate fired");
    }

    private class BORegisterFragmentAdapter extends FragmentPagerAdapter {

        public BORegisterFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Log.d(TAG,"getItem called");
            switch(position) {
                case 0:
                    return _firstFragment;
                case 1:
                    return _secondFragment;
                default:
                    return _firstFragment;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }


    public static class FirstRegisterFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View layout = inflater.inflate(R.layout.fragment_register1,container,false);
            Log.d(TAG, "FirstFragment onCreateView");
            return layout;
        }
    }

    public static class SecondRegisterFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View layout = inflater.inflate(R.layout.fragment_register2,container,false);
            return layout;
        }
    }




}
