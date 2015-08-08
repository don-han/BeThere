package com.donhan.apps.bethere;

import com.orm.SugarRecord;

/**
 * Created by Akshay on 8/8/2015.
 */
public class CheckInLocation extends SugarRecord<CheckInLocation> {

    String name;

    public CheckInLocation () {}

    public CheckInLocation (String name) {
        this.name = name;
    }

}
