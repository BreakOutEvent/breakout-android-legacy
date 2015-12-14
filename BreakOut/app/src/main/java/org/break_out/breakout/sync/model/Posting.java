package org.break_out.breakout.sync.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Tino on 14.12.2015.
 */

@DatabaseTable(tableName = "postings")
public class Posting {

    public static final String COLUMN_TEXT = "text";


    @DatabaseField(columnName = COLUMN_TEXT)
    private String _text;

    public void setText(String text) {
        _text = text;
    }

    @Override
    public String toString() {
        return "Posting{" +
                "text='" + _text + '\'' +
                '}';
    }
}
