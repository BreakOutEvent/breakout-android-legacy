package org.break_out.breakout.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import org.break_out.breakout.R;

/**
 * Created by Tino on 06.02.2016.
 */
public class BOSpinner extends RelativeLayout {

    private View _vUnderlineNormal = null;
    private View _vUnderlineHighlight = null;

    private ImageView _ivDrawableLeft = null;
    private ImageView _ivDrawableRight = null;

    private Spinner _spSpinner = null;

    public BOSpinner(Context context) {
        this(context, null);
    }

    public BOSpinner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BOSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs);
    }

    private void init(AttributeSet attrs) {
        inflate(getContext(), R.layout.view_spinner, this);

        _ivDrawableLeft = (ImageView) findViewById(R.id.drawable_left);
        _ivDrawableRight = (ImageView) findViewById(R.id.drawable_right);

        _vUnderlineNormal = findViewById(R.id.spinner_underline_normal);
        _vUnderlineHighlight = findViewById(R.id.spinner_underline_hightlight);

        _spSpinner = (Spinner) findViewById(R.id.spinner);

        // Get attributes
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.BOSpinner);
        try {
            // Drawables
            int drawableLeft = ta.getResourceId(R.styleable.BOSpinner_drawableLeft, -1);
            int drawableRight = ta.getResourceId(R.styleable.BOSpinner_drawableRight, -1);

            if(drawableLeft != -1) {
                _ivDrawableLeft.setImageResource(drawableLeft);
            } else {
                _ivDrawableLeft.setVisibility(View.GONE);
            }

            if(drawableRight != -1) {
                _ivDrawableRight.setImageResource(drawableRight);
            } else {
                _ivDrawableRight.setVisibility(View.GONE);
            }

            // Entries
            CharSequence[] entries = ta.getTextArray(R.styleable.BOSpinner_android_entries);
            if(entries != null) {
                String[] entryStrings = new String[entries.length];
                for(int i = 0; i < entries.length; i++) {
                    entryStrings[i] = entries[i].toString();
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, entryStrings);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                _spSpinner.setAdapter(adapter);
            }
        } finally {
            ta.recycle();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                highlight();
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        unhighlight();
                    }
                }, 1000);
                break;
        }
        return false;
    }

    private void highlight() {
        _vUnderlineNormal.setVisibility(View.GONE);
        _vUnderlineHighlight.setVisibility(View.VISIBLE);
    }

    private void unhighlight() {
        _vUnderlineNormal.setVisibility(View.VISIBLE);
        _vUnderlineHighlight.setVisibility(View.GONE);
    }

    public void setRightDrawableOnClickListener(OnClickListener listener) {
        _ivDrawableRight.setOnClickListener(listener);
    }
}
