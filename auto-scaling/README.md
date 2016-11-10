#Auto Scaling Use Case

Supports scaling based on the following: 
* Cpu Utilization %
* Memory Utilization %
* Vnfcs

Using this Repo
------------------
## Building
We use [Maven](https://maven.apache.org/) for building Java
    
    git clone https://github.com/Openet-Labs/machine-learning.git
    cd machine-learning/auto-scaling/
    mvn -DskipTests clean package
    cd ..
    ./deploy.sh

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
To generate training data follow [generate training](https://github.com/Openet-Labs/machine-learning/tree/master/auto-scaling/training-data-generator)
    
    
## Running
Edit the `runAutoScaling.sh` file which can be found in the bin folder and set `SPARK_HOME` to the path of your local Apache Spark installation:
example:
    SPARK_HOME=/home/openet/enigma/spark/spark-1.6.1-bin-hadoop2.6

To run:
    cd bin
    ./runAutoScaling.sh
    
