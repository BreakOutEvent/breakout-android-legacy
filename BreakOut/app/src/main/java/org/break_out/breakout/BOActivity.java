package org.break_out.breakout;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by Maximilian Duehr on 21.12.2015.
 */
public abstract class BOActivity extends AppCompatActivity {

    private static final String PERMISSION_INTERNET = Manifest.permission.INTERNET;
    private static final String PERMISSION_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;

    static final int REQUESTCODE_LOCATION = 0;

    private HashMap<Integer, PermissionCallback> _hashMap_callbacks;
    private final static String TAG = "BOActivity";

    /**
     * use the android workflow to trigger the callback method
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public final void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionResult");
        PermissionCallback permissionCallback = getCallbacks().get(requestCode);
        if(permissionCallback != null) {
            Log.d(TAG, "result size: " + grantResults.length);
            permissionCallback.onResult(grantResults[0] == PackageManager.PERMISSION_GRANTED);
        }
    }

    /**
     * check if the searched permission is granted
     *
     * @param requestCode requestcode for the permission
     * @param callback    callback object that handles the result
     */
    @TargetApi(23)
    public void checkForPermission(int requestCode, PermissionCallback callback) {
        Log.d(TAG, "checkForPermission");
        getCallbacks().put(requestCode, callback);
        if(hasDenieablePermissions()) {
            Log.d(TAG, "deniable permissions!");
            if(shouldShowRequestPermissionRationale(getPermissionFromRequestcode(requestCode)))
            {
                requestPermissions(new String[]{getPermissionFromRequestcode(requestCode)}, requestCode);
            }
        } else {
            //if function is called on an old device the permissions cannot be not granted, simulate a 'true' answer
            onRequestPermissionsResult(requestCode, new String[]{getPermissionFromRequestcode(requestCode)}, new int[]{1});
        }
    }
    /**
     * @return hashMap of callbacks
     */
    private HashMap<Integer, PermissionCallback> getCallbacks() {
        if(_hashMap_callbacks == null) {
            _hashMap_callbacks = new HashMap<>();
        }
        return _hashMap_callbacks;
    }

    /**
     * check for API level and see if permissions can be denied
     *
     * @return true when SDK version is >=23
     */
    public boolean hasDenieablePermissions() {
        return Build.VERSION.SDK_INT >= 23;
    }

    /**
     * interface for receiving permission results
     */
    public interface PermissionCallback {
        void onResult(boolean granted);
    }

    /**
     * convert final REQUESTCODE to permission
     *
     * @param requestCode one of the declared final REQUESTCODE constants
     * @return the android permission as a String
     */
    public String getPermissionFromRequestcode(int requestCode) {
        switch(requestCode) {
            case REQUESTCODE_LOCATION:
                return Manifest.permission.ACCESS_FINE_LOCATION;
        }
        return null;
    }
}
