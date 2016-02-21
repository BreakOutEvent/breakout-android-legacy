package org.break_out.breakout.sync;

import org.break_out.breakout.sync.model.SyncEntity;

import java.util.List;

/**
 * Interface to be implemented when you want to provide download
 * functionality for an Entity. The {@link BOSyncController} will use
 * implementations of this loader interface to load entities (download
 * and/or get from local DB) in the {@link BOSyncController#get(int, int, BOEntityLoader)}
 * method.<br />
 * The recommended way to implement this interface is the following:<br />
 * <ol>
 *     <li>Create a static inner class implementing {@link BOEntityLoader} in your
 *     entity class with the entity class as the generic type.</li>
 *     <li>Create a static method called {@code getLoader()} returning
 *     your an instance of your implementation of the interface.</li>
 *     <li>You can then simply call {@link BOSyncController#get(int, int, BOEntityLoader)}
 *     like {@code controller.get(0, 100, YourEntity.getLoader()}.</li>
 * </ol>
 * Created by Tino on 21.02.2016.
 */
public interface BOEntityLoader<T extends SyncEntity> {

    // TODO: Find a way to handle caching more generically
    public List<T> load(int first, int last);

}
