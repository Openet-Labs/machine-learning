#Auto Scaling Use Case
This use case showcases Reactive Auto Scaling of VNFs which is based on current input values and 
Predictive Auto Scaling of VNFs which is scaling based on predicted values for more information refer to: [Auto Scaling](https://en.wikipedia.org/wiki/Autoscaling) 

We use [Apache Spark](http://spark.apache.org/) as our engine.

It supports scaling based on the following:
* CPU Utilization %
* Memory Utilization %
* Vnfc count

Using this Repo
------------------
## Building
We use [Maven](https://maven.apache.org/) for building Java
    
    git clone https://github.com/Openet-Labs/machine-learning.git
    cd machine-learning/auto-scaling/
    mvn clean install
    cd ..
    ./deploy.sh

Running`deploy.sh` will create a `bin` folder with all the required files for running the use case:
* auto-scaling-usecase-1.0-SNAPSHOT.jar - the use case jar file
* autoscale.properties - the properties file used for configuring the use case
* log4j.properties - the properties file used for configuring logging options
* runAutoScaling.sh - the script used to run the use case
* training-data-generator-1.0-SNAPSHOT-executable.jar - the sample training data generation jar
* application.properties - the properties file for the training data generation
* sample_input.json - contains sample json input we will use for testing
* sendTestInput.sh - sends the sample_input.json to the input kafka topic

## Setup

### Apache Kafka
Kafka topics are used as input for this use case, ensure that you have Kafka service installed and running. 
You can either: 

* install Kafka as standalone, refer to [Kafka Quickstart](https://kafka.apache.org/quickstart)
* install Enigma which comes with kafka pre-installed [Vagrant Enigma](https://github.com/Openet-Labs/vagrant-enigma) 

Continue to create the required topics:

    cd [kafka_installed_path]

To create kafka input topic - com.openet.autoscaling

    bin/kafka-topics.sh --create --topic com.openet.autoscaling --zookeeper localhost:2181 --replication-factor 1 --partitions 1
To create kafka training topic - com.openet.autoscaling.train

    bin/kafka-topics.sh --create --topic com.openet.autoscaling.train --zookeeper localhost:2181 --replication-factor 1 --partitions 1

*Note that some parameters could be different depending on your environment*


### Configuration
We use `autoscale.properties` file for use case configuration which can found in the `bin` folder.


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
###Start the use case
Edit the `runAutoScaling.sh` file which can be found in the bin folder and set `SPARK_HOME` to reflect the path of your local Apache Spark installation,
example:

    SPARK_HOME=/home/openet/enigma/spark/spark-1.6.1-bin-hadoop2.6

To Start the Use Case:

    cd bin
    ./runAutoScaling.sh

### Testing the use case
Start the provided netcat script in another terminal to receive scaling rest calls from the use case
    
    ./startNetcatRestListerner.sh

Edit the script to set the variables `KAFKA_PATH` to your local kafka installation path and `KAFKA_TOPIC_INPUT` to your respective input topic.
After than run the script as below.

    ./sendTestInput.sh

Once the use case has completed processing the netcat script above would reflect the scaling called issued by the use case

### Input format
Real time input into the kafka topic should be in the [correct format](https://github.com/Openet-Labs/machine-learning/tree/master/auto-scaling/auto-scaling-usecase/etc/sample_input.json)

