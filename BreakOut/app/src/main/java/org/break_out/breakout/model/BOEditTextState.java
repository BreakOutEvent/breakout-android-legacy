package org.break_out.breakout.model;

import java.io.Serializable;

/**
 * Class used to store and restore the state of a {@link org.break_out.breakout.ui.views.BOEditText}.
 *
 * Created by Tino on 10.02.2016.
 */
public class BOEditTextState implements Serializable {

    /**
     * The text entered by the user.
     */
    public String text = "";

}
