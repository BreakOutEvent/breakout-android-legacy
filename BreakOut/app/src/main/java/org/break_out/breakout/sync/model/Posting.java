package org.break_out.breakout.sync.model;

import android.location.Location;
import android.util.Log;

import com.orm.dsl.Ignore;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Tino on 14.12.2015.
 */

public class Posting extends SyncEntity {

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
    public boolean uploadToServer() {
        boolean success = false;

        // Get current timestamp as the sent timestamp
        _sentTimestamp = System.currentTimeMillis();

        RequestBody body = RequestBody.create(JSON, toJSON());
        Request request = new Request.Builder()
                .url(BASE_URL + "/test/post/")
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
    public boolean updateOnServer() {
        // TODO: Currently randomly returns success or failure
        return (Math.random() > 0.5);
    }

    @Override
    public boolean deleteOnServer() {
        // TODO: Currently randomly returns success or failure
        return (Math.random() > 0.5);
    }

    @Override
    public String toString() {
        return "Posting{" +
                "text='" + _text + '\'' +
                "state='" + getState() + '\'' +
                "}";
    }

}
