package com.openet.labs.ml.autoscale;

import com.openet.labs.ml.autoscale.utils.EnigmaKafkaUtils;
import com.openet.labs.ml.autoscale.utils.UdfTimestampToDayOfWeek;
import com.openet.labs.ml.autoscale.utils.UdfTimestampToMinOfHour;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.regression.DecisionTreeRegressor;
import org.apache.spark.ml.regression.RandomForestRegressor;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.api.java.UDF1;
import static org.apache.spark.sql.functions.callUDF;
import static org.apache.spark.sql.functions.explode;
import org.apache.spark.sql.types.DataTypes;
import static org.apache.spark.sql.functions.callUDF;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import static org.apache.spark.sql.functions.callUDF;
import static org.apache.spark.sql.functions.callUDF;
import static org.apache.spark.sql.functions.callUDF;
import static org.apache.spark.sql.functions.callUDF;
import static org.apache.spark.sql.functions.callUDF;
import static org.apache.spark.sql.functions.callUDF;
import static org.apache.spark.sql.functions.callUDF;
import static org.apache.spark.sql.functions.callUDF;
import scala.collection.mutable.WrappedArray;

public class AutoScalingMain implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = Logger.getLogger(AutoScalingMain.class);

    private Properties properties;

    private PropertiesParser parser;

    //Kafka
    private EnigmaKafkaUtils enigmaKafkaUtils;
    private String kafkaHost;

    private String kafkaTopic;
    private String kafkaTrainTopic;
    private Integer perTopicKafkaPartitions;
    private String zookeeperQuorum;
    private String kafkaCosumerGroup;
    private String kafkaBroker;

    //Spark
    private transient JavaSparkContext jsc;
    private transient JavaStreamingContext javaStreamingContext;
    private transient SQLContext sqlContext;
    private Map<String, PipelineModel> pipelineModelMap;
    private Map<String, ItemVdu> vduItemsMap;

    public Map<String, ItemVdu> getVduItemsMap() {
        return vduItemsMap;
    }

    public void setVduItemsMap(Map<String, ItemVdu> vduItemsMap) {
        this.vduItemsMap = vduItemsMap;
    }

    public static void main(String[] args) {

    }

    public void init() throws IOException {

        LOGGER.info("Start init");
        enableFileLog();
        setParser(new PropertiesParser());
        setEnigmaKafkaUtils(new EnigmaKafkaUtils());
        setPropertyValues();
        // Create SQLContext and register UDF
        if (null != getJavaSparkContext()) {

            setSqlContext(new SQLContext(getJavaSparkContext()));
            registerUdfs(getSqlContext());
            setJavaStreamingContext(new JavaStreamingContext(jsc, Durations.milliseconds(2000)));
        }
        setPipelineModelMap(new HashMap<>());
        setVduItemsMap(new HashMap<>());

        LOGGER.info("Complete init");
    }

    public void train() {

        LOGGER.info("Start Training");

        JavaRDD<String> trainDataRDD = getJavaSparkContext().emptyRDD();

        trainDataRDD = getTrainingData();

        if (trainDataRDD.isEmpty() || trainDataRDD.count() == 0) {

            LOGGER.info("No Training records found: exiting!!!");
            return;
        }

        LOGGER.info("trainDataRDD records: " + trainDataRDD.count());
        // Convert RDD string JSON into dataframe
        DataFrame inputTrainDF = getSqlContext().read().json(trainDataRDD).cache();
        LOGGER.debug("Input Train DataFrame: " + inputTrainDF.first());
        LOGGER.info("inputTrainDF records: " + inputTrainDF.count());

        List<String> listVdus = inputTrainDF.select("Vdu").javaRDD().map(x -> x.get(0).toString()).distinct().collect();
        LOGGER.info("listVdus: " + listVdus);

        DataFrame inputTrainFeaturesDF = inputTrainDF.withColumn("dayofweek", callUDF("ts2Day", inputTrainDF.col("Timestamp"))).withColumn("hrminofday",
                callUDF("ts2HrMin", inputTrainDF.col("Timestamp")));

        VectorAssembler assembler = new VectorAssembler().setInputCols(new String[]{"dayofweek", "hrminofday"}).setOutputCol("features");
        DataFrame inputTrainVectorDF = assembler.transform(inputTrainFeaturesDF);

        LOGGER.debug("inputTrainVectorDF: " + inputTrainVectorDF.first());
        LOGGER.info("inputTrainVectorDF records: " + inputTrainVectorDF.count());

        for (String vduId : listVdus) {

            DataFrame perVduTrainDataDF = inputTrainVectorDF.filter(inputTrainDF.col("Vdu").equalTo(vduId));
            DataFrame perVduVnfcsTrainDataDF = perVduTrainDataDF.select(perVduTrainDataDF.col("Vnfcs").cast("double").alias("label"), perVduTrainDataDF.col("features"));
            DataFrame perVduCpuTrainDataDF = perVduTrainDataDF.select(perVduTrainDataDF.col("Memory").alias("label"), perVduTrainDataDF.col("features"));
            DataFrame perVduMemoryTrainDataDF = perVduTrainDataDF.select(perVduTrainDataDF.col("Cpu").alias("label"), perVduTrainDataDF.col("features"));

            // Train model
            PipelineModel modelVnfc = trainModel(perVduVnfcsTrainDataDF, "predictedVnfc");
            PipelineModel modelCpu = trainModel(perVduCpuTrainDataDF, "predictedCpu");
            PipelineModel modelMemory = trainModel(perVduMemoryTrainDataDF, "predictedMemory");

            // Store the model into a map
            ItemVdu vdu = new ItemVdu(vduId);
            vdu.setModelVnfc(modelVnfc);
            vdu.setModelCpu(modelCpu);
            vdu.setModelMemory(modelMemory);
            getVduItemsMap().put(vduId, vdu);
        }

        LOGGER.info("train():: Current model in pipelineModelMap:: " + getVduItemsMap().keySet().toString());
        LOGGER.info("Complete Training");

    }

    public PipelineModel trainModel(DataFrame perVduTrainDataDF, String predictOutCol) {

        // Train a Decision Tree model
        DecisionTreeRegressor decisiontree = new DecisionTreeRegressor()
                .setLabelCol("label")
                .setFeaturesCol("features")
                .setMaxBins(10000)
                .setMaxDepth(10)
                .setPredictionCol(predictOutCol);

        // Chain indexer and forest in a Pipeline
        Pipeline pipeline = new Pipeline().setStages(new PipelineStage[]{decisiontree});

        // Train model.  This also runs the indexer.
        PipelineModel model = pipeline.fit(perVduTrainDataDF);

        return model;

    }

    public void processInputStream() {

        LOGGER.info("Starting processInputStream");
        JavaDStream<String> inputDataStream = getEnigmaKafkaUtils().getKafkaDirectInputStreamOffset(getJavaStreamingContext(), getKafkaHost(), getKafkaTopic(), getKafkaCosumerGroup(), getZookeeperQuorum(), getKafkaBroker(), false);

        inputDataStream.foreachRDD(new VoidFunction<JavaRDD<String>>() {

            private static final long serialVersionUID = 1L;

            @Override
            public void call(JavaRDD<String> inputRDD) throws Exception {

                if (inputRDD.count() > 0) {

                    LOGGER.info("inputRDD: " + inputRDD.first());

                    JavaRDD<String> validatedInputRDD = inputRDD.filter(x -> validateInput(x));

//                    if (validatedInputRDD.count() > 0) {
//
//                        VnfMigrateEntry vme = processMain(validatedInputRDD);
//                        if (vme.isIsMoveVnf() && !isIsMovingVnf()) {
//                            LOGGER.info("Starting to move VNF");
//                            moveVnf(vme);
//                        }
//
//                    } else {
//                        LOGGER.info("Invalid Input!");
//                    }
                }

            }

        });

    }

    public List<String> parseJsonInput(JavaRDD<String> inputRDD) {

        LOGGER.info("Start parseJsonInput");

        DataFrame inputDF = getSqlContext().read().json(inputRDD).cache();

        DataFrame inputVnfDF = inputDF.select(explode(inputDF.col("vnfs")).alias("vnfs"), inputDF.col("timestamp"));

        DataFrame inputVdusDF = inputVnfDF.select(explode(inputVnfDF.col("vnfs.vdus")).alias("vdus"), inputVnfDF.col("vnfs.id").alias("vnfid"), inputVnfDF.col("vnfs.flavor").alias("flavor"), inputVnfDF.col("vnfs.flavors").alias("flavors"), inputVnfDF.col("vnfs._links.scale_up.href").alias("scale_up"), inputVnfDF.col("vnfs._links.scale_down.href").alias("scale_down"), inputVnfDF.col("vnfs._links.scale_to_flavor.href").alias("scale_to_flavor"), inputVnfDF.col("timestamp"));

        DataFrame inputVnfcDF = inputVdusDF.select(explode(inputVdusDF.col("vdus.vnfcs")).alias("vnfcs"), inputVdusDF.col("vdus.id").alias("vduid"), inputVdusDF.col("*"));

        DataFrame inputFinalDF = inputVnfcDF.select(inputVnfcDF.col("*"), inputVnfcDF.col("vnfcs.id").alias("vnfcid"), inputVnfcDF.col("vnfcs.cpu").alias("cpu"), inputVnfcDF.col("vnfcs.memory").alias("memory"))
                .drop(inputVnfcDF.col("vdus"))
                .drop(inputVnfcDF.col("vnfcs"));

        DataFrame inputVnfcWithCountDF = inputFinalDF.groupBy("vduid").count()
                .withColumnRenamed("count", "vnfcCount")
                .join(inputFinalDF, "vduid");

        inputVnfcWithCountDF.show(false);

        DataFrame inputPredictDF = inputVnfcWithCountDF.withColumn("dayofweek", callUDF("ts2Day", inputVnfcWithCountDF.col("timestamp").cast("String")))
                .withColumn("hrminofday", callUDF("ts2HrMin", inputVnfcWithCountDF.col("timestamp").cast("String")))
                ;

        VectorAssembler assembler = new VectorAssembler().setInputCols(new String[]{"dayofweek", "hrminofday"}).setOutputCol("features");
        DataFrame inputPredictVectorDF = assembler.transform(inputPredictDF);

//        inputPredictVectorDF.show();
        List<String> listVdus = inputPredictVectorDF.select("vduid").javaRDD().map(x -> x.get(0).toString()).distinct().collect();
        LOGGER.info("listVdus: " + listVdus);

        DataFrame finalPredictedDF = getSqlContext().emptyDataFrame();
        for (String vduid : listVdus) {

            ItemVdu vduItem = getVduItemsMap().get(vduid);

            DataFrame inputPerVduPredictVectorDF = inputPredictVectorDF.filter(inputPredictVectorDF.col("vduid").equalTo(vduid));
            DataFrame inputPerVduPredictVnfcDF = vduItem.getModelVnfc().transform(inputPerVduPredictVectorDF);
            DataFrame inputPerVduPredictCpuDF = vduItem.getModelCpu().transform(inputPerVduPredictVnfcDF);
            DataFrame inputPerVduPredictMemoryDF = vduItem.getModelMemory().transform(inputPerVduPredictCpuDF);

            if (finalPredictedDF.equals(getSqlContext().emptyDataFrame())) {
                finalPredictedDF = inputPerVduPredictMemoryDF;
            } else {
                finalPredictedDF = finalPredictedDF.unionAll(inputPerVduPredictMemoryDF);
            }

        }

        List<String> listParsed = finalPredictedDF
                .drop(finalPredictedDF.col("features"))
                .drop(finalPredictedDF.col("dayofweek"))
                .drop(finalPredictedDF.col("hrminofday"))
                .drop(finalPredictedDF.col("timestamp"))
                .toJSON().toJavaRDD().collect();

        inputDF.unpersist();

        LOGGER.info("Complete parseJsonInput");

        return listParsed;

        //        LOGGER.info("listParsed: " + listParsed.toString());        
//        LOGGER.info("listParsed: " + listParsed.toString());
    }

    public boolean validateInput(String in) {

//        if (!in.contains("SFC")) {
//            return false;
//        }
//        if (!in.contains("Node_Topology")) {
//            return false;
//        }
//        if (!in.contains("Link_Latency")) {
//            return false;
//        }
//        if (!in.contains("Timestamp")) {
//            return false;
//        }
//        if (in.equals(null) || in.equals("")) {
//            return false;
//        }
        return true;
    }

    private void voting() {

        // reactive scale up, predictive scale up = take bigger number
        // reactive scale up, predictive scale down = take reactive
        // reactive scale down, predictive scale up = take predictive, unless predictive is wrong
        // reactive scale down, predictive scale down = take reactive
    }

    public JavaRDD<String> getTrainingData() {

        return getEnigmaKafkaUtils().kafkaGetRDD(getJavaSparkContext(), getKafkaHost(), getKafkaTrainTopic(), getKafkaCosumerGroup(), getZookeeperQuorum(), getKafkaBroker(), getPerTopicKafkaPartitions());
    }

    public void registerUdfs(SQLContext sqlContext) {

        UdfTimestampToDayOfWeek ts2Day = new UdfTimestampToDayOfWeek();
        sqlContext.udf().register("ts2Day", (UDF1<?, ?>) ts2Day, DataTypes.DoubleType);

        UdfTimestampToMinOfHour ts2HrMin = new UdfTimestampToMinOfHour();
        sqlContext.udf().register("ts2HrMin", (UDF1<?, ?>) ts2HrMin, DataTypes.DoubleType);

    }

    public void setPropertyValues() throws IOException {

        setKafkaHost(getParser().getKafkaHost(getUseCaseProperties()));
        setKafkaTopic(getParser().getKafkaTopic(getUseCaseProperties()));
        setKafkaTrainTopic(getParser().getKafkaTopicTrain(getUseCaseProperties()));
        setKafkaCosumerGroup(getParser().getKafkaConsumerGroup(getUseCaseProperties()));
        setPerTopicKafkaPartitions(getParser().getPerTopicKafkaPartitions(getUseCaseProperties()));
        setZookeeperQuorum(getParser().getKafkaZookeeperQuorum(getUseCaseProperties()));
        setKafkaBroker(getParser().getKafkaBroker(getUseCaseProperties()));

    }

    private void enableFileLog() {

        LOGGER.setLevel(Level.DEBUG);
        RollingFileAppender rfa = new RollingFileAppender();
        rfa.setFile("/tmp/AutoScaling.log");
        rfa.setMaxFileSize("50MB");
        rfa.setLayout(new PatternLayout("%d - [%p] - %m%n"));
        rfa.setAppend(false);
        rfa.activateOptions();
        LOGGER.addAppender(rfa);
    }

    public synchronized void close() throws IOException {
        if (null != getJavaStreamingContext()) {
            getJavaStreamingContext().stop();
            setJavaStreamingContext(null);
        }

    }

    //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
    public JavaStreamingContext getJavaStreamingContext() {
        return javaStreamingContext;
    }

    public void setJavaStreamingContext(JavaStreamingContext javaStreamingContext) {
        this.javaStreamingContext = javaStreamingContext;
    }

    public Map<String, PipelineModel> getPipelineModelMap() {
        return pipelineModelMap;
    }

    public void setPipelineModelMap(Map<String, PipelineModel> pipelineModelMap) {
        this.pipelineModelMap = pipelineModelMap;
    }

    public SQLContext getSqlContext() {
        return sqlContext;
    }

    public void setSqlContext(SQLContext sqlContext) {
        this.sqlContext = sqlContext;
    }

    public JavaSparkContext getJavaSparkContext() {
        return jsc;
    }

    public void setJavaSparkContext(JavaSparkContext jsc) {
        this.jsc = jsc;
    }

    public Properties getUseCaseProperties() throws FileNotFoundException, IOException {
        if (null != properties) {
            return properties;
        }
        properties = new Properties();

        URL url = ClassLoader.getSystemResource("application.properties");
        properties.load(url.openStream());
        return properties;
    }

    public void setUseCaseProperties(Properties properties) {
        this.properties = properties;
    }

    public PropertiesParser getParser() {
        return parser;
    }

    public void setParser(PropertiesParser parser) {
        this.parser = parser;
    }

    public EnigmaKafkaUtils getEnigmaKafkaUtils() {
        return enigmaKafkaUtils;
    }

    public void setEnigmaKafkaUtils(EnigmaKafkaUtils enigmaKafkaUtils) {
        this.enigmaKafkaUtils = enigmaKafkaUtils;
    }

    public String getKafkaHost() {
        return kafkaHost;
    }

    public void setKafkaHost(String kafkaHost) {
        this.kafkaHost = kafkaHost;
    }

    public String getKafkaTopic() {
        return kafkaTopic;
    }

    public void setKafkaTopic(String kafkaTopic) {
        this.kafkaTopic = kafkaTopic;
    }

    public String getKafkaTrainTopic() {
        return kafkaTrainTopic;
    }

    public void setKafkaTrainTopic(String kafkaTrainTopic) {
        this.kafkaTrainTopic = kafkaTrainTopic;
    }

    public Integer getPerTopicKafkaPartitions() {
        return perTopicKafkaPartitions;
    }

    public void setPerTopicKafkaPartitions(Integer perTopicKafkaPartitions) {
        this.perTopicKafkaPartitions = perTopicKafkaPartitions;
    }

    public String getZookeeperQuorum() {
        return zookeeperQuorum;
    }

    public void setZookeeperQuorum(String zookeeperQuorum) {
        this.zookeeperQuorum = zookeeperQuorum;
    }

    public String getKafkaCosumerGroup() {
        return kafkaCosumerGroup;
    }

    public void setKafkaCosumerGroup(String kafkaCosumerGroup) {
        this.kafkaCosumerGroup = kafkaCosumerGroup;
    }

    public String getKafkaBroker() {
        return kafkaBroker;
    }

    public void setKafkaBroker(String kafkaBroker) {
        this.kafkaBroker = kafkaBroker;
    }
    //</editor-fold>
}
