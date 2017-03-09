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

    public double getLatitude() {
        return _latitude;
    }

    public double getLongitude() {
        return _longitude;
    }

    public int getRemoteId() {
        return _remoteId;
    }

    public int getTeamId() {
        return _teamId;
    }

    public int getEventId() {
        return _eventId;
    }

    public String getTeamName() {
        return _teamName;
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


    public void setRemoteId(int remoteId) {
        _remoteId = remoteId;
    }

    public void setTeamId(int teamId) {
        _teamId = teamId;
    }

    public void setEventId(int eventId) {
        _eventId = eventId;
    }

    public void setTeamName(String teamName) {
        _teamName = teamName;
    }

    public void setLatitude(double latitude) {
        _latitude = latitude;
    }

    public void setLongitude(double longitude) {
        _longitude = longitude;
    }

    public void setIsPosted(boolean isPosted) {
        _isPosted = isPosted;
    }

    public void setDuringEvent(boolean isDuringEvent) {
        _duringEvent = isDuringEvent;
    }


    public long getTimestamp() {
        return _timestamp;
    }

    public static BOLocation fromJSON(JSONObject object) throws JSONException {
        int remoteId = object.getInt("id");
        BOLocation location = BOLocationManager.getLocationById(remoteId);
        if(location != null) {
            return location;
        }
        double latitude = object.getDouble("latitude");
        double longitude = object.getDouble("longitude");
        long timestamp = object.getLong("date");
        int teamId = object.getInt("teamId");
        int eventId = object.getInt("eventId");
        boolean duringEvent = object.getBoolean("duringEvent");
        String teamName = object.getString("team");
        Log.d(TAG, "teamId: " + teamId);

        TeamManager.getInstance().createTeam(teamId, teamName);

        location = BOLocationManager.createLocation(remoteId, teamId, eventId, teamName, timestamp, latitude, longitude);
        location.setIsPosted(true);
        location.setDuringEvent(duringEvent);

        return location;
    }
}
