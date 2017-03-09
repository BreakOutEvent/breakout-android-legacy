package org.break_out.breakout.model;

import android.content.Context;

import org.break_out.breakout.manager.TeamManager;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Maximilian Dühr on 01.06.2016.
 */
public class Team {
    private int _remoteId;
    private String _teamName;
    private int _donateSum;
    private BOMedia _profileImage;

    public Team() {
    }

    public Team(int remoteId, String teamName) {
        _remoteId = remoteId;
        _teamName = teamName;
    }

    public int getRemoteId() {
        return _remoteId;
    }

    public int getDonateSum() {
        return _donateSum;
    }

    BOMedia getProfileImage() {
        return _profileImage;
    }

    public void setRemoteId(int id) {
        _remoteId = id;
    }

    public void setDonateSum(int sum) {

    }

    public String getTeamName() {
        return _teamName;
    }


    public static Team fromJson(Context c, JSONObject teamObject) throws JSONException {
        int teamId = teamObject.getInt("id");
        String name = teamObject.getString("name");
        JSONObject profilePictureObject = teamObject.getJSONObject("profilePic");
        BOMedia pictureObject = BOMedia.sizedMediaFromJSON(c, profilePictureObject, BOMedia.SIZE.LARGE);
        int distance = teamObject.getJSONObject("distance").getInt("actual_distance");
        Team t = null;
        if((t = TeamManager.getInstance().getTeamById(teamId)) == null) {
            t = new Team();
        }
        return t;
    }
}
