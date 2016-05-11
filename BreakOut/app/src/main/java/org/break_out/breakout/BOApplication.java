package org.break_out.breakout;

import android.app.Application;

import com.orm.SugarApp;

import org.break_out.breakout.secrets.BOSecrets;

/**
 * Created by Tino on 29.01.2016.
 */
public class BOApplication extends SugarApp {

    @Override
    public void onCreate() {
        super.onCreate();

        BOSecrets secrets = new BOSecrets();

        // Enable to get error logs

        // Configure Flurry
        //FlurryAgent.setLogEnabled(false);

        // Init Flurry
        //FlurryAgent.init(this, secrets.getFlurryToken());
    }

}
