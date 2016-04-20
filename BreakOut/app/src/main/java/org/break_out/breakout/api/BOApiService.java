package org.break_out.breakout.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Tino on 19.04.2016.
 */
public interface BOApiService {

    @GET("/event/")
    Call<List<EventModel>> listEvents();

    @GET("/posting/")
    Call<List<PostingModel>> listAllPostings();

    @POST("/posting/")
    Call<PostingModel> createPosting(@Body PostingModel posting);

    @GET("/posting/")
    Call<List<PostingModel>> getPostings(@Body long[] ids);

    @GET("/posting/get/since/{id}/")
    Call<long[]> getNewPostingIds(@Path("id") long lastKnownId);

}
