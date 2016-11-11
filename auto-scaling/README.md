#Auto Scaling Use Case
This use case showcases rective and predictive scaling of VNFs. We use [Apache Spark](http://spark.apache.org/) as our engine.

It supports scaling based on the following:
1. CPU Utilization %
2. Memory Utilization %
3. Vnfc count

Using this Repo
------------------
## Building
We use [Maven](https://maven.apache.org/) for building Java
    
    git clone https://github.com/Openet-Labs/machine-learning.git
    cd machine-learning/auto-scaling/
    mvn -DskipTests clean package
    cd ..
    ./deploy.sh

Running`deploy.sh` will create a `bin` folder with all the required files for running the use case:
* auto-scaling-usecase-1.0-SNAPSHOT.jar - the use case jar file
* autoscale.properties - the properties file used for configuring the use case
* log4j.properties - the properties file used for configuring logging options
* runAutoScaling.sh - the script used to run the use case
* training-data-generator-1.0-SNAPSHOT-executable.jar - the sample training data generation jar
* application.properties - the properties file for the training data generation

## Setup

### Configuration
We use `autoscale.properties` file for use case configuration which can found in the `bin` folder.
Kafka topics are used as input for this use case, some default values have already been configured

Kafka related
* kafka.topic - real time input
* kafka.topic.train - training input topic
* kafka.host - hostname of the machine that is running the kafka service
* kafka.consumer.group
* kafka.perTopicKafkaPartitions
* kafka.zk.quorum
* kafka.broker

Apache Spark related
* spark.streaming.duration - the frequency in milliseconds the real time input kafka topic is queried

Use Case specific
* autoscaling.future.interval - the duration in minutes in the future for vnfc prediction


### Training data
To generate sample training data follow [Training Data Generator](https://github.com/Openet-Labs/machine-learning/tree/master/auto-scaling/training-data-generator)
    
    
## Running
Edit the `runAutoScaling.sh` file which can be found in the bin folder and set `SPARK_HOME` to the path of your local Apache Spark installation,
example:

    SPARK_HOME=/home/openet/enigma/spark/spark-1.6.1-bin-hadoop2.6

To Start the Use Case:

    cd bin
    ./runAutoScaling.sh
    
To Test insert the real time input into the kafka topic in the [correct format](https://github.com/Openet-Labs/machine-learning/tree/master/auto-scaling/auto-scaling-usecase/etc/sample_input.json)
