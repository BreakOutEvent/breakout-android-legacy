package org.break_out.breakout.api;

import com.google.gson.annotations.SerializedName;

import org.break_out.breakout.sync.model.Posting;

/**
 * Created by Tino on 19.04.2016.
 */
public class PostingModel {

    // This timestamp has to be in seconds!
    public Long date;
    public Long id;
    public String text;

    @SerializedName("postingLocation")
    public LocationModel postingLocation;

    public static class LocationModel {

        @SerializedName("latitude")
        public Double latitude;

        @SerializedName("longitude")
        public Double longitude;

        public LocationModel(Double latitude, Double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

    }

    public PostingModel(Posting posting) {
        date = posting.getCreatedTimestamp();
        text = posting.getText();

        // TODO: Location (still a bug in the backend)!
    }
}
