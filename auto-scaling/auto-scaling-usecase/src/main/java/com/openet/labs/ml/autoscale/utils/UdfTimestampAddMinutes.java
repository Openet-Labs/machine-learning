package com.openet.labs.ml.autoscale.utils;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;
import org.apache.spark.sql.api.java.UDF2;

public class UdfTimestampAddMinutes implements UDF2<Long, Integer, Long>, Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public Long call(Long ts, Integer minutes) throws Exception {

        Timestamp tsCurrent = new Timestamp(ts);

        java.util.Calendar cal = Calendar.getInstance();
        cal.setTime(tsCurrent);
        cal.add(java.util.Calendar.MINUTE, minutes);

        return cal.getTimeInMillis();
    }

}