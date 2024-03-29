package org.break_out.breakout.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import org.break_out.breakout.R;

import java.io.Serializable;

/**
 * Created by Tino on 08.02.2016.
 */
public class BOEditText extends BOUnderlinedView implements View.OnFocusChangeListener {

    public static final String TAG = "BOEditText";

    public static class BOEditTextState implements Serializable {

        /**
         * The text entered by the user.
         */
        public String text = "";

    }

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
    public View initCustomContentView(boolean isInDarkMode) {
        EditText editText = new EditText(getContext());
        editText.setTextColor(ContextCompat.getColor(getContext(), isInDarkMode ? R.color.white_transparent_80 : R.color.black_transparent_50));
        editText.setHintTextColor(ContextCompat.getColor(getContext(), isInDarkMode ? R.color.white_transparent_50 : R.color.black_transparent_25));
        editText.setBackgroundResource(0);
        editText.setHint(getHint());
        editText.setPadding(0, 0, 0, 0);
        editText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);

        // Remove default background (underline)
        if(android.os.Build.VERSION.SDK_INT >= 16) {
            editText.setBackground(null);
        } else {
            editText.setBackgroundDrawable(null);
        }

        editText.setOnFocusChangeListener(this);

        return editText;
    }

    @Override
    public void processCustomAttrs(AttributeSet attrs) {
        // Get attributes
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.BOEditText);
        try {
            // Entries
            int inputType = ta.getInt(R.styleable.BOEditText_android_inputType, -1);

            EditText editText = getCustomContentView(EditText.class);
            editText.setInputType(inputType);
        } finally {
            ta.recycle();
        }
    }

    @Override
    public Serializable getState() {
        EditText editText = getCustomContentView(EditText.class);

        BOEditTextState state = new BOEditTextState();
        state.text = editText.getText().toString();

        return state;
    }

    @Override
    public void setState(Serializable serializedState) {
        if(serializedState == null || !(serializedState instanceof BOEditTextState)) {
            Log.e(TAG, "Could not load state from serializable (null or wrong state type).");
            return;
        }

        EditText editText = getCustomContentView(EditText.class);

        BOEditTextState state = (BOEditTextState) serializedState;
        editText.setText(state.text);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus) {
            highlight();
        } else {
            unhighlight();
        }
    }

    public void addTextChangedListener(TextWatcher textWatcher) {
        getCustomContentView(EditText.class).addTextChangedListener(textWatcher);
    }

    public String getText() {
        return getCustomContentView(EditText.class).getText().toString();
    }

    public void setText(@Nullable String text) {
        getCustomContentView(EditText.class).setText(text != null ? text : "");
    }
}
