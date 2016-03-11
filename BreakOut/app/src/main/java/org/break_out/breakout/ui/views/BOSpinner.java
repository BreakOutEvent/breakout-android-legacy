package org.break_out.breakout.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.break_out.breakout.R;

import java.io.Serializable;

/**
 * Created by Tino on 06.02.2016.
 */
public class BOSpinner extends BOUnderlinedView {

    private static final String TAG = "BOSpinner";

    private boolean _selected = false;

    public static class BOSpinnerState implements Serializable {

        /**
         * The selected index on the spinner.
         * This value is -1 if no item has been selected
         * yet.
         */
        public int selectedIndex = -1;

    }

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

        Spinner spinner = getCustomContentView(Spinner.class);
        return spinner.getSelectedItem().toString();
    }

    @Override
    public View initCustomContentView() {
        Spinner spinner = new Spinner(getContext());
        spinner.setBackgroundResource(R.drawable.ic_arrow_drop_down_white_24dp);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                _selected = (position != getCustomContentView(Spinner.class).getAdapter().getCount());
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
                ArrayAdapter<String> adapter = new BOSpinnerAdapter(getContext(), entryStrings);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                Spinner spinner = getCustomContentView(Spinner.class);
                spinner.setAdapter(adapter);
                spinner.setSelection(adapter.getCount());
            }
        } finally {
            ta.recycle();
        }
    }

    @Override
    public Serializable getState() {
        BOSpinnerState state = new BOSpinnerState();
        state.selectedIndex = getCustomContentView(Spinner.class).getSelectedItemPosition();

        return state;
    }

    @Override
    public void setState(Serializable serializedState) {
        if(serializedState == null || !(serializedState instanceof BOSpinnerState)) {
            Log.e(TAG, "Could not load state from serializable (null or wrong state type).");
            return;
        }

        Spinner spinner = getCustomContentView(Spinner.class);

        BOSpinnerState state = (BOSpinnerState) serializedState;
        int selectedIndex = state.selectedIndex;

        if(selectedIndex == -1 || selectedIndex > spinner.getAdapter().getCount()-1) {
            return;
        }

        spinner.setSelection(selectedIndex);
    }

    private class BOSpinnerAdapter extends ArrayAdapter<String> {

        private String[] items;
        private String hint = "";

        public BOSpinnerAdapter(Context context, String[] entries) {
            super(context, android.R.layout.simple_spinner_item, entries);

            items = entries;
            hint = getHint();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = new TextView(getContext());
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

            if(position == getCount()) {
                textView.setText(hint);
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.white_transparent_50));
            } else {
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.white_transparent_80));
                textView.setText(items[position]);
            }

            return textView;
        }
    }

}
