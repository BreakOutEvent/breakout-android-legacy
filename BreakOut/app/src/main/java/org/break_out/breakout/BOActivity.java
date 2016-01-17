package org.break_out.breakout;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.break_out.breakout.manager.UserManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Maximilian Duehr on 21.12.2015.
 */
public abstract class BOActivity extends AppCompatActivity {

    private static final String TAG = "BOActivity";

    private Map<Integer, PermissionCallback> _callbacks = new HashMap<Integer, PermissionCallback>();
    private int _nextRequestCode = 0;

    /**
     * Enum for the permission that can be requested within
     * this Activity. Note that all permissions listet in this
     * enum have to be in the manifest as well!
     */
    public enum Permission {
        LOCATION
    }

    /**
     * Interface for receiving permission results.
     */
    public interface PermissionCallback {
        /**
         * The requested permission has been granted.
         */
        public void permissionGranted();

        /**
         * The requested permission has been denied.
         */
        public void permissionDenied();
    }

    /**
     * Will be called when the permission has been asked from the user.
     * This method overrides the corresponding Android callback method to invoke
     * the callback registered to this request.<br />
     * If a permission is always denied, you might want to check if it is specified
     * in the manifest!
     *
     * @param requestCode The request code
     * @param permissions The permissions that had been asked
     * @param grantResults An array indicating which permissions had been granted
     */
    @Override
    public final void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Send a "permission denied" callback as a default reaction to wrong request results (wrong sizes of the arrays)
        if(grantResults.length != 1 || permissions.length != grantResults.length) {
            fireCallback(requestCode, false);
            return;
        }

        boolean granted = (grantResults[0] == PackageManager.PERMISSION_GRANTED);
        fireCallback(requestCode, granted);
    }

    /**
     * Invoke the callback corresponding to the given request code.
     *
     * @param requestCode The request code of the callback
     * @param granted If the permission had been granted or not
     */
    private void fireCallback(Integer requestCode, boolean granted) {
        PermissionCallback callback = _callbacks.get(requestCode);

        if(callback != null) {
            if(granted) {
                callback.permissionGranted();
            } else {
                callback.permissionDenied();
            }
        }

        // Callbacks will only be used once -> remove them after use
        _callbacks.remove(callback);
    }

    /**
     * Call this method to request a permission.
     * The registered callback will get notified as soon
     * as the result of the request is received.<br />
     * Note that this method will always return <code>false</code>
     * if the user selected "never ask me again" and denied the
     * permission.
     *
     * @param permission The permission to be requested
     * @param callback A callback object receiving the result of the request
     */
    public void getPermission(Permission permission, PermissionCallback callback) {
        int requestCode = generateRequestCode();

        // Register callback
        _callbacks.put(requestCode, callback);

        // For SDK version >= 23 request permission, otherwise instantly grant the permission
        if(hasDeniablePermissions()) {
            Log.d(TAG, "Deniable permissions -> ask for permission");

            String permissionString = getPermissionString(permission);

            // Check if permission has already been granted before
            if(ContextCompat.checkSelfPermission(this, permissionString) == PackageManager.PERMISSION_GRANTED) {
                fireCallback(requestCode, true);
            } else {
                requestPermissions(new String[] {permissionString}, requestCode);
            }
        } else {
            Log.d(TAG, "No deniable permissions -> grant permission instantly");

            fireCallback(requestCode, true);
        }
    }

    /**
     * Check the API level and see if permissions can be denied
     * (which is possible for SDK version greater than 22).
     *
     * @return True when SDK version is >= 23, false otherwise
     */
    private boolean hasDeniablePermissions() {
        return Build.VERSION.SDK_INT >= 23;
    }

    /**
     * Generates a request code which will be unique for
     * this Activity instance. This request code can be
     * used for one specific permission request.
     *
     * @return A unique request code
     */
    private int generateRequestCode() {
        int code = _nextRequestCode;
        _nextRequestCode++;

        return code;
    }

    /**
     * Returns the corresponding string to a permission.
     *
     * @param permission The permission to get the String for
     * @return The permission String (empty String when an error occurred)
     */
    private String getPermissionString(Permission permission) {
        // Note: One permission per permission group is enough!
        switch(permission) {
            case LOCATION:
                return Manifest.permission.ACCESS_FINE_LOCATION;
            default:
                return "";
        }
    }

    /**
     * This method will return true if the app is running
     * on a tablet. The check is based on qualified resource
     * files. Every device with a smallest screen width of
     * 600dp or devices classified as xlarge will be seen as tablets.
     *
     * @return True if this device is a tablet, false otherwise
     */
    public boolean isTablet() {
        return getResources().getBoolean(R.bool.isTablet);
    }

    /**
     * This method will return true if this Activity is
     * currently running in portrait mode.
     *
     * @return True if this Activity is in portrait mode, false otherwise
     */
    public boolean isPortrait() {
        return !isLandscape();
    }

    /**
     * This method will return true if this Activity is
     * currently running in landscape mode.
     *
     * @return True if this Activity is in landscape mode, false otherwise
     */
    public boolean isLandscape() {
        return (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == UserManager.REQUEST_CODE_LOGIN) {
            UserManager.getInstance(this).loginActivityDone(resultCode, data);
        }
    }
}
