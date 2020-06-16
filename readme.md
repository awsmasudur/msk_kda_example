# Steps to follow

1. Create an EC2 Windows machine on a VPC where you will create your Kafka cluster using Amazon MSK. Create an IAM role for EC2 (https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/iam-roles-for-amazon-ec2.html). For test purpose you can assign AdministratorAccess policy on that role. Select the role when you are creating the EC2 instance. We will use this role to create Kafka cluster and also configure Kinesid Data Analytics APP.
2. Configure below tools on your newly created machine  
a) Install <a href="https://aws.amazon.com/cli/">AWS CLI</a> and configure aws region (ap-southeast-2).    
b) Install git  
c) Download and install IntelliJ community edition: https://www.jetbrains.com/idea/  
d) Download and configure Apache Maven (it's important to environment variable) : https://maven.apache.org/install.html  

## Create a Kafka cluster on Amazon MSK
You can follow the MSK getting started guideline here: https://docs.aws.amazon.com/msk/latest/developerguide/getting-started.html. In the below example I am using default VPC with 3 existing subnets. For security group, create a security group manually and for Inbound rules assign the same security group name as a source for "all traffic". After you finish creating the security group, attach it with your existing EC2 machine that you have created earlier. To create a cluster run the below AWS CLI command after changing the subnet and security group name in the <a href="/CreateMSK.json">CreateMSK.json</a> file.   
`aws kafka create-cluster --cli-input-json file://CreateMSK.json`  

## Compile Flink APP
1. Clone the msk_kda_example repository on your local Windows machine    
2. Open <a href="KDA-kafka-to-kafka-app">KDA-kafka-to-kafka-app</a> using IntelliJ and review the code. You don't need to change anything for this demo.  
3. Open command prompt and change your directory path to KDF-kafka-to-kafka-app.  
4. Example `C:\Users\mypath\Documents\msk_kda_example\KDA-kafka-to-kafka-app`.  
5. Run `mvn package -Dflink.version=1.8.2`.  
6. The command should retrun BUILD SUCCESS info.  
7. If the build is success you will see a jar file in target folder - \msk_kda_example\KDF-kafka-to-kafka-app\target\kdfKafkaToKafka-1.2.jar.  


## Upload your APP
1. Go to your AWS management console and create a S3 bucket and a sub folder on that bucket.  
2. Upload the kdfKafkaToKafka-1.2.jar to your S3 bucket.  


## Create your Flink app on KDA
1. Create an IAM role for Kinesis Data Analytics (KDA) application. For test purpose you can assign AdministratorAccess policy on that role.  
2. Open <a href="CreateKDAKafkaToKafkaApp.json">CreateKDAKafkaToKafkaApp.json </a> and change below configurations.  
3. ServiceExecutionRole --> specify the role arn that your have created for your KDA applicaiton.  
4. BucketARN --> Change the S3 bucket name.  
5. FileKey --> Change the path where you upload the kdfKafkaToKafka-1.2.jar file.  
6. bootstrap.servers --> From aws managerment console go to MSK and click on the Kafka cluster that you have created earlier. Click on "View client information" and copy the TSL bootstrap server information. Replace bootstrap.servers setting with that copied information for KafkaSource and KafkaSink PropertyGroupId.  
7. Change the SecurityGroupIds and SubnetIds as similar to your MSK cluster.    
8. Run below AWS CLI command to create the KDA APP.  
`aws kinesisanalyticsv2 create-application --cli-input-json file://CreateKDAKafkaToKafkaApp.json`  
9. If will create a KDA application called meetupDemo1.  


## Start the Flink APP
1. Open <a href="StartKDAKafkaToKafkaApp.json">StartKDAKafkaToKafkaApp.json </a> and change below configurations.  
2. ApplicationName --> meetupDemo1.  
3. Run below AWS CLI command to start the KDA App.  
`aws kinesisanalyticsv2 start-application --cli-input-json file://StartKDAKafkaToKafkaApp.json`  
4. Go to AWS Kinesis data analytics console and monitor whether your KDA app has started. 
5. Click on Application Details. If the applicaiton is running successfully, You will see a Application graph with visual representation of the data flow consisting of operators and intermediate results of your Flink app.
  
## Create topic in Kafka
1. Working with Kafka from a Linux machine is easier than Windows. For that, create an EC2 instance with Amazon Linux AMI.  
2. For test purpose assign the same security group that you have created earlier for your Kafka cluster.    
3. Install Java - `sudo yum install java-1.8.0`  
4. Download Apache kafka - `wget https://archive.apache.org/dist/kafka/2.2.1/kafka_2.12-2.2.1.tgz`  
5. Run this command - `tar -xzf kafka_2.12-2.2.1.tgz`  
6. Go to the kafka_2.12-2.2.1 directory  
7. From aws managerment console go to MSK and click on the Kafka cluster that you have created earlier. Click on "View client information". Copy the Zookeeper configuration.  
8. run the following command on your EC2 linux machine. Replace with your cluster Zookeeper configuration.  
`export ZOOK="ZookeeperConnectString"`  
9. Go to your kafka directory and run the following command  
`bin/kafka-topics.sh --create --zookeeper $ZOOK --replication-factor 3 --partitions 1 --topic sourcetopic`  
`bin/kafka-topics.sh --create --zookeeper $ZOOK --replication-factor 3 --partitions 1 --topic destinationtopic`  
10. run the following command to see the list of topics on your cluster. It will return three topics name on a newly crated cluster.    
`bin/kafka-topics.sh --list --zookeeper $ZOOK`  

## Run Consumer APP  
1. From aws managerment console go to MSK and click on the Kafka cluster that you have created earlier. Click on "View client information". Copy the Plaintext bootstrap server information.  
2. run the following command  
`export BOOTSERVER="Replace with Plaintext Bootstrap servers configuration"`  
3. From your EC2 linux machine run the following command. It will return a empty result, but keep this running for the time being.    
`bin/kafka-console-consumer.sh --bootstrap-server $BOOTSERVER --topic destinationtopic --from-beginning`  

## Run the producer APP
1. Switch back to your Windows machine.  
2. Open <a href="kafkaDG"> kafkaDG</a> project with IntelliJ that you have clone earlier from git repo.  
3. Open the testProducer.java file. Change the brokerlist with your MSK clusters' Plaintext broker configuration.  
4. Run the java application.  
5. You will see that your java app is sending data to your sourcetopic and you will also see your EC2 consumer running on linux started reading data from the destinationtopic topic. Here the KDA app is replicating data from your sourcetopic to the  destinationtopic topic.  
6. To view the activities of your flink app, go to Kinesis data analytics console and monitor your KDA app.

 


  
     




 
