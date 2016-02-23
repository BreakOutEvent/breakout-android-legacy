package org.break_out.breakout.sync;

import org.break_out.breakout.sync.model.SyncEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Interface to be implemented when you want to provide download
 * functionality for an Entity. The {@link BOSyncController} will use
 * implementations of this loader interface to download entities (download
 * and/or get from local DB) in the {@link BOSyncController#get(int, int, BOEntityDownloader)}
 * method.
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
public abstract class BOEntityDownloader<T extends SyncEntity> {

    public abstract List<T> download(List<Long> idsToDownload);

    public List<T> download(long[] idsToDownload) {
        List<Long> idsList = new ArrayList<Long>();

        for(int i = 0; i < idsToDownload.length; i++) {
            idsList.add(idsToDownload[i]);
        }

        return download(idsList);
    }

}
