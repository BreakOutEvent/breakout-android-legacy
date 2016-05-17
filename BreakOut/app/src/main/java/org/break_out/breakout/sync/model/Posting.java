package org.break_out.breakout.sync.model;

import android.support.annotation.Nullable;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import org.break_out.breakout.BOLocation;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import okhttp3.MediaType;

/**
 * Created by Tino on 14.12.2015.
 */

public class Posting extends SugarRecord {

    @Ignore
    private static final String TAG = "Posting";

    @Ignore
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    /**
     * Stores the timestamp of creation <b>in seconds</b>.
     */
    private Long id;
    private long _createdTimestamp = 0L;
    private BOLocation _location = null;
    private String _text = "";
    private boolean _hasMedia = false;
    private String _uploadToken = "";
    private String _remoteID = "";
    private String _fileURL="";
    private BOMedia _linkedMedia;
    public Posting() {
        // Timestamp has to be in seconds
        _createdTimestamp = System.currentTimeMillis()/60;
    }

    public Posting(String message,@Nullable BOLocation location,@Nullable BOMedia media) {
        this();
        _hasMedia = media!=null;
        if(_hasMedia) {
            _linkedMedia = media;
        }
        if(location != null) {
            _location = location;
        }
        _text = message;
    }

    public void setText(String text) {
        _text = text;
    }

    public String getText() {
        return _text;
    }

    public long getCreatedTimestamp() {
        return _createdTimestamp;
    }

    @Nullable
    public File getMediaFile() { return _linkedMedia == null ? null : _linkedMedia.getFile();}

    @Nullable
    public BOLocation getLocation() { return _location;}

    public void setLocation(BOLocation location) {
        _location = location;
    }

    public boolean hasMedia() {
        return _linkedMedia!=null;
    }

    public boolean hasUploadCredentials() {
        return (!_remoteID.isEmpty()&&!_uploadToken.isEmpty());
    }

    public String getUploadToken() {
        return _uploadToken;
    }

    public String getRemoteID() {
        return _remoteID;
    }

    public Long getID(){return id;}

    public BOMedia getMedia() {
        return _linkedMedia;
    }

    public void setRemoteID(String id) { _remoteID = id;}

    public void setUploadCredentials(String id,String token) {
        _remoteID = id;
        _uploadToken = token;
    }

    public void setLinkedMedia(BOMedia media) {
        _linkedMedia = media;
    }

    private void setTimestamp(long timestamp) {
        _createdTimestamp = timestamp;
    }


    public static Posting fromJSON(JSONObject object) throws JSONException {
        String id = object.getString("id");
        String text = object.getString("text");
        String timestamp = object.getString("date");
        long latitude = !object.getString("postingLocation").equals("null") ? object.getJSONObject("postingLocation").getLong("latitude") : 0;
        long longitude = !object.getString("postingLocation").equals("null") ? object.getJSONObject("postingLocation").getLong("longitude") : 0;
        BOLocation location = new BOLocation();
        if(!(latitude == 0 || longitude == 0)) {
            location.setLongitude(latitude);
            location.setLongitude(longitude);
        } else {
            location = null;
        }

        Posting returnPosting = new Posting(text,location,null);
        returnPosting.setRemoteID(id);
        returnPosting.setTimestamp(Long.parseLong(timestamp));
        return returnPosting;
    }

}
