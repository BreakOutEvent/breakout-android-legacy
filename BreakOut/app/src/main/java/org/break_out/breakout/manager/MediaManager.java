package org.break_out.breakout.manager;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;

import org.break_out.breakout.sync.model.BOMedia;
import org.break_out.breakout.sync.model.Posting;

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


    private MediaManager() {

    }

    public static MediaManager getInstance() {
        if (instance == null) {
            instance = new MediaManager();
        }
        return instance;
    }

    public ArrayList<BOMedia> getSavedMediaList() {
        ArrayList<BOMedia> resultList = new ArrayList<>();
        resultList.addAll(BOMedia.listAll(BOMedia.class));
        return resultList;
    }

    public ArrayList<BOMedia> getSavedVideoList() {
        ArrayList<BOMedia> resultList = new ArrayList<>();
        ArrayList<BOMedia> allMediaList = getSavedMediaList();
        for (BOMedia m : allMediaList) {
            if (m.getType() == BOMedia.TYPE.VIDEO) {
                resultList.add(m);
            }
        }
        return resultList;
    }

    public ArrayList<BOMedia> getSavedPictureList() {
        ArrayList<BOMedia> resultList = new ArrayList<>();
        ArrayList<BOMedia> allMediaList = getSavedMediaList();
        for (BOMedia m : allMediaList) {
            if (m.getType() == BOMedia.TYPE.IMAGE) {
                resultList.add(m);
            }
        }
        return resultList;
    }

    public ArrayList<BOMedia> getSavedAudioList() {
        ArrayList<BOMedia> resultList = new ArrayList<>();
        ArrayList<BOMedia> allMediaList = getSavedMediaList();
        for (BOMedia m : allMediaList) {
            if (m.getType() == BOMedia.TYPE.AUDIO) {
                resultList.add(m);
            }
        }
        return resultList;
    }

    public BOMedia createTempMedia(Context c, BOMedia.TYPE type) {
        //TODO
        File outputFile = null;
        String filename = UUID.randomUUID().toString();
        switch (type) {
            case IMAGE:
                outputFile = new File(c.getExternalCacheDir(), filename + ".jpg");
                break;
            case VIDEO:
                outputFile = new File(c.getFilesDir(), filename + ".mp4");
                break;
            case AUDIO:
                outputFile = new File(c.getFilesDir(), filename + ".mp3");
        }
        BOMedia resultMedia = new BOMedia("", BOMedia.TYPE.IMAGE, outputFile);
        if (resultMedia.getUrl().isEmpty()) {
            resultMedia.setSaveState(BOMedia.SAVESTATE.TEMP);
        } else {
            resultMedia.setSaveState(BOMedia.SAVESTATE.SAVED);
        }
        resultMedia.save();
        return resultMedia;
    }

    /**
     * Moves file of incoming post to internal Storage
     * @param c
     * @param media
     * @param listener
     * */
    public void moveToInternal(Context c, final BOMedia media,@Nullable OnFileMovedListener listener) {
        String postfix = ".jpg";
        CutAndPastePostingToInternalRunnable runnable = new CutAndPastePostingToInternalRunnable(media, c, listener);
        Handler movingHandler = new Handler();
        movingHandler.post(runnable);

    }

    private class CutAndPastePostingToInternalRunnable implements Runnable {
        private BOMedia _media;
        private Context _context;
        private OnFileMovedListener _listener = null;
        private boolean hasListener = false;
        public CutAndPastePostingToInternalRunnable(BOMedia m, Context c,@Nullable OnFileMovedListener listener) {
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
            File toFile = new File(_context.getFilesDir(),UUID.randomUUID().toString()+"."+getFileExt(fromFile.getName()));

            try {
                FileInputStream inputStream = new FileInputStream(fromFile);
                FileOutputStream outputStream = new FileOutputStream(toFile);

                byte[] buffer = new byte[1024];
                int length;
                while((length = inputStream.read(buffer))>0) {
                    outputStream.write(buffer,0,length);
                }

                //establish new reference
                _media.setFile(toFile);
                _media.setSaveState(BOMedia.SAVESTATE.SAVED);

                //delete old reference
                fromFile.delete();

                if(hasListener) {
                    _listener.onFileMoved(toFile);
                }


            }catch(Exception e) {
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
