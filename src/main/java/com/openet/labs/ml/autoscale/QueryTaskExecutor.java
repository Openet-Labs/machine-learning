package com.openet.labs.ml.autoscale;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Properties;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.DataFrame;
//Import Row.
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.api.java.UDF1;
import org.apache.spark.sql.api.java.UDF2;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

public class QueryTaskExecutor {

	public static JavaRDD<List<Object>> executeQuery(SQLContext sqlContext, JavaRDD<List<Object>> objectRDD,
			StructType schema, String query) {

		UdfLocalDateToAge objParseAgeFromDob = new UdfLocalDateToAge();
		sqlContext.udf().register("GetAge", (UDF1<?, ?>) objParseAgeFromDob, DataTypes.IntegerType);
		
		UdfTimeStampToDay udfTimeStampToDay = new UdfTimeStampToDay();
        sqlContext.udf().register("udfTimeStampToDay", (UDF1<?, ?>) udfTimeStampToDay, DataTypes.IntegerType);

        UdfTimeStampToMinutes udfTimeStampToMinutes = new UdfTimeStampToMinutes();
        sqlContext.udf().register("udfTimeStampToMinutes", (UDF1<?, ?>) udfTimeStampToMinutes, DataTypes.IntegerType);

        UdfTimeStampToLong udfTimeStampToLong = new UdfTimeStampToLong();
        sqlContext.udf().register("udfTimeStampToLong", (UDF1<?, ?>) udfTimeStampToLong, DataTypes.LongType);

		JavaRDD<Row> rowRDD = DataConverter.convertListObjectToRowRDD(objectRDD);
		DataFrame dataFrame = sqlContext.createDataFrame(rowRDD, schema);
		dataFrame.registerTempTable("dataTable");

		DataFrame queryResult = sqlContext.sql(query);

		JavaRDD<List<Object>> resultsRDD = DataConverter.convertDataFrameToListObjectRDD(queryResult);

		return resultsRDD;
	}

	public static JavaRDD<List<Object>> executeQuery(JavaSparkContext sc, JavaRDD<String> inputFileRDD,
			Properties props) {

		SQLContext sqlContext = new SQLContext(sc);
		List<StructField> fields = SchemaParser.getStructField(props);
		StructType schema = DataTypes.createStructType(fields);
		String query = SchemaParser.getQuery(props);

		JavaRDD<List<Object>> objectRDD = inputFileRDD.map(new InputParser(fields));

		JavaRDD<List<Object>> resultsRDD = QueryTaskExecutor.executeQuery(sqlContext, objectRDD, schema, query);

		return resultsRDD;
	}

	public static DataFrame executeQueryDF(SQLContext sqlContext, JavaRDD<List<Object>> objectRDD, StructType schema,
			String query) {

		UdfTimeStampToDay udfTimeStampToDay = new UdfTimeStampToDay();
		sqlContext.udf().register("udfTimeStampToDay", (UDF1<?, ?>) udfTimeStampToDay, DataTypes.IntegerType);

		UdfTimeStampToMinutes udfTimeStampToMinutes = new UdfTimeStampToMinutes();
		sqlContext.udf().register("udfTimeStampToMinutes", (UDF1<?, ?>) udfTimeStampToMinutes, DataTypes.IntegerType);

		UdfTimeStampToLong udfTimeStampToLong = new UdfTimeStampToLong();
		sqlContext.udf().register("udfTimeStampToLong", (UDF1<?, ?>) udfTimeStampToLong, DataTypes.LongType);
                
                UdfTimeStampToDayOfMonth udfTimeStampToDayOfMonth = new UdfTimeStampToDayOfMonth();
		sqlContext.udf().register("udfTimeStampToDayOfMonth", (UDF1<?, ?>) udfTimeStampToDayOfMonth, DataTypes.IntegerType);

		JavaRDD<Row> rowRDD = DataConverter.convertListObjectToRowRDD(objectRDD);
		DataFrame dataFrame = sqlContext.createDataFrame(rowRDD, schema);
		dataFrame.registerTempTable("dataTable");

		DataFrame queryResult = sqlContext.sql(query);

		return queryResult;
	}

	public static DataFrame executeQueryDF(JavaSparkContext sc, JavaRDD<String> inputFileRDD, Properties props) {

		SQLContext sqlContext = new SQLContext(sc);
		List<StructField> fields = SchemaParser.getStructField(props);
		StructType schema = DataTypes.createStructType(fields);
		String query = SchemaParser.getQuery(props);

		JavaRDD<List<Object>> objectRDD = inputFileRDD.map(new InputParser(fields));

		JavaRDD<Row> rowRDD = DataConverter.convertListObjectToRowRDD(objectRDD);
		DataFrame dataFrame = sqlContext.createDataFrame(rowRDD, schema);
		dataFrame.registerTempTable("dataTable");

		DataFrame queryResult = sqlContext.sql(query);

		return queryResult;
	}
        
        public static DataFrame executeQueryDF(JavaSparkContext sc, JavaRDD<String> inputFileRDD, Properties props, String query) {

		SQLContext sqlContext = new SQLContext(sc);
		List<StructField> fields = SchemaParser.getStructField(props);
		StructType schema = DataTypes.createStructType(fields);		

		JavaRDD<List<Object>> objectRDD = inputFileRDD.map(new InputParser(fields));

		JavaRDD<Row> rowRDD = DataConverter.convertListObjectToRowRDD(objectRDD);
		DataFrame dataFrame = sqlContext.createDataFrame(rowRDD, schema);
		dataFrame.registerTempTable("dataTable");

		DataFrame queryResult = sqlContext.sql(query);

		return queryResult;
	}
        

	public static DataFrame executeQueryDF(SQLContext sqlContext, DataFrame inputDF, String query) {

		UdfTimeStampAddSecs udfTimeStampAddSecs = new UdfTimeStampAddSecs();
		UdfTimeStampToDayOfMonth udfTimeStampToDayOfMonth = new UdfTimeStampToDayOfMonth();
                UdfTimeStampToDayOfYear udfTimeStampToDayOfYear = new UdfTimeStampToDayOfYear();
		
		sqlContext.udf().register("udfTimeStampAddSecs", (UDF2<?, ?, ?>) udfTimeStampAddSecs, DataTypes.TimestampType);
		sqlContext.udf().register("UdfTimeStampToDayOfMonth", (UDF1<?, ?>) udfTimeStampToDayOfMonth, DataTypes.TimestampType);
                sqlContext.udf().register("UdfTimeStampToDayOfYear", (UDF1<?, ?>) udfTimeStampToDayOfYear, DataTypes.TimestampType);		
		
		inputDF.registerTempTable("dataTable");

		DataFrame resultDF = sqlContext.sql(query);

		return resultDF;
	}

	public static void saveData(JavaRDD<String> result, String path, boolean isSaveAsSingleFile)
			throws IllegalArgumentException, IOException, URISyntaxException {

		String filePath = path;
		if (filePath.contains("file://")) {
			filePath = filePath.replace("file://", "");
		}
		FileSystem fs = new FileSystem();
		if (fs.isDirectory(filePath)) {
			fs.deleteFile(filePath);
		}

		if (isSaveAsSingleFile) {
			result.coalesce(1, true).saveAsTextFile(path);
		} else {
			result.saveAsTextFile(path);
		}
	}

}
