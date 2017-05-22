package org.break_out.breakout.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.break_out.breakout.R;
import org.break_out.breakout.constants.Constants;
import org.break_out.breakout.secrets.BOSecrets;

/**
 * Created by Maximilian Dühr on 15.03.2017.
 */

public final class URLUtils {
    private static final String TAG = "URLUtils";

    public static String getBaseUrl(Context c){
        boolean isTest = isTest(c);
        Log.d(TAG,"getBseUrl called, test: "+isTest);
        if(isTest){
            Log.d(TAG,"is test");
            return Constants.Api.BASE_URL_TEST;
        }
        Log.d(TAG,"is no test");
        return Constants.Api.BASE_URL;
    }

    public static String getBaseUrlWithoutProtocol(Context c){
        boolean isTest = isTest(c);
        if(isTest){
            return Constants.Api.BASE_URL_TEST_NO_PROT;
        }
        return Constants.Api.BASE_URL_NO_PROT;
    }

    public static String getPort(){
        return Constants.Api.BASE_UR_TEST_PORT;
    }

    public static String getMediaUrl(Context c){
        if(isTest(c)){
            return Constants.Api.MEDIA_URL_TEST;
        }
        return Constants.Api.MEDIA_URL;
    }

    public static String getClientSecret(Context c){
        if(isTest(c)){
            return c.getString(R.string.secret_secret);
        }
        return new BOSecrets().getClientSecret(c);
    }

    public static String getClientId(Context c){
        if(isTest(c)){
            return c.getString(R.string.secret_id);
        }
        return "breakout_app";
    }

    private static boolean isTest(Context c){
        SharedPreferences preferences = c.getSharedPreferences(c.getString(R.string.PREFERENCES_GLOBAL),Context.MODE_PRIVATE);
        return preferences.getBoolean(c.getString(R.string.PREFERENCE_IS_TEST),false);
    }
}
