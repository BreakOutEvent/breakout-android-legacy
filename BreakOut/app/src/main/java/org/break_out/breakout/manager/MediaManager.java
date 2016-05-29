package org.break_out.breakout.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageView;

import org.break_out.breakout.model.BOMedia;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Maximilian Dühr on 04.05.2016.
 */
public class MediaManager {
    private final static String TAG = "MediaManager";
    private static MediaManager instance;
    private LruCache<String, Bitmap> _lruCache;


    private MediaManager() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        _lruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount() / 1024;
            }
        };
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
        if(_lruCache.get(key) == null) {
            _lruCache.put(key, bitmap);
        }
    }

    @Nullable
    public Bitmap getFromCache(String key) {
        return _lruCache.get(key);
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
        for(BOMedia m : getSavedMediaList()) {
            if(m.getRemoteID() == id) {
                return m;
            }
        }
        return null;
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
}
