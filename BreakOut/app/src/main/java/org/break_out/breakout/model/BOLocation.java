package org.break_out.breakout.model;

import android.util.Log;

import org.break_out.breakout.manager.BOLocationManager;
import org.break_out.breakout.manager.TeamManager;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Maximilian Dühr on 01.03.2016.
 */
public class BOLocation {
    private static final String TAG = "BOLocation";

    private long _timestamp;
    private int _remoteId;
    private double _latitude;
    private double _longitude;
    private int _teamId;
    private int _eventId;
    private String _teamName;
    private Posting _posting;
    private boolean _isPosted = true;
    private boolean _duringEvent = false;

    public BOLocation() {
    }

    public BOLocation(long timestamp, double lati, double longi) {
        _timestamp = timestamp;
        _latitude = lati;
        _longitude = longi;
    }

    public BOLocation(Posting posting, long timestamp, double lati, double longi) {
        this(timestamp, lati, longi);
        _posting = posting;
    }

    public static BOLocation fromJSON(JSONObject teamObject, JSONObject locationObject) throws JSONException {
        int remoteId = locationObject.getInt("id");
        BOLocation location = BOLocationManager.getLocationById(remoteId);
        if (location != null) {
            return location;
        }
        double latitude = locationObject.getDouble("latitude");
        double longitude = locationObject.getDouble("longitude");
        long timestamp = locationObject.getLong("date");
        int eventId = teamObject.getInt("event");
        boolean duringEvent = locationObject.getBoolean("duringEvent");
        int teamId = teamObject.getInt("id");
        String teamName = teamObject.getString("name");
        Log.d(TAG, "teamId: " + teamId);

        TeamManager.getInstance().createTeam(teamId, teamName);

        location = BOLocationManager.createLocation(remoteId, teamId, eventId, teamName, timestamp, latitude, longitude);
        location.setIsPosted(true);
        location.setDuringEvent(duringEvent);

        return location;
    }

    public double getLatitude() {
        return _latitude;
    }

    public void setLatitude(double latitude) {
        _latitude = latitude;
    }

    public double getLongitude() {
        return _longitude;
    }

    public void setLongitude(double longitude) {
        _longitude = longitude;
    }

    public int getRemoteId() {
        return _remoteId;
    }

    public void setRemoteId(int remoteId) {
        _remoteId = remoteId;
    }

    public int getTeamId() {
        return _teamId;
    }

    public void setTeamId(int teamId) {
        _teamId = teamId;
    }

    public int getEventId() {
        return _eventId;
    }

    public void setEventId(int eventId) {
        _eventId = eventId;
    }

    public String getTeamName() {
        return _teamName;
    }

    public void setTeamName(String teamName) {
        _teamName = teamName;
    }

    public Posting getPosting() {
        return _posting;
    }

    public boolean isPosted() {
        return _isPosted;
    }

    public boolean isDuringEvent() {
        return _duringEvent;
    }

    public void setDuringEvent(boolean isDuringEvent) {
        _duringEvent = isDuringEvent;
    }

    public void setIsPosted(boolean isPosted) {
        _isPosted = isPosted;
    }

    public Long getTimestamp() {
        return _timestamp;
    }
}
