package com.openet.labs.machineLearning.autoScale;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.types.StructField;

public class InputParser implements Serializable, Function<String, List<Object>> {

    private static final long serialVersionUID = 1L;
    private char delimiter = ',';
    private boolean isRemoveColumns = false;
    List<StructField> structFields;
    //
    private static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");    	
	
    public static DateTimeFormatter getDateFormat() {
		return dateFormat;
	}

	public static void setDateFormat(DateTimeFormatter dateFormat) {
		InputParser.dateFormat = dateFormat;
	}

	public List<StructField> getStructFields() {
        return structFields;
    }

    public void setStructFields(List<StructField> structFields) {
        this.structFields = structFields;
    }

    private final CSVFormat csvFormat = CSVFormat.RFC4180.withDelimiter(',').withAllowMissingColumnNames().withIgnoreEmptyLines();

    public InputParser(List<StructField> structFields) {
        setStructFields(structFields);
    }
    
    public InputParser(List<StructField> structFields, DateTimeFormatter dateFormat) {
    	this(structFields);
    	setDateFormat(dateFormat);        
    }

    @Override
    public List<Object> call(String line) throws IOException {
        List<Object> data = new ArrayList<>();

        try (CSVParser parser = new CSVParser(new StringReader(line), csvFormat)) {

            for (CSVRecord fields : parser.getRecords()) {
                for (int i = 0; i < fields.size(); i++) {

                    data.add(formatData(fields.get(i).trim(), structFields.get(i)));
                }
            }
        }

        return data;
    }

    private Object formatData(String data, StructField field) {
        switch (field.dataType().toString()) {
        case "StringType":
            return data;
        case "IntegerType":
            return Integer.parseInt(data);
        case "LongType":
            return Long.parseLong(data);
        case "FloatType":
            return Float.parseFloat(data);
        case "DoubleType":
            return Double.parseDouble(data);
        case "BooleanType":
            return Boolean.parseBoolean(data);
        case "DateType":
            LocalDate input = LocalDate.parse(data, dateFormat);
            return java.sql.Date.valueOf(input);            
        case "TimestampType":
        	return Timestamp.valueOf(data.trim()); //timestamp            
        default:
            return data;

        }
    }

    public char getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(char delimiter) {
        this.delimiter = delimiter;
    }

    public boolean isRemoveColumns() {
        return isRemoveColumns;
    }

    public void setRemoveColumns(boolean isRemoveColumns) {
        this.isRemoveColumns = isRemoveColumns;
    }

}