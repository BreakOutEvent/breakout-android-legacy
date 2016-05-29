package org.break_out.breakout.sync.model;

import android.content.Context;
import android.support.annotation.Nullable;

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
    private int _userId = -1;
    private long _createdTimestamp = 0L;
    private BOLocation _location = null;
    private String _text = "";
    private boolean _hasMedia = false;
    private String _uploadToken = "";
    private String _mediaId = "";
    private int _profileImageId = -1;
    private String _remoteID = "";
    private String _fileURL = "";
    private String _teamName = "";
    private String _userName = "";
    private String _profilePicturePath;
    private BOMedia _linkedMedia;
    private BOMedia _profileImage;

    //challenge details if any
    private int _provenChallengeId = -1;
    private String _challengeDescription = "";

    //LocationData
    private String _locationName = "";

    public Posting() {
        _createdTimestamp = System.currentTimeMillis() / 1000;
    }

    public Posting(String message, @Nullable BOLocation location, @Nullable BOMedia media) {
        this();
        _hasMedia = media != null;
        if(_hasMedia) {
            _linkedMedia = media;
        }
        if(location != null) {
            _location = location;
        }
        _text = message;
    }

    public Posting(String teamName, String message, @Nullable BOLocation location, @Nullable BOMedia media) {
        this();
        _hasMedia = media != null;
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

    public String getTeamName() {
        return _teamName;
    }

    public long getCreatedTimestamp() {
        return _createdTimestamp;
    }

    @Nullable
    public File getMediaFile() {
        return _linkedMedia == null ? null : _linkedMedia.getFile();
    }

    public String getMediaId() {
        return _mediaId;
    }

    @Nullable
    public BOLocation getLocation() {
        return _location;
    }

    public void setLocation(BOLocation location) {
        _location = location;
    }

    public boolean hasMedia() {
        return _linkedMedia != null;
    }

    public boolean hasUploadCredentials() {
        return (!_remoteID.isEmpty() && !_uploadToken.isEmpty());
    }

    public String getUploadToken() {
        return _uploadToken;
    }

    public String getRemoteID() {
        return _remoteID;
    }

    public int getUserId() {
        return _userId;
    }

    public BOMedia getMedia() {
        return _linkedMedia;
    }

    public BOMedia getProfileImage() {
        return _profileImage;
    }

    public int getProvenChallengeId() {
        return _provenChallengeId;
    }

    public String getChallengeDescription() {
        return _challengeDescription;
    }

    public String getUsername() {
        return _userName;
    }

    public int getProfileImageId() {
        return _profileImageId;
    }

    public String getLocationName() {
        return _locationName;
    }

    //setters
    public void setText(String text) {
        _text = text;
    }

    public void setRemoteID(String id) {
        _remoteID = id;
    }

    public void setMediaId(String id) {
        _mediaId = id;
    }

    public void setUploadCredentials(String id, String token) {
        setMediaId(id);
        _uploadToken = token;
    }

    public void setLinkedMedia(BOMedia media) {
        _linkedMedia = media;
    }

    public void setProfileImage(BOMedia media) {
        _profileImage = media;
    }

    private void setTimestamp(long timestamp) {
        _createdTimestamp = timestamp;
    }

    public void setUsername(String username) {
        _userName = username;
    }

    public void setTeamName(String teamname) {
        _teamName = teamname;
    }

    public void setProfilePicturePath(String path) {
        _profilePicturePath = path;
    }

    public void setChallengeId(int id) {
        _provenChallengeId = id;
    }

    public void setChallengeDescription(String descr) {
        _challengeDescription = descr;
    }

    public void setProfileImageId(int profileImageId) {
        _profileImageId = profileImageId;
    }

    public void setLocationName(String locationName) {
        _locationName = locationName;
    }


    public static Posting fromJSON(Context c, JSONObject object) throws JSONException {
        String id = object.getString("id");
        String text = object.getString("text");
        String timestamp = object.getString("date");
        JSONObject locationObject = null;
        BOLocation location = null;
        JSONArray mediaArray;
        BOMedia correlatingMedia = null;
        if((mediaArray = object.getJSONArray("media")).length() > 0) {
            int mediaID = mediaArray.getJSONObject(0).getInt("id");
            if(MediaManager.getMediaByID(mediaID) == null) {
                correlatingMedia = BOMedia.fromJSON(c, mediaArray, BOMedia.SIZE.MEDIUM);
            } else {
                correlatingMedia = MediaManager.getMediaByID(mediaID);
            }
        }

        JSONObject userObject = object.getJSONObject("user");
        JSONObject profilePictureObject = userObject.getJSONObject("profilePic");
        JSONObject proveObject = object.isNull("proves") ? null : object.getJSONObject("proves");
        int profilePictureId = profilePictureObject.getInt("id");
        JSONObject userDataObject = userObject.getJSONObject("participant");

        String teamName = userDataObject.getString("teamName");
        Posting returnPosting = new Posting(teamName, text, location, null);
        returnPosting.setProfileImageId(profilePictureId);
        returnPosting.setRemoteID(id);
        returnPosting.setTimestamp(Long.parseLong(timestamp));
        returnPosting.setProfileImage(smallImageFromJSON(c, profilePictureObject));
        try {
            if(!object.isNull("postingLocation")) {
                locationObject = object.getJSONObject("postingLocation");
                long latitude = locationObject.getLong("latitude");
                long longitude = locationObject.getLong("longitude");
                JSONObject locationDataObject = locationObject.getJSONObject("locationData");
                StringBuilder localityStringBuilder = new StringBuilder();
                if(!locationDataObject.isNull("LOCALITY")) {
                    localityStringBuilder.append(locationDataObject.getString("LOCALITY"))
                            .append(", ");
                }
                if(!locationDataObject.isNull("COUNTRY")) {
                    localityStringBuilder.append(locationDataObject.getString("COUNTRY"));
                }
                String locationName = localityStringBuilder.toString();
                returnPosting.setLocationName(locationName);
                location = new BOLocation();
                if(!(latitude == 0 || longitude == 0)) {
                    location.setLatitude(latitude);
                    location.setLongitude(longitude);
                } else {
                    location = null;
                }
            }

        } catch(JSONException e) {
            e.printStackTrace();
        }

        if(correlatingMedia != null) {
            correlatingMedia.setPosting(returnPosting);
            returnPosting.setLinkedMedia(correlatingMedia);
        }

        if(proveObject != null) {
            returnPosting.setChallengeId(proveObject.getInt("id"));
            returnPosting.setChallengeDescription(proveObject.getString("description"));
        }
        return returnPosting;
    }

    @Nullable
    private static BOMedia smallImageFromJSON(Context c, JSONObject object) {
        BOMedia smallMedia = null;
        try {
            JSONArray sizesArray = object.getJSONArray("sizes");
            for(int i = 0; i < sizesArray.length(); i++) {
                JSONObject sizeObject = sizesArray.getJSONObject(i);
                int width = sizeObject.getInt("width");
                if(width <= 200) {
                    smallMedia = MediaManager.createInternalMedia(c, BOMedia.TYPE.IMAGE);
                    smallMedia.setURL(sizeObject.getString("url"));
                    smallMedia.setIsDownloaded(false);
                    return smallMedia;
                }
            }
        } catch(JSONException e) {
            e.printStackTrace();
        }
        return smallMedia;
    }

}
