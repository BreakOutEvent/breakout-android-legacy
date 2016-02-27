package org.break_out.breakout.sync.model;

import android.location.Location;
import android.util.Log;

import com.orm.dsl.Ignore;

import org.break_out.breakout.sync.BOEntityDownloader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Tino on 14.12.2015.
 */

public class Posting extends BOSyncEntity {

    private static final String TAG = "Posting";

    @Ignore
    public static final String BASE_URL = "http://breakout-development.herokuapp.com";

    @Ignore
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private String _challengeId = "";
    private long _createdTimestamp = 0L;
    private Location _location = null;
    @Ignore
    private Long _sentTimestamp = null;
    private String _text = "";

    // SugarORM needs an empty constructor
    public Posting() {
        _createdTimestamp = System.currentTimeMillis();
    }

    public void setText(String text) {
        _text = text;
    }

    public String getText() {
        return _text;
    }

    private String toJSON() {
        return "{" +
            "\"challenge_id\": \"" + _challengeId + "\"," +
            "\"created\": \"" + _createdTimestamp + "\"," +
            "\"location\": {" +
                "\"lat\": " + (_location != null ? (int)(_location.getLatitude()) : "0") + "," +
                "\"lon\": " + (_location != null ? (int)(_location.getLongitude()) : "0") +
            "}," +
            "\"sent\": \"" + _sentTimestamp + "\"," +
            "\"text\": \"" + _text + "\"" +
        "}";
    }

    @Override
    public boolean uploadToServerSync() {
        boolean success = false;

        // Get current timestamp as the sent timestamp
        _sentTimestamp = System.currentTimeMillis();

        RequestBody body = RequestBody.create(JSON, toJSON());
        Request request = new Request.Builder()
                .url(BASE_URL + "/posting/")
                .post(body)
                .build();
        OkHttpClient client = new OkHttpClient();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if(response.code() == 201) {
                success = true;
            }
        } catch(IOException e) {
            e.printStackTrace();

            success = false;
        }

        // The sent timestamp is only valid for one request
        _sentTimestamp = null;

        return success;
    }

    @Override
    public boolean updateOnServerSync() {
        // TODO: Currently randomly returns success or failure
        return (Math.random() > 0.5);
    }

    @Override
    public boolean deleteOnServerSync() {
        // TODO: Currently randomly returns success or failure
        return (Math.random() > 0.5);
    }

    public static PostingDownloader getDownloader() {
        return new PostingDownloader();
    }

    public static class PostingDownloader extends BOEntityDownloader<Posting> {

        @Override
        public List<Posting> downloadSync(List<Long> idsToDownload) {

            List<Posting> postings = new ArrayList<Posting>();

            String idsToDownloadJsonString = "[";
            for(Long id : idsToDownload) {
                idsToDownloadJsonString += idsToDownloadJsonString.equals("[") ? ("" + id) : ("," + id);
            }
            idsToDownloadJsonString += "]";

            Request request = new Request.Builder()
                    .url(BASE_URL + "/posting/get/ids/")
                    .post(RequestBody.create(JSON, idsToDownloadJsonString))
                    .build();

            OkHttpClient client = new OkHttpClient();

            try {
                Response response = client.newCall(request).execute();
                if(response.code() == 200) {
                    String jsonString = response.body().string();
                    if(jsonString == null) {
                        return postings;
                    }

                    JSONArray idArr = new JSONArray(jsonString);

                    for(int i = 0; i < idArr.length(); i++) {
                        postings.add(Posting.fromJSON(idArr.getJSONObject(i)));
                    }
                }
            } catch(IOException e) {
                e.printStackTrace();
            } catch(JSONException e) {
                e.printStackTrace();
                Log.d(TAG, "Requesting the server for new IDs didn't return a JSON array");
            }

            return postings;
        }

        @Override
        public List<Long> downloadNewIDsSync(long lastKnownId) {
            List<Long> newIds = new ArrayList<Long>();

            Request request = new Request.Builder()
                    .url(BASE_URL + "/posting/get/since/" + lastKnownId + "/")
                    .build();

            OkHttpClient client = new OkHttpClient();

            try {
                Response response = client.newCall(request).execute();
                if(response.code() == 200) {
                    String jsonString = response.body().string();
                    if(jsonString == null) {
                        return newIds;
                    }

                    JSONArray idArr = new JSONArray(jsonString);

                    for(int i = 0; i < idArr.length(); i++) {
                        newIds.add(idArr.getLong(i));
                    }
                }
            } catch(IOException e) {
                e.printStackTrace();
            } catch(JSONException e) {
                e.printStackTrace();
                Log.d(TAG, "Requesting the server for new IDs didn't return a JSON array");
            }

            return newIds;
        }
    }

    public static Posting fromJSON(JSONObject jsonObj) {
        Posting p = new Posting();

        try {
            p.setId(jsonObj.getLong("id"));
            p.setText(jsonObj.getString("text"));
        } catch(JSONException e) {
            e.printStackTrace();
        }

        return p;
    }

    @Override
    public String toString() {
        return "Posting(" + getId() + ") {" +
                "text(" + _text + ") " +
                "state=(" + getState() + ") " +
                "downPrio=(" + getDownloadPriority() + ")" +
                "}";
    }

}
