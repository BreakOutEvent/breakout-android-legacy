package org.break_out.breakout.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import org.break_out.breakout.R;

/**
 * Created by Tino on 08.02.2016.
 */
public abstract class BOUnderlinedView extends FrameLayout {

    private View _vContent;

    private View _vUnderlineNormal = null;
    private View _vUnderlineHighlight = null;

    private ImageView _ivDrawableLeft = null;
    private ImageView _ivDrawableRight = null;

    private String _hint = "";

    private float _touchStartX, _touchStartY;

    public BOUnderlinedView(Context context) {
        this(context, null);
    }

    public BOUnderlinedView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BOUnderlinedView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs);
    }

    private void init(AttributeSet attrs) {
        inflate(getContext(), R.layout.view_underlined, this);

        initViews();
        initFromAttrs(attrs);
        initContent();
        processCustomAttrs(attrs);
        initFocus();
    }

    private void initViews() {
        _ivDrawableLeft = (ImageView) findViewById(R.id.drawable_left);
        _ivDrawableRight = (ImageView) findViewById(R.id.drawable_right);

        _vUnderlineNormal = findViewById(R.id.underline_normal);
        _vUnderlineHighlight = findViewById(R.id.underline_highlight);
    }

    private void initFromAttrs(AttributeSet attrs) {
        // Get attributes
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.BOUnderlinedView);
        try {
            // Drawables
            int drawableLeft = ta.getResourceId(R.styleable.BOUnderlinedView_drawableLeft, -1);
            int drawableRight = ta.getResourceId(R.styleable.BOUnderlinedView_drawableRight, -1);

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

            // Hint
            String hint = ta.getString(R.styleable.BOUnderlinedView_android_hint);
            if(hint != null) {
                _hint = hint;
            }
        } finally {
            ta.recycle();
        }
    }

    private void initFocus() {
        setFocusable(true);
        setFocusableInTouchMode(true);

        setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    highlight();
                } else {
                    unhighlight();
                }
            }
        });
    }

    private void initContent() {
        FrameLayout placeholder = (FrameLayout) findViewById(R.id.placeholder);
        placeholder.removeAllViews();
        _vContent = initCustomContent();
        placeholder.addView(_vContent);
    }

    public abstract View initCustomContent();

    public abstract void processCustomAttrs(AttributeSet attrs);

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                _touchStartX = event.getX();
                _touchStartY = event.getY();
                break;
            case MotionEvent.ACTION_UP: {
                float endX = event.getX();
                float endY = event.getY();
                if(isClick(_touchStartX, endX, _touchStartY, endY)) {
                    requestFocus();
                }
                break;
            }
        }

        return false;
    }

    private boolean isClick(float startX, float endX, float startY, float endY) {
        float differenceX = Math.abs(startX - endX);
        float differenceY = Math.abs(startY - endY);
        if(differenceX > 5 || differenceY > 5) {
            return false;
        }
        return true;
    }

    public <T extends View> T getContentView(Class<T> type) {
        return (T) _vContent;
    }

    public String getHint() {
        return _hint;
    }

    public void highlight() {
        _vUnderlineNormal.setVisibility(View.GONE);
        _vUnderlineHighlight.setVisibility(View.VISIBLE);
    }

    public void unhighlight() {
        _vUnderlineNormal.setVisibility(View.VISIBLE);
        _vUnderlineHighlight.setVisibility(View.GONE);
    }

    public void setOnRightDrawableClickListener(OnClickListener listener) {
        _ivDrawableRight.setOnClickListener(listener);
    }

}
