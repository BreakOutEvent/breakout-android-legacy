package org.break_out.breakout.model;

import com.orm.SugarRecord;

/**
 * Created by Maximilian Dühr on 01.06.2016.
 */
public class Team extends SugarRecord {
    private int _remoteId;
    private String _teamName;

    public Team() {}

    public Team(int remoteId,String teamName) {
        _remoteId = remoteId;
        _teamName = teamName;
    }

    public int getRemoteId() {
        return _remoteId;
    }

    public String getTeamName() {
        return _teamName;
    }
}
