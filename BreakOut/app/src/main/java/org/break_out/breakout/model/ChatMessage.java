package org.break_out.breakout.model;

import com.orm.SugarRecord;

/**
 * Created by Maximilian Duehr on 27.05.2016.
 */
public class ChatMessage extends SugarRecord {
    private static final String TAG = "ChatMessage";
    private int _remoteId;
    private long _timestamp;
    private int _userId;
    private String _message;

    public ChatMessage() {
        //empty constructor for SugarRecord
    }
}
