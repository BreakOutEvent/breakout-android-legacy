package org.break_out.breakout.model;

import android.content.Context;
import android.support.annotation.Nullable;

import org.break_out.breakout.manager.MediaManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by Tino on 14.12.2015.
 */

public class Posting {
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
    private int _remoteID = -1;
    private String _fileURL = "";
    private String _teamName = "";
    private String _userName = "";
    private String _profilePicturePath;
    private BOMedia _linkedMedia;
    private BOMedia _profileImage;
    private int _likes = 0;
    private int _comments = 0;
    private boolean _hasliked = false;

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
        this(message,location,media);
        this._teamName = teamName;
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
        return (_remoteID != -1 && !_uploadToken.isEmpty());
    }

    public String getUploadToken() {
        return _uploadToken;
    }

    public int getRemoteID() {
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

    public int getLikes() {
        return _likes;
    }

    public int getComments() {
        return _comments;
    }

    public int getProfileImageId() {
        return _profileImageId;
    }

    public boolean hasLiked() {
        return _hasliked;
    }

    public String getLocationName() {
        return _locationName;
    }

    //setters
    public void setText(String text) {
        _text = text;
    }

    public void setRemoteID(int id) {
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

    public void setLikes(int likes) {
        _likes = likes;
    }

    public void setCommentCout(int comments) {
        _comments = comments;
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

    public void setHasLiked(boolean hasLiked) {
        _hasliked = hasLiked;
    }


    public static Posting fromJSON(Context c, JSONObject object) throws JSONException {
        int id = object.getInt("id");
        int likes = object.getInt("likes");
        String text = object.getString("text");
        String timestamp = object.getString("date");
        JSONObject locationObject = null;
        JSONArray commentArray = object.getJSONArray("comments");
        BOLocation location = null;
        JSONArray mediaArray;
        BOMedia correlatingMedia = null;
        if((mediaArray = object.getJSONArray("media")).length() > 0) {
            String type = mediaArray.getJSONObject(0).getString("type");
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
        returnPosting.setHasLiked(object.getBoolean("hasLiked"));
        returnPosting.setTimestamp(Long.parseLong(timestamp));
        returnPosting.setProfileImage(previewImageFromJSON(c, profilePictureObject));
        returnPosting.setLikes(likes);
        returnPosting.setCommentCout(commentArray.length());
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
    private static BOMedia previewImageFromJSON(Context c, JSONObject object) {
        BOMedia smallMedia = null;
        try {
            JSONArray sizesArray = object.getJSONArray("sizes");
            for(int i = 0; i < sizesArray.length(); i++) {
                JSONObject sizeObject = sizesArray.getJSONObject(i);
                if(sizeObject.get("type").equals(BOMedia.TYPE.IMAGE.toString())) {
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
