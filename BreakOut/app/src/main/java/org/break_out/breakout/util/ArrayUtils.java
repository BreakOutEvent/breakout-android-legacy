package org.break_out.breakout.util;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Created by Tino on 16.04.2016.
 */
public class ArrayUtils {

    public static @NonNull String getStringAtPosition(Context context, int arrayResourceId, int position) {
        if(position < 0) {
            return "";
        }

        String[] arr = context.getResources().getStringArray(arrayResourceId);

        if(position >= arr.length) {
            return "";
        }

        return arr[position];
    }

    public static int getPositionOfString(Context context, int arrayResourceId, String value) {
        String[] arr = context.getResources().getStringArray(arrayResourceId);

        for(int i = 0; i < arr.length; i++) {
            if(arr[i].equals(value)) {
                return i;
            }
        }

        return -1;
    }

}
