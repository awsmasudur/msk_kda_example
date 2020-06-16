# Steps to follow

1. Create an EC2 Windows machine on a VPC where you will create your Kafka cluster using Amazon MSK. Create an IAM role for EC2 (https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/iam-roles-for-amazon-ec2.html). For test purpose you can assign AdministratorAccess policy on that role. Select the role when you are creating the EC2 instance. We will use this role to create Kafka cluster and also configure Kinesid Data Analytics APP.
2. Configure below tools on your newly created machine<br />
a) Install <a href="https://aws.amazon.com/cli/">AWS CLI</a><br />
b) Install git<br />
c) Download and install IntelliJ community edition: https://www.jetbrains.com/idea/
d) Download and configure Apache Maven (it's important to environment variable) : https://maven.apache.org/install.html

## Create a Kafka cluster on Amazon MSK
You can follow the MSK getting started guideline here: https://docs.aws.amazon.com/msk/latest/developerguide/getting-started.html. In the below example I am using default VPC with 3 existing subnets. For security group, create a security group manually and for Inbound rules assign the same security group name as a source for "all traffic". After you finish creating the security group, attach it with your existing EC2 machine that you have created earlier. To create a cluster run the below AWS CLI command after changing the subnet and security group name in the <a href="createMSK.json">createMSK.json</a> file. 
aws kafka create-cluster --cli-input-json file://createMSK.json



 
