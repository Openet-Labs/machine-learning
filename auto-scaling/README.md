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
    
### Running
    bin/runAutoScaling.sh
    
