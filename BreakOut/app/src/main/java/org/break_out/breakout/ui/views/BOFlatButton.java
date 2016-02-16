package org.break_out.breakout.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import org.break_out.breakout.R;

/**
 * Created by Tino on 12.02.2016.
 */
public class BOFlatButton extends FrameLayout implements View.OnClickListener {

    private static final String TAG = "BOFlatButton";

    private int _textColor = ContextCompat.getColor(getContext(), R.color.white);

    private Button _btButton = null;
    private ProgressBar _pbLoadingIndicator = null;

    private OnClickListener _listener = null;

    public BOFlatButton(Context context) {
        this(context, null);
    }

    public BOFlatButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BOFlatButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs);
    }

    private void init(AttributeSet attrs) {
        inflate(getContext(), R.layout.view_flat_button, this);

        _btButton = (Button) findViewById(R.id.bt_button);
        _pbLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        // Init default values
        _btButton.setBackgroundResource(R.drawable.btn_rounded_orange_50dp);
        _pbLoadingIndicator.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getContext(), R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);

        _btButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        _btButton.setAllCaps(true);
        _btButton.setGravity(Gravity.CENTER);
        _btButton.setOnClickListener(this);
        setClickable(true);

        if(android.os.Build.VERSION.SDK_INT >= 21) {
            // Hides shadow
            _btButton.setStateListAnimator(null);
        }

        if(android.os.Build.VERSION.SDK_INT >= 23) {
            int[] rippleAttrs = new int[] {android.R.attr.selectableItemBackground};
            TypedArray rippleTa = getContext().obtainStyledAttributes(rippleAttrs);
            Drawable rippleDrawable = rippleTa.getDrawable(0);
            rippleTa.recycle();
            _btButton.setForeground(rippleDrawable);
        }

        if(attrs == null) {
            return;
        }

        // Get attributes
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.BOFlatButton);
        try {
            int buttonColor = ta.getInt(R.styleable.BOFlatButton_buttonColor, 0);
            CharSequence text = ta.getText(R.styleable.BOFlatButton_android_text);
            boolean showBorder = ta.getBoolean(R.styleable.BOFlatButton_showBorder, false);

            // Button color
            if(buttonColor == 0) {
                // Orange
                _btButton.setBackgroundResource(R.drawable.btn_rounded_orange_50dp);
                _textColor = ContextCompat.getColor(getContext(), R.color.white);
                _pbLoadingIndicator.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getContext(), R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
            } else if(buttonColor == 1) {
                // White
                _btButton.setBackgroundResource(R.drawable.btn_rounded_white_50dp);
                _textColor = Color.BLACK;
                _pbLoadingIndicator.getIndeterminateDrawable().setColorFilter(Color.BLACK, android.graphics.PorterDuff.Mode.SRC_IN);
            }

            _btButton.setTextColor(_textColor);

            // Text
            if(text != null) {
                _btButton.setText(text);
            }

            // Border
            // TODO: Show a border based on the color of the button (black or orange)
        } finally {
            ta.recycle();
        }
    }

    @Override
    public void setOnClickListener(OnClickListener listener) {
        _listener = listener;
    }

    @Override
    public void onClick(View v) {
        if(_listener == null) {
            return;
        } else if(_pbLoadingIndicator.getVisibility() == View.GONE) {
            _listener.onClick(v);
        }
    }

    public void setShowLoadingIndicator(boolean showIndicator) {
        if(showIndicator && !isShowingLoadingIndicator()) {
            // Show indicator
            _btButton.setTextColor(Color.TRANSPARENT);
            _pbLoadingIndicator.setVisibility(View.VISIBLE);
            _btButton.setClickable(false);
        } else {
            // Hide indicator
            _btButton.setTextColor(_textColor);
            _pbLoadingIndicator.setVisibility(View.GONE);
            _btButton.setClickable(true);
        }
    }

    public boolean isShowingLoadingIndicator() {
        return (_pbLoadingIndicator != null && _pbLoadingIndicator.getVisibility() == View.VISIBLE);
    }

    public void setEnabled(boolean enabled) {
        _btButton.setEnabled(enabled);
        _btButton.setAlpha(enabled ? 1.0f : 0.5f);
    }
}
