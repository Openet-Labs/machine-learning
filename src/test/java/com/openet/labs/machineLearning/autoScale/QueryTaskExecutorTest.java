package com.openet.labs.machineLearning.autoScale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SQLContext;
//Import factory methods provided by DataTypes.
import org.apache.spark.sql.types.DataTypes;
//import org.hibernate.engine.spi.RowSelection;
import org.apache.spark.sql.types.StructField;
//Import StructType and StructField
import org.apache.spark.sql.types.StructType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(value = IntegrationTest.class)
public class QueryTaskExecutorTest implements Serializable {

    private static final long serialVersionUID = 1L;

    private Properties props;
    private List<String> input;
    private transient SparkConf sparkConf;
    private transient JavaSparkContext sc;
    private transient SQLContext sqlContext;

    private List<StructField> fields;
    private StructType schema;
    private String query;    
    private DateTimeFormatter dateFormatter;
    private JavaRDD<String> inputFileRDD;
    private JavaRDD<List<Object>> objectRDD;

    @Before
    public void setupTest() {
        sparkConf = new SparkConf().setAppName("com.openet.enigma.common.QueryTaskExecutorTest").setMaster("local");
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
        query = SchemaParser.getQuery(props);        
        
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
    public void testExecuteQueryTypes() throws IllegalArgumentException, IOException, URISyntaxException {

        query = "SELECT date,msisdn,action,status FROM dataTable where status = 'Success'";
        JavaRDD<List<Object>> resultsRDD = QueryTaskExecutor.executeQuery(sqlContext, objectRDD, schema, query);

        //Ensure types are correct
        Object obj = resultsRDD.first().get(0);
        assertTrue(obj.getClass().equals(Long.class));
        obj = resultsRDD.first().get(1);
        assertTrue(obj.getClass().equals(Long.class));
        obj = resultsRDD.first().get(2);
        assertTrue(obj.getClass().equals(String.class));
        obj = resultsRDD.first().get(3);
        assertTrue(obj.getClass().equals(String.class));

    }

    @Test
    public void testExecuteQueryResultCount() {
        //input has 10 rows
        assertEquals(10, objectRDD.count());
        query = "SELECT date,msisdn,action,status FROM dataTable where action in ('AddSubscriber','SubscribeOffer') AND status = 'Success'";
        JavaRDD<List<Object>> resultsRDD = QueryTaskExecutor.executeQuery(sqlContext, objectRDD, schema, query);
        //output has 3 rows
        assertEquals(3, resultsRDD.count());
    }

    @Test
    public void testExecuteQueryResultValues() {
        query = "SELECT date,msisdn,action,status FROM dataTable where msisdn = 201276353640";
        JavaRDD<List<Object>> resultsRDD = QueryTaskExecutor.executeQuery(sqlContext, objectRDD, schema, query);
        //output has 1 rows
        assertEquals(1, resultsRDD.count());
        //check values
        assertEquals(1438117684, (long) resultsRDD.first().get(0));
        assertEquals(201276353640L, (long) resultsRDD.first().get(1));
        assertEquals("SubscribeOffer", (String) resultsRDD.first().get(2));
        assertEquals("Success", (String) resultsRDD.first().get(3));

    }

    @Test
    public void testExecuteQueryResultColumns() {
        query = "SELECT date,msisdn,action,status FROM dataTable where msisdn = 201276353640 AND status = 'Success'";
        JavaRDD<List<Object>> resultsRDD = QueryTaskExecutor.executeQuery(sqlContext, objectRDD, schema, query);
        //output has 4 columns
        assertEquals(4, resultsRDD.first().size());

        query = "SELECT msisdn,action FROM dataTable where msisdn = 201276353640";
        resultsRDD = QueryTaskExecutor.executeQuery(sqlContext, objectRDD, schema, query);
        //output has 2 columns
        assertEquals(2, resultsRDD.first().size());
        //check values        
        assertEquals(201276353640L, (long) resultsRDD.first().get(0));
        assertEquals("SubscribeOffer", (String) resultsRDD.first().get(1));

    }
    
    @Test
    public void testExecuteQueryResultDateColumns() {
    	
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
        dateFormatter = DateTimeFormatter.ofPattern(dateFormat); 
        
        List<StructField> fieldsNew = SchemaParser.getStructField(propsNew);
        StructType schemaNew = DataTypes.createStructType(fieldsNew);
        
    	List<String> inputNew = Arrays.asList("20/01/1980,201276308836,UnSubscriberOffer,Success"
    											, "29/01/1980,201276376631,AddSubscriber,Failed");
    	
    	JavaRDD<String> inputFileNewRDD = sc.parallelize(inputNew);        
    	JavaRDD<List<Object>> objectNewRDD = inputFileNewRDD.map(new InputParser(fieldsNew, dateFormatter));
        
        String queryNew = "SELECT dob,msisdn,action,status FROM dataTable where status = 'Success'";
        JavaRDD<List<Object>> resultsRDD = QueryTaskExecutor.executeQuery(sqlContext, objectNewRDD, schemaNew, queryNew);
        //output has 4 columns
        assertEquals(4, resultsRDD.first().size());       
        
        //check values        
        LocalDate resultDate = ((Date)resultsRDD.first().get(0)).toLocalDate();        
        assertEquals("20/01/1980", resultDate.format(dateFormatter));
        

    }

}
