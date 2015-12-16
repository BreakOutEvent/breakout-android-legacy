package org.break_out.breakout.sync.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by Tino on 14.12.2015.
 */

@DatabaseTable(tableName = "postings")
public class Posting implements Serializable {

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TEXT = "text";
    public static final String COLUMN_SENT = "sent";

    @DatabaseField(columnName = COLUMN_ID, generatedId = true)
    private int _localId;

    @DatabaseField(columnName = COLUMN_TEXT)
    private String _text;

    @DatabaseField(columnName = COLUMN_SENT)
    private boolean _sent = false;


    public int getLocalId() {
        return _localId;
    }

    public void setText(String text) {
        _text = text;
    }

    public String getText() {
        return _text;
    }

    public void setSent(boolean sent) {
        _sent = sent;
    }

    public boolean isSent() {
        return _sent;
    }

    @Override
    public String toString() {
        return "Posting{" +
                "text='" + _text + '\'' +
                '}';
    }
}
