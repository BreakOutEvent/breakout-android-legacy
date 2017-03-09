package org.break_out.breakout;

import android.app.Application;

import com.facebook.stetho.Stetho;

import org.break_out.breakout.secrets.BOSecrets;

/**
 * Created by Tino on 29.01.2016.
 */
public class BOApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        BOSecrets secrets = new BOSecrets();
        Stetho.initializeWithDefaults(this);
        // Enable to get error logs

        // Configure Flurry
        //FlurryAgent.setLogEnabled(false);

        // Init Flurry
        //FlurryAgent.init(this, secrets.getFlurryToken());
    }

}
