package org.break_out.breakout.ui.views;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.break_out.breakout.R;

/**
 * Created by Tino on 06.02.2016.
 */
public class BOSpinner extends BOUnderlinedView {

    private static final String TAG = "BOSpinner";

    private boolean _selected = false;

    public BOSpinner(Context context) {
        super(context);
    }

    public BOSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BOSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public String getSelectedValue() {
        if(!_selected) {
            return null;
        }

        Spinner spinner = getContentView(Spinner.class);
        return spinner.getSelectedItem().toString();
    }

    @Override
    public View initCustomContent() {
        Spinner spinner = new Spinner(getContext());
        spinner.setBackgroundResource(R.drawable.ic_arrow_drop_down_white_24dp);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                _selected = true;

                Log.d(TAG, getSelectedValue());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                _selected = false;
            }
        });

        return spinner;
    }

    @Override
    public void processCustomAttrs(AttributeSet attrs) {
        // Get attributes
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.BOSpinner);
        try {
            // Entries
            CharSequence[] entries = ta.getTextArray(R.styleable.BOSpinner_android_entries);
            if(entries != null) {
                String[] entryStrings = new String[entries.length];
                for(int i = 0; i < entries.length; i++) {
                    entryStrings[i] = entries[i].toString();
                }
                ArrayAdapter<String> adapter = new BOSpinnerAdapter(getContext(), R.layout.item_spinner, entryStrings);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                getContentView(Spinner.class).setAdapter(adapter);
            }
        } finally {
            ta.recycle();
        }
    }

    public class BOSpinnerAdapter extends ArrayAdapter<String> {

        private String[] items;
        private String hint = "";

        public BOSpinnerAdapter(Context context, int txtViewResourceId, String[] entries) {
            super(context, txtViewResourceId, entries);
            items = entries;
            hint = getHint();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();
            View v = inflater.inflate(R.layout.item_spinner, parent, false);
            TextView textView = (TextView) v.findViewById(R.id.spinner_textview);

            if(!_selected) {
                textView.setText(hint);
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.white_transparent_50));
            } else {
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.white_transparent_80));
                textView.setText(items[position]);
            }

            return v;
        }
    }

}
