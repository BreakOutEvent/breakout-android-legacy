package org.break_out.breakout.main;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.break_out.breakout.R;

public class RegisterActivity extends FragmentActivity {
    private static final String TAG = "RegisterActivity";
    private EditText _editText_last;
    private ScrollView _scrollView_credentials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        _editText_last = (EditText) findViewById(R.id.register_f1_editText_emergencyNumber);
        _scrollView_credentials = (ScrollView) findViewById(R.id.register_scrollView_credentials);

        _editText_last.setOnEditorActionListener(new OnUserInputFinishedListener());
    }

    private void scrollToEnd() {
        _scrollView_credentials.fullScroll(ScrollView.FOCUS_DOWN);
    }


    //Detect if the user has entered the credential and scroll down to the end of the list if yes
    private class OnUserInputFinishedListener implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

            hideKeyboard();
            scrollToEnd();
            return true;
        }

        private void hideKeyboard() {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(_editText_last.getWindowToken(), 0);
        }
    }


}