package org.break_out.breakout.model;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Maximilian Dühr on 30.05.2016.
 */
public class EmergencyHelp {
    private static final String TAG = "EmergencyHelp";
    private String _title;
    private ArrayList<EmergencyInfo> _infos;

    public EmergencyHelp(String title, EmergencyInfo... infos) {
        _infos = new ArrayList<>();
        _infos.addAll(Arrays.asList(infos));
        _title = title;
    }

    public ArrayList<EmergencyInfo> getInfos() {
        return _infos;
    }

    public String getTitle() {
        return _title;
    }



    public static class EmergencyInfo {
        private String _text;
        private String _number;

        public EmergencyInfo(String text,String number) {
            _text = text;
            _number = number;
        }

        public SpannableString toSpan() {
            SpannableString emergencyInfoString = new SpannableString(_text+" "+_number);
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Log.d(TAG,"CLICKED!");
                }
            };
            if(!_number.isEmpty()) {
                emergencyInfoString.setSpan(clickableSpan,_text.length()+1, _text.length()+_number.length()+1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            }
            return emergencyInfoString;
        }
    }
}
