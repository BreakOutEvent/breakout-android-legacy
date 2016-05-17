package org.break_out.breakout.sync.model;

import android.widget.Toast;

import com.orm.SugarRecord;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Maximilian Dühr on 04.05.2016.
 */
public class BOMedia extends SugarRecord {
    private TYPE _mediaType;
    private String _url;
    private int _storeID;
    private File _mediaFile;
    private SAVESTATE _mediaSaveState;

    public BOMedia() {
        //emtpy constructor for SugarRecord
    }

    public BOMedia(String url,TYPE type, File file) {
        _url = url;
        _mediaType = type;
        _mediaFile = file;
    }

    public TYPE getType() { return _mediaType;}

    public String getUrl() { return _url;}

    public File getFile() { return _mediaFile;}

    public SAVESTATE getSavestate() { return _mediaSaveState;}

    public void setSaveState(SAVESTATE state) {
        _mediaSaveState = state;
    }

    public boolean setURL(String url) {
        try{
            URL objectURL = new URL (url);
            _url = url;
            return true;
        } catch(MalformedURLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setFile(File file) {
        _mediaFile = file;
    }

    public enum TYPE {
        IMAGE,VIDEO,AUDIO;
    }

    public enum SAVESTATE {
        TEMP,SAVED,DIRTY;
    }
}
