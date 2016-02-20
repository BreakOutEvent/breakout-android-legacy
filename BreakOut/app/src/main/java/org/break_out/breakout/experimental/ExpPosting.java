package org.break_out.breakout.experimental;

import com.orm.SugarRecord;

/**
 * Created by Tino on 20.02.2016.
 */
public class ExpPosting extends SugarRecord {

    @Override
    public String toString() {
        return "[" + getId() + "]";
    }
}
