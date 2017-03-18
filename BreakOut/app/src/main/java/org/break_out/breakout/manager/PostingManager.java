package org.break_out.breakout.manager;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import org.break_out.breakout.model.BOLocation;
import org.break_out.breakout.constants.Constants;
import org.break_out.breakout.model.BOMedia;
import org.break_out.breakout.model.Challenge;
import org.break_out.breakout.model.Posting;
import org.break_out.breakout.ui.activities.PostScreenActivity;
import org.break_out.breakout.ui.fragments.LoadingListener;
import org.break_out.breakout.ui.fragments.SelectedPostingFragment;
import org.break_out.breakout.util.URLUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


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
        if (instance == null) {
            instance = new PostingManager();
        }
        return instance;
    }

    public static Posting buildPosting(String message, BOLocation location, BOMedia media) {
        return new Posting(message, location, media);
    }

    public void sendPostingToServer(Context c, Posting p, @Nullable Challenge chosenChallenge, PostScreenActivity.PostingSentListener listener) {
        new SendPostToServerTask(c, p, chosenChallenge, listener).execute();
    }

    // TODO: Use Retrofit
    public void likePosting(Context c, Posting posting) {
        new LikePostTask(c, posting).execute();
    }

    // TODO: Use Retrofit
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
            if(!progressDialog.isShowing()) {
                progressDialog.show();
            }
        }

        @Override
        protected Posting doInBackground(Void... params) {
            try {

                OkHttpClient client = new OkHttpClient.Builder()
                        .build();

                JSONObject requestObject = new JSONObject();
                requestObject.accumulate("text", posting.getText())
                        .accumulate("date", posting.getCreatedTimestamp());
                if(posting.hasMedia()){
                    requestObject.accumulate("uploadMediaTypes", new JSONArray().put("image"));
                }
                if (posting.getLocation() != null) {
                    requestObject.accumulate("postingLocation", new JSONObject()
                            .accumulate("latitude", posting.getLocation().getLatitude())
                            .accumulate("longitude", posting.getLocation().getLongitude()));
                }

                RequestBody requestBody = RequestBody.create(JSON, requestObject.toString());


                Request request = new Request.Builder()
                        .addHeader("Authorization", "Bearer " + UserManager.getInstance(context).getCurrentUser().getAccessToken())
                        .addHeader("Content-Type", "application/json")
                        .url(URLUtils.getBaseUrl(context) + "/posting/")
                        .post(requestBody)
                        .build();

                Response response = client.newCall(request).execute();
                String responseBody = response.body().string();
                JSONObject responseJSON = new JSONObject(responseBody);
                Log.d(TAG, responseJSON.toString());
                posting.setRemoteID(responseJSON.getInt("id"));
                if (posting.hasMedia()) {
                    JSONObject mediaDataJSON = new JSONObject(new JSONArray(responseJSON.getString("media")).getJSONObject(0).toString());
                    posting.setUploadCredentials(mediaDataJSON.getString("id"), mediaDataJSON.getString("uploadToken"));
                    Log.d(TAG,"credentials: "+posting.getUploadToken()+" "+posting.getMediaId());
                }
                response.body().close();

                return posting;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Posting posting) {
            super.onPostExecute(posting);
            if (posting != null) {
                if (posting.hasMedia()) {
                    if (posting.hasUploadCredentials()) {
                        if (chosenChallenge != null) {
                            new PostChallengeTask(context, chosenChallenge, posting, null).execute();
                        }
                        new UploadMediaToServerTask(posting, curListener, progressDialog).execute();
                    } else {
                        if (chosenChallenge != null) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            new PostChallengeTask(context, chosenChallenge, posting, curListener).execute();
                        } else {
                            curListener.onPostSend();
                        }
                    }
                } else {
                    if (chosenChallenge != null) {
                        new PostChallengeTask(context, chosenChallenge, posting, curListener).execute();
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    } else {
                        curListener.onPostSend();
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                }
            } else {
                Toast.makeText(context, "something went wrong", Toast.LENGTH_SHORT).show();
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                curListener.onPostSend();
            }
            if(progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    // TODO: Use Retrofit
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
            if(chosenChallenge != null){
                try {
                    OkHttpClient client = new OkHttpClient.Builder()
                            .build();
                    JSONObject challengeObject = new JSONObject();
                    challengeObject.accumulate("postingId", posting.getRemoteID())
                            .accumulate("status", Challenge.STATE.WITH_PROOF.toString());
                    Request challengeRequest = new Request.Builder()
                            .addHeader("challengeId", chosenChallenge.getRemoteID() + "")
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Authorization", "Bearer " + UserManager.getInstance(context).getCurrentUser().getAccessToken())
                            .url(URLUtils.getBaseUrl(context) + "/event/" + chosenChallenge.getEventID() + "/team/" + UserManager.getInstance(context).getCurrentUser().getTeamId() + "/challenge/" + chosenChallenge.getRemoteID() + "/status/")
                            .put(RequestBody.create(JSON, challengeObject.toString()))
                            .build();

                    client.newCall(challengeRequest).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (listener != null) {
                listener.onPostSend();
            }
            BOLocationManager.getInstance(context).postUnUploadedLocationsToServer();
        }
    }

    // TODO: Maybe use Retrofit?
    private class UploadMediaToServerTask extends AsyncTask<Void, Integer, Boolean> {
        Posting toBeUploadedPosting;
        String attachmentFileName = "";
        PostScreenActivity.PostingSentListener curListener;
        ProgressDialog dialog;

        public UploadMediaToServerTask(Posting posting, PostScreenActivity.PostingSentListener listener, @Nullable ProgressDialog dialog) {
            toBeUploadedPosting = posting;
            attachmentFileName = toBeUploadedPosting.getMedia().getFile().getName();
            curListener = listener;
            if (toBeUploadedPosting.getMediaFile() == null) {
                //TODO:Handle missing file
            }
            this.dialog = dialog;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (dialog != null) {
                dialog.setMessage("uploading Media...");
                if(!dialog.isShowing()) {
                    dialog.show();
                }
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (toBeUploadedPosting.getMediaFile() != null && toBeUploadedPosting.getMediaFile().length() > 0) {
                Log.d(TAG,"post media task called");
                try {
                    OkHttpClient client = new OkHttpClient.Builder()
                            .build();

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
                    if (!response.isSuccessful()) {
                        //TODO: handle errors
                    } else {
                        //find out the media id to prevent double loading
                        String responseBody = response.body().string();
                        Log.d(TAG, "response : " + responseBody);
                        return true;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                curListener.onPostSend();
            }
            if (dialog != null) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        }
    }

    // TODO: Use Retrofit
    public class LikePostTask extends AsyncTask<Void, Void, Boolean> {
        private Context c;
        private Posting p;

        public LikePostTask(Context c, Posting p) {
            this.c = c;
            this.p = p;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .build();
            try {
                Request likeRequest = new Request.Builder().url(URLUtils.getBaseUrl(c) + "/posting/" + p.getRemoteID() + "/like/")
                        .addHeader("Authorization", "Bearer " + UserManager.getInstance(c).getCurrentUser().getAccessToken())
                        .post(RequestBody.create(JSON, new JSONObject().put("date", System.currentTimeMillis() / 1000).toString()))
                        .build();
                Response response = client.newCall(likeRequest).execute();
                String responseString = response.body().string();
                Log.d(TAG, responseString);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public interface PostingListener {
        void onPostingListChanged();
    }

    public interface NewPostingFetchedListener extends PostingListener {
        void noNewPostings();
    }
}
