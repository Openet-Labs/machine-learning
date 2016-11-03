package com.openet.labs.ml.autoscale;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.apache.spark.sql.api.java.UDF1;

/**
 *
 * @author Ehsun Behravesh <ehsun.behravesh@openet.com>
 */
public class UdfTimeStampToDayOfYear implements UDF1<Timestamp, Integer>, Serializable {

    @Override
    public Integer call(Timestamp ts) throws Exception {
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(ts);
        
        return cal.get(Calendar.DAY_OF_YEAR); 
    }
    
}
