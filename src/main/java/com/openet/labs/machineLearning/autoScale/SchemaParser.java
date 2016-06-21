package com.openet.labs.machineLearning.autoScale;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;

public class SchemaParser {

    public static List<StructField> getStructField(Properties props) {

        List<StructField> fields = null;
        TreeMap<Integer, String> treeMap = new TreeMap<>();

        for (String key : props.stringPropertyNames()) {
            if (key.matches("com.openet.enigma.usecase.*.column.*.name")) {
                int index = Integer
                        .parseInt(key.substring(key.indexOf("column.") + ("column.").length(), key.indexOf(".name")));
                treeMap.put(index, key);
            }
        }

        if (treeMap.size() == 0) {
            return fields;
        }

	// create a list of filter based on properties file, each key =
        // column/filter
        fields = new ArrayList<>();

        for (Map.Entry<Integer, String> entry : treeMap.entrySet()) {

            String keyName = entry.getValue();
            String columnName = props.getProperty(keyName);
            String keyType = keyName.replace(".name", ".type");
            String columnType = props.getProperty(keyType);
            if (columnType == null) {
                columnType = "";
            }

            switch (columnType.toLowerCase()) {
                case "string":
                    fields.add(DataTypes.createStructField(columnName, DataTypes.StringType, true));
                    break;
                case "int":
                case "integer":
                    fields.add(DataTypes.createStructField(columnName, DataTypes.IntegerType, true));
                    break;
                case "long":
                    fields.add(DataTypes.createStructField(columnName, DataTypes.LongType, true));
                    break;
                case "float":
                    fields.add(DataTypes.createStructField(columnName, DataTypes.FloatType, true));
                    break;
                case "double":
                    fields.add(DataTypes.createStructField(columnName, DataTypes.DoubleType, true));
                    break;
                case "bool":
                case "boolean":
                    fields.add(DataTypes.createStructField(columnName, DataTypes.BooleanType, true));
                    break;
                case "date":
                    fields.add(DataTypes.createStructField(columnName, DataTypes.DateType, true));
                    break;
                case "timestamp":
                    fields.add(DataTypes.createStructField(columnName, DataTypes.TimestampType, true));
                    break;
                default:
                    fields.add(DataTypes.createStructField(columnName, DataTypes.StringType, true));
            }
        }

        return fields;
    }

    public static String getQuery(Properties props) {

        String query = "";

        for (String key : props.stringPropertyNames()) {
            if (key.matches("com.openet.enigma.usecase.*.query.*")) {
                query = props.getProperty(key);
            }
        }

        return query;
    }

    public static String getPredictQuery(Properties props) {

        String predictQuery = "";

        for (String key : props.stringPropertyNames()) {
            if (key.matches("com.openet.enigma.usecase.*.predictQuery.*")) {
                predictQuery = props.getProperty(key);
            }
        }

        return predictQuery;
    }

    public static String getTrainQuery(Properties props) {

        String trainQuery = "";

        for (String key : props.stringPropertyNames()) {
            if (key.matches("com.openet.enigma.usecase.*.trainQuery.*")) {
                trainQuery = props.getProperty(key);
            }
        }

        return trainQuery;
    }

    public static String getFilePath(Properties props) {

        String path = "";
        for (String key : props.stringPropertyNames()) {
            if (key.matches("com.openet.enigma.usecase.*.savepath")) {
                path = props.getProperty(key).trim();
            }
        }

        return path;
    }

    public static boolean getIsSingleFileSave(Properties props) {

        String value;
        for (String key : props.stringPropertyNames()) {
            if (key.matches("com.openet.enigma.usecase.*.savesinglefile")) {
                value = props.getProperty(key);
                if (Boolean.parseBoolean(value)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static String getDateFormat(Properties props) {

        //default pattern
        String dateFormatPattern = "dd/MM/yyyy";
        for (String key : props.stringPropertyNames()) {
            if (key.matches("com.openet.enigma.usecase.*.dateformat")) {
                dateFormatPattern = props.getProperty(key).trim();
            }
        }

        return dateFormatPattern;
    }

}
