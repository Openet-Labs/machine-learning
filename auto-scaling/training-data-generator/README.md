## Training Data Generator for the [Use Case] (https://github.com/Openet-Labs/machine-learning/tree/master/auto-scaling)

### Prerequisites
* **Clone** the repository and **build** the parent project according to this [guide](https://github.com/Openet-Labs/machine-learning/tree/master/auto-scaling)

### Setup
```
cd [directory-that-you-cloned-in]/machine-learning/auto-scaling/training-data-generator/target
ls training-data-generator-1.0-SNAPSHOT-executable.jar
```

You should see the *executable jar* file in the `ls` command output, otherwise you should build the project again.

```
cp ../application.properties .
```

Edit the values in the `application.properties` file based on your [**Apache Kafka**](https://kafka.apache.org/) broker. The key names are self-descriptive:

* **kafka.broker**: The hostname/IP address and the port number of the broker 
* **kafka.topic.train**: The kafka topic from which the [use case](https://github.com/Openet-Labs/machine-learning/tree/master/auto-scaling) reads the training data. _You do **not** need to change it unless you have changed the same key in the use case properties file._
* **kafka.group.id**: The kafka consumer group id of the [use case](https://github.com/Openet-Labs/machine-learning/tree/master/auto-scaling). _You do **not** need to change it unless you have changed the same key in the use case properties file._

### Run
```
java -jar training-data-generator-1.0-SNAPSHOT-executable.jar
```

_*You can verify if the training records have been published into the topic by means of [Kafka console consumer](https://kafka.apache.org/documentation#quickstart).*_

### Custom training data
If you want to train the use case with your own training data you should publish the data into the same Kafka topic. The data format should be as the following example for each record:
~~~JSON
{
    "Vdu": "webcache",
    "Vnfcs": 3,
    "Cpu": 50.0,
    "Mempry": 50.0,
    "Timestamp": 1478762317 
}
~~~

* Vdu: ID of the 
* Vnfcs: Count of Vnfcs of that Vdu
*