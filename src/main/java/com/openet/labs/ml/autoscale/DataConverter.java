package com.openet.labs.ml.autoscale;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;

public class DataConverter {

    public static JavaRDD<Row> convertListObjectToRowRDD(JavaRDD<List<Object>> objectRDD) {
        JavaRDD<Row> rowRDD = objectRDD.map(new Function<List<Object>, Row>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Row call(List<Object> records) throws Exception {
                return RowFactory.create(records.toArray());
            }
        });

        return rowRDD;
    }

    public static JavaRDD<List<Object>> convertDataFrameToListObjectRDD(DataFrame dataFrame) {
        JavaRDD<List<Object>> rowRDD = dataFrame.javaRDD().map(new Function<Row, List<Object>>() {
            private static final long serialVersionUID = 1L;

            @Override
            public List<Object> call(Row row) throws Exception {
                List<Object> data = new ArrayList<>();
                for (int i = 0; i < row.length(); i++) {
                    data.add(row.get(i));
                }

                return data;
            }
        });

        return rowRDD;
    }

    public static JavaRDD<double[]> convertDataFrameToArrayDoubleRDD(DataFrame dataFrame) {

        JavaRDD<double[]> doubleRDD = dataFrame.javaRDD().map(new Function<Row, double[]>() {
            private static final long serialVersionUID = 1L;

            @Override
            public double[] call(Row row) throws Exception {

                double[] data = new double[row.length()];
                for (int i = 0; i < row.length(); i++) {
                    data[i] = Double.parseDouble((row.get(i).toString()));
                }

                return data;
            }
        });

        return doubleRDD;
    }
    
    public static JavaRDD<double[]> convertArrayDoubleToVectorRDD(DataFrame dataFrame) {

        JavaRDD<double[]> doubleRDD = dataFrame.javaRDD().map(new Function<Row, double[]>() {
            private static final long serialVersionUID = 1L;

            @Override
            public double[] call(Row row) throws Exception {

                double[] data = new double[row.length()];
                for (int i = 0; i < row.length(); i++) {
                    data[i] = Double.parseDouble((row.get(i).toString()));
                }

                return data;
            }
        });

        return doubleRDD;
    }

    public static JavaRDD<List<String>> convertListObjectToListStringRDD(JavaRDD<List<Object>> objectRDD) {
        JavaRDD<List<String>> stringRDD = objectRDD.map(new Function<List<Object>, List<String>>() {
            private static final long serialVersionUID = 1L;

            @Override
            public List<String> call(List<Object> objectList) throws Exception {
                List<String> data = new ArrayList<>();
                for (int i = 0; i < objectList.size(); i++) {
                    data.add(objectList.get(i).toString());
                }

                return data;
            }
        });

        return stringRDD;
    }

    public static JavaRDD<double[]> convertListObjectToArrayDoubleRDD(JavaRDD<List<Object>> objectRDD) {

        JavaRDD<double[]> doubleRDD = objectRDD.map(new Function<List<Object>, double[]>() {
            private static final long serialVersionUID = 1L;

            @Override
            public double[] call(List<Object> objectList) throws Exception {
                double[] data = new double[objectList.size()];
                for (int i = 0; i < objectList.size(); i++) {
                    data[i] = Double.parseDouble((objectList.get(i).toString()));
                }

                return data;
            }
        });

        return doubleRDD;

    }

    public static JavaRDD<List<String>> convertListObjectToListStringRDD(JavaRDD<List<Object>> objectRDD, List<StructField> fields, String dateFormatPattern) {

        JavaRDD<List<String>> stringRDD = objectRDD.map(new Function<List<Object>, List<String>>() {

            private static final long serialVersionUID = 1L;

            @Override
            public List<String> call(List<Object> objectList) throws Exception {
                List<String> data = new ArrayList<>();
                for (int i = 0; i < objectList.size(); i++) {
                    if (fields.get(i).dataType() == DataTypes.DateType) {
                        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(dateFormatPattern);
                        Date date = (Date) objectList.get(i);
                        data.add(dateFormatter.format(date.toLocalDate()));
                    } else {
                        data.add(objectList.get(i).toString());
                    }
                }

                return data;
            }
        });

        return stringRDD;
    }

}
