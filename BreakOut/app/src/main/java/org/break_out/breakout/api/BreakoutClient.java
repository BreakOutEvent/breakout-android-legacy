package org.break_out.breakout.api;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by florianschmidt on 07/12/2016.
 */

public interface BreakoutClient {

    @GET("posting/")
    Observable<List<NewPosting>> getAllPostings(@Query("offset") int offset, @Query("limit") int limit);
}
