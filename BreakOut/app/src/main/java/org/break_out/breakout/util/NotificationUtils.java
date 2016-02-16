package org.break_out.breakout.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import org.break_out.breakout.R;

/**
 * This utility class provides methods for
 * simply notifying the user. There are e.g. methods
 * for showing a {@link Toast} message or dialogs.
 * <br /><br />
 * Created by Tino on 16.02.2016.
 */
public class NotificationUtils {

    /**
     * Shows a {@link Toast} message.
     *
     * @param context The context
     * @param text The text to be displayed
     * @param showLong If the Toast should be shown for a long time or not
     */
    public static void showToast(Context context, String text, boolean showLong) {
        Toast.makeText(context, text, showLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }

    /**
     * Displays a very simple dialog with a title, a text and
     * an ok button to provide information to the user.
     *
     * @param context The context
     * @param title The title
     * @param text The text to be displayed
     */
    public static void showInfoDialog(Context context, String title, String text) {
        new AlertDialog.Builder(context, R.style.AppTheme_AlertDialog)
                .setTitle(title)
                .setMessage(text)
                .setPositiveButton(android.R.string.yes, null)
                .show();
    }

}
