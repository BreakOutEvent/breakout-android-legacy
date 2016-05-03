package org.break_out.breakout.sync.model;

import com.orm.SugarRecord;

import java.io.File;

/**
 * Created by Maximilian Dühr on 04.05.2016.
 */
public class Media extends SugarRecord {
    private TYPE _mediaType;
    private int _storeID;
    private File _mediaFile;

    public Media() {
        //emtpy constructor for SugarRecord
    }

    public Media(TYPE type, File file) {
        _mediaType = type;
        _mediaFile = file;
    }


    enum TYPE {
        IMAGE,VIDEO,AUDIO;
    }
}
