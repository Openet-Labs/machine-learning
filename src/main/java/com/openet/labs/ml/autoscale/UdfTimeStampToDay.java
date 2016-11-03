package com.openet.labs.ml.autoscale;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;

import org.apache.spark.sql.api.java.UDF1;

public class UdfTimeStampToDay implements UDF1<Timestamp, Integer>, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public Integer call(Timestamp ts) throws Exception {

		java.util.Calendar cal = Calendar.getInstance();
		cal.setTime(ts);
		return cal.get(java.util.Calendar.DAY_OF_WEEK);
	}

}