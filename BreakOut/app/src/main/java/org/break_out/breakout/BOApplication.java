package org.break_out.breakout;

import android.app.Application;

import com.instabug.library.Instabug;

import org.break_out.breakout.secrets.BOSecrets;

/**
 * Created by Tino on 29.01.2016.
 */
public class BOApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Instabug.initialize(this, new BOSecrets().getInstabugToken());
    }

}
