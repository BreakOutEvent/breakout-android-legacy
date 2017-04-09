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
    private static ArrayList<Team> _teams;

    private TeamManager() {
        _teams = new ArrayList<>();
    }

    public static TeamManager getInstance() {
        if(_instance == null) {
            _instance = new TeamManager();
        }
        return _instance;
    }

    public Team createTeam(int remoteId,String teamName) {
        Team t;
        if((t = getTeamById(remoteId))!=null) {
            return t;
        } else {
            t = new Team(remoteId,teamName);
            if(!_teams.contains(t)){
                _teams.add(t);
            }
            //t.save();
        }
        return t;
    }

    @Nullable
    public Team getTeamById(int id) {
        for(Team t : _teams){
            if(t.getRemoteId() == id){
                return t;
            }
        }
        return null;
    }

    @Nullable
    public ArrayList<Team> getAllTeams() {
        return _teams;
    }
}
