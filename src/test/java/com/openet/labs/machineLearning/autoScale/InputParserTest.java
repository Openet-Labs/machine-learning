package com.openet.labs.machineLearning.autoScale;

import org.junit.Test;

import static org.junit.Assert.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
//Import factory methods provided by DataTypes.
import org.apache.spark.sql.types.DataTypes;
//import org.hibernate.engine.spi.RowSelection;
import org.apache.spark.sql.types.StructField;

import org.junit.After;
import org.junit.Before;

import org.junit.experimental.categories.Category;

@Category(value = IntegrationTest.class)
public class InputParserTest implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> input;
    private transient SparkConf sparkConf;
    private transient JavaSparkContext sc;

    private JavaRDD<String> inputFileRDD;

    @Before
    public void setupTest() {
        sparkConf = new SparkConf().setAppName("com.openet.enigma.common.QueryTaskExecutorTest").setMaster("local");
        sc = new JavaSparkContext(sparkConf);

        input = Arrays.asList("michal,21,201276358109,1.75,2.0025,true,11/01/2013");
        inputFileRDD = sc.parallelize(input);

    }

    @After
    public void tearDown() {
        if (sc != null) {
            sc.stop();
        }
    }

    @Test
    public void testInputParserDefinedTypes() {

        List<StructField> fields = new ArrayList<>();
        fields.add(DataTypes.createStructField("name", DataTypes.StringType, true));
        fields.add(DataTypes.createStructField("age", DataTypes.IntegerType, true));
        fields.add(DataTypes.createStructField("msisdn", DataTypes.LongType, true));
        fields.add(DataTypes.createStructField("height", DataTypes.FloatType, true));
        fields.add(DataTypes.createStructField("latitude", DataTypes.DoubleType, true));
        fields.add(DataTypes.createStructField("Subscriber", DataTypes.BooleanType, true));
        fields.add(DataTypes.createStructField("LocalDate", DataTypes.DateType, true));

        JavaRDD<List<Object>> objectRDD = inputFileRDD.map(new InputParser(fields));

        //Ensure types are correct
        Object obj = objectRDD.first().get(0);
        assertTrue(obj.getClass().equals(String.class));
        obj = objectRDD.first().get(1);
        assertTrue(obj.getClass().equals(Integer.class));
        obj = objectRDD.first().get(2);
        assertTrue(obj.getClass().equals(Long.class));
        obj = objectRDD.first().get(3);
        assertTrue(obj.getClass().equals(Float.class));
        obj = objectRDD.first().get(4);
        assertTrue(obj.getClass().equals(Double.class));
        obj = objectRDD.first().get(5);
        assertTrue(obj.getClass().equals(Boolean.class));
        obj = objectRDD.first().get(6);
        assertTrue(obj.getClass().equals(LocalDate.class));
    }

}
