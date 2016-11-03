package com.openet.labs.ml.autoscale;

import com.openet.labs.ml.autoscale.UdfTimeStampToDayOfMonth;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Ehsun Behravesh <ehsun.behravesh@openet.com>
 */
public class UdfTimeStampToDayOfMonthTest {

    /**
     * Test of call method, of class UdfTimeStampToDayOfMonth.
     * @throws java.lang.Exception
     */
    @Test
    public void testCall() throws Exception {
        System.out.println("UdfTimeStampToDayOfMonthTest");
        Calendar cal = GregorianCalendar.getInstance();        
        
        Timestamp ts = new Timestamp(cal.getTime().getTime());
        UdfTimeStampToDayOfMonth instance = new UdfTimeStampToDayOfMonth();
        
        Integer expResult = cal.get(Calendar.DAY_OF_MONTH);
        Integer result = instance.call(ts);
        
        assertEquals(expResult, result);        
    }
    
}
