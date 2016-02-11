package org.break_out.breakout.model;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Class used to store and restore the state of a {@link org.break_out.breakout.ui.views.BODatePicker}.
 *
 * Created by Tino on 10.02.2016.
 */
public class BODatePickerState implements Serializable {

    /**
     * The selected date.
     */
    public Calendar date = null;

}
