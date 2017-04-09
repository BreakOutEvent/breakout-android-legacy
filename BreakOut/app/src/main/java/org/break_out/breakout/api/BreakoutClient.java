package org.break_out.breakout.api;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by florianschmidt on 07/12/2016.
 */

public interface BreakoutClient {

    @GET("posting/")
    Observable<List<RemotePosting>> getAllPostings(@Query("page") int offset,
                                                @Query("userid") int userIdHasLiked);

    @GET("posting/{id}/")
    Observable<RemotePosting>  getPostingById(@Path("id") int id);

    @POST("posting/{postingId}/like/")
    Observable<ResponseBody> likePosting(@Path("postingId") int postingId,
                                         @Body Like like);

    @DELETE("posting/{postingId}/like/")
    Observable<ResponseBody> unlikePosting(@Path("postingId") int postingId);

}
