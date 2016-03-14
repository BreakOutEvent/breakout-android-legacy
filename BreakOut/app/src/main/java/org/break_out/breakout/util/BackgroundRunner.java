package org.break_out.breakout.util;

import android.os.AsyncTask;
import android.os.Bundle;
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
 * <p>Here is some code giving you an idea of how to use this class:</p>
 *
 * <pre>
 * {@code
 *     public class YourActivity extends Activity {
 *
 *         private String ID = "your_id";
 *         private String KEY_RESULT = "key_result";
 *
 *         private void startTask() {
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
 *         private class YourRunnable implements BackgroundRunner.BackgroundRunnable {
 *              @Nullable
 *              @Override
 *              public Bundle run(@Nullable Bundle params) {
 *                  // This will run in the background
 *
 *                  Bundle result = new Bundle();
 *                  result.putString(KEY_RESULT, "result");
 *                  return result;
 *              }
 *         }
 *
 *         private class YourListener implements BackgroundRunner.BackgroundListener {
 *              @Override
 *              public void onResult(@Nullable Bundle result) {
 *                  if(result == null) {
 *                      return;
 *                  }
 *
 *                  String resultString = result.getString(KEY_RESULT);
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

    /**
     * Static map of all runners mapped by id.
     */
    private static Map<String, BackgroundRunner> _runners = new HashMap<>();

    /**
     * If the runner is currently running or not.
     */
    private boolean _isRunning = false;

    /**
     * Will be true if the runner finished but the
     * result has not been returned to a listener yet.
     */
    private boolean _isDone = false;

    /**
     * The result from the runnable of this runner.
     * This value might even be null after the runnable
     * finished, as it is allowed to return null from runnables.
     * To find out if the runnable already finished, see {@link #_isDone}.
     */
    private Bundle _result = null;

    /**
     * The runnable to be run in the background by this runner.
     * This runnable must not be null in order to execute the runner.
     */
    private BackgroundRunnable _runnable = null;

    /**
     * The listener for this runner. Can always be null.
     */
    private BackgroundListener _listener = null;

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
         * <p>This methods takes a {@link Bundle} as a parameter.
         * This is the same bundle as passed to {@link #execute(Bundle)} when
         * executing the runner. It can be null if no parameters were passed.</p>
         *
         * <p>When implementing this method and running a task in the background,
         * you might often want to return a result. This method allows you to return
         * any Bundle containing your result values. It will be delivered to the
         * {@link org.break_out.breakout.util.BackgroundRunner.BackgroundListener#onResult(Bundle)}
         * method. If your runnable should not return any result, simply return null or an empty
         * Bundle.</p>
         *
         * @param params The parameters provided when executing the runner
         * @return The result Bundle or null
         */
        public @Nullable Bundle run(@Nullable Bundle params);
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
         * {@link org.break_out.breakout.util.BackgroundRunner.BackgroundRunnable#run(Bundle)}).</p>
         *
         * <p>If there was no listener registered at the time when the runnable finished,
         * the result will be stored and sent as soon as there is a listener registered to the
         * runnable again.</p>
         *
         * <p>This callback will only be called once for every run of the runnable.</p>
         *
         * @param result The result Bundle of the runnable or null
         */
        public void onResult(@Nullable Bundle result);
    }

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

    private BackgroundRunner(String id) {
        _runners.put(id, this);
    }

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
     * <p>Executes this runnable in a separate AsyncTask with the given parameters.
     * If the runner is already running, this method will
     * do nothing. <b>You cannot execute the same runner multiple
     * times in parallel.</b></p>
     *
     * <p>When executing a runner again without a listener receiving the previous result
     * before, this result will be lost.</p>
     *
     * @param params The parameter Bundle to be considered by the executed runnable or null
     */
    public void execute(@Nullable Bundle params) {
        if(_runnable == null) {
            Log.e(TAG, "Cannot run without a BackgroundRunnable");
            return;
        }

        if(_isRunning) {
            Log.e(TAG, "Runner is already running");
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
     * Executes this runnable in a separate AsyncTask without any parameters.
     * To pass parameters to the runnable, see {@link #execute(Bundle)}.
     * If the runner is already running, this method will
     * do nothing. <b>You cannot execute the same runner multiple
     * times in parallel.</b>
     */
    public void execute() {
        execute(null);
    }

    /**
     * Checks if this runner is currently running.
     * If so, you cannot execute it again during this time.
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
     * currently registered to the runner, the result will be stored
     * so that the listener can be called once re-registered to this
     * runner.
     *
     * @param result The result of the runnable or null
     */
    private void setResult(@Nullable Bundle result) {
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

    /**
     * The AsyncTask running the runnable in the background.
     * When the runnable finished its work, {@link #setResult(Bundle)}
     * will be called with the result returned by the runnable.
     */
    private class RunnerTask extends AsyncTask<Bundle, Void, Bundle> {

        @Override
        protected Bundle doInBackground(Bundle... params) {
            if(params == null || params.length != 1) {
                return _runnable.run(null);
            } else {
                return _runnable.run(params[0]);
            }
        }

        @Override
        protected void onPostExecute(Bundle result) {
            super.onPostExecute(result);
            setResult(result);
        }
    }

}
