package org.break_out.breakout.util;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>This class allows you to run anything on a background thread
 * using an {@link AsyncTask}. The advantage of using this runner class
 * instead of normal AsyncTasks is that you can register and remove a listener
 * to your task (specified by a string ID) whenever you want. This can be
 * very handy when you want to run tasks from an Activity.</p>
 *
 * <p>The background task will continue to run when the Activity, which started it,
 * is pause. However, it will stop as soon as the Activity is destroyed!</p>
 *
 * <p>Here is some pseudo code giving you an idea of how to use this class:</p>
 *
 * <pre>
 * {@code
 *     class YourActivity extends Activity {
 *
 *         String ID = "your_id";
 *
 *         void startTask() {
 *             BackgroundRunner runner = BackgroundRunner.getRunner(ID);
 *             runner.setRunnable(new YourRunnable());
 *
 *             // You don't need to register a listener here because you
 *             // already to it in onResume()
 *
 *             runner.execute(new YourRunnable());
 *         }
 *
 *         @Override
 *         protected void onPause() {
 *              super.onPause();
 *              BackgroundRunner.getRunner(ID).removeListener();
 *         }
 *
 *         @Override
 *         protected void onResume() {
 *             super.onResume();
 *             BackgroundRunner.getRunner(ID).setListener(new YourListener());
 *         }
 *
 *         class YourRunnable implements BackgroundRunner.BackgroundRunnable {
 *              @Nullable
 *              @Override
 *              public Object run(@Nullable Object... params) {
 *                  // This will run in the background
 *                  return "result";
 *              }
 *         }
 *
 *         class YourListener implements BackgroundRunner.BackgroundListener {
 *              @Override
 *              public void onResult(@Nullable Object result) {
 *                  String resultString = (String) result;
 *                  // Do something with the result
 *              }
 *         }
 *     }
 * }</pre>
 *
 * Created by Tino on 12.03.2016.
 */
public class BackgroundRunner {

    private static final String TAG = "BackgroundRunner";

    // Static values
    private static Map<String, BackgroundRunner> _runners = new HashMap<>();

    // Object-specific values
    private boolean _isRunning = false;
    private boolean _isDone = false;
    private Object _result = null;

    private BackgroundRunnable _runnable = null;
    private BackgroundListener _listener = null;

    // Interfaces

    /**
     * Implement this interface to provide a runnable task
     * to a runner. This runnable can be executed by a {@link BackgroundRunner}.
     */
    public interface BackgroundRunnable {

        /**
         * <p>Implement your custom task in this method. When set to
         * a {@link BackgroundRunner} and executed, this task will automatically be
         * run in a separate thread in an AsyncTask. <b>You don't have
         * to open a new thread in this method.</b></p>
         *
         * <p>This methods takes an array of Objects as a parameter.
         * These are the parameters you pass to {@link #execute(Object...)} when
         * executing a runner. They can be null if no parameters were specified.</p>
         *
         * <p>When implementing this method and running a task in the background,
         * you might often want to return a result. This method allows you to return
         * any Object. You will have to cast it to the correct return type in your implementation of the
         * {@link org.break_out.breakout.util.BackgroundRunner.BackgroundListener#onResult(Object)}
         * method. If your runnable should not return any result, simply return null.</p>
         *
         * @param params The parameters provided when executing the runner
         * @return The result object or null
         */
        public @Nullable Object run(@Nullable Object... params);
    }

    /**
     * Implement this listener to receive the result of a
     * {@link org.break_out.breakout.util.BackgroundRunner.BackgroundRunnable}.
     */
    public interface BackgroundListener {

        /**
         * <p>This method will be called as soon as the runner's
         * runnable finished. It will deliver the result of the runnable,
         * which may be null (depending on your implementation of
         * {@link org.break_out.breakout.util.BackgroundRunner.BackgroundRunnable#run(Object...)}).
         * You might want to cast this object to the appropriate type.</p>
         *
         * <p>If there was no listener registered at the time when the runnable finished,
         * the result will be stored and sent as soon as there is a listener registered to the
         * runnable again.</p>
         *
         * <p>This callback will only be called once for ever run of the runnable.</p>
         *
         * @param result The result of the runnable or null
         */
        public void onResult(@Nullable Object result);
    }

    // Static methods

    /**
     * Returns a runner with the given string ID.
     * If there is no runner with that ID yet, it will be
     * created and returned. If there is already a runner with
     * that ID, this runner will be returned.
     *
     * @param id Unique identifier for that runner
     * @return A runner for the given ID
     */
    public static BackgroundRunner getRunner(String id) {
        if(!_runners.containsKey(id)) {
            return new BackgroundRunner(id);
        }

        return _runners.get(id);
    }

    // Constructor
    public BackgroundRunner(String id) {
        _runners.put(id, this);
    }

    // Methods

    /**
     * <p>Sets the runnable to be executed by this runner. You have
     * to set a runnable exactly once to every runner in order to
     * be able to execute it.</p>
     *
     * <p><b>You can only set a runnable once (it is not possible
     * to overwrite a runnable that has already been set to this runner)!</b></p>
     *
     * @param runnable The runnable to be executed in the background
     */
    public void setRunnable(BackgroundRunnable runnable) {
        if(_runnable != null) {
            return;
        }

        _runnable = runnable;
    }

    /**
     * <p>Register a listener to this runner.
     * You can always call this method (also for
     * re-registering a listener after an Activity has been
     * paused).</p>
     *
     * <p>If the runner already finished executing its runnable, your
     * listener will instantly be called. In that way it is guaranteed that
     * you always receive the result of the runnable. Once the listener
     * has been called, the result of the runnable will be deleted. You
     * will only get notified once.</p>
     *
     * @param listener The listener to be called once the runnable finished
     */
    public void setListener(BackgroundListener listener) {
        _listener = listener;
        tryCallListener();
    }

    /**
     * Remove the registered listener.
     * It is save to call this method even if you haven't set
     * a listener before.
     */
    public void removeListener() {
        _listener = null;
    }

    /**
     * Execute this runnable in a separate AsyncTask.
     * If the runner is already running, this method will
     * do nothing. <b>You cannot execute the same runner multiple
     * times im parallel.</b>
     *
     * @param params The parameters to be considered by the executed runnable
     */
    public void execute(Object ... params) {
        if(_runnable == null) {
            Log.e(TAG, "Cannot run without a BackgroundRunnable");
            return;
        }

        if(_isRunning) {
            Log.e(TAG, "Is already running");
            return;
        }

        _result = null;
        _isRunning = true;

        if(params == null) {
            AsyncTaskUtils.executeTask(new RunnerTask());
        } else {
            AsyncTaskUtils.executeTask(new RunnerTask(), params);
        }
    }

    /**
     * Checks if this runner is currently running.
     * If so, you cannot execute it again in this time.
     *
     * @return True if the runner is currently running, false otherwise
     */
    public boolean isRunning() {
        return _isRunning;
    }

    /**
     * Store the result of the runnable for this runner.
     * The running state will be set to <i>not running</i> and this
     * method will try to call the listener. If there is no listener
     * currently registered to this runner, the result will be stored
     * so that the listener can be called once re-registered to this
     * runner.
     *
     * @param result The result of the runnable
     */
    private void setResult(Object result) {
        _result = result;
        _isRunning = false;
        _isDone = true;
        tryCallListener();
    }

    /**
     * If there is a listener registered to this runner, this method
     * will call it and reset the result.
     */
    private void tryCallListener() {
        if(_listener != null && _isDone) {
            _listener.onResult(_result);
            _result = null;
            _isDone = false;
        }
    }

    // AsyncTask

    /**
     * The AsyncTask running the runnable in the background.
     */
    private class RunnerTask extends AsyncTask<Object, Void, Object> {

        @Override
        protected Object doInBackground(Object... params) {
            return _runnable.run(params);
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            setResult(result);
        }
    }

}
