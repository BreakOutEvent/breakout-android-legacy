package org.break_out.breakout.model;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import org.break_out.breakout.manager.MediaManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Maximilian Dühr on 04.05.2016.
 */
public class BOMedia extends SugarRecord {
    private static final String TAG = "BOMedia";
    private TYPE _mediaType;
    private String _url;
    private int _remoteID;
    private String _fileURI;
    private SIZE _size;
    private int _sizeFromServer;

    @Ignore
    private MediaChangedListener _listener = null;

    @Ignore
    private File _mediaFile;

    private SAVESTATE _mediaSaveState;
    private Posting _linkedPosting;
    private boolean _isDownloaded = false;

    @Ignore
    private static final String JSON_ID = "id";
    @Ignore
    private static final String JSON_TYPE = "type";
    @Ignore
    private static final String JSONARR_SIZES = "sizes";
    @Ignore
    private static final String JSON_URL = "url";
    @Ignore
    private static final String JSON_WIDTH = "width";
    @Ignore
    private static final String JSON_HEIGHT = "height";
    @Ignore
    private static final String JSON_SIZE = "size";

    public BOMedia() {
        //emtpy constructor for SugarRecord
    }

    public BOMedia(String url, TYPE type, File file) {
        _url = url;
        _mediaType = type;
        _mediaFile = file;
        _fileURI = Uri.fromFile(_mediaFile).toString();
    }

    public BOMedia(String url, TYPE type, File file, Posting linkedPosting) {
        this(url, type, file);
        _linkedPosting = linkedPosting;
    }

    public BOMedia(String url, TYPE type, File file, Posting linkedPosting, boolean isDownloaded) {
        this(url, type, file, linkedPosting);
        _isDownloaded = isDownloaded;
    }

    public SIZE getSize() {
        return _size;
    }

    @Override
    public boolean delete() {
        if(_mediaFile != null) {
            _mediaFile.delete();
        }
        return super.delete();
    }

    public int getRemoteID() {
        return _remoteID;
    }

    public void registerMediaChangedListener(MediaChangedListener listener) {
        _listener = listener;
        save();
    }

    public MediaChangedListener getListener() {
        return _listener;
    }


    public TYPE getType() {
        return _mediaType;
    }

    public String getUrl() {
        return _url;
    }

    public File getFile() {
        return new File(getFileUri().getPath());
    }

    public Uri getFileUri() {
        return Uri.parse(_fileURI);
    }

    public SAVESTATE getSavestate() {
        return _mediaSaveState;
    }


    public boolean isDownloaded() {
        return _isDownloaded;
    }

    public void setSaveState(SAVESTATE state) {
        _mediaSaveState = state;
        callListener();
        this.save();
    }

    public boolean setURL(String url) {
        try {
            URL objectURL = new URL(url);
            _url = url;
            this.save();
            callListener();
            return true;
        } catch(MalformedURLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setIsDownloaded(boolean state) {
        _isDownloaded = state;
        callListener();
        this.save();
    }

    public void setFile(File file) {
        _mediaFile = file;
        _fileURI = Uri.fromFile(_mediaFile).toString();
        callListener();
        this.save();
    }

    public void setSize(SIZE size, int remoteSize) {
        _size = size;
        _sizeFromServer = remoteSize;
    }

    public enum TYPE {
        IMAGE, VIDEO, AUDIO;
    }

    public void setPosting(Posting posting) {
        _linkedPosting = posting;
        callListener();
        this.save();
    }

    public void setRemoteID(int id) {
        _remoteID = id;
        callListener();
        this.save();
    }

    public enum SAVESTATE {
        TEMP, SAVED, DIRTY;
    }

    private void callListener() {
        if(_listener != null) {
            _listener.onMediaChanged();
        }
    }

    /**
     * generates BOMedia object from JSON array
     *
     * @param c
     * @param mediaArray json array representing media
     * @return
     * @throws JSONException
     */
    public static BOMedia fromJSON(Context c, JSONArray mediaArray, SIZE size) throws JSONException {
        BOMedia media = null;
        MediaManager manager = MediaManager.getInstance();
        if(!(mediaArray == null)) {
            //get the media array
            for(int i = 0; i < mediaArray.length(); i++) {
                JSONObject curObj = mediaArray.getJSONObject(i);

                //fetch data for different image sizes
                //TODO: fetch best size for current internet connection
                JSONArray sizesArray = curObj.getJSONArray(JSONARR_SIZES);

                for(int j = 0; j < sizesArray.length(); j++) {
                    JSONObject currentSize = sizesArray.getJSONObject(j);
                    int id = currentSize.getInt(JSON_ID);
                    ArrayList<BOMedia> mediaList;
                    if(sizesArray.getJSONObject(j).getInt(JSON_SIZE) >= 10000) {
                        String url = currentSize.getString(JSON_URL);
                        if(MediaManager.getMediaByID(id) == null) {
                            media = manager.createExternalMedia(c, TYPE.IMAGE);
                            media.setRemoteID(id);
                            media.setURL(url);
                        } else {
                            return MediaManager.getMediaByID(id);
                        }
                    }
                }
            }
            return media;
        } else {
            throw new JSONException("Malformed Media");
        }
    }

    public static BOMedia sizedMediaFromJSON(Context c, JSONObject mediaObject, SIZE maxSize) throws JSONException {
        BOMedia media = null;
        MediaManager manager = MediaManager.getInstance();
        if(mediaObject != null) {
            JSONArray sizesArray = mediaObject.getJSONArray(JSONARR_SIZES);
            switch(maxSize) {
                case SMALL:
                    Log.d(TAG,"small sized");
                    for (int j = 0; j < sizesArray.length(); j++) {
                        JSONObject currentSize = sizesArray.getJSONObject(j);
                        int id = currentSize.getInt(JSON_ID);
                        if (sizesArray.getJSONObject(j).getInt(JSON_WIDTH) < 150) {
                            String url = currentSize.getString(JSON_URL);
                            if (MediaManager.getMediaByID(id) == null) {
                                media = manager.createExternalMedia(c, TYPE.IMAGE);
                                media.setRemoteID(id);
                                media.setURL(url);
                                return media;
                            } else {
                                return MediaManager.getMediaByID(id);
                            }
                        }
                    }
                    break;
                case MEDIUM:
                    Log.d(TAG,"medium sized");
                    for (int j = 0; j < sizesArray.length(); j++) {
                        JSONObject currentSize = sizesArray.getJSONObject(j);
                        int id = currentSize.getInt(JSON_ID);
                        if (sizesArray.getJSONObject(j).getInt(JSON_WIDTH) >= 150 || sizesArray.getJSONObject(j).getInt(JSON_WIDTH) <= 400) {
                            String url = currentSize.getString(JSON_URL);
                            if (MediaManager.getMediaByID(id) == null) {
                                media = manager.createExternalMedia(c, TYPE.IMAGE);
                                media.setRemoteID(id);
                                media.setURL(url);
                                return media;
                            } else {
                                return MediaManager.getMediaByID(id);
                            }
                        }
                    }
                    return sizedMediaFromJSON(c, mediaObject, SIZE.SMALL);

                case LARGE:
                    for (int j = 0; j < sizesArray.length(); j++) {
                        JSONObject currentSize = sizesArray.getJSONObject(j);
                        int id = currentSize.getInt(JSON_ID);
                        if (sizesArray.getJSONObject(j).getInt(JSON_WIDTH) >= 400 || sizesArray.getJSONObject(j).getInt(JSON_WIDTH) <= 1300) {
                            String url = currentSize.getString(JSON_URL);
                            if (MediaManager.getMediaByID(id) == null) {
                                media = manager.createExternalMedia(c, TYPE.IMAGE);
                                media.setRemoteID(id);
                                media.setURL(url);
                                return media;
                            } else {
                                return MediaManager.getMediaByID(id);
                            }
                        }
                    }
                    return sizedMediaFromJSON(c, mediaObject, SIZE.MEDIUM);
            }
            return media;
        } else {
            throw new JSONException("Malformed Media");
        }
    }

    public interface MediaChangedListener {
        void onMediaChanged();
    }

    public enum SIZE {
        SMALL, MEDIUM, LARGE;
    }


}