package org.break_out.breakout.manager;

import android.support.annotation.Nullable;
import android.util.Log;

import org.break_out.breakout.model.Team;

import java.util.ArrayList;

/**
 * Created by Maximilian Dühr on 01.06.2016.
 */
public class TeamManager {
    private static final String TAG = "TeamManager";
    private static TeamManager _instance;

    private TeamManager() {}

    public static TeamManager getInstance() {
        if(_instance == null) {
            _instance = new TeamManager();
        }
        return _instance;
    }

    public Team createTeam(int remoteId,String teamName) {
        Team t;
        Log.d(TAG,"team created");
        if((t = getTeamById(remoteId))!=null) {
            return t;
        } else {
            t = new Team(remoteId,teamName);
            Log.d(TAG,"new team! "+remoteId);
            t.save();
        }
        return t;
    }

    @Nullable
    public Team getTeamById(int id) {
        Log.d(TAG,"getTeamById "+id);
        ArrayList<Team> resultList = new ArrayList<>();
        resultList.addAll(Team.findWithQuery(Team.class,"SELECT * FROM Team WHERE _REMOTE_ID = "+id+" LIMIT 1"));
        if(resultList.size() > 0) {
            Log.d(TAG,"teamId already there : "+id);
            return resultList.get(0);
        }
        return null;
    }

    public ArrayList<Team> getAllTeams() {
        ArrayList<Team> resultList = new ArrayList<>();
        resultList.addAll(Team.findWithQuery(Team.class,"SELECT * FROM TEAM ORDER BY _REMOTE_ID DESC"));
        return resultList;
    }
}
