package org.break_out.breakout.ui.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

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
 *     |Your layout|   |
 *     |           |   | scrollable
 *     |           |   |
 *     |-----------|   v
 *     |           |
 *     +-----------+
 * </pre>
 *
 * Bigger than 400dp:
 * <pre>
 *     +----------------------------------+
 *     |                                  |
 *     |          +-----------+           |   ^
 *     |          |Your layout|           |   |
 *     |          |           |           |   | scrollable
 *     |          |           |           |   |
 *     |          +-----------+           |   v
 *     |                                  |
 *     +----------------------------------+
 * </pre>
 *
 * <br /><br />
 *
 * Created by Tino on 02.02.2016.
 */
public class BackgroundImageActivity extends BOActivity {

    private FrameLayout _flPlaceHolder = null;
    private ImageView _ivBackground = null;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(R.layout.activity_background_image);

        _flPlaceHolder = (FrameLayout) findViewById(R.id.placeholder);
        _ivBackground = (ImageView) findViewById(R.id.background_image);

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = layoutInflater.inflate(layoutResID, null);

        _flPlaceHolder.removeAllViews();
        _flPlaceHolder.addView(v);
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
}
