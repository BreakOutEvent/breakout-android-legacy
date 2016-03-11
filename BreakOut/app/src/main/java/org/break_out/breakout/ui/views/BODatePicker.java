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

import java.io.Serializable;
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

    public static class BODatePickerState implements Serializable {

        /**
         * The selected date.
         */
        public Calendar date = null;

    }

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
    public View initCustomContentView() {
        TextView dateView = new TextView(getContext());
        dateView.setOnClickListener(this);
        dateView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

        refreshTextView(dateView);

        return dateView;
    }

    @Override
    public void processCustomAttrs(AttributeSet attrs) {
        // No custom attributes needed for this view
    }

    @Override
    public Serializable getState() {
        BODatePickerState state = new BODatePickerState();
        state.date = _date;

        return state;
    }

    @Override
    public void setState(Serializable serializedState) {
        if(serializedState == null || !(serializedState instanceof BODatePickerState)) {
            Log.e(TAG, "Could not load state from serializable (null or wrong state type).");
            return;
        }

        BODatePickerState state = (BODatePickerState) serializedState;

        _date = state.date;
        refreshTextView(getCustomContentView(TextView.class));
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

        DatePickerDialog dpd;
        if(_date == null) {
            dpd = DatePickerDialog.newInstance(this, 1990, Calendar.JANUARY, 1);
        } else {
            dpd = DatePickerDialog.newInstance(this, _date.get(Calendar.YEAR), _date.get(Calendar.MONTH), _date.get(Calendar.DAY_OF_MONTH));
        }
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
        _date = date;

        refreshTextView(getCustomContentView(TextView.class));
    }

    private void refreshTextView(TextView dateView) {
        DateFormat format = DateFormat.getDateInstance(DateFormat.LONG);

        if(_date != null) {
            dateView.setText(format.format(_date.getTime()));
            dateView.setTextColor(ContextCompat.getColor(getContext(), R.color.white_transparent_80));
        } else {
            dateView.setText(getHint());
            dateView.setTextColor(ContextCompat.getColor(getContext(), R.color.white_transparent_50));
        }
    }
}
