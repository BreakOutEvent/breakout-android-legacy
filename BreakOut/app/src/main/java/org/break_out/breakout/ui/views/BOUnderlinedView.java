package org.break_out.breakout.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import org.break_out.breakout.R;

import java.io.Serializable;

/**
 * This view implements the basic functionality for any view with
 * a simple line at the bottom. This line will be highlighted in
 * the primaryColor when the view has been selected (gained focus).
 * <br />
 * There are two drawables: One on the left of the view and one on
 * the right. Both are on the bottom line. Only the right drawable
 * is clickable. You can apply a listener to it using {@link #setRightDrawableOnClickListener(OnClickListener)}.
 * <br /><br />
 * Created by Tino on 08.02.2016.
 */
public abstract class BOUnderlinedView extends FrameLayout {

    private static final String TAG = "BOUnderlinedView";

    private View _vContent;

    private View _vUnderlineNormal = null;
    private View _vUnderlineHighlight = null;

    private ImageView _ivDrawableLeft = null;
    private ImageView _ivDrawableRight = null;

    private String _hint = "";

    private boolean _isUnderlined = true;
    private boolean _isInDarkMode = false;

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
        unhighlight();
        initFocus();
    }

    private void initViews() {
        _ivDrawableLeft = (ImageView) findViewById(R.id.drawable_left);
        _ivDrawableRight = (ImageView) findViewById(R.id.drawable_right);

        _vUnderlineNormal = findViewById(R.id.underline_normal);
        _vUnderlineHighlight = findViewById(R.id.underline_highlight);
    }

    private void initFromAttrs(AttributeSet attrs) {
        if(attrs == null) {
            return;
        }

        // Get attributes
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.BOUnderlinedView);
        try {
            // Drawables
            int drawableLeft = ta.getResourceId(R.styleable.BOUnderlinedView_drawableLeft, -1);
            int drawableRight = ta.getResourceId(R.styleable.BOUnderlinedView_drawableRight, -1);
            float drawableLeftAlpha = ta.getFloat(R.styleable.BOUnderlinedView_drawableLeftAlpha, 1.0f);
            float drawableRightAlpha = ta.getFloat(R.styleable.BOUnderlinedView_drawableRightAlpha, 1.0f);

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

            // Set alpha values
            _ivDrawableLeft.setAlpha(drawableLeftAlpha);
            _ivDrawableRight.setAlpha(drawableRightAlpha);

            // Hint
            String hint = ta.getString(R.styleable.BOUnderlinedView_android_hint);
            if(hint != null) {
                _hint = hint;
            }

            // Underlined or not
            _isUnderlined = ta.getBoolean(R.styleable.BOUnderlinedView_underlined, true);
            _vUnderlineNormal.setVisibility(_isUnderlined ? View.VISIBLE : View.GONE);
            _vUnderlineHighlight.setVisibility(_isUnderlined ? View.VISIBLE : View.GONE);

            // Dark mode or not
            _isInDarkMode = ta.getBoolean(R.styleable.BOUnderlinedView_darkMode, false);
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

    /**
     * Set the content view (above the bottom line)
     * to be the custom view returned by {@link #initCustomContentView(boolean)} )}.
     */
    private void initContent() {
        FrameLayout placeholder = (FrameLayout) findViewById(R.id.placeholder);
        placeholder.removeAllViews();
        _vContent = initCustomContentView(_isInDarkMode);
        placeholder.addView(_vContent);
    }

    /**
     * Create any view in this method to be displayed
     * on the bottom line of the underlined view.<br />
     * <b>Don't keep a reference to this view to modify it later!
     * Always use {@link #getCustomContentView(Class)} to get a reference
     * to your custom view.</b>
     *
     * @return The view to be displayed above the bottom line
     */
    public abstract View initCustomContentView(boolean isInDarkMode);

    /**
     * Call this method to get a reference to the custom view
     * being displayed above the bottom line.
     * Specify the class of your custom content view in the parameters
     * to automatically receive the correct type of view.
     *
     * @param type The class of the custom content view
     * @param <T> The class of the custom content view
     *
     * @return A reference to the custom content view
     */
    public <T extends View> T getCustomContentView(Class<T> type) {
        return (T) _vContent;
    }

    /**
     * This method will be called after processing more general style attributes
     * (like hint and drawables) to let you specify and process custom attributes
     * when implementing {@link BOUnderlinedView}.
     *
     * @param attrs The attributes of the view as specified in XML
     */
    public abstract void processCustomAttrs(AttributeSet attrs);

    /**
     * Return any instance of {@link Serializable} storing the state
     * of the custom view. This method can be used to save the state
     * of the view (e.g. when the Activity is paused).
     *
     * @return Any Serializable storing the state of the custom content view
     */
    public abstract Serializable getState();

    /**
     * Set a formerly stored {@link Serializable} to be the state of
     * the custom content view again.
     *
     * @param serializedState The saved state
     */
    public abstract void setState(Serializable serializedState);

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

    /**
     * Detect if a tap by the user is a click or not (might e.g. also
     * be scrolling). This method takes the start and end coordinates
     * of the touch action to calculate a distance between pressing and
     * releasing and deciding if this is a click or not.
     *
     * @param startX The x position of the touch down action
     * @param endX The x position of the touch up action
     * @param startY The y position of the touch down action
     * @param endY The y position of the touch up action
     *
     * @return True if the touch actions belong to a click, false otherwise
     */
    private boolean isClick(float startX, float endX, float startY, float endY) {
        float differenceX = Math.abs(startX - endX);
        float differenceY = Math.abs(startY - endY);
        if(differenceX > 5 || differenceY > 5) {
            return false;
        }
        return true;
    }

    /**
     * Returns the hint for this view as specified via XML.
     *
     * @return The hint or an empty String if no hint is available
     */
    public String getHint() {
        return _hint;
    }

    public void highlight() {
        if(_isUnderlined) {
            _vUnderlineNormal.setVisibility(View.GONE);
            _vUnderlineHighlight.setVisibility(View.VISIBLE);
        }
    }

    public void unhighlight() {
        if(_isUnderlined) {
            _vUnderlineNormal.setVisibility(View.VISIBLE);
            _vUnderlineHighlight.setVisibility(View.GONE);
        }
    }

    /**
     * Set an {@link android.view.View.OnClickListener} to the right drawable
     * of the underlined view. This might be useful to e.g. provide further information
     * on how to fill a field.
     *
     * @param listener The listener
     */
    public void setRightDrawableOnClickListener(OnClickListener listener) {
        _ivDrawableRight.setOnClickListener(listener);
    }

    public boolean isInDarkMode() {
        return _isInDarkMode;
    }

}
