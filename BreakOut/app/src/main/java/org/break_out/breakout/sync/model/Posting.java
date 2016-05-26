package org.break_out.breakout.sync.model;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import org.break_out.breakout.BOLocation;
import org.break_out.breakout.manager.MediaManager;
import org.break_out.breakout.model.BOMedia;
import org.json.JSONArray;
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
    private String _mediaId = "";
    private String _remoteID = "";
    private String _fileURL="";
    private String _teamName = "";
    private String _userName = "";
    private BOMedia _linkedMedia;
    public Posting() {
        _createdTimestamp = System.currentTimeMillis()/1000;
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

    public Posting(String teamName,String message,@Nullable BOLocation location,@Nullable BOMedia media) {
        this();
        _hasMedia = media!=null;
        if(_hasMedia) {
            _linkedMedia = media;
        }
        if(location != null) {
            _location = location;
        }
        _text = message;
        _teamName = teamName;
    }

    //getters
    public String getText() {
        return _text;
    }

    public String getTeamName() { return _teamName;}

    public long getCreatedTimestamp() {
        return _createdTimestamp;
    }

    @Nullable
    public File getMediaFile() { return _linkedMedia == null ? null : _linkedMedia.getFile();}

    public String getMediaId() {return _mediaId;}

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

    public String getusername() { return _userName; }

    //setters
    public void setText(String text) {
        _text = text;
    }

    public void setRemoteID(String id) { _remoteID = id;}

    public void setMediaId(String id) { _mediaId = id;}

    public void setUploadCredentials(String id,String token) {
        setMediaId(id);
        _uploadToken = token;
    }

    public void setLinkedMedia(BOMedia media) {
        _linkedMedia = media;
    }

    private void setTimestamp(long timestamp) {
        _createdTimestamp = timestamp;
    }

    public void setUsername(String username) {
        _userName = username;
    }

    public void setTeamname(String teamname) { _teamName = teamname;}


    public static Posting fromJSON(Context c, JSONObject object) throws JSONException {
        String id = object.getString("id");
        String text = object.getString("text");
        String timestamp = object.getString("date");
        JSONObject locationObject = object.getJSONObject("postingLocation");
        long latitude = locationObject.getLong("latitude");
        long longitude = locationObject.getLong("longitude");
        Log.d(TAG,"latitude: "+latitude+" longitude: "+longitude);
        String teamName = locationObject.getString("team");
        BOLocation location = new BOLocation();
        if(!(latitude == 0 || longitude == 0)) {
            location.setLatitude(latitude);
            location.setLongitude(longitude);
            Log.d(TAG,"location set lat long");
        } else {
            location = null;
        }
        JSONArray mediaArray;
        BOMedia correlatingMedia = null;
        if((mediaArray= object.getJSONArray("media")).length()>0) {
            int mediaID = mediaArray.getJSONObject(0).getInt("id");
            if(MediaManager.getMediaByID(mediaID) == null) {
                correlatingMedia = BOMedia.fromJSON(c, mediaArray);
            } else {
                correlatingMedia = MediaManager.getMediaByID(mediaID);
            }
        }

        JSONObject userObject = object.getJSONObject("user");
        JSONObject userDataObject = userObject.getJSONObject("participant");
        String teamname = userDataObject.getString("teamName");


        Posting returnPosting = new Posting(teamName,text,location,null);
        returnPosting.setRemoteID(id);
        returnPosting.setTimestamp(Long.parseLong(timestamp));
        returnPosting.setTeamname(teamname);

        if(correlatingMedia != null) {
            correlatingMedia.setPosting(returnPosting);
            returnPosting.setLinkedMedia(correlatingMedia);
            Log.d(TAG,"media is set!");
        }
        return returnPosting;
    }

}
