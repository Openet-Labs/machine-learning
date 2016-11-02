package com.openet.labs.machineLearning.autoScale.utils;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.apache.spark.sql.api.java.UDF1;

public class UdfTimestampToMinOfHour implements UDF1<String, Double>, Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public Double call(String ts) throws Exception {
        Date date = new Date();
        date.setTime((long) Long.parseLong(ts));

        java.util.Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        return (double) ((cal.get(java.util.Calendar.HOUR_OF_DAY) * 60) + cal.get(java.util.Calendar.MINUTE));
    }

}
