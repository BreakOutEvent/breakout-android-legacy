package org.break_out.breakout.api;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by florianschmidt on 07/12/2016.
 */

public interface BreakoutClient {

    @GET("posting/")
    Observable<List<NewPosting>> getAllPostings(@Query("offset") int offset,
                                                @Query("limit") int limit);

    @POST("posting/{postingId}/like/")
    Observable<ResponseBody> likePosting(@Header("Authorization") String auth,
                                         @Path("postingId") int postingId,
                                         @Body Like like);

}
