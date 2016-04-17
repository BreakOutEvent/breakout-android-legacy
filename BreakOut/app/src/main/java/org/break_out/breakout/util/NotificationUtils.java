package org.break_out.breakout.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import org.break_out.breakout.R;

import it.sephiroth.android.library.tooltip.Tooltip;

/**
 * <p>
 * This utility class provides methods for
 * simply notifying the user. There are e.g. methods
 * for showing a {@link Toast} message, dialogs and tooltips.
 * </p>
 * Created by Tino on 16.02.2016.
 */
public class NotificationUtils {

    public interface PositiveNegativeListener {
        public void onPositiveClicked();
        public void onNegativeClicked();
    }

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
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    public static void showInfoDialog(Context context, int titleResourceId, int textResourceId) {
        showInfoDialog(context, context.getString(titleResourceId), context.getString(textResourceId));
    }

    public static void showPositiveNegativeDialog(Context context, String title, String text, String positiveButton, String negativeButton, final PositiveNegativeListener listener) {
        new AlertDialog.Builder(context, R.style.AppTheme_AlertDialog)
                .setTitle(title)
                .setMessage(text)
                .setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onPositiveClicked();
                    }
                })
                .setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onNegativeClicked();
                    }
                })
                .show();
    }

    public static void showPositiveNegativeDialog(Context context, int titleResourceId, int textResourceId, int negativeTextResourceId, int positiveTextResourceId, final PositiveNegativeListener listener) {
        showPositiveNegativeDialog(context, context.getString(titleResourceId), context.getString(textResourceId), context.getString(negativeTextResourceId), context.getString(positiveTextResourceId), listener);
    }

    /**
     * Displays a simple white tooltip above the given anchor view with the
     * text specified text.
     *
     * @param context The context
     * @param anchorView The tooltip will be shown above this view
     * @param text The text to be displayed as a tooltip
     */
    public static void showTooltip(Context context, View anchorView, String text) {
        Tooltip.make(context,
                new Tooltip.Builder((int)(Math.random()*100))
                        .anchor(anchorView, Tooltip.Gravity.TOP)
                        .closePolicy(new Tooltip.ClosePolicy()
                                .insidePolicy(true, false)
                                .outsidePolicy(true, false), 0)
                        .text(text)
                        .maxWidth(800)
                        .withArrow(true)
                        .floatingAnimation(Tooltip.AnimationBuilder.DEFAULT)
                        .withStyleId(R.style.AppTheme_Tooltip)
                        .withOverlay(false)
                        .build()
        ).show();
    }

    /**
     * Displays a simple white tooltip above the given anchor view with the
     * text specified by the given string resource ID.
     *
     * @param context The context
     * @param anchorView The tooltip will be shown above this view
     * @param textId The resource ID of the string to be displayed
     */
    public static void showTooltip(Context context, View anchorView, int textId) {
        showTooltip(context, anchorView, context.getString(textId));
    }

}
