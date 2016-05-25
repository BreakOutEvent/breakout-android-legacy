package org.break_out.breakout.model;

import android.support.annotation.Nullable;

import com.orm.SugarRecord;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Maximilian Duehr on 24.05.2016.
 */
public class Challenge extends SugarRecord {
    private int _remoteID;
    private int _eventID;
    private int _teamID;
    private int _sponsorID;
    private String _teamName;
    private String _description;
    private String _status;
    private boolean _isSponsorHidden = false;
    private int _amount;
    private JSONObject _contract;
    private JSONObject _unregisteredSponsor;
    private int _postingID;

    public Challenge(int remoteID,int eventID, int teamID,int sponsorID,String teamName,String description,String status,boolean isSponsorHidden,int amount,JSONObject contract,JSONObject unregisteredSponsor) {
        _remoteID = remoteID;
        _eventID = eventID;
        _teamID = teamID;
        _sponsorID = sponsorID;
        _teamName = teamName;
        _description = description;
        _status = status;
        _isSponsorHidden = isSponsorHidden;
        _amount = amount;
        _contract = contract;
        _unregisteredSponsor = unregisteredSponsor;
    }

    public int getRemoteID() { return _remoteID;}
    public int getEventID() { return _eventID;}
    public int teamID() { return _teamID;}
    public String getTeamName() { return _teamName;}
    public String getDescription() { return _description;}
    public boolean isSponsorHidden() { return _isSponsorHidden;}
    public int getAmount() { return _amount;}
    public JSONObject getContract() { return _contract;}
    public JSONObject getUnregisteredSponsor() { return _unregisteredSponsor;}

    public void setPostingID(int postingID) {_postingID = postingID;}


    @Nullable
    public static ArrayList<Challenge> fromJSON(JSONArray jsonArray) {
        ArrayList<Challenge> resultList = new ArrayList<>();
        try {
            for(int i=0; i<jsonArray.length();i++)  {
                Challenge newChallenge = fromJSON(jsonArray.getJSONObject(i));
                resultList.add(newChallenge);
            }
            return resultList;
        }catch(JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public static Challenge fromJSON(JSONObject object) {
        try {
            int remoteID = object.getInt("id");
            int eventID = object.getInt("eventId");
            String status = object.getString("status");
            int teamID = object.getInt("teamId");
            String teamName = object.getString("team");
            int sponsorID = object.getInt("sponsorId");
            int userID = object.getInt("userId");
            boolean sponsorIsHidden = object.getBoolean("sponsorIsHidden");
            JSONObject unregisteredSponsorObject = !object.getString("unregisteredSponsor").isEmpty() ? object.getJSONObject("unregisteredSponsor") : new JSONObject("");
            int amount = object.getInt("amount");
            String description = object.getString("description");
            JSONObject contractObject = object.getJSONObject("contract");
            int contractID = contractObject.getInt("id");
            String documentType = contractObject.getString("type");
            String uploadToken = contractObject.getString("uploadToken");
            JSONArray sizesArray = contractObject.getJSONArray("sizes");

            return new Challenge(remoteID,eventID,teamID,sponsorID,teamName,description,status,sponsorIsHidden,amount,contractObject,unregisteredSponsorObject);
        } catch(JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
