package org.break_out.breakout.ui.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;

import org.break_out.breakout.R;

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

    private ImageView _ivHeader = null;
    private FrameLayout _flPlaceholderContent = null;

    private ImageView _ivBackground = null;
    private View _vBackgroundBlack = null;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(R.layout.activity_background_image);

        _ivHeader = (ImageView) findViewById(R.id.placeholder_header);
        _flPlaceholderContent = (FrameLayout) findViewById(R.id.placeholder_content);

        _ivBackground = (ImageView) findViewById(R.id.background_image);
        _vBackgroundBlack = findViewById(R.id.background_black);

        View vOuterLayout = findViewById(R.id.outer_layout);
        vOuterLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                updateBackgroundPosition();
            }
        });

        ScrollView svContent = (ScrollView) findViewById(R.id.scrollview);
        svContent.getViewTreeObserver().addOnScrollChangedListener(this);

        setContentLayout(layoutResID);
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

    @Override
    public void onScrollChanged() {
        updateBackgroundPosition();
    }

    /**
     * This method will update the y position of the transparent black
     * background view to align with the top of the content view.
     */
    private void updateBackgroundPosition() {
        int[] loc = new int[2];
        _flPlaceholderContent.getLocationInWindow(loc);

        _vBackgroundBlack.setY(loc[1]);
    }
}
