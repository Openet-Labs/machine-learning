package com.openet.labs.ml.autoscale;

import java.io.Serializable;
import java.sql.Timestamp;

import org.apache.spark.sql.api.java.UDF1;

public class UdfTimeStampToLong implements UDF1<Timestamp, Long>, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public Long call(Timestamp ts) throws Exception {

		return ts.getTime();
	}

}