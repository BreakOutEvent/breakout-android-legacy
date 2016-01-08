package org.break_out.breakout.sync.model;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

/**
 * Created by Tino on 07.01.2016.
 */
public abstract class SyncEntity extends SugarRecord {

    @Ignore
    public static final String IS_UPLOADING_NAME = "_is_uploading";

    @Ignore
    public static final String IS_UPDATING_NAME = "_is_updating";

    @Ignore
    public static final String IS_DELETING = "_is_deleting";

    private boolean _isUploading;
    private boolean _isUpdating;
    private boolean _isDeleting;

    @Ignore
    public enum SyncState {
        UPLOADING,
        UPDATING,
        DELETING,
        NORMAL
    }

    // SugarORM needs an empty constructor
    public SyncEntity() {

    }

    public void setState(SyncState state) {
        switch(state) {
            case UPLOADING:
                _isUploading = true;
                _isUpdating = false;
                _isDeleting = false;
                break;
            case UPDATING:
                _isUploading = false;
                _isUpdating = true;
                _isDeleting = false;
                break;
            case DELETING:
                _isUploading = false;
                _isUpdating = false;
                _isDeleting = true;
                break;
            default:
                _isUploading = false;
                _isUpdating = false;
                _isDeleting = false;
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

    public abstract boolean uploadToServer();

    public abstract boolean updateOnServer();

    public abstract boolean deleteOnServer();

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }

        if(!(obj instanceof SyncEntity)) {
            return false;
        }

        final SyncEntity other = (SyncEntity) obj;
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
