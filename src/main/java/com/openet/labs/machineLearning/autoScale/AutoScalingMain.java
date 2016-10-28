package com.openet.labs.machineLearning.autoScale;

import com.openet.labs.machineLearning.autoScale.utils.EnigmaKafkaUtils;
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
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.regression.RandomForestRegressor;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.api.java.UDF1;
import static org.apache.spark.sql.functions.callUDF;
import static org.apache.spark.sql.functions.explode;
import org.apache.spark.sql.types.DataTypes;

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
    private transient SQLContext sqlContext;
    private Map<String, PipelineModel> pipelineModelMap;

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

    public static void main(String[] args) {

    }

    public void init() throws IOException {

        enableFileLog();
        setParser(new PropertiesParser());
        setEnigmaKafkaUtils(new EnigmaKafkaUtils());
        setPropertyValues();
        // Create SQLContext and register UDF
        if (null != getJavaSparkContext()) {

            setSqlContext(new SQLContext(getJavaSparkContext()));
            registerUDF(getSqlContext());
        }
        setPipelineModelMap(new HashMap<>());

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

        DataFrame inputTrainFeaturesDF = inputTrainDF.withColumn("dayofweek", callUDF("ts2Day", inputTrainDF.col("Timestamp"))).withColumn("hrminofday",
                callUDF("ts2HrMin", inputTrainDF.col("Timestamp")));

        List<String> listVdus = inputTrainDF.select("Vdu").javaRDD().map(x -> x.get(0).toString()).distinct().collect();
        LOGGER.info("listVdus: " + listVdus);

        VectorAssembler assembler = new VectorAssembler().setInputCols(new String[]{"dayofweek", "hrminofday"}).setOutputCol("features");
        DataFrame inputTrainVectorDF = assembler.transform(inputTrainFeaturesDF);
//
        LOGGER.debug("inputTrainFeaturesDF: " + inputTrainFeaturesDF.first());
        LOGGER.info("inputTrainFeaturesDF records: " + inputTrainFeaturesDF.count());

        LOGGER.debug("inputTrainVectorDF: " + inputTrainVectorDF.first());
        LOGGER.info("inputTrainVectorDF records: " + inputTrainVectorDF.count());

        for (String vdu : listVdus) {

            DataFrame perVduTrainDataDF = inputTrainVectorDF.filter(inputTrainDF.col("Vdu").equalTo(vdu)).select(inputTrainVectorDF.col("Metric").alias("label"), inputTrainVectorDF.col("features"));
            LOGGER.debug("perVduTrainDataDF: " + perVduTrainDataDF.first());
            LOGGER.info("perVduTrainDataDF records: " + perVduTrainDataDF.count());
            // Create labeledPoint dataframe

            // Train a RandomForest model
            RandomForestRegressor randomForest = new RandomForestRegressor().setLabelCol("label").setFeaturesCol("features");

            // Chain indexer and forest in a Pipeline
            Pipeline pipeline = new Pipeline().setStages(new PipelineStage[]{randomForest});

            // Train model.  This also runs the indexer.
            PipelineModel model = pipeline.fit(perVduTrainDataDF);

            // Store the model into a map
            getPipelineModelMap().put(vdu, model);
        }
        LOGGER.info("train():: Current model in pipelineModelMap:: " + getPipelineModelMap().keySet().toString());
        LOGGER.info("Complete Training");

    }

    public JavaRDD<String> getTrainingData() {

        return getEnigmaKafkaUtils().kafkaGetRDD(getJavaSparkContext(), getKafkaHost(), getKafkaTrainTopic(), getKafkaCosumerGroup(), getZookeeperQuorum(), getKafkaBroker(), getPerTopicKafkaPartitions());
    }

    public void registerUDF(SQLContext sqlContext) {

        sqlContext.udf().register("ts2Day", new UDF1<String, Double>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Double call(String ts) throws Exception {
                Date date = new Date();
                date.setTime((long) Long.parseLong(ts));

                java.util.Calendar cal = Calendar.getInstance();
                cal.setTime(date);

                return (double) cal.get(java.util.Calendar.DAY_OF_WEEK);
            }
        }, DataTypes.DoubleType);

        sqlContext.udf().register("ts2HrMin", new UDF1<String, Double>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Double call(String ts) throws Exception {
                Date date = new Date();
                date.setTime((long) Long.parseLong(ts));

                java.util.Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                cal.setTimeZone(TimeZone.getTimeZone("GMT"));
                return (double) ((cal.get(java.util.Calendar.HOUR_OF_DAY) * 60) + cal.get(java.util.Calendar.MINUTE));
            }
        }, DataTypes.DoubleType);
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

    //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
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
