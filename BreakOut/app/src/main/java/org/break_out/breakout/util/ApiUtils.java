package org.break_out.breakout.util;

import android.content.Context;

import org.break_out.breakout.api.BOApiService;
import org.break_out.breakout.constants.Constants;
import org.break_out.breakout.manager.UserManager;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Tino on 20.04.2016.
 */
public class ApiUtils {

    public static BOApiService getService(Context context) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(UserManager.getInstance(context).getOAuthClient())
                .build();

        return retrofit.create(BOApiService.class);
    }

}
