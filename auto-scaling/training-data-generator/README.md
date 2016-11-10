## Training Data Generator for the [Use Case] (https://github.com/Openet-Labs/machine-learning/tree/master/auto-scaling)

### Prerequisites
* **Clone** the repository and **build** the parent project according to this [guide](https://github.com/Openet-Labs/machine-learning/tree/master/auto-scaling)

### Setup
```
cd [directory-that-you-cloned-in]/machine-learning/auto-scaling/training-data-generator/target
ls training-data-generator-1.0-SNAPSHOT-executable.jar
```

You should see the file in the `ls` command output, otherwise you should build the project again.

```
cp ../application.properties .
```

Edit the values in the `application.properties` file based on your [**Apache Kafka**](https://kafka.apache.org/) broker. The key names are self-descriptive:
**
* 1
* 2
**

