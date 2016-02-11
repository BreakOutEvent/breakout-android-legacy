package org.break_out.breakout.model;

import java.io.Serializable;

/**
 * Class used to store and restore the state of a {@link org.break_out.breakout.ui.views.BOSpinner}.
 *
 * Created by Tino on 10.02.2016.
 */
public class BOSpinnerState implements Serializable {

    /**
     * The selected index on the spinner.
     * This value is -1 if no item has been selected
     * yet.
     */
    public int selectedIndex = -1;

}
