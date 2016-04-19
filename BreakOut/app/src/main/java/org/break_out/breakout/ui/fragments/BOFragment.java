package org.break_out.breakout.ui.fragments;

import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tino on 18.04.2016.
 */
public class BOFragment extends Fragment {

    private static final String TAG = "BOFragment";

    private Map<Integer, PermissionListener> _listeners = new HashMap<Integer, PermissionListener>();
    private int _nextRequestCode = 0;

    /**
     * Interface for receiving permission results.
     */
    public interface PermissionListener {
        public void onPermissionsResult(Map<String, Boolean> result);
    }

    /**
     * <p>
     * Will be called when the permission has been asked from the user.
     * This method overrides the corresponding Android callback method to invoke
     * the listener registered to this request.
     * </p>
     *
     * <p>
     * If a permission is always denied, you might want to check if it is specified
     * in the manifest!
     * </p>
     *
     * @param requestCode The request code
     * @param permissions The permissions that had been asked
     * @param grantResults An array indicating which permissions had been granted
     */
    @Override
    public final void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Map<String, Boolean> result = new HashMap<String, Boolean>();

        for(int i = 0; i < permissions.length; i++) {
            result.put(permissions[i], grantResults[i] == PackageManager.PERMISSION_GRANTED);
        }

        fireListeners(requestCode, result);
    }

    /**
     * Invoke the listener corresponding to the given request code.
     *
     * @param requestCode The request code of the listener
     * @param result A map of booleans (for grant/denial) for every permission
     */
    private void fireListeners(Integer requestCode, Map<String, Boolean> result) {
        PermissionListener listener = _listeners.get(requestCode);

        if(listener != null) {
            listener.onPermissionsResult(result);

            // Callbacks will only be used once -> remove them after use
            _listeners.remove(requestCode);
        }
    }

    /**
     * Call this method to request runtime permissions.
     * The registered listener will get notified as soon
     * as the result of the request is received.<br />
     * Note that the listener will always receive {@code false}
     * for permissions where the user selected "never ask me again"
     * and denied the permission.
     *
     * @param listener A listener object receiving the result of the request
     * @param permissions The {@link android.Manifest.permission}s you want to request
     */
    public void getPermissions(PermissionListener listener, String... permissions) {
        int requestCode = generateRequestCode();

        // Prepare an array with all permissions granted
        Map<String, Boolean> allPermissionsGrantedMap = new HashMap<String, Boolean>();
        for(int i = 0; i < permissions.length; i++) {
            allPermissionsGrantedMap.put(permissions[i], true);
        }

        // Register listener
        _listeners.put(requestCode, listener);

        // For SDK version >= 23 request permission, otherwise instantly grant the permission
        if(hasDeniablePermissions()) {
            Log.d(TAG, "Deniable permissions -> ask for permission");

            boolean allGranted = true;

            for(String p : permissions) {
                // Check if permission has already been granted
                if(ContextCompat.checkSelfPermission(getContext(), p) == PackageManager.PERMISSION_DENIED) {
                    allGranted = false;
                    break;
                }
            }

            if(allGranted) {
                fireListeners(requestCode, allPermissionsGrantedMap);
                return;
            }

            // Request permissions
            requestPermissions(permissions, requestCode);
        } else {
            Log.d(TAG, "No deniable permissions -> grant permission instantly");
            fireListeners(requestCode, allPermissionsGrantedMap);
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

}
