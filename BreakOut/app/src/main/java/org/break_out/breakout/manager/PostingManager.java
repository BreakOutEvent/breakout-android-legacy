package org.break_out.breakout.manager;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import org.break_out.breakout.BOLocation;
import org.break_out.breakout.constants.Constants;
import org.break_out.breakout.model.BOMedia;
import org.break_out.breakout.sync.model.Posting;
import org.break_out.breakout.ui.activities.PostScreenActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import okhttp3.*;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;


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

    public static Posting buildPosting(String message) {
        return new Posting(message, null, null);
    }

    public static Posting buildPosting(String message, BOLocation location) {
        return new Posting(message, location, null);
    }

    public static Posting buildPosting(String message, BOLocation location, BOMedia media) {
        return new Posting(message,location,media);
    }

    public static Posting buildPosting(String message,BOMedia media) {
        return new Posting(message,null,media);
    }

    public void sendPostingToServer(Context c, Posting p, PostScreenActivity.PostingSentListener listener) {
        new SendPostToServerTask(c, p,listener).execute();
    }

    public static Posting buildRemotPosting(String username,String message,BOLocation location,BOMedia media) {
        Posting returnPosting = buildPosting(message,location,media);
        returnPosting.setUsername(username);
        return returnPosting;
    }

    /**
     * get posting from already saved postings by id
     * @param id remoteID of the posting
     * @return posting with matching remote ID or null
     */
    @Nullable
    public Posting getPostingById(int id) {
        ArrayList<Posting> postings = new ArrayList<>();
        postings.addAll(Posting.listAll(Posting.class));
        for(Posting curPosting : postings) {
            if(Integer.parseInt(curPosting.getRemoteID()) == id) {
                return curPosting;
            }
        }
        return null;
    }


    public void uploadImage(Posting posting) {
        if (posting.hasMedia()) {
            File imageFile = posting.getMediaFile();
        }
    }

    public void getAllPosts(Context c,@Nullable PostingListener postingListener) {
        new FetchPostingsTask(c,postingListener).execute();
    }

    public void resetPostingList() {
        Posting.deleteAll(Posting.class);
    }

    private class SendPostToServerTask extends AsyncTask<Void, Void, Posting> {
        private Posting posting;
        private Context context;
        private PostScreenActivity.PostingSentListener curListener;

        public SendPostToServerTask(Context c, Posting posting, PostScreenActivity.PostingSentListener listener) {
            this.posting = posting;
            context = c;
            curListener = listener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
                requestObject.accumulate("text",posting.getText())
                        .accumulate("date",posting.getCreatedTimestamp())
                        .accumulate("uploadMediaTypes",new JSONArray().put("image"));
                if(posting.getLocation()!=null) {
                    requestObject.accumulate("postingLocation",new JSONObject()
                            .accumulate("latitude",posting.getLocation().getLatitude())
                            .accumulate("longitude",posting.getLocation().getLongitude()));
                } else {
                    Log.d(TAG,"location is null. wtf");
                }

                RequestBody requestBody = RequestBody.create(JSON, requestObject.toString());

                Log.d(TAG,"Json request:\n"+requestObject.toString());

                Request request = new Request.Builder()
                        .addHeader("Authorization", "Bearer "+UserManager.getInstance(context).getCurrentUser().getAccessToken())
                        .addHeader("Content-Type","application/json")
                        .url(Constants.Api.BASE_URL+"/posting/")
                        .post(requestBody)
                        .build();

                Response response = client.newCall(request).execute();
                String responseBody = response.body().string();
                Log.d(TAG,responseBody);
                if(posting.hasMedia()) {
                    JSONObject responseJSON = new JSONObject(responseBody);
                    JSONObject mediaDataJSON = new JSONObject(new JSONArray(responseJSON.getString("media")).getJSONObject(0).toString());
                    posting.setUploadCredentials(mediaDataJSON.getString("id"),mediaDataJSON.getString("uploadToken"));
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
            if (posting.hasMedia()) {
                if (posting.hasUploadCredentials()) {
                    new UploadMediaToServerTask(posting,curListener).execute();
                }
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

    private class FetchPostingsTask extends AsyncTask<Void,Void,ArrayList<Posting>> {
        private PostingListener listener;
        private Context context;

        public FetchPostingsTask(Context c) {context = c;}

        public FetchPostingsTask(Context c,PostingListener listener) {
            this.listener = listener;
            context = c;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
                    responseList = generateFromJSON(context,array);

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
            Log.d(TAG,"onProgressUpdate called, posting saved");
        }

        @Override
        protected void onPostExecute(ArrayList<Posting> postings) {
            super.onPostExecute(postings);
            for(Posting p : postings) {
                if(getPostingById(Integer.parseInt(p.getRemoteID())) == null) {
                    p.save();
                    onProgressUpdate();
                }
            }
            if(listener != null) {
                listener.onPostingListChanged();
            }
        }

        private ArrayList<Posting> generateFromJSON(Context c,JSONArray array) {
            ArrayList<Posting> responseList = new ArrayList<Posting>();
            try {
                for(int i=0; i<array.length();i++) {
                    JSONObject object = array.getJSONObject(i);
                    Posting tempPost = Posting.fromJSON(c,object);
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

        public UploadMediaToServerTask(Posting posting, PostScreenActivity.PostingSentListener listener) {
            toBeUploadedPosting = posting;
            attachmentFileName = toBeUploadedPosting.getMedia().getFile().getName();
            curListener = listener;
            if(toBeUploadedPosting.getMediaFile() == null){
                //TODO:Handle missing file
                Log.d(TAG,"file not found");
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if(toBeUploadedPosting.getMediaFile() != null && toBeUploadedPosting.getMediaFile().length()>0) {
                try {
                    OkHttpClient client = new OkHttpClient();

                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("id", toBeUploadedPosting.getRemoteID())
                            .addFormDataPart("file", toBeUploadedPosting.getMediaFile().getName(),
                                    RequestBody.create(MediaType.parse("image/jpeg"),toBeUploadedPosting.getMediaFile()))
                            .build();

                    Request request = new Request.Builder()
                            .header("X-UPLOAD-TOKEN", toBeUploadedPosting.getUploadToken())
                            .url(Constants.Api.MEDIA_URL)
                            .post(requestBody)
                            .build();

                    Response response = client.newCall(request).execute();
                    if(!response.isSuccessful()) {
                        //TODO: handle errors
                        Log.d(TAG,"no success: "+response.code()+" message: "+response.message());
                    } else {
                        //find out the media id to prevent double loading
                        String responseBody = response.body().string();
                        Log.d(TAG,"Body: "+responseBody);
                        JSONObject responseObject = new JSONObject(responseBody);
                        JSONArray mediaArray = responseObject.getJSONArray("media");
                        toBeUploadedPosting.getMedia().setID(mediaArray.getJSONObject(0).getInt("id"));
                        toBeUploadedPosting.getMedia().setIsDownloaded(true);
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
            if(result) {
                curListener.onPostSend();
            }
        }
    }

    public interface PostingListener {
        void onPostingListChanged();
    }
}
