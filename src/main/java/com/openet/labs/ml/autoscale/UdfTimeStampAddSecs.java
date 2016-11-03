package com.openet.labs.ml.autoscale;

import java.io.Serializable;
import java.sql.Timestamp;
import org.apache.spark.sql.api.java.UDF2;

public class UdfTimeStampAddSecs implements UDF2<Timestamp, Integer, Timestamp>, Serializable{
    
    private static final long serialVersionUID = 1L;

    @Override
    public Timestamp call(Timestamp inputTS, Integer seconds) throws Exception {
        
        Timestamp adjustedTS = new Timestamp(inputTS.getTime() + (seconds * 1000L));
        
        return adjustedTS;
    }

}