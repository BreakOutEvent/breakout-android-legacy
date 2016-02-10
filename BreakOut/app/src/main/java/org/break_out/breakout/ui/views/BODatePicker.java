package org.break_out.breakout.ui.views;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.break_out.breakout.R;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Tino on 08.02.2016.
 */
public class BODatePicker extends BOUnderlinedView implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private static final String TAG = "BODatePicker";
    private static final String TAG_DIALOG = "BODatePickerDialog";

    private Calendar _date = null;

    public BODatePicker(Context context) {
        super(context);
    }

    public BODatePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BODatePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public View initCustomContent() {
        TextView dateView = new TextView(getContext());
        dateView.setOnClickListener(this);
        dateView.setTextColor(ContextCompat.getColor(getContext(), R.color.white_transparent_50));
        dateView.setText(getHint());
        dateView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

        return dateView;
    }

    @Override
    public void processCustomAttrs(AttributeSet attrs) {
        // No custom attributes needed for this view
    }

    public Calendar getSelectedDate() {
        return _date;
    }

    @Override
    public void onClick(View v) {
        if(!(getContext() instanceof Activity)) {
            Log.e(TAG, "The context must be an activity.");
            return;
        }

        Activity activity = (Activity) getContext();

        DatePickerDialog dpd = DatePickerDialog.newInstance(this, 1990, Calendar.JANUARY, 1);
        dpd.showYearPickerFirst(true);
        dpd.setThemeDark(false);
        dpd.vibrate(false);
        dpd.dismissOnPause(true);
        dpd.show(activity.getFragmentManager(), TAG_DIALOG);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Calendar date = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        date.set(year, monthOfYear, dayOfMonth);

        DateFormat format = DateFormat.getDateInstance(DateFormat.LONG);

        TextView dateView = getContentView(TextView.class);
        dateView.setText(format.format(date.getTime()));
        dateView.setTextColor(ContextCompat.getColor(getContext(), R.color.white_transparent_80));
    }
}
