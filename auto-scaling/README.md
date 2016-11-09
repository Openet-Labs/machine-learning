#Auto Scaling Use Case

Supports scaling based on the following: 
* Cpu Utilization %
* Memory Utilization %
* Vnfcs

Using this Repo
------------------
### Building
We use [Maven](https://maven.apache.org/) for building Java
    
    git clone https://github.com/Openet-Labs/machine-learning.git
    cd machine-learning/auto-scaling/
    mvn -DskipTests clean package
    ./deploy.sh

### Setup

#### Configuration
We use kafka topics as input for this use case:
* Traning input: kafka.topic=com.openet.autoscaling
* Real time input: kafka.topic.train=com.openet.autoscaling.train
This can be set in the properties file

#### Training data
    To generate training data follow [generate training](https://github.com/Openet-Labs/machine-learning/tree/master/auto-scaling/training-data-generator)
    
    
### Running
    bin/runAutoScaling.sh
    
