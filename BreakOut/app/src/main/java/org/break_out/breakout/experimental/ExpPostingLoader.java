package org.break_out.breakout.experimental;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Tino on 20.02.2016.
 */
public class ExpPostingLoader {

    private static final String KEY_ID_ARR = "id_arr";
    public static final String KEY_LAST_KNOWN_ID = "last_known_id";

    private static final String SEPARATOR = ",";

    private Context _context = null;

    public ExpPostingLoader(Context context) {
        _context = context;
    }

    public List<ExpPosting> getLatestPostings() {
        // 1) Get missing IDs
        int lastKnownIdBefore = loadInt(KEY_LAST_KNOWN_ID);
        List<Integer> missingIds = loadMissingIdsArr();
        missingIds.addAll(getMissingIdsFromServer(lastKnownIdBefore));

        // 2) Download 10 latest postings if needed
        int lastKnownIdAfter = loadInt(KEY_LAST_KNOWN_ID);
        if(lastKnownIdBefore != lastKnownIdAfter) {
            boolean success = downloadPostingsToLocalDB(missingIds.subList(Math.max(0, missingIds.size()-10), missingIds.size()-1));

            if(success) {
                // Remove successfully uploaded postings from missing IDs list
                missingIds = missingIds.subList(0, Math.max(0, missingIds.size()-11));
            }
        }

        // 3) Store missing posting IDs
        storeMissingIdsArr(missingIds);

        return getLastLocalPostings();
    }

    public List<ExpPosting> getPostingsInRange(int first, int last) {
        if(first > last) {
            return new ArrayList<ExpPosting>();
        }

        int lastKnownIdBefore = loadInt(KEY_LAST_KNOWN_ID);
        List<Integer> allMissingIDs = loadMissingIdsArr();
        allMissingIDs.addAll(getMissingIdsFromServer(lastKnownIdBefore));

        List<Integer> neededIDs = new ArrayList<Integer>();

        int lastKnown = loadInt(KEY_LAST_KNOWN_ID);

        for(int i = Math.min(lastKnown, first); i <= Math.min(lastKnown, last); i++) {
            if(allMissingIDs.contains(i)) {
                neededIDs.add(i);
            }
        }

        // Download all missing postings (neededIDs)
        boolean success = downloadPostingsToLocalDB(neededIDs);
        if(success) {
            // Update missing IDs array in shared prefs
            allMissingIDs.removeAll(neededIDs);
            storeMissingIdsArr(allMissingIDs);
        }

        List<Integer> rangeIds = new ArrayList<Integer>();
        for(int i = first; i <= last; i++) {
            rangeIds.add(i);
        }

        return getLocalPostingsById(rangeIds);
    }

    private List<ExpPosting> getLocalPostingsById(List<Integer> ids) {
        List<ExpPosting> postings = new ArrayList<ExpPosting>();

        if(ids.isEmpty()) {
            return postings;
        }

        String[] idStrings = new String[ids.size()];

        for(int i = 0; i < ids.size(); i++) {
            idStrings[i] = "" + ids.get(i);
        }

        for(ExpPosting p : ExpPosting.findById(ExpPosting.class, idStrings)) {
            postings.add(p);
        }

        return postings;
    }

    private boolean downloadPostingsToLocalDB(List<Integer> ids) {
        if(ids.isEmpty()) {
            return true;
        }

        // TODO: Download postings with ids from server
        List<ExpPosting> postings = new ArrayList<ExpPosting>();

        // FIXME: Test code with dummy data
        for(int id : ids) {
            ExpPosting p = new ExpPosting();
            p.setId(new Long(id));

            postings.add(p);
        }

        for(ExpPosting p : postings) {
            p.save();
        }

        return true;
    }

    private List<ExpPosting> getLastLocalPostings() {
        List<ExpPosting> postings = new ArrayList<ExpPosting>();

        for(ExpPosting p : ExpPosting.find(ExpPosting.class, null, null, null, null, "10")) {
            postings.add(p);
        }

        return postings;
    }

    private List<Integer> getMissingIdsFromServer(int since) {
        List<Integer> missingIds = new ArrayList<Integer>();

        // TODO: Get IDs from server
        int lastKnownId = loadInt(KEY_LAST_KNOWN_ID);

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://breakout-development.herokuapp.com/posting/get/since/" + lastKnownId + "/")
                .build();

        try {
            Response response = client.newCall(request).execute();
            String bodyIdsString = response.body().string();

            Log.d("Loader", "Response body: " + bodyIdsString);

            JSONArray idsArr = new JSONArray(bodyIdsString);
            for(int i = 0; i < idsArr.length(); i++) {
                missingIds.add(idsArr.getInt(i));
            }
        } catch(IOException e) {
            e.printStackTrace();
        } catch(JSONException e) {
            e.printStackTrace();
        }

        Collections.sort(missingIds);

        // Update last known ID
        if(missingIds.size() > 0) {
            storeInt(KEY_LAST_KNOWN_ID, missingIds.get(missingIds.size() - 1));
        }

        return missingIds;
    }

    private void storeInt(String key, int i) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(_context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt(key, i);

        editor.commit();
    }

    public int loadInt(String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(_context);

        return prefs.getInt(key, -1);
    }

    private void storeMissingIdsArr(List<Integer> arr) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(_context);
        SharedPreferences.Editor editor = prefs.edit();

        String arrString = "";
        for(int i : arr) {
            arrString += (arrString.equals("") ? i : SEPARATOR + i);
        }

        Log.d("Loader", "arrString = " + arrString);

        editor.putString(KEY_ID_ARR, arrString);

        editor.commit();
    }

    public List<Integer> loadMissingIdsArr() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(_context);

        String arrString = prefs.getString(KEY_ID_ARR, null);

        if(arrString == null) {
            return new ArrayList<Integer>();
        }

        String[] idStrings = arrString.split(SEPARATOR);

        List<Integer> ids = new ArrayList<Integer>();

        for(int i = 0; i < idStrings.length; i++) {
            try {
                ids.add(Integer.parseInt(idStrings[i]));
            } catch(NumberFormatException ex) {

            }
        }

        Collections.sort(ids);

        return ids;
    }

}
