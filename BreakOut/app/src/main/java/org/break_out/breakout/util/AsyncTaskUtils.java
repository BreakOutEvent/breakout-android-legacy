package org.break_out.breakout.util;

import android.os.AsyncTask;
import android.os.Build;

/**
 * <p>A utility class for working with {@link AsyncTask}.</p>
 *
 * Created by Tino on 12.03.2016.
 */
public class AsyncTaskUtils {

    /**
     * Executes the given AsyncTask.
     * If the SDK version is Honeycomb or higher, the task will be
     * run on an executor.
     *
     * @param task The AsyncTask you want to execute
     * @param <P> Parameters
     */
    public static <P> void executeTask(AsyncTask<P, ?, ?> task) {
        executeTask(task, (P[]) null);
    }

    /**
     * Executes the given AsyncTask.
     * If the SDK version is Honeycomb or higher, the task will be
     * run on an executor.
     *
     * @param task The AsyncTask you want to execute
     * @param params The parameters for the task
     * @param <P> Parameters
     */
    public static <P> void executeTask(AsyncTask<P, ?, ?> task, P... params) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        } else {
            task.execute(params);
        }
    }

}
