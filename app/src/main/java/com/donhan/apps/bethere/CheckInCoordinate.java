package com.donhan.apps.bethere;

import com.orm.SugarRecord;

/**
 * Created by donhan on 8/8/15.
 */
public class CheckInCoordinate extends SugarRecord<CheckInCoordinate> {
    String name;
    double lat;
    double lon;
    long timestamp;

    public CheckInCoordinate() {
    }

    public CheckInCoordinate(String name, double lat, double lon){
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.timestamp = System.currentTimeMillis();
    }
}
