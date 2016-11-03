package com.openet.labs.ml.autoscale;


import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.tree.model.DecisionTreeModel;
import org.apache.spark.mllib.tree.model.RandomForestModel;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import scala.Tuple2;

public class PredictRequestPerSecond implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(PredictRequestPerSecond.class);

    private DecisionTreeModel decisionTreeModel;
    private RandomForestModel randomForestModel;
    private String modelPath;
    private AlgorithmML algo = AlgorithmML.DecisionTreeModel;

    public enum AlgorithmML {
        DecisionTreeModel, RandomForestModel
    };

    // this is for real time prediction
    public double runPredictionRps(Properties props, JavaSparkContext jsc, String timeStamp) throws Exception {

        // do init 
        if (!isModelsLoaded()) {
            setAlgo(AlgorithmML.RandomForestModel);
            setModelSavePath(props);
            loadModels(jsc.sc());
        }

        String command = getExecCommand(props);
        String trainFilePath = getTrainingFilePath(props);
        LOGGER.info("Detected Command: " + command);
        LOGGER.info("Training file path: " + trainFilePath);

        if (command.equals("TRAIN") || !isModelsLoaded()) {

            LOGGER.info("Models not loaded or TRAIN command detected, proceeding to train!");
            JavaRDD<String> inputFileRDD = jsc.textFile(trainFilePath);
            trainModel(inputFileRDD, props, jsc);
            saveModels(jsc.sc());

        } else {
            LOGGER.info("Model loaded!!");
        }

        //do prediction for timeStamp
        double predictedRps;
        double data[] = convertInputToPredictFormat(timeStamp);

        RandomForestRegression dfr = new RandomForestRegression();
        Tuple2<Double, Double> result = dfr.predictModel(getRandomForestModel(), data);
        predictedRps = result._2.longValue();

        return predictedRps;

    }

   

    public double[] convertInputToPredictFormat(String timeStampCurrent) {

        double data[] = new double[3];
        Timestamp tsCurrent = Timestamp.valueOf(timeStampCurrent);
        data[0] = tsCurrent.getTime();
        java.util.Calendar cal = Calendar.getInstance();
        cal.setTime(tsCurrent);
        data[1] = cal.get(java.util.Calendar.DAY_OF_WEEK);
        data[2] = (cal.get(java.util.Calendar.HOUR_OF_DAY) * 60) + cal.get(java.util.Calendar.MINUTE);

        return data;

    }

    public DataFrame prepData(JavaRDD<List<Object>> objectRDD, StructType schema, SQLContext sqlContext) {
        // Get max date
        String maxDatetimeQuery = "SELECT MAX(datetime) FROM dataTable";
        DataFrame maxDataDF = QueryTaskExecutor.executeQueryDF(sqlContext, objectRDD, schema, maxDatetimeQuery);
        Row maxRow = maxDataDF.first();
        String maxDate = maxRow.get(0).toString();

        Timestamp maxTimestamp = Timestamp.valueOf(maxDate);
        Calendar cal = Calendar.getInstance();
        cal.setTime(maxTimestamp);
        cal.add(Calendar.DAY_OF_WEEK, -7);
        Timestamp oneWeekBeforeTimestamp = new Timestamp(cal.getTime().getTime());
        cal.add(Calendar.DAY_OF_WEEK, -7);
        Timestamp twoWeekBeforeTimestamp = new Timestamp(cal.getTime().getTime());
        cal.add(Calendar.DAY_OF_WEEK, -7);
        Timestamp threeWeekBeforeTimestamp = new Timestamp(cal.getTime().getTime());

        // Latest One Week
        String prepOneWeekBeforeQuery = "SELECT datetime, rps FROM dataTable WHERE datetime > '" + oneWeekBeforeTimestamp.toString() + "'";
        DataFrame trainDataFirstSegmentDF = QueryTaskExecutor.executeQueryDF(sqlContext, objectRDD, schema, prepOneWeekBeforeQuery);

        // Two Weeks Before
        String prepTwoWeekBeforeQuery = "SELECT datetime, rps, udfTimeStampToMinutes(datetime) AS minOfDay FROM dataTable WHERE datetime BETWEEN '"
                + twoWeekBeforeTimestamp.toString() + "' AND '" + oneWeekBeforeTimestamp.toString() + "'";
        DataFrame trainDataSecondSegmentDF = QueryTaskExecutor.executeQueryDF(sqlContext, objectRDD, schema, prepTwoWeekBeforeQuery);

        String prepTwoWeekBefore5minsQuery = "SELECT datetime, rps FROM dataTable WHERE (minOfDay%5) = 0";
        DataFrame trainDataSecondSegment5minsDF = QueryTaskExecutor.executeQueryDF(sqlContext, trainDataSecondSegmentDF, prepTwoWeekBefore5minsQuery);

        // Three Weeks Before
        String prepThreeWeekBeforeQuery = "SELECT datetime, rps, udfTimeStampToMinutes(datetime) AS minOfDay FROM dataTable WHERE datetime BETWEEN '"
                + threeWeekBeforeTimestamp.toString() + "' AND '" + twoWeekBeforeTimestamp.toString() + "'";
        DataFrame trainDataThirdSegmentDF = QueryTaskExecutor.executeQueryDF(sqlContext, objectRDD, schema, prepThreeWeekBeforeQuery);

        String prepThreeWeekBefore15minsQuery = "SELECT datetime, rps FROM dataTable WHERE (minOfDay%15) = 0";
        DataFrame trainDataThirdSegment15minsDF = QueryTaskExecutor.executeQueryDF(sqlContext, trainDataThirdSegmentDF, prepThreeWeekBefore15minsQuery);

        // // Rest of Old Week
        String prepFourWeekBeforeQuery = "SELECT datetime, rps, udfTimeStampToMinutes(datetime) AS minOfDay FROM dataTable WHERE datetime < '" + threeWeekBeforeTimestamp.toString()
                + "'";
        DataFrame trainDataFourthSegmentDF = QueryTaskExecutor.executeQueryDF(sqlContext, objectRDD, schema, prepFourWeekBeforeQuery);

        String prepFourWeekBefore30minsQuery = "SELECT datetime, rps FROM dataTable WHERE (minOfDay%30) = 0";
        DataFrame trainDataFourthSegment30minsDF = QueryTaskExecutor.executeQueryDF(sqlContext, trainDataFourthSegmentDF, prepFourWeekBefore30minsQuery);

        DataFrame trainDataFinalDF = trainDataFirstSegmentDF.unionAll(trainDataSecondSegment5minsDF).unionAll(trainDataThirdSegment15minsDF)
                .unionAll(trainDataFourthSegment30minsDF).orderBy("datetime");

        return trainDataFinalDF;

    }

    public void trainModel(JavaRDD<String> inputFileRDD, Properties props, JavaSparkContext jsc) throws IllegalArgumentException, IOException, URISyntaxException {

        List<StructField> fields = SchemaParser.getStructField(props);
        StructType schema = DataTypes.createStructType(fields);
        SQLContext sqlContext = new SQLContext(jsc);
        String trainQuery = SchemaParser.getTrainQuery(props);

        JavaRDD<List<Object>> objectRDD = inputFileRDD.map(new InputParser(fields));

        //do data prep
        DataFrame trainDataPrepDF = this.prepData(objectRDD, schema, sqlContext);

        DataFrame trainDataDF = QueryTaskExecutor.executeQueryDF(sqlContext, trainDataPrepDF, trainQuery);

        JavaRDD<List<Object>> trainDataObjectRDD = DataConverter.convertDataFrameToListObjectRDD(trainDataDF);

        JavaRDD<double[]> trainDataDoubleRDD = DataConverter.convertListObjectToArrayDoubleRDD(trainDataObjectRDD);

        JavaRDD<LabeledPoint> trainDataLabeledPointRDD = trainDataDoubleRDD.map(x -> new LabeledPoint(x[0], Vectors.dense(Arrays.copyOfRange(x, 1, x.length))));

        if (AlgorithmML.DecisionTreeModel == algo) {
            DecisionTreeRegression dtr = new DecisionTreeRegression();
            DecisionTreeModel modelDT = dtr.trainModel(trainDataLabeledPointRDD);
            this.setDecisionTreeModel(modelDT);
        } else if (AlgorithmML.RandomForestModel == algo) {
            RandomForestRegression dfr = new RandomForestRegression();
            RandomForestModel modelRF = dfr.trainModel(trainDataLabeledPointRDD);
            this.setRandomForestModel(modelRF);
        }

    }

    // this function for predicting a weekly plan 
    public JavaPairRDD<Double, Double> doPredictions(JavaRDD<String> inputFileRDD, Properties props, JavaSparkContext jsc) {

        List<StructField> fields = SchemaParser.getStructField(props);
        StructType schema = DataTypes.createStructType(fields);
        SQLContext sqlContext = new SQLContext(jsc);
        String predictQuery = SchemaParser.getPredictQuery(props);

        JavaRDD<List<Object>> objectRDD = inputFileRDD.map(new InputParser(fields));
        DataFrame predictDataDF = QueryTaskExecutor.executeQueryDF(sqlContext, objectRDD, schema, predictQuery);
        JavaRDD<List<Object>> predictDataObjectRDD = DataConverter.convertDataFrameToListObjectRDD(predictDataDF);
        JavaRDD<double[]> predictDataDoubleRDD = DataConverter.convertListObjectToArrayDoubleRDD(predictDataObjectRDD);

        JavaPairRDD<Double, Double> predictionRDD = null;

        if (AlgorithmML.DecisionTreeModel == algo) {
            DecisionTreeRegression dtr = new DecisionTreeRegression();
            predictionRDD = dtr.predictModel(this.getDecisionTreeModel(), predictDataDoubleRDD);
        } else if (AlgorithmML.RandomForestModel == algo) {
            RandomForestRegression dfr = new RandomForestRegression();
            predictionRDD = dfr.predictModelRdd(this.getRandomForestModel(), predictDataDoubleRDD);
        }

        return predictionRDD;
    }

    public void setModelSavePath(Properties props) {

        String savePath = SchemaParser.getFilePath(props);
        // if path is not defined in property use default path, i.e hdfs
        if ("".equals(savePath) || savePath.length() < 1) {
            //savePath = arguments.getOutputDir();
        }
        if (savePath.contains("file://")) {
            savePath = savePath.replace("file://", "");
        }

        String saveModelPath = savePath + "/models/" + algo.toString();
        setModelPath(saveModelPath);

    }

    public void saveModels(SparkContext sc) throws IllegalArgumentException, IOException, URISyntaxException {

        LOGGER.info("Saving Models");

        String saveModelPath = getModelPath();
        LOGGER.info("Saving Model at Path: " + saveModelPath);

        FileSystem fs = new FileSystem();
        if (fs.isDirectory(saveModelPath)) {
            fs.deleteFile(saveModelPath);
        }

        if (AlgorithmML.DecisionTreeModel == algo) {

            this.getDecisionTreeModel().save(sc, saveModelPath);
        } else if (AlgorithmML.RandomForestModel == algo) {

            this.getRandomForestModel().save(sc, saveModelPath);
        }

    }

    public void loadModels(SparkContext sc) throws IllegalArgumentException, IOException, URISyntaxException {

        LOGGER.info("Loading Models");

        String saveModelPath = getModelPath();
        LOGGER.info("Loading Model at Path: " + saveModelPath);
        LOGGER.info("Algo: " + algo);

        FileSystem fs = new FileSystem();
        if (fs.isDirectory(saveModelPath)) {

            if (AlgorithmML.DecisionTreeModel == algo) {
                this.setDecisionTreeModel(DecisionTreeModel.load(sc, saveModelPath));
            } else if (AlgorithmML.RandomForestModel == algo) {
                this.setRandomForestModel(RandomForestModel.load(sc, saveModelPath));
            }

        }

    }

    public boolean isModelsLoaded() {

        if (AlgorithmML.DecisionTreeModel == algo) {
            if (null == this.getDecisionTreeModel()) {
                return false;
            }
        } else if (AlgorithmML.RandomForestModel == algo) {
            if (null == this.getRandomForestModel()) {
                return false;
            }
        }
        return true;
    }

    public String getExecCommand(Properties props) {
        String runCommand = "";

        for (String key : props.stringPropertyNames()) {
            if (key.matches("com.openet.enigma.usecase.predictAutoScaling.command")) {
                runCommand = props.getProperty(key);
            }
        }
        return runCommand;
    }

    public String getTrainingFilePath(Properties props) {
        String runCommand = "";

        for (String key : props.stringPropertyNames()) {
            if (key.matches("com.openet.enigma.usecase.predictAutoScaling.trainFilePath")) {
                runCommand = props.getProperty(key);
            }
        }
        return runCommand;
    }

    public DecisionTreeModel getDecisionTreeModel() {
        return decisionTreeModel;
    }

    public void setDecisionTreeModel(DecisionTreeModel decisionTreeModel) {
        this.decisionTreeModel = decisionTreeModel;
    }

    public RandomForestModel getRandomForestModel() {
        return randomForestModel;
    }

    public void setRandomForestModel(RandomForestModel randomForestModel) {
        this.randomForestModel = randomForestModel;
    }

    public String getModelPath() {
        return modelPath;
    }

    private void setModelPath(String modelPath) {
        this.modelPath = modelPath;
    }

    public AlgorithmML getAlgo() {
        return algo;
    }

    public void setAlgo(AlgorithmML algo) {
        this.algo = algo;
    }

}
