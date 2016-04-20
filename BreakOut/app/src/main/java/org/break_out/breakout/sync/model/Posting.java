package org.break_out.breakout.sync.model;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.orm.dsl.Ignore;

import org.break_out.breakout.api.BOApiService;
import org.break_out.breakout.api.PostingModel;
import org.break_out.breakout.sync.BOEntityDownloader;
import org.break_out.breakout.util.ApiUtils;
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
import retrofit2.Call;
import retrofit2.Callback;

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

    // This timestamp is in seconds!
    private long _createdTimestamp = 0L;
    private Location _location = null;
    private String _text = "";

    // SugarORM needs an empty constructor
    public Posting() {
        _createdTimestamp = System.currentTimeMillis();
    }

    public Posting(PostingModel model) {
        setRemoteId(model.id);
        setText(model.text);
        _createdTimestamp = model.date;
    }

    public void setText(String text) {
        _text = text;
    }

    public String getText() {
        return _text;
    }

    public long getCreatedTimestamp() {
        return _createdTimestamp;
    }

    @Override
    public boolean uploadToServerSync(Context context) {
        BOApiService service = ApiUtils.getService(context);

        Call<PostingModel> call = service.createPosting(new PostingModel(this));
        try {
            retrofit2.Response<PostingModel> response = call.execute();

            if(!response.isSuccessful()) {
                return false;
            }

            PostingModel p = response.body();
            setRemoteId(p.id);

            return true;
        } catch(IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateOnServerSync(Context context) {
        // TODO: Currently randomly returns success or failure
        return (Math.random() > 0.5);
    }

    @Override
    public boolean deleteOnServerSync(Context context) {
        // TODO: Currently randomly returns success or failure
        return (Math.random() > 0.5);
    }

    public static PostingDownloader getDownloader() {
        return new PostingDownloader();
    }

    public static class PostingDownloaderOld extends BOEntityDownloader<Posting> {

        @Override
        public List<Posting> downloadSync(Context context, List<Long> idsToDownload) {

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
        public List<Long> downloadNewIDsSync(Context context, long lastKnownId) {
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

    public static class PostingDownloader extends BOEntityDownloader<Posting> {

        @Override
        public List<Posting> downloadSync(Context context, List<Long> idsToDownload) {

            List<Posting> postings = new ArrayList<Posting>();

            long[] idsToDownloadArr = new long[idsToDownload.size()];
            for(int i = 0; i < idsToDownload.size(); i++) {
                long id = idsToDownload.get(i);
                idsToDownloadArr[i] = id;
            }

            BOApiService service = ApiUtils.getService(context);

            Call<List<PostingModel>> call = service.getPostings(idsToDownloadArr);
            try {
                retrofit2.Response<List<PostingModel>> response = call.execute();

                if(response.isSuccessful()) {
                    for(PostingModel model : response.body()) {
                        postings.add(new Posting(model));
                    }
                }
            } catch(IOException e) {
                e.printStackTrace();
            }

            return postings;
        }

        @Override
        public List<Long> downloadNewIDsSync(Context context, long lastKnownId) {
            List<Long> newIds = new ArrayList<Long>();

            BOApiService service = ApiUtils.getService(context);

            Call<long[]> call = service.getNewPostingIds(lastKnownId);

            try {
                retrofit2.Response<long[]> response = call.execute();

                if(response.isSuccessful()) {
                    for(long id : response.body()) {
                        newIds.add(id);
                    }
                }
            } catch(IOException e) {
                e.printStackTrace();
            }

            return newIds;
        }
    }

    public static Posting fromJSON(JSONObject jsonObj) {
        Posting p = new Posting();

        try {
            p.setRemoteId(jsonObj.getLong("id"));
            p.setText(jsonObj.getString("text"));
        } catch(JSONException e) {
            e.printStackTrace();
        }

        return p;
    }

    private String toJSON() {
        return "{" +
                "\"challenge_id\": \"" + _challengeId + "\"," +
                "\"created\": \"" + _createdTimestamp + "\"," +
                "\"location\": {" +
                "\"lat\": " + (_location != null ? (int)(_location.getLatitude()) : "0") + "," +
                "\"lon\": " + (_location != null ? (int)(_location.getLongitude()) : "0") +
                "}," +
                "\"text\": \"" + _text + "\"" +
                "}";
    }

    @Override
    public String toString() {
        return "Posting(" + getRemoteId() + ") {" +
                "text(" + _text + ") " +
                "state=(" + getState() + ") " +
                "downPrio=(" + getDownloadPriority() + ")" +
                "}";
    }

}