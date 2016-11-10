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
* **kafka.topic.train**: The kafka topic from which the [Use case](https://github.com/Openet-Labs/machine-learning/tree/master/auto-scaling) reads the training data. _You do **not** to change it unless you have changed the same key in the use case properties file._
* **kafka.group.id**: The kafka consumer group id of the [Use case](https://github.com/Openet-Labs/machine-learning/tree/master/auto-scaling). _You do **not** to change it unless you have changed the same key in the use case properties file._

### Run
```
java -jar training-data-generator-1.0-SNAPSHOT-executable.jar
```
