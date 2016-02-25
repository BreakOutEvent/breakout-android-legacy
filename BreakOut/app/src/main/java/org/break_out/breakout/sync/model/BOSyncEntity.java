package org.break_out.breakout.sync.model;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

/**
 * Created by Tino on 07.01.2016.
 */
public abstract class BOSyncEntity extends SugarRecord {

    @Ignore
    public static final String ID_COLUMN = "id";

    @Ignore
    public static final String IS_UPLOADING_COLUMN = "_is_uploading";

    @Ignore
    public static final String IS_UPDATING_COLUMN = "_is_updating";

    @Ignore
    public static final String IS_DELETING_COLUMN = "_is_deleting";

    @Ignore
    public static final String IS_DOWNLOADING_COLUMN = "_is_downloading";

    @Ignore
    public static final String DOWNLOAD_PRIORITY_COLUMN = "_download_priority";

    private boolean _isUploading = false;
    private boolean _isUpdating = false;
    private boolean _isDeleting = false;
    private boolean _isDownloading = false;

    private int _downloadPriority = 0;

    private boolean _isDeleted = false;

    @Ignore
    public enum SyncState {
        UPLOADING,
        UPDATING,
        DELETING,
        DOWNLOADING,
        NORMAL
    }

    // SugarORM needs an empty constructor
    public BOSyncEntity() {

    }

    public void setState(SyncState state) {
        switch(state) {
            case UPLOADING:
                _isUploading = true;
                _isUpdating = false;
                _isDeleting = false;
                _isDownloading = false;
                break;
            case UPDATING:
                _isUploading = false;
                _isUpdating = true;
                _isDeleting = false;
                _isDownloading = false;
                break;
            case DELETING:
                _isUploading = false;
                _isUpdating = false;
                _isDeleting = true;
                _isDownloading = false;

                // Mark as deleted
                markAsDeleted();

                break;
            case DOWNLOADING:
                _isUploading = false;
                _isUpdating = false;
                _isDeleting = false;
                _isDownloading = true;
                break;
            default:
                _isUploading = false;
                _isUpdating = false;
                _isDeleting = false;
                _isDownloading = false;
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

    public abstract boolean uploadToServerSync();

    public abstract boolean updateOnServerSync();

    public abstract boolean deleteOnServerSync();

    public void setDownloadPriority(int priority) {
        if(priority < 0) {
            priority = 0;
        }

        _downloadPriority = priority;
    }

    public int getDownloadPriority() {
        return _downloadPriority;
    }

    private void markAsDeleted() {
        _isDeleted = true;
    }

    public boolean isDeleted() {
        return _isDeleted;
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
        if(getId() != other.getId()) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

}
