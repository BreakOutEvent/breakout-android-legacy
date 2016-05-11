package org.break_out.breakout.manager;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import org.break_out.breakout.BOLocation;
import org.break_out.breakout.constants.Constants;
import org.break_out.breakout.sync.model.BOMedia;
import org.break_out.breakout.sync.model.Posting;
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
        return new Posting(message, location,media);
    }

    public static Posting buildPosting(String message,BOMedia media) {
        return new Posting(message,null,media);
    }

    public void sendPostingToServer(Context c, Posting p) {
        new SendPostToServerTask(c, p).execute();
    }

    public void uploadImage(Posting posting) {
        if (posting.hasMedia()) {
            File imageFile = posting.getMediaFile();
        }
    }

    public void getAllPosts(@Nullable PostingListener postingListener) {
        new FetchPostingsTask(postingListener).execute();
    }

    public void resetPostingList() {
        Posting.deleteAll(Posting.class);
    }

    private class SendPostToServerTask extends AsyncTask<Void, Void, Posting> {
        private Posting posting;
        private Context context;

        public SendPostToServerTask(Context c, Posting posting) {
            this.posting = posting;
            context = c;
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
                    new UploadMediaToServerTask(posting).execute();
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

        public FetchPostingsTask() {}

        public FetchPostingsTask(PostingListener listener) {
            this.listener = listener;
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
                    responseList = generateFromJSON(array);

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
        protected void onPostExecute(ArrayList<Posting> postings) {
            super.onPostExecute(postings);
            for(Posting p : postings) {
                p.save();
                onProgressUpdate();
            }
            if(listener != null) {
                listener.onPostingListChanged();
            }
        }

        private ArrayList<Posting> generateFromJSON(JSONArray array) {
            ArrayList<Posting> responseList = new ArrayList<Posting>();
            try {
                for(int i=0; i<array.length();i++) {
                    JSONObject object = array.getJSONObject(i);
                    Posting tempPost = Posting.fromJSON(object);
                    responseList.add(tempPost);
                }
            } catch(JSONException e) {
                e.printStackTrace();
            }

            return responseList;
        }
    }

    private class UploadMediaToServerTask extends AsyncTask<Void, Void, Boolean> {
        private Posting toBeUploadedPosting;
        String attachmentFileName = "";

        public UploadMediaToServerTask(Posting posting) {
            toBeUploadedPosting = posting;
            attachmentFileName = toBeUploadedPosting.getMedia().getFile().getName();
            if(toBeUploadedPosting.getMediaFile() == null){
                //TODO:Handle missing file
                Log.d(TAG,"file not found");
            }
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
                        //find out which URL the image is stored at and prevent double data loading
                        int size = 0;
                        String setURL = "";
                        String responseBody = response.body().string();
                        Log.d(TAG,"Body: "+responseBody);
                        /*JSONArray responseArray = new JSONArray(response.body().string());
                        for(int i = 0; i<responseArray.length(); i++) {
                            JSONObject curObject = new JSONObject(responseArray.getString(i));
                            int currentImageSize = curObject.getInt("size");
                            if(currentImageSize > size) {
                                setURL = curObject.getString("url");
                            }
                        }
                        if(!setURL.isEmpty()) {
                            toBeUploadedPosting.getMedia().setURL(setURL);
                            toBeUploadedPosting.getMedia().setSaveState(BOMedia.SAVESTATE.SAVED);
                        }*/

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    public interface PostingListener {
        void onPostingListChanged();
    }
}
