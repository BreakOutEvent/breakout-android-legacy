package org.break_out.breakout.experimental;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Tino on 20.02.2016.
 */
public class ExpPostingLoader {

    private static final String KEY_ID_ARR = "id_arr";
    public static final String KEY_LAST_KNOWN_ID = "last_known_id";

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static final String SEPARATOR = ",";

    private Context _context = null;

    public ExpPostingLoader(Context context) {
        _context = context;
    }

    public List<ExpPosting> getPostings(int limit) {
        // 1) Get missing IDs
        List<Integer> missingIds = updateMissingIdsFromServer();

        // 2) Download 10 latest postings if needed
        boolean success = downloadPostingsToLocalDB(missingIds.subList(Math.max(0, missingIds.size()-limit), missingIds.size()-1));

        if(success) {
            // Remove successfully uploaded postings from missing IDs list
            missingIds = missingIds.subList(0, Math.max(0, missingIds.size()-(limit+1)));
        }

        // 3) Store missing posting IDs
        storeMissingIdsArr(missingIds);

        return getLastLocalPostings(limit);
    }

    public List<ExpPosting> getPostings(int first, int last) {
        if(first > last) {
            int tempFirst = first;
            first = last;
            last = tempFirst;
        }

        List<Integer> allMissingIDs = updateMissingIdsFromServer();

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

    public List<Integer> updateMissingIdsFromServer() {
        int lastKnownIdBefore = loadInt(KEY_LAST_KNOWN_ID);
        List<Integer> allMissingIDs = loadMissingIdsArr();
        allMissingIDs.addAll(getMissingIDsFromServer(lastKnownIdBefore));

        storeMissingIdsArr(allMissingIDs);

        return allMissingIDs;
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

        Log.d("Loader", "Downloading: " + ids);

        List<ExpPosting> postings = new ArrayList<ExpPosting>();

        String idsJson = "[";
        for(int id : ids) {
            idsJson += (idsJson.equals("[") ? "" + id : "," + id);
        }
        idsJson += "]";

        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(JSON, idsJson);
        Request request = new Request.Builder()
                .url("http://breakout-development.herokuapp.com/posting/get/ids")
                .post(body)
                .build();

        try {
            Response response = client.newCall(request).execute();
            String bodyPostingsString = response.body().string();

            Log.d("Loader", "Response body: " + bodyPostingsString);

            JSONArray idsArr = new JSONArray(bodyPostingsString);
            for(int i = 0; i < idsArr.length(); i++) {
                JSONObject postingObj = idsArr.getJSONObject(i);

                ExpPosting p = new ExpPosting();
                p.setId(postingObj.getLong("id"));
                p.setText(postingObj.getString("text"));

                postings.add(p);
            }
        } catch(IOException e) {
            e.printStackTrace();
            return false;
        } catch(JSONException e) {
            e.printStackTrace();
            return false;
        }

        for(ExpPosting p : postings) {
            p.save();
        }

        return true;
    }

    private List<ExpPosting> getLastLocalPostings(int limit) {
        List<ExpPosting> postings = new ArrayList<ExpPosting>();

        for(ExpPosting p : ExpPosting.find(ExpPosting.class, null, null, null, null, "" + limit)) {
            postings.add(p);
        }

        return postings;
    }

    private List<Integer> getMissingIDsFromServer(int since) {
        List<Integer> missingIds = new ArrayList<Integer>();

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
