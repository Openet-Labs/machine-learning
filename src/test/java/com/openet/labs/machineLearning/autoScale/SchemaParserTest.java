package com.openet.labs.machineLearning.autoScale;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

//Import factory methods provided by DataTypes.
import org.apache.spark.sql.types.DataTypes;
//import org.hibernate.engine.spi.RowSelection;
import org.apache.spark.sql.types.StructField;
import org.junit.Before;

public class SchemaParserTest {
    Properties completeProps;
    Properties missingTypeProps;
    Properties emptyProps;

    @Before
    public void setupTest() {
        completeProps = new Properties();
        completeProps.setProperty("com.openet.enigma.usecase.filterEDR.column.1.name", "date");
        completeProps.setProperty("com.openet.enigma.usecase.filterEDR.column.1.type", "long");
        completeProps.setProperty("com.openet.enigma.usecase.filterEDR.column.2.name", "msisdn");
        completeProps.setProperty("com.openet.enigma.usecase.filterEDR.column.2.type", "long");
        completeProps.setProperty("com.openet.enigma.usecase.filterEDR.column.3.name", "action");
        completeProps.setProperty("com.openet.enigma.usecase.filterEDR.column.3.type", "String");
        completeProps.setProperty("com.openet.enigma.usecase.filterEDR.column.4.name", "status");
        completeProps.setProperty("com.openet.enigma.usecase.filterEDR.column.4.type", "String");
        completeProps.setProperty("com.openet.enigma.usecase.filterEDR.query.1", "SELECT date,msisdn,action,status FROM dataTable where status = 'Success'");
        completeProps.setProperty("com.openet.enigma.usecase.filterEDR.savepath", "file:///home/openet/TestEnigma/Enigma/Server/results");
        completeProps.setProperty("com.openet.enigma.usecase.filterEDR.savesinglefile", "true");        
        
        missingTypeProps = new Properties();
        missingTypeProps.setProperty("com.openet.enigma.usecase.filterEDR.column.1.name", "date");
        missingTypeProps.setProperty("com.openet.enigma.usecase.filterEDR.column.2.name", "msisdn");

        emptyProps = new Properties();
    }

    @Test
    public void testGetDataFields() {
        List<StructField> validFields = new ArrayList<>();
        validFields.add(DataTypes.createStructField("date", DataTypes.LongType, true));
        validFields.add(DataTypes.createStructField("msisdn", DataTypes.LongType, true));
        validFields.add(DataTypes.createStructField("action", DataTypes.StringType, true));
        validFields.add(DataTypes.createStructField("status", DataTypes.StringType, true));
        //ensure fields are initialized correctly
        List<StructField> resultFields = SchemaParser.getStructField(completeProps);
        assertEquals(validFields, resultFields);
    }

    @Test
    public void testGetDataFieldsNoTypes() {
        List<StructField> validFields = new ArrayList<>();
        validFields.add(DataTypes.createStructField("date", DataTypes.StringType, true));
        validFields.add(DataTypes.createStructField("msisdn", DataTypes.StringType, true));
        //if no type specified it should default to String
        List<StructField> resultFields = SchemaParser.getStructField(missingTypeProps);
        assertEquals(validFields, resultFields);
    }

    @Test
    public void testGetDataFieldsEmptyProperties() {
        //if properties are empty it will return null
        List<StructField> resultFields = SchemaParser.getStructField(emptyProps);
        assertNull(resultFields);
    }

    @Test
    public void testGetQuery() {
        String expectedQuery = "SELECT date,msisdn,action,status FROM dataTable where status = 'Success'";
        String resultQuery = SchemaParser.getQuery(completeProps);
        assertEquals(expectedQuery, resultQuery);
    }
    
    @Test
    public void testGetFilePath() {
        String expectedPath = "file:///home/openet/TestEnigma/Enigma/Server/results";
        String resultPath = SchemaParser.getFilePath(completeProps);
        assertEquals(expectedPath, resultPath);
        resultPath = SchemaParser.getFilePath(missingTypeProps);
        assertEquals("", resultPath);
    }
    
    @Test
    public void testIsSingleFileSave() {        
        boolean resultIsSingleFile = SchemaParser.getIsSingleFileSave(completeProps);
        assertEquals(true, resultIsSingleFile);
        resultIsSingleFile = SchemaParser.getIsSingleFileSave(missingTypeProps);
        assertEquals(false, resultIsSingleFile);
    }
    
    @Test
    public void testDateFormat() {    
    	completeProps.setProperty("com.openet.enigma.usecase.filterEDR.dateformat", "dd-MM-yyyy");
        String dateFormatPattern = SchemaParser.getDateFormat(completeProps);
        assertEquals(dateFormatPattern, "dd-MM-yyyy");
        // test for default value
        dateFormatPattern = SchemaParser.getDateFormat(missingTypeProps);
        assertEquals(dateFormatPattern, "dd/MM/yyyy");
     
    }

}
