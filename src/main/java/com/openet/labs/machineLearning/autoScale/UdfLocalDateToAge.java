package com.openet.labs.machineLearning.autoScale;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;

import org.apache.spark.sql.api.java.UDF1;

public class UdfLocalDateToAge implements UDF1<LocalDate, Integer>, Serializable{
	 

	private static final long serialVersionUID = 1L;

	@Override
	 public Integer call(LocalDate dob) throws Exception {
		
        LocalDate end = LocalDate.now(ZoneId.systemDefault());
        Period p = Period.between(dob, end);	 
	 	return p.getYears();
	 }
}