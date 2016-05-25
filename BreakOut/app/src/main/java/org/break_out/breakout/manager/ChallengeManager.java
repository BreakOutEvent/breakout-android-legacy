package org.break_out.breakout.manager;

import android.support.annotation.Nullable;

import org.break_out.breakout.model.Challenge;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Maximilian Duehr on 24.05.2016.
 */
public class ChallengeManager  {
    private static ChallengeManager _instance;

    private ChallengeManager() { }

    public static ChallengeManager getInstance() {
        if(_instance == null) {
            _instance = new ChallengeManager();
        }
        return _instance;
    }

    public Challenge createChallenge(JSONObject challengeObject) {
        return Challenge.fromJSON(challengeObject);
    }

    @Nullable
    public Challenge getChallengeByRemoteID(int id) {
        for(Challenge c: getAllChallenges()) {
            if(c.getRemoteID() == id) {
                return c;
            }
        }
        return null;
    }

    public ArrayList<Challenge> getAllChallenges() {
        ArrayList<Challenge> returnChallenge = new ArrayList<>();
        returnChallenge.addAll(Challenge.listAll(Challenge.class));

        return returnChallenge;
    }
}
