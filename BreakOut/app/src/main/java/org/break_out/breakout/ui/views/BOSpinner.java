package org.break_out.breakout.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.break_out.breakout.R;

/**
 * Created by Tino on 06.02.2016.
 */
public class BOSpinner extends BOUnderlinedView {

    public BOSpinner(Context context) {
        super(context);
    }

    public BOSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BOSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public View initCustomContent() {
        Spinner spinner = new Spinner(getContext());
        spinner.setBackgroundResource(R.drawable.ic_arrow_drop_down_white_24dp);

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
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, entryStrings);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                getContent(Spinner.class).setAdapter(adapter);
            }
        } finally {
            ta.recycle();
        }
    }
}
