package org.break_out.breakout.sync;

import android.content.Context;

import org.break_out.breakout.sync.model.BOSyncEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Interface to be implemented when you want to provide downloadSync
 * functionality for an Entity.
 * <br />
 * The recommended way to implement this interface is the following:
 * <br />
 * <ol>
 *     <li>Create a static inner class implementing {@link BOEntityDownloader} in your
 *     entity class with the entity class as the generic type.</li>
 *     <li>Create a static method called {@code getDownloader()} returning
 *     your an instance of your implementation of the interface.</li>
 * </ol>
 * Created by Tino on 21.02.2016.
 */
public abstract class BOEntityDownloader<T extends BOSyncEntity> {

    /**
     * Download all entities from the server with the given IDs
     * synchronously. If the downloadSync fails for some entities just
     * don't put them into the resulting list.<br />
     * <b>Do not open a new thread in this method! This will be done by
     * the {@link BOSyncController}.</b>
     *
     * @param idsToDownload The IDs of the entities to downloadSync from the server
     * @return A list of all the downloaded entities
     */
    public abstract List<T> downloadSync(Context context, List<Long> idsToDownload);

    public List<T> downloadSync(Context context, long[] idsToDownload) {
        List<Long> idsList = new ArrayList<Long>();

        for(int i = 0; i < idsToDownload.length; i++) {
            idsList.add(idsToDownload[i]);
        }

        return downloadSync(context, idsList);
    }

    /**
     * Download a list of IDs of entities that have been created
     * after the last known ID synchronously.<br />
     * <b>Do not open a new thread in this method! This will be done by
     * the {@link BOSyncController}.</b>
     *
     * @return A list of the IDs of newly created entities from the server
     */
    public abstract List<Long> downloadNewIDsSync(Context context, long lastKnownId);

}
