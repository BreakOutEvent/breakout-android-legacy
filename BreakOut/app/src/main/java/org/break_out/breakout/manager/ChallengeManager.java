package org.break_out.breakout.manager;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import org.break_out.breakout.constants.Constants;
import org.break_out.breakout.model.Challenge;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Maximilian Duehr on 24.05.2016.
 */
public class ChallengeManager  {
    private static final String TAG = "ChallengeManager";
    private static ChallengeManager _instance;
    private static ArrayList<Challenge> _challenges;

    private ChallengeManager() {
        _challenges = new ArrayList<>();
        Log.d(TAG,"arrayList reseted");
    }

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
        return _challenges;
    }

    public void fetchChallenges(Context c,@Nullable ChallengesFetchedListener listener) {
        new FetchChallengesTask(c,listener).execute();
    }

    public interface ChallengesFetchedListener {
        void onChallengesFetched();
    }

    private class FetchChallengesTask extends AsyncTask<Void,Void,ArrayList<Challenge>> {
        Context _context;
        ChallengesFetchedListener _listener;

        public FetchChallengesTask(Context c,@Nullable ChallengesFetchedListener listener) {
            _context = c;
            _listener = listener;
        }
        @Override
        protected ArrayList<Challenge> doInBackground(Void... params) {
            int eventId = UserManager.getInstance(_context).getCurrentUser().getEventId();
            int teamId = UserManager.getInstance(_context).getCurrentUser().getTeamId();

            OkHttpClient client = new OkHttpClient();
            Request callRequest = new Request.Builder().url(Constants.Api.BASE_URL + "/" + "event" + "/" + eventId + "/" + "team" + "/" + teamId + "/" + "challenge" + "/").build();

            try{
                Response response = client.newCall(callRequest).execute();
                String responseString = response.body().string();
                ArrayList<Challenge> responseList = Challenge.fromJSON(new JSONArray(responseString));
                for(Challenge c : responseList) {
                    if(!isSaved(c)){
                        getAllChallenges().add(c);
                    }
                }
                return getAllChallenges();
            } catch(Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Challenge> list) {
            super.onPostExecute(list);
            if(_listener != null) {
                _listener.onChallengesFetched();
            }
        }

        private boolean isSaved(Challenge challenge) {
            boolean saved = false;
            for(Challenge c : getAllChallenges()) {
                if(c.getRemoteID() == challenge.getRemoteID()) {
                    saved = true;
                }
            }
            return saved;
        }
    }
}
