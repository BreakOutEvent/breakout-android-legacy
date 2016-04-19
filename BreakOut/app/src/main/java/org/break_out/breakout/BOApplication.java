package org.break_out.breakout;

import android.app.Application;

import com.flurry.android.FlurryAgent;
import com.instabug.library.Instabug;
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

        Instabug.initialize(this, secrets.getInstabugToken());

        // configure Flurry
        FlurryAgent.setLogEnabled(false);

        // init Flurry
        FlurryAgent.init(this, secrets.getFlurryToken());
    }

}
