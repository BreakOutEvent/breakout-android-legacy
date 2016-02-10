package org.break_out.breakout.ui.views;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import org.break_out.breakout.R;

/**
 * Created by Tino on 08.02.2016.
 */
public class BOEditText extends BOUnderlinedView implements View.OnFocusChangeListener {

    public static final String TAG = "BOEditText";

    public BOEditText(Context context) {
        super(context);
    }

    public BOEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BOEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public View initCustomContent() {
        EditText editText = new EditText(getContext());
        editText.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        editText.setHintTextColor(ContextCompat.getColor(getContext(), R.color.white_transparent_50));
        editText.setBackgroundResource(0);
        editText.setHint(getHint());

        editText.setOnFocusChangeListener(this);

        return editText;
    }

    @Override
    public void processCustomAttrs(AttributeSet attrs) {

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus) {
            highlight();
        } else {
            unhighlight();
        }
    }
}
