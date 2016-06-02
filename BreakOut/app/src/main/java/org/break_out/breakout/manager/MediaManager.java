package org.break_out.breakout.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageView;

import org.break_out.breakout.model.BOMedia;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Maximilian Dühr on 04.05.2016.
 */
public class MediaManager {
    private final static String TAG = "MediaManager";
    private static MediaManager instance;
    private LruCache<String, Bitmap> _lruCache;


    private MediaManager() {
    }

    public static MediaManager getInstance() {
        if(instance == null) {
            instance = new MediaManager();
        }
        return instance;
    }

    public static ArrayList<BOMedia> getSavedMediaList() {
        ArrayList<BOMedia> resultList = new ArrayList<>();
        resultList.addAll(BOMedia.listAll(BOMedia.class));
        return resultList;
    }

    public ArrayList<BOMedia> getSavedVideoList() {
        ArrayList<BOMedia> resultList = new ArrayList<>();
        ArrayList<BOMedia> allMediaList = getSavedMediaList();
        for(BOMedia m : allMediaList) {
            if(m.getType() == BOMedia.TYPE.VIDEO) {
                resultList.add(m);
            }
        }
        return resultList;
    }

    public ArrayList<BOMedia> getSavedPictureList() {
        ArrayList<BOMedia> resultList = new ArrayList<>();
        ArrayList<BOMedia> allMediaList = getSavedMediaList();
        for(BOMedia m : allMediaList) {
            if(m.getType() == BOMedia.TYPE.IMAGE) {
                resultList.add(m);
            }
        }
        return resultList;
    }

    public ArrayList<BOMedia> getSavedAudioList() {
        ArrayList<BOMedia> resultList = new ArrayList<>();
        ArrayList<BOMedia> allMediaList = getSavedMediaList();
        for(BOMedia m : allMediaList) {
            if(m.getType() == BOMedia.TYPE.AUDIO) {
                resultList.add(m);
            }
        }
        return resultList;
    }

    public ArrayList<BOMedia> getUndownloadedMediaList() {
        ArrayList<BOMedia> resultList = new ArrayList<>();
        ArrayList<BOMedia> allMediaList = getSavedMediaList();
        resultList.clear();
        for(BOMedia m : allMediaList) {
            if(!m.isDownloaded()) {
                if(!m.getUrl().isEmpty()) {
                    resultList.add(m);
                }
            }
        }
        return resultList;
    }

    public void addToCache(String key, Bitmap bitmap) {
        if(!(key==null || bitmap == null)) {
            if(getLruCache().get(key) == null) {
                getLruCache().put(key, bitmap);
            }
        }
    }

    public LruCache<String,Bitmap> getLruCache() {
        if(_lruCache == null) {
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
            final int cacheSize = maxMemory / 8;
            _lruCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap value) {
                    return value.getByteCount() / 1024;
                }
            };
        }
        return _lruCache;
    }

    @Nullable
    public Bitmap getFromCache(String key) {
        return getLruCache().get(key);
    }

    public BOMedia createExternalMedia(Context c, BOMedia.TYPE type) {
        //TODO
        File outputFile = null;
        String filename = UUID.randomUUID().toString();
        switch(type) {
            case IMAGE:
                outputFile = new File(c.getExternalCacheDir(), filename + ".jpg");
                break;
            case VIDEO:
                outputFile = new File(c.getExternalCacheDir(), filename + ".mp4");
                break;
            case AUDIO:
                outputFile = new File(c.getExternalCacheDir(), filename + ".mp3");
                break;
            default:
                outputFile = new File(c.getExternalCacheDir(), filename + ".jpg");
        }
        BOMedia resultMedia = new BOMedia("", BOMedia.TYPE.IMAGE, outputFile);
        if(resultMedia.getUrl().isEmpty()) {
            resultMedia.setSaveState(BOMedia.SAVESTATE.TEMP);
        } else {
            resultMedia.setSaveState(BOMedia.SAVESTATE.SAVED);
        }
        resultMedia.save();
        return resultMedia;
    }

    public static BOMedia createInternalMedia(Context c, BOMedia.TYPE type) {
        File outputFile = null;
        String filename = UUID.randomUUID().toString();
        switch(type) {
            case IMAGE:
                outputFile = new File(c.getFilesDir(), filename + ".jpg");
                break;
            case VIDEO:
                outputFile = new File(c.getFilesDir(), filename + ".mp4");
                break;
            case AUDIO:
                outputFile = new File(c.getFilesDir(), filename + ".mp3");
                break;
            default:
                outputFile = new File(c.getExternalCacheDir(), filename + ".jpg");
        }
        BOMedia resultMedia = new BOMedia("", BOMedia.TYPE.IMAGE, outputFile);
        if(resultMedia.getUrl().isEmpty()) {
            resultMedia.setSaveState(BOMedia.SAVESTATE.TEMP);
        } else {
            resultMedia.setSaveState(BOMedia.SAVESTATE.SAVED);
        }
        resultMedia.save();
        return resultMedia;
    }


    public static boolean isMediaAlreadySaved(int remoteID) {
        boolean result = false;
        for(BOMedia media : getSavedMediaList()) {
            if(media.getRemoteID() == remoteID) {
                result = true;
            }
        }
        return result;
    }

    public static boolean isAtLeastSize(int remoteId, BOMedia.SIZE minimumSize) {
        if(isMediaAlreadySaved(remoteId)) {
            switch(minimumSize) {
                case SMALL:
                    return true;
                case MEDIUM:
                    return getMediaByID(remoteId).getSize() == BOMedia.SIZE.MEDIUM || getMediaByID(remoteId).getSize() == BOMedia.SIZE.LARGE;
                case LARGE:
                    return minimumSize == BOMedia.SIZE.LARGE;
            }
        }
        return false;
    }

    public void setMedia(BOMedia media, View v) {
        media = getMediaByID(media.getRemoteID());
        if(v instanceof ImageView) {
            ImageView view = (ImageView) v;
            view.setImageURI(media.getFileUri());

        }
    }

    public void setSizedImage(BOMedia media, ImageView iv, BOMedia.SIZE size, boolean isSquare) {
        int width = 100;
        int height = 100;
        switch(size) {
            case SMALL:
                if(isSquare) {
                    width = height = 100;
                } else {
                    width = 100;
                    height = 75;
                }
                break;
            case MEDIUM:
                if(isSquare) {
                    width = height = 400;
                } else {
                    width = 400;
                    height = 350;
                }
                break;
            case LARGE:
                if(isSquare) {
                    width = height = 800;
                } else {
                    width = 800;
                    height = 650;
                }
                break;

        }
        Bitmap mediumBitmap = decodeSampledBitmapFromFile(media,width,height);
        iv.setImageBitmap(mediumBitmap);
    }

    /**
     * Moves file of incoming post to internal Storage
     *
     * @param c
     * @param media
     * @param listener
     */
    public static void moveToInternal(Context c, final BOMedia media, @Nullable OnFileMovedListener listener) {
        String postfix = ".jpg";
        CutAndPastePostingToInternalRunnable runnable = new CutAndPastePostingToInternalRunnable(media, c, listener);
        Handler movingHandler = new Handler();
        movingHandler.post(runnable);

    }

    public static BOMedia createMedia(int id, BOMedia.TYPE type, String url) {
        if(isMediaAlreadySaved(id)) {

        }
        return null;
    }

    public static Bitmap decodeSampledBitmapFromFile(BOMedia media, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(media.getFile().getPath(), options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(media.getFile().getPath(), options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if(height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    @Nullable
    public static BOMedia getMediaByID(int id) {
        List<BOMedia> medias = BOMedia.findWithQuery(BOMedia.class, "SELECT * FROM BO_Media WHERE _REMOTE_ID='" + id + "' LIMIT 1");
        BOMedia m = medias.size() == 1 ? medias.get(0) : null;
        return m;
    }

    public static void loadMediaFromServer(BOMedia media, @Nullable ImageView populateView, BOMedia.SIZE size) {
        new LoadImageTask(media,populateView,size).execute();
    }

    private static class CutAndPastePostingToInternalRunnable implements Runnable {
        private BOMedia _media;
        private Context _context;
        private OnFileMovedListener _listener = null;
        private boolean hasListener = false;

        public CutAndPastePostingToInternalRunnable(BOMedia m, Context c, @Nullable OnFileMovedListener listener) {
            _media = m;
            _context = c;
            if(listener != null) {
                _listener = listener;
                hasListener = true;
            }
        }

        @Override
        public void run() {
            //declare both files
            File fromFile = _media.getFile();
            File toFile = new File(_context.getFilesDir(), UUID.randomUUID().toString() + "." + getFileExt(fromFile.getName()));

            try {
                FileInputStream inputStream = new FileInputStream(fromFile);
                FileOutputStream outputStream = new FileOutputStream(toFile);

                byte[] buffer = new byte[1024];
                int length;
                while((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }

                //establish new reference
                _media.setFile(toFile);
                _media.setSaveState(BOMedia.SAVESTATE.SAVED);
                _media.save();

                //delete old reference
                fromFile.delete();

                if(hasListener) {
                    _listener.onFileMoved(toFile);
                }


            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        private String getFileExt(String filename) {
            return filename.substring(filename.lastIndexOf(".") + 1, filename.length());
        }
    }

    public interface OnFileMovedListener {
        void onFileMoved(File result);
    }

    private static class LoadImageTask extends AsyncTask<Void, Void, File> {
        private BOMedia downloadingMedia;
        private BOMedia.SIZE size;
        private ImageView populateView;

        public LoadImageTask(BOMedia media, ImageView iv, BOMedia.SIZE size) {
            downloadingMedia = media;
            populateView = iv;
        }

        @Override
        protected File doInBackground(Void... params) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(downloadingMedia.getUrl())
                    .build();
            try {
                Response response = client.newCall(request).execute();
                InputStream inputStream = response.body().byteStream();
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                OutputStream outStream = new FileOutputStream(downloadingMedia.getFile());
                byte[] buffer = new byte[1024];
                int read;
                while((read = bufferedInputStream.read(buffer)) >= 0) {
                    outStream.write(buffer, 0, read);
                }
                outStream.flush();
                outStream.close();
                bufferedInputStream.close();
                response.body().close();
                return downloadingMedia.getFile();
            } catch(IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(File resFile) {
            super.onPostExecute(resFile);
            if(resFile != null) {
                if(resFile.length() > 0) {
                    downloadingMedia.setIsDownloaded(true);
                    Bitmap resultBitmap = size == BOMedia.SIZE.SMALL ? MediaManager.decodeSampledBitmapFromFile(downloadingMedia, 50, 50) : MediaManager.decodeSampledBitmapFromFile(downloadingMedia, 200, 200);
                    if(populateView != null) {
                        populateView.setImageBitmap(resultBitmap);
                    }
                    MediaManager.getInstance().addToCache(downloadingMedia.getUrl(), resultBitmap);
                } else {
                    return;
                }
            }
        }
    }
}
