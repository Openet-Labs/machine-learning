package com.openet.labs.machineLearning.autoScale;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;

import org.apache.spark.sql.api.java.UDF1;

public class UdfTimeStampToMinutes implements UDF1<Timestamp, Integer>, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public Integer call(Timestamp ts) throws Exception {

		java.util.Calendar cal = Calendar.getInstance();
		cal.setTime(ts);
		int minuteOfDay = (cal.get(java.util.Calendar.HOUR_OF_DAY) * 60) + cal.get(java.util.Calendar.MINUTE);
		return minuteOfDay;

	}

}