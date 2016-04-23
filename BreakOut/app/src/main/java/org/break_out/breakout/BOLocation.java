package org.break_out.breakout;

import com.orm.SugarRecord;

/**
 * Created by Maximilian Dühr on 01.03.2016.
 */
public class BOLocation extends SugarRecord {

    private long _timestamp;
    private double _latitude;
    private double _longitude;

    public BOLocation() {

    }

    public BOLocation(long timestamp, double lati, double longi) {
        _timestamp = timestamp;
        _latitude = lati;
        _longitude = longi;
    }

    public double getLatitude() {
        return _latitude;
    }

    public double getLongitude() {
        return _longitude;
    }

    public long getTimestamp() {
        return _timestamp;
    }
}
