package org.break_out.breakout.manager;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.break_out.breakout.BOLocation;
import org.break_out.breakout.constants.Constants;
import org.break_out.breakout.sync.model.Posting;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.*;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;


/**
 * Created by Maximilian Dühr on 24.04.2016.
 */
public class PostingManager {
    private final static String TAG = "PostingManager";
    private static PostingManager instance;

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

    public static Posting buildPosting(String message, BOLocation location, File file) {
        return new Posting(message, location, file);
    }

    public void sendPostingToServer(Context c, Posting p) {
        new SendPostToServerTask(c, p).execute();
    }

    public void uploadImage(Posting posting) {
        if (posting.hasImage()) {
            File imageFile = posting.getImageFile();
        }
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
                URL targetUrl = new URL(Constants.Api.BASE_URL + "/posting/");
                HttpsURLConnection connection = (HttpsURLConnection) targetUrl.openConnection();
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(10000);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Accept", "*/*");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Authorization", "Bearer " + UserManager.getInstance(context).getCurrentUser().getAccessToken());
                connection.setRequestProperty("Connection", "close");
                connection.setDoInput(true);
                connection.setDoOutput(true);

                NameValuePair<String> postingPair = new NameValuePair<>("text", posting.getText());
                NameValuePair<BOLocation> locationPair = new NameValuePair<>("postingLocation", posting.getLocation());
                NameValuePair<String> mediaPair = new NameValuePair<>("media", "image");

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

                posting.setUploadCredentials(mediaObject.getString("id"), mediaObject.getString("uploadToken"));
                return posting;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Posting posting) {
            super.onPostExecute(posting);
            if (posting.hasImage()) {
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

    private class UploadMediaToServerTask extends AsyncTask<Void, Void, Boolean> {
        private Posting toBeUploadedPosting;
        String attachmentFileName = "";

        public UploadMediaToServerTask(Posting posting) {
            toBeUploadedPosting = posting;
            attachmentFileName = toBeUploadedPosting.getImageFile().getName();
            if(toBeUploadedPosting.getImageFile() == null){
                //TODO:Handle missing file
                Log.d(TAG,"file not found");
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if(toBeUploadedPosting.getImageFile() != null && toBeUploadedPosting.getImageFile().length()>0) {
                try {
                    OkHttpClient client = new OkHttpClient();

                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("id", toBeUploadedPosting.getRemoteID())
                            .addFormDataPart("file", toBeUploadedPosting.getImageFile().getName(),
                                    RequestBody.create(MediaType.parse("image/jpeg"),toBeUploadedPosting.getImageFile()))
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
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}
