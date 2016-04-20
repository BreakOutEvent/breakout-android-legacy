package org.break_out.breakout.sync.model;

import android.content.Context;
import android.util.Log;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

/**
 * Created by Tino on 07.01.2016.
 */
public abstract class BOSyncEntity extends SugarRecord {

    @Ignore
    private static final String TAG = "BOSyncEntitity";

    @Ignore
    public static final String COLUMN_REMOTE_ID = "_remote_id";
    @Ignore
    public static final String COLUMN_IS_UPLOADING = "_is_uploading";
    @Ignore
    public static final String COLUMN_IS_UPDATING = "_is_updating";
    @Ignore
    public static final String COLUMN_IS_DELETING = "_is_deleting";
    @Ignore
    public static final String COLUMN_IS_DOWNLOADING = "_is_downloading";
    @Ignore
    public static final String COLUMN_IS_INVALID = "_is_invalid";
    @Ignore
    public static final String COLUMN_DOWNLOAD_PRIORITY = "_download_priority";

    @Ignore
    public static final int PRIORITY_NONE = 0;
    @Ignore
    public static final int PRIORITY_LOW = 1;
    @Ignore
    public static final int PRIORITY_MID = 2;
    @Ignore
    public static final int PRIORITY_HIGH = 3;

    private long _remoteId = -1;

    private boolean _isUploading = false;
    private boolean _isUpdating = false;
    private boolean _isDeleting = false;
    private boolean _isDownloading = false;
    private boolean _isInvalid = false;
    private boolean _isDeleted = false;

    private int _downloadPriority = PRIORITY_NONE;

    @Ignore
    public enum SyncState {
        UPLOADING,
        UPDATING,
        DELETING,
        DOWNLOADING,
        INVALID,
        DELETED,
        NORMAL
    }

    // SugarORM needs an empty constructor
    public BOSyncEntity() {

    }

    public void setRemoteId(long remoteId) {
        if(remoteId < 0) {
            _remoteId = -1;
            Log.e(TAG, "Cannot set remote ID to a negative value!");

            return;
        }

        _remoteId = remoteId;
    }

    public long getRemoteId() {
        return _remoteId;
    }

    public boolean hasRemoteId() {
        return (_remoteId != -1);
    }

    public void setState(SyncState state) {
        switch(state) {
            case UPLOADING:
                _isUploading = true;
                _isUpdating = false;
                _isDeleting = false;
                _isDownloading = false;
                _isInvalid = false;
                _isDeleted = false;
                break;
            case UPDATING:
                _isUploading = false;
                _isUpdating = true;
                _isDeleting = false;
                _isDownloading = false;
                _isInvalid = false;
                _isDeleted = false;
                break;
            case DELETING:
                _isUploading = false;
                _isUpdating = false;
                _isDeleting = true;
                _isDownloading = false;
                _isInvalid = false;
                _isDeleted = false;
                break;
            case DOWNLOADING:
                _isUploading = false;
                _isUpdating = false;
                _isDeleting = false;
                _isDownloading = true;
                _isInvalid = false;
                _isDeleted = false;
                break;
            case INVALID:
                _isUploading = false;
                _isUpdating = false;
                _isDeleting = false;
                _isDownloading = false;
                _isInvalid = true;
                _isDeleted = false;
                break;
            case DELETED:
                _isUploading = false;
                _isUpdating = false;
                _isDeleting = false;
                _isDownloading = false;
                _isInvalid = false;
                _isDeleted = true;
                break;
            case NORMAL:
            default:
                _isUploading = false;
                _isUpdating = false;
                _isDeleting = false;
                _isDownloading = false;
                _isInvalid = false;
                break;
        }
    }

    public SyncState getState() {
        if(_isUploading) {
            return SyncState.UPLOADING;
        } else if(_isUpdating) {
            return SyncState.UPDATING;
        } else if(_isDeleting) {
            return SyncState.DELETING;
        } else if(_isDownloading) {
            return SyncState.DOWNLOADING;
        } else if(_isInvalid) {
            return SyncState.INVALID;
        } else if(_isDeleted) {
            return SyncState.DELETED;
        } else {
            return SyncState.NORMAL;
        }
    }

    public boolean isUploading() {
        return _isUploading;
    }

    public boolean isUpdating() {
        return _isUpdating;
    }

    public boolean isDeleting() {
        return _isDeleting;
    }

    public boolean isDownloading() {
        return _isDownloading;
    }

    public boolean isInvalid() {
        return _isInvalid;
    }

    public boolean isDeleted() {
        return _isDeleted;
    }

    public abstract boolean uploadToServerSync(Context context);

    public abstract boolean updateOnServerSync(Context context);

    public abstract boolean deleteOnServerSync(Context context);

    public void setDownloadPriority(int priority) {
        if(priority < PRIORITY_NONE) {
            priority = PRIORITY_NONE;
        }

        _downloadPriority = priority;
    }

    public int getDownloadPriority() {
        return _downloadPriority;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }

        if(!(obj instanceof BOSyncEntity)) {
            return false;
        }

        final BOSyncEntity other = (BOSyncEntity) obj;
        if(getRemoteId() != other.getRemoteId()) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return new Long(getRemoteId()).hashCode();
    }

}
