package org.break_out.breakout;

import com.orm.SugarRecord;

import org.break_out.breakout.sync.model.Posting;

/**
 * Created by Maximilian Dühr on 01.03.2016.
 */
public class BOLocation extends SugarRecord {

    private long _timestamp;
    private double _latitude;
    private double _longitude;
    private Posting _posting;

    public BOLocation() {
    }

    public BOLocation(long timestamp, double lati, double longi) {
        _timestamp = timestamp;
        _latitude = lati;
        _longitude = longi;
    }

    public BOLocation(Posting posting, long timestamp, double lati, double longi) {
        this(timestamp,lati,longi);
        _posting = posting;
    }

    public double getLatitude() {
        return _latitude;
    }

    public double getLongitude() {
        return _longitude;
    }

    public void setLatitude(double latitude) {
        _latitude = latitude;
    }

    public void setLongitude(double longitude) {
        _longitude = longitude;
    }

    public long getTimestamp() {
        return _timestamp;
    }
}
