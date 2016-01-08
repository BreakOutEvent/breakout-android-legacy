package org.break_out.breakout.sync.model;

/**
 * Created by Tino on 14.12.2015.
 */

public class Posting extends SyncEntity {

    private String _text;

    // SugarORM needs an empty constructor
    public Posting() {

    }

    public void setText(String text) {
        _text = text;
    }

    public String getText() {
        return _text;
    }

    @Override
    public boolean uploadToServer() {
        // Currently randomly returns success or failure
        return (Math.random() > 0.5);
    }

    @Override
    public boolean updateOnServer() {
        // TODO
        return false;
    }

    @Override
    public boolean deleteOnServer() {
        // Currently randomly returns success or failure
        return (Math.random() > 0.5);
    }

    @Override
    public String toString() {
        return "Posting{" +
                "text='" + _text + '\'' +
                "state='" + getState() + '\'' +
                "}";
    }

}
