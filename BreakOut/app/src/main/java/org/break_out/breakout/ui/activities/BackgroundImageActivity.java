package org.break_out.breakout.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.break_out.breakout.R;
import org.break_out.breakout.util.DimenUtils;

/**
 * This activity extends {@link BOActivity} and will display its
 * layout responsive to the screen width on a full screen background image.
 *
 * <ul>
 * <li>For devices with a screen width smaller than 400dp, the layout
 * will be displayed with the full width of the screen.</li>
 * <li>For devices with a screen width bigger than 400dp, the layout will
 * only be displayed with a width of 400dp and centered on the screen.</li>
 * </ul>
 *
 * The layout will also be wrapped in a scroll view to ensure a good user
 * experience even if the screen height is too small for the layout.
 *
 * <br /><br />
 *
 * Smaller than 400dp:
 * <pre>
 *     +-----------+
 *     |           |
 *     |-----------|   ^
 *     |Your image |   |
 *     |-----------|   |
 *     |           |   |
 *     |-----------|   | scrollable
 *     |Your layout|   |
 *     |           |   |
 *     |           |   v
 *     +-----------+
 * </pre>
 *
 * Bigger than 400dp:
 * <pre>
 *     +---------------------------------+
 *     |                                 |
 *     |          +-----------+          |   ^
 *     |          |Your image |          |   |
 *     |----------+-----------+----------|   |
 *     |          |Your layout|          |   | scrollable
 *     |          |           |          |   |
 *     |          |           |          |   |
 *     |          +-----------+          |   v
 *     |                                 |
 *     +---------------------------------+
 * </pre>
 *
 * Set your layout file using {@link #setContentView(int)} as always.
 *
 * <br />
 *
 * To set a custom header image use {@link #setHeaderImage(int)}.
 *
 * <br /><br />
 *
 * Created by Tino on 02.02.2016.
 */
public class BackgroundImageActivity extends BOActivity implements ViewTreeObserver.OnScrollChangedListener{

    private static final String TAG = "BackgroundImageActivity";

    private static final boolean IS_STATUSBAR_TRANSPARENT = false;

    private ImageView _ivHeader = null;
    private FrameLayout _flPlaceholderContent = null;
    private FrameLayout _flPlaceholderBottomSheet = null;

    private ImageView _ivBackground = null;
    private View _vBackgroundBlack = null;

    private View _vCloseButton = null;

    private BottomSheetBehavior _bottomSheetBehavior = null;

    private int _statusBarHeight = 0;
    private View _vTopSpace = null;
    private View _vBottomSpace = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _statusBarHeight = DimenUtils.getStatusBarHeight(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(R.layout.activity_background_image);

        _ivHeader = (ImageView) findViewById(R.id.placeholder_header);
        _flPlaceholderContent = (FrameLayout) findViewById(R.id.placeholder_content);
        _flPlaceholderBottomSheet = (FrameLayout) findViewById(R.id.placeholder_bottom_sheet);

        _ivBackground = (ImageView) findViewById(R.id.background_image);
        _vBackgroundBlack = findViewById(R.id.background_black);

        View vOuterView = findViewById(R.id.outer_layout);
        vOuterView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                updateBackgroundPosition();
                updateCloseButtonPosition();
            }
        });

        // ScrollView and listener for scroll changes
        NestedScrollView svContent = (NestedScrollView) findViewById(R.id.scrollview_content);
        svContent.getViewTreeObserver().addOnScrollChangedListener(this);

        // Init space views
        // Keeps content below close button
        _vTopSpace = findViewById(R.id.v_top_space);
        // Keeps bottom sheet from hiding the content
        _vBottomSpace = findViewById(R.id.v_bottom_space);

        // Set up close button
        _vCloseButton = findViewById(R.id.close);
        _vCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        _vCloseButton.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                _vCloseButton.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                _vTopSpace.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, _vCloseButton.getHeight()));
            }
        });

        setContentLayout(layoutResID);
    }

    /**
     * Set a custom layout to be displayed as a bottom sheet.
     *
     * @param layoutResID The layout to be displayed as a bottom sheet
     */
    public void setBottomSheetView(int layoutResID, int peekHeightDp) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = layoutInflater.inflate(layoutResID, null);

        _flPlaceholderBottomSheet.removeAllViews();
        _flPlaceholderBottomSheet.addView(v);

        View vBottomSheet = findViewById(R.id.bottom_sheet);

        // Calculate peek height in px
        int peekHeightPx = (int) DimenUtils.dpToPx(peekHeightDp, this);

        // Set peek height to bottom sheet behavior
        _bottomSheetBehavior = BottomSheetBehavior.from(vBottomSheet);
        _bottomSheetBehavior.setPeekHeight(peekHeightPx);

        // Set bottom spacing to keep bottom sheet from hiding the content
        _vBottomSpace.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, peekHeightPx));

        // Make bottom sheet visible
        vBottomSheet.setVisibility(View.VISIBLE);
    }

    /**
     * Returns the bottom sheet behavior of the bottom sheet in
     * this Activity. This can be used to register listeners etc.
     *
     * @return The bottom sheet behavior
     */
    public BottomSheetBehavior getBottomSheetBehavior() {
        return _bottomSheetBehavior;
    }

    /**
     * Set a custom layout as the main content of the activity.
     *
     * @param layoutResID The layout to replace the content placeholder
     */
    private void setContentLayout(int layoutResID) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = layoutInflater.inflate(layoutResID, null);

        _flPlaceholderContent.removeAllViews();
        _flPlaceholderContent.addView(v);
    }

    /**
     * Sets a custom header image for this activity.
     *
     * @param imageResID The image resource id (e.g. R.drawable.image)
     */
    public void setHeaderImage(int imageResID) {
        _ivHeader.setImageResource(imageResID);
    }

    /**
     * This method allows you to set a custom {@link android.view.View.OnClickListener} to
     * this activity's header image view.
     *
     * @param listener The listener
     */
    public void setHeaderImageOnClickListener(View.OnClickListener listener) {
        _ivHeader.setOnClickListener(listener);
    }

    /**
     * Set a custom background image to this background image
     * activity. By default there will be a blurry background image
     * in the style of BreakOut.
     *
     * @param imageResID The resource ID of the image
     */
    public void setBackgroundImage(int imageResID) {
        _ivBackground.setImageResource(imageResID);
    }

    public void setCloseButtonVisible(boolean visible) {
        _vCloseButton.setVisibility(visible ? View.VISIBLE : View.GONE);
        _vTopSpace.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (_vCloseButton.getVisibility() == View.VISIBLE ? _vCloseButton.getHeight() : 0)));
    }

    @Override
    public void onScrollChanged() {
        updateBackgroundPosition();
        updateCloseButtonPosition();
    }

    /**
     * This method will update the y position of the transparent black
     * background view to align with the top of the content view.
     */
    private void updateBackgroundPosition() {
        int[] loc = new int[2];
        _flPlaceholderContent.getLocationInWindow(loc);

        _vBackgroundBlack.setY(loc[1] - (IS_STATUSBAR_TRANSPARENT ? 0 : _statusBarHeight));
    }

    /**
     * This method will update the y position of the close button in the top
     * left corner to align with the top of the content view or stay at top if
     * the content is smaller than the display.
     */
    private void updateCloseButtonPosition() {
        int[] loc = new int[2];
        _vTopSpace.getLocationInWindow(loc);

        int scrolledClosePos = ((loc[1] - (IS_STATUSBAR_TRANSPARENT ? 0 : _statusBarHeight)) + _vTopSpace.getHeight()) - _vCloseButton.getHeight();
        _vCloseButton.setY(scrolledClosePos <= (IS_STATUSBAR_TRANSPARENT ? _statusBarHeight : 0) ? scrolledClosePos : (IS_STATUSBAR_TRANSPARENT ? _statusBarHeight : 0));
    }
}
