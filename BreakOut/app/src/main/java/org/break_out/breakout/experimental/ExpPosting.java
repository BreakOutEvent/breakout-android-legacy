package org.break_out.breakout.experimental;

import com.orm.SugarRecord;

/**
 * Created by Tino on 20.02.2016.
 */
public class ExpPosting extends SugarRecord {

    private String _text = null;

    public void setText(String text) {
        _text = text;
    }

    public String getText() {
        return _text;
    }

    @Override
    public String toString() {
        return "[" + getId() + "] " + _text;
    }
}
