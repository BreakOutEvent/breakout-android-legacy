package org.break_out.breakout.manager;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.break_out.breakout.BOLocation;
import org.break_out.breakout.R;
import org.break_out.breakout.constants.Constants;
import org.break_out.breakout.model.BOMedia;
import org.break_out.breakout.model.Challenge;
import org.break_out.breakout.sync.model.Posting;
import org.break_out.breakout.ui.activities.PostScreenActivity;
import org.break_out.breakout.ui.fragments.AllPostsFragment;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;


/**
 * Created by Maximilian Dühr on 24.04.2016.
 */
public class PostingManager {
    private final static String TAG = "PostingManager";
    private static PostingManager instance;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private PostingManager() {

    }

    public static PostingManager getInstance() {
        if(instance == null) {
            instance = new PostingManager();
        }
        return instance;
    }

    public static Posting buildPosting(String message) {
        return new Posting(message, null, null);
    }

    public static Posting buildPosting(String message, BOLocation location) {
        return new Posting(message, location, null);
    }

    public static Posting buildPosting(String message, BOLocation location, BOMedia media) {
        return new Posting(message, location, media);
    }

    public static Posting buildPosting(String message, BOMedia media) {
        return new Posting(message, null, media);
    }

    public void sendPostingToServer(Context c, Posting p, @Nullable Challenge chosenChallenge, PostScreenActivity.PostingSentListener listener) {
        new SendPostToServerTask(c, p, chosenChallenge, listener).execute();
    }

    public static Posting buildRemotPosting(String username, String message, BOLocation location, BOMedia media) {
        Posting returnPosting = buildPosting(message, location, media);
        returnPosting.setUsername(username);
        return returnPosting;
    }

    public ArrayList<Posting> getBeforeId(int id, int maxSize) {
        ArrayList<Posting> returnList = new ArrayList<>();
        Posting newestPosting = getNewestPosting();
        id = id == 0 ? newestPosting.getRemoteID()+1 : id;
        Log.d(TAG,"get after id: "+id);
        returnList.addAll(Posting.findWithQuery(Posting.class,"Select * FROM Posting WHERE _REMOTE_ID < "+id+" ORDER BY _REMOTE_ID DESC LIMIT "+maxSize));
        return returnList;
    }

    public ArrayList<Posting> getAfterId(int id) {
        ArrayList<Posting> returnList = new ArrayList<>();
        returnList.addAll(Posting.findWithQuery(Posting.class, "SELECT * FROM Posting WHERE _REMOTE_ID > " + id + " ORDER BY _REMOTE_ID ASC"));
        return returnList;
    }

    public void likePosting(Context c,Posting posting) {
        new LikePostTask(c,posting).execute();
    }


    public Posting getNewestPosting() {
        return getAllPostings().get(0);
    }

    public  ArrayList<Posting> getAllPostings() {
        ArrayList<Posting> postingList = new ArrayList<>();
        postingList.addAll(Posting.findWithQuery(Posting.class,"Select * FROM Posting ORDER BY _REMOTE_ID DESC"));
        for(int i = 0 ; i<postingList.size(); i++) {
            Log.d(TAG,i+". posting in list "+postingList.get(i).getRemoteID());
        }
        return postingList;
    }

    /**
     * get posting from already saved postings by id
     *
     * @param id remoteID of the posting
     * @return posting with matching remote ID or null
     */
    @Nullable
    public Posting getPostingById(int id) {
        ArrayList<Posting> postings = new ArrayList<>();
        postings.addAll(Posting.findWithQuery(Posting.class,"SELECT * FROM Posting WHERE _REMOTE_ID ="+id+" LIMIT 1"));
        if(postings.isEmpty()) {
            return null;
        } else {
            return postings.get(0);
        }
    }

    public void getAllPosts(Context c, @Nullable PostingListener postingListener, @Nullable AllPostsFragment.LoadingListener listener) {
        new FetchPostingsTask(c, postingListener, listener).execute();
    }

    public void getPostingsAfterIdFromServer(int id,@Nullable  NewPostingFetchedListener listener) {
        new GetPostingsAfterIdTask(id,listener).execute();
    }

    public void resetPostingList() {
        Posting.deleteAll(Posting.class);
    }

    private class GetPostingsAfterIdTask extends AsyncTask<Void,Void,ArrayList<Integer>> {
        private int id;
        private NewPostingFetchedListener listener = null;

        public GetPostingsAfterIdTask(int id, @Nullable NewPostingFetchedListener listener) {
            this.id = id;
            this.listener = listener;
        }

        @Override
        protected ArrayList<Integer> doInBackground(Void... params) {
            OkHttpClient client = new OkHttpClient();
            Request getRequest = new Request.Builder()
                    .url(Constants.Api.BASE_URL+"/posting/get/since/"+id+"/").build();
            Log.d(TAG,"posting manager do in background");
            try {
                Response response = client.newCall(getRequest).execute();
                String responseString = response.body().string();
                Log.d(TAG,"response: "+responseString);
                if(!responseString.isEmpty()) {
                    try {
                        JSONArray array = new JSONArray(responseString);
                        for(int i = 0; i < array.length(); i++) {
                            int id = array.getInt(i);

                        }
                    }catch(JSONException e) {
                        e.printStackTrace();
                    }
                }

            }catch(IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Integer> postingsIds) {
            super.onPostExecute(postingsIds);
            Log.d(TAG, "onPostExecute called!");
            if (postingsIds != null) {
                if (postingsIds.isEmpty()) {
                    if (listener != null) {
                        listener.noNewPostings();
                    } else {
                        listener.onPostingListChanged();
                    }
                }
            } else {
                if(listener!=null) {
                    listener.noNewPostings();
                }
            }
        }
    }

    private class SendPostToServerTask extends AsyncTask<Void, Void, Posting> {
        private Posting posting;
        private Context context;
        private Challenge chosenChallenge;
        private ProgressDialog progressDialog;
        private PostScreenActivity.PostingSentListener curListener;

        public SendPostToServerTask(Context c, Posting posting, @Nullable Challenge chosenChallenge, PostScreenActivity.PostingSentListener listener) {
            this.posting = posting;
            this.chosenChallenge = chosenChallenge;
            context = c;
            curListener = listener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("creating Posting...");
            progressDialog.show();
        }

        @Override
        protected Posting doInBackground(Void... params) {
            try {
                /*URL targetUrl = new URL(Constants.Api.BASE_URL + "/posting/");
                HttpsURLConnection connection = (HttpsURLConnection) targetUrl.openConnection();
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(10000);
                connection.setRequestMethod("POST");*/
                //connection.setRequestProperty("Accept", "*/*");
                /*connection.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Authorization", "Bearer " + UserManager.getInstance(context).getCurrentUser().getAccessToken());
                connection.setRequestProperty("Connection", "close");
                connection.setDoInput(true);
                connection.setDoOutput(true);

                NameValuePair<String> postingPair = new NameValuePair<>("text", posting.getText());
                NameValuePair<BOLocation> locationPair = new NameValuePair<>("postingLocation", posting.getLocation());
                NameValuePair<String> mediaPair = new NameValuePair<>("uploadMediaTypes", "image");

                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(outputStream, "UTF-8"));
                StringBuilder requestBuilder = new StringBuilder();
                JSONObject testObject = new JSONObject();
                testObject.accumulate(postingPair.name, postingPair.value);
                testObject.accumulate("date", System.currentTimeMillis() + "");
                testObject.accumulate(mediaPair.name, new JSONArray().put(mediaPair.value));
                writer.write(testObject.toString());
                writer.flush();
                writer.close();


                int responseCode = connection.getResponseCode();
                BufferedReader inputReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String result = "";
                StringBuilder responseBuilder = new StringBuilder();
                while ((result = inputReader.readLine()) != null) {
                    responseBuilder.append(result);
                }
                connection.connect();
                JSONObject responseObject = new JSONObject(responseBuilder.toString());
                JSONObject mediaObject = new JSONObject(new JSONArray(responseObject.getString("media")).getJSONObject(0).toString());

                posting.setUploadCredentials(mediaObject.getString("id"), mediaObject.getString("uploadToken"));*/

                OkHttpClient client = new OkHttpClient();

                JSONObject requestObject = new JSONObject();
                requestObject.accumulate("text", posting.getText())
                        .accumulate("date", posting.getCreatedTimestamp())
                        .accumulate("uploadMediaTypes", new JSONArray().put("image"));
                if(posting.getLocation() != null) {
                    requestObject.accumulate("postingLocation", new JSONObject()
                            .accumulate("latitude", posting.getLocation().getLatitude())
                            .accumulate("longitude", posting.getLocation().getLongitude()));
                }

                RequestBody requestBody = RequestBody.create(JSON, requestObject.toString());


                Request request = new Request.Builder()
                        .addHeader("Authorization", "Bearer " + UserManager.getInstance(context).getCurrentUser().getAccessToken())
                        .addHeader("Content-Type", "application/json")
                        .url(Constants.Api.BASE_URL + "/posting/")
                        .post(requestBody)
                        .build();

                Response response = client.newCall(request).execute();
                String responseBody = response.body().string();
                JSONObject responseJSON = new JSONObject(responseBody);
                Log.d(TAG, responseJSON.toString());
                posting.setRemoteID(responseJSON.getInt("id"));
                if(posting.hasMedia()) {
                    JSONObject mediaDataJSON = new JSONObject(new JSONArray(responseJSON.getString("media")).getJSONObject(0).toString());
                    posting.setUploadCredentials(mediaDataJSON.getString("id"), mediaDataJSON.getString("uploadToken"));
                }
                response.body().close();

                return posting;
            } catch(Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Posting posting) {
            super.onPostExecute(posting);
            if(posting!=null) {
                if(posting.hasMedia()) {
                    if(posting.hasUploadCredentials()) {
                        if(chosenChallenge != null) {
                            new PostChallengeTask(context, chosenChallenge, posting, null).execute();
                        }
                        new UploadMediaToServerTask(posting, curListener, progressDialog).execute();
                    } else {
                        if(chosenChallenge != null) {
                            if(progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            new PostChallengeTask(context, chosenChallenge, posting, curListener).execute();
                        }else  {
                            curListener.onPostSend();
                        }
                    }
                } else {
                    if(chosenChallenge != null) {
                        new PostChallengeTask(context, chosenChallenge, posting, curListener).execute();
                        if(progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    } else {
                        curListener.onPostSend();
                        if(progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                }
            } else {
                Toast.makeText(context,"something went wrong",Toast.LENGTH_SHORT).show();
                if(progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                curListener.onPostSend();

            }
        }

        private class NameValuePair<T> {
            String name;
            T value;

            public NameValuePair(String name, T value) {
                this.name = name;
                this.value = value;
            }
        }
    }

    private class PostChallengeTask extends AsyncTask<Void, Void, Void> {
        private Context context;
        private Challenge chosenChallenge;
        private Posting posting;
        private PostScreenActivity.PostingSentListener listener;

        public PostChallengeTask(Context context, Challenge challenge, Posting posting, @Nullable PostScreenActivity.PostingSentListener listener) {
            this.context = context;
            this.chosenChallenge = challenge;
            this.posting = posting;
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject challengeObject = new JSONObject();
                challengeObject.accumulate("postingId", posting.getRemoteID())
                        .accumulate("status", Challenge.STATE.WITH_PROOF.toString());
                Request challengeRequest = new Request.Builder()
                        .addHeader("challengeId", chosenChallenge.getRemoteID() + "")
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", "Bearer " + UserManager.getInstance(context).getCurrentUser().getAccessToken())
                        .url(Constants.Api.BASE_URL + "/event/" + chosenChallenge.getEventID() + "/team/" + UserManager.getInstance(context).getCurrentUser().getTeamId() + "/challenge/" + chosenChallenge.getRemoteID() + "/status/")
                        .put(RequestBody.create(JSON, challengeObject.toString()))
                        .build();

                Response challengeResponse = client.newCall(challengeRequest).execute();
            } catch(Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(listener != null) {
                listener.onPostSend();
            }
        }
    }

    private class FetchPostingsTask extends AsyncTask<Void, Void, ArrayList<Posting>> {
        private PostingListener listener;
        private Context context;
        private AllPostsFragment.LoadingListener loadingListener;

        public FetchPostingsTask(Context c) {
            context = c;
        }

        public FetchPostingsTask(Context c, PostingListener listener, @Nullable AllPostsFragment.LoadingListener loadingListener) {
            this.listener = listener;
            context = c;
            this.loadingListener = loadingListener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(loadingListener!=null) {
                loadingListener.onLoadingTriggered();
            }
        }

        @Override
        protected ArrayList<Posting> doInBackground(Void... params) {
            ArrayList<Posting> responseList = new ArrayList<>();
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(Constants.Api.POSTINGLIST_URL)
                    .addHeader("Accept", "application/json;")
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if(response.isSuccessful()) {
                    String JSONResponse = response.body().string();
                    JSONArray array = new JSONArray(JSONResponse);
                    responseList = generateFromJSON(context, array);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
            return responseList;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            if(listener != null) {
                listener.onPostingListChanged();
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Posting> responseList) {
            super.onPostExecute(responseList);
            Log.d(TAG,"responseList size: "+responseList.size());
            for(Posting p : responseList) {
                if(getPostingById(p.getRemoteID()) == null) {
                    Log.d(TAG,"new posting");
                    p.save();
                }
            }
            if(listener != null) {
                listener.onPostingListChanged();
            }
            if(loadingListener!=null) {
                loadingListener.onLoadingDismissed();
            }
        }

        private ArrayList<Posting> generateFromJSON(Context c, JSONArray array) {
            ArrayList<Posting> responseList = new ArrayList<Posting>();
            try {
                for(int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    Posting tempPost = Posting.fromJSON(c, object);
                    responseList.add(tempPost);
                }
            } catch(JSONException e) {
                e.printStackTrace();
            }

            return responseList;
        }
    }

    private class UploadMediaToServerTask extends AsyncTask<Void, Integer, Boolean> {
        Posting toBeUploadedPosting;
        String attachmentFileName = "";
        PostScreenActivity.PostingSentListener curListener;
        ProgressDialog dialog;

        public UploadMediaToServerTask(Posting posting, PostScreenActivity.PostingSentListener listener, @Nullable ProgressDialog dialog) {
            toBeUploadedPosting = posting;
            attachmentFileName = toBeUploadedPosting.getMedia().getFile().getName();
            curListener = listener;
            if(toBeUploadedPosting.getMediaFile() == null) {
                //TODO:Handle missing file
            }
            this.dialog = dialog;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(dialog != null) {
                dialog.setMessage("uploading Media...");
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if(toBeUploadedPosting.getMediaFile() != null && toBeUploadedPosting.getMediaFile().length() > 0) {
                try {
                    OkHttpClient client = new OkHttpClient();

                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("id", toBeUploadedPosting.getMediaId())
                            .addFormDataPart("file", toBeUploadedPosting.getMediaFile().getName(),
                                    RequestBody.create(MediaType.parse("image/jpeg"), toBeUploadedPosting.getMediaFile()))
                            .build();

                    Request request = new Request.Builder()
                            .header("X-UPLOAD-TOKEN", toBeUploadedPosting.getUploadToken())
                            .url(Constants.Api.MEDIA_URL)
                            .post(requestBody)
                            .build();

                    Response response = client.newCall(request).execute();
                    if(!response.isSuccessful()) {
                        //TODO: handle errors
                    } else {
                        //find out the media id to prevent double loading
                        String responseBody = response.body().string();
                        Log.d(TAG, "response : " + responseBody);
                        try {
                            JSONObject responseObject = new JSONObject(responseBody);
                            JSONArray mediaArray = responseObject.getJSONArray("media");
                            toBeUploadedPosting.getMedia().setRemoteID(mediaArray.getJSONObject(0).getInt("id"));
                            toBeUploadedPosting.getMedia().setIsDownloaded(true);
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }
                        return true;
                    }

                } catch(Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if(result) {
                curListener.onPostSend();
            }
            if(dialog != null) {
                if(dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        }
    }

    public class LikePostTask extends AsyncTask<Void,Void,Boolean> {
        private Context c;
        private Posting p;
        public LikePostTask(Context c,Posting p) {
            this.c = c;
            this.p = p;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            OkHttpClient client = new OkHttpClient();
            try {
                Request likeRequest = new Request.Builder().url(Constants.Api.BASE_URL + "/posting/" + p.getRemoteID() + "/like/")
                        .addHeader("Authorization", "Bearer " + UserManager.getInstance(c).getCurrentUser().getAccessToken())
                        .post(RequestBody.create(JSON, new JSONObject().put("date", System.currentTimeMillis() / 1000).toString()))
                        .build();
                Response response = client.newCall(likeRequest).execute();
                String responseString = response.body().string();
                Log.d(TAG,responseString);
            }catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public interface PostingListener {
        void onPostingListChanged();
    }

    public static interface NewPostingFetchedListener extends PostingListener {

        public abstract void noNewPostings();
    }
}
