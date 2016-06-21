package com.openet.labs.machineLearning.autoScale;

import static org.junit.Assert.assertEquals;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(value = IntegrationTest.class)
public class DataFormatterTest implements Serializable {

    private static final long serialVersionUID = 1L;

    private Properties props;
    private List<String> input;
    private transient SparkConf sparkConf;
    private transient JavaSparkContext sc;
    private transient SQLContext sqlContext;

    private List<StructField> fields;
    private StructType schema;
    private JavaRDD<String> inputFileRDD;
    private JavaRDD<List<Object>> objectRDD;

    @Before
    public void setupTest() {
        sparkConf = new SparkConf().setAppName("com.openet.labs.machineLearning.autoScale.DataConverterTest").setMaster("local");
        sc = new JavaSparkContext(sparkConf);
        sqlContext = new SQLContext(sc);

        props = new Properties();
        props.setProperty("com.openet.enigma.usecase.filterEDR.column.1.name", "date");
        props.setProperty("com.openet.enigma.usecase.filterEDR.column.1.type", "long");
        props.setProperty("com.openet.enigma.usecase.filterEDR.column.2.name", "msisdn");
        props.setProperty("com.openet.enigma.usecase.filterEDR.column.2.type", "long");
        props.setProperty("com.openet.enigma.usecase.filterEDR.column.3.name", "action");
        props.setProperty("com.openet.enigma.usecase.filterEDR.column.3.type", "String");
        props.setProperty("com.openet.enigma.usecase.filterEDR.column.4.name", "status");
        props.setProperty("com.openet.enigma.usecase.filterEDR.column.4.type", "String");

        props.setProperty("com.openet.enigma.usecase.filterEDR.query.1", "SELECT date,msisdn,action,status FROM dataTable where status = 'Success'");

        input = Arrays.asList("1438296383,201276308836,UnSubscriberOffer,Success", "1436514748,201276358109,AddSubscriber,Success", "1437669762,201276376631,AddSubscriber,Failed",
                "1438117684,201276353640,SubscribeOffer,Success", "1435837813,201276325380,UpdateSubscriber,Failed", "1437105217,201276363210,UnSubscriberOffer,Success",
                "1437159362,201276370139,AddSubscriber,Success", "1435885734,201276305161,UpdateSubscriber,Failed", "1436483883,201276398853,UnSubscriberOffer,Success",
                "1437729616,201276308515,UpdateSubscriber,Failed");

        fields = SchemaParser.getStructField(props);
        schema = DataTypes.createStructType(fields);

        inputFileRDD = sc.parallelize(input);
        objectRDD = inputFileRDD.map(new InputParser(fields)).cache();

    }

    @After
    public void tearDown() {
        if (sc != null) {
            sc.stop();
        }
    }

    @Test
    public void testConvertListObjectToRowRDD() {

        JavaRDD<Row> rowRDD = DataConverter.convertListObjectToRowRDD(objectRDD);
        DataFrame dataFrame = sqlContext.createDataFrame(rowRDD, schema);

        assertEquals(rowRDD.count(), dataFrame.javaRDD().count());

    }

    @Test
    public void testConvertDataFrameToListObjectRDD() {

        JavaRDD<Row> rowRDD = DataConverter.convertListObjectToRowRDD(objectRDD);
        DataFrame dataFrame = sqlContext.createDataFrame(rowRDD, schema);

        JavaRDD<List<Object>> resultRDD = DataConverter.convertDataFrameToListObjectRDD(dataFrame);

        assertEquals(dataFrame.javaRDD().count(), resultRDD.count());

    }

    @Test
    public void testconvertListObjectToListStringRDD() {

        JavaRDD<List<String>> stringRDD = DataConverter.convertListObjectToListStringRDD(objectRDD);
        assertEquals(objectRDD.count(), stringRDD.count());
        String expectedResult = "[1438296383, 201276308836, UnSubscriberOffer, Success]";
        assertEquals(expectedResult, stringRDD.first().toString());

    }
    
    @Test
    public void testconvertListObjectToListStringDateRDD() {

    	Properties propsNew = new Properties();
    	propsNew.setProperty("com.openet.enigma.usecase.filterEDR.column.1.name", "dob");
    	propsNew.setProperty("com.openet.enigma.usecase.filterEDR.column.1.type", "date");
    	propsNew.setProperty("com.openet.enigma.usecase.filterEDR.column.2.name", "msisdn");
    	propsNew.setProperty("com.openet.enigma.usecase.filterEDR.column.2.type", "long");
    	propsNew.setProperty("com.openet.enigma.usecase.filterEDR.column.3.name", "action");
    	propsNew.setProperty("com.openet.enigma.usecase.filterEDR.column.3.type", "String");
    	propsNew.setProperty("com.openet.enigma.usecase.filterEDR.column.4.name", "status");
    	propsNew.setProperty("com.openet.enigma.usecase.filterEDR.column.4.type", "String");
    	propsNew.setProperty("com.openet.enigma.usecase.filterEDR.dateformat", "dd/MM/yyyy");
    	
    	String dateFormat = SchemaParser.getDateFormat(propsNew);
    	final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(dateFormat); 
        
        List<StructField> fieldsNew = SchemaParser.getStructField(propsNew);
                
    	List<String> inputNew = Arrays.asList("20/01/1980,201276308836,UnSubscriberOffer,Success"
    											, "29/01/1980,201276376631,AddSubscriber,Failed");
    	
    	JavaRDD<String> inputFileNewRDD = sc.parallelize(inputNew);        
    	JavaRDD<List<Object>> objectNewRDD = inputFileNewRDD.map(new InputParser(fieldsNew, dateFormatter));
    	        
        JavaRDD<List<String>> stringRDD = DataConverter.convertListObjectToListStringRDD(objectNewRDD, fieldsNew, dateFormat);
        System.out.println(stringRDD.first());
        assertEquals(objectNewRDD.count(), stringRDD.count());
        String expectedResult = "[20/01/1980, 201276308836, UnSubscriberOffer, Success]";
        assertEquals(expectedResult, stringRDD.first().toString());

    }

}
