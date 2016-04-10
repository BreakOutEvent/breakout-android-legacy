package org.break_out.breakout.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

/**
 * Created by Tino on 05.04.2016.
 */
public class DimenUtils {

    public static float dpToPx(int dp, Context context) {
        Resources r = context.getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        Resources r = context.getResources();

        int resourceId = r.getIdentifier("status_bar_height", "dimen", "android");
        if(resourceId > 0) {
            result = r.getDimensionPixelSize(resourceId);
        }

        return result;
    }

}
