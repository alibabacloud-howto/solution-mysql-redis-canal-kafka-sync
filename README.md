## Project URL
[http://gitlab.alibaba-inc.com/alibabacloud-labs/solution-mysql-redis-canal-kafka-sync](http://gitlab.alibaba-inc.com/alibabacloud-labs/solution-mysql-redis-canal-kafka-sync)


## 1. Architecture overview

![image desc](https://labex.io/upload/G/V/B/XRb0l220tb36.png)


## 2. Index

- [Scene introduction](http://gitlab.alibaba-inc.com/alibabacloud-labs/solution-mysql-redis-canal-kafka-sync#3-scene-introduction)
- [Use Terraform to create resources & setup environment](http://gitlab.alibaba-inc.com/alibabacloud-labs/solution-mysql-redis-canal-kafka-sync#4-use-terraform-to-create-resources-setup-environment)
- [Install Canal](http://gitlab.alibaba-inc.com/alibabacloud-labs/solution-mysql-redis-canal-kafka-sync#5-install-canal)
- [Data synchronization test](http://gitlab.alibaba-inc.com/alibabacloud-labs/solution-mysql-redis-canal-kafka-sync#6-data-synchronization-test)
- [Demo with rendering the Redis data on Web App](http://gitlab.alibaba-inc.com/alibabacloud-labs/solution-mysql-redis-canal-kafka-sync#7-demo-with-rendering-the-redis-data-on-web-app)

## 3. Scene introduction

### 3.1 Experimental scene

This document uses Kafka and [Canal](https://github.com/alibaba/canal) to achieve data synchronization between Redis and MySQL. The architecture diagram is as follows:

![image desc](https://labex.io/upload/G/V/B/XRb0l220tb36.png)

Through the architecture diagram, we can clearly know the components that need to be used, MySQL, Canal, Kafka, Redis, etc.

Except for Canal which needs to be deployed on ECS, these components will be created using Terraform tools.

### 3.2 How Canal works

1. Canal simulates the interactive protocol of the MySQL slave, pretending to be a MySQL slave, and sends the dump protocol to the MySQL master.

2. The MySQL master receives the dump request and starts to push the binary log to the slave (ie canal).

3. Canal parses binary log objects (originally byte streams) and sends them to storage destinations, such as MySQL, Kafka, Elastic Search, etc.

## 4. Use Terraform to create resources & setup environment

### 4.1 Install Terraform

Run the following command to update the apt installation source.(This experiment uses the Ubuntu 16.04 system)

```
apt update
```

![image desc](https://labex.io/upload/Y/D/L/rztUc4BBLFXM.png)

Run the following command to install the unpacking tool:

```
apt install -y unzip zip
```

![image desc](https://labex.io/upload/S/F/D/3MrxW06YQ113.png)

Run the following command to download the Terraform installation package:

```
wget http://labex-ali-data.oss-us-west-1.aliyuncs.com/terraform/terraform_0.14.6_linux_amd64.zip
```

![image desc](https://labex.io/upload/O/E/T/h9fs71HhZYat.png)

Run the following command to unpack the Terraform installation package to /usr/local/bin:

```
unzip terraform_0.14.6_linux_amd64.zip -d /usr/local/bin/
```

![image desc](https://labex.io/upload/O/H/H/iGN8qs6pi0tm.png)

### 4.2 Create resources

Refer back to the user's home directory as shown below, click AccessKey Management.

![image desc](https://labex.io/upload/G/I/B/BUGx6Dcu78BY.jpg)

Click Create AccessKey. After AccessKey has been created successfully, AccessKeyID and AccessKeySecret are displayed. AccessKeySecret is only displayed once. Click Download CSV FIle to save the AccessKeySecret 

![image desc](https://labex.io/upload/H/F/B/8Y6UiUGsRpR3.jpg)

Back to the ECS command line,

Enter the following command,

```
mkdir -p terraform && cd terraform
```

![image desc](https://labex.io/upload/X/K/C/vB9CkOZU8YGB.jpg)

Enter the command ``vim main.tf``, copy the content of this file ([http://gitlab.alibaba-inc.com/alibabacloud-labs/solution-mysql-redis-canal-kafka-sync/blob/master/deployment/terraform/main.tf](http://gitlab.alibaba-inc.com/alibabacloud-labs/solution-mysql-redis-canal-kafka-sync/blob/master/deployment/terraform/main.tf)) to the file, save and exit. ***Please pay attention to modify YOUR-ACCESS-ID and YOUR-ACCESS-KEY to your own AccessKey***

![image desc](https://labex.io/upload/H/U/Y/2FMBfWxJIclN.jpg)

Enter the following command,

```
terraform init
```

![image desc](https://labex.io/upload/V/U/V/qElZZrUrGGWM.jpg)

Enter the following command,

```
terraform plan
```

![image desc](https://labex.io/upload/Q/T/P/bogZAjU5CIqd.jpg)

Enter the following command,

```
terraform apply
```

![image desc](https://labex.io/upload/U/D/M/wFTw3w4kTt6O.jpg)

Enter "yes" to start creating related resources. It takes about 10 minutes, please wait patiently.

![image desc](https://labex.io/upload/X/C/A/qOk4VhVdvZZe.jpg)

Created successfully.

![image desc](https://labex.io/upload/H/E/I/ul7NUZr8hp2F.jpg)

### 4.3  Install MySQL client

Back to the ECS console, you can see the two ECS instances just created. First, remotely log in to the instance of "labex".

![image desc](https://labex.io/upload/X/W/L/hmRrUxQ0VxAK.jpg)

```
ssh root@<labex-ECS-public-IP>
```

> The default account name and password of the ECS instance:
> 
> Account name: root
> 
> Password: Aliyun-test

Enter the following command to install the MySQL client.

```
apt update && apt -y install mysql-client
```

![image desc](https://labex.io/upload/K/E/U/OrMbMyVe4T13.jpg)

### 4.4 Install Redis client

Enter the following command to download the Redis installation package.

```
wget https://labex-ali-data.oss-us-west-1.aliyuncs.com/redis/redis-5.0.12.tar.gz
```

![image desc](https://labex.io/upload/C/C/U/v31bqXJLFSYD.jpg)

Enter the following command to decompress the installation package.

```
tar -xzf redis-5.0.12.tar.gz
```

![image desc](https://labex.io/upload/A/M/B/4zBX5ihBBm0j.jpg)

Enter the following command to compile Redis.

```
cd redis-5.0.12 && make 
```

![image desc](https://labex.io/upload/W/O/B/bvT8Tnrsvh1s.jpg)

Enter the command ``vim /etc/profile``, copy the following content to the file, save and exit.

```
vim /etc/profile
```

```
export PATH=$PATH:/root/redis-5.0.12/src
```

![image desc](https://labex.io/upload/R/M/D/sLoOzXJdTMiB.jpg)

Enter the following command to make the modification effective.

```
source /etc/profile
```

![image desc](https://labex.io/upload/O/J/U/P6n0UgrkHrRD.jpg)

Enter the following command.

```
redis-cli --help
```

![image desc](https://labex.io/upload/A/A/C/z22rc4UePLxa.jpg)

Note that redis-cli has been installed.

### 4.5 Install JDK

Input the following command to download the installation package。

```
cd && wget https://labex-ali-data.oss-accelerate.aliyuncs.com/spark-analysis/jdk-8u181-linux-x64.tar.gz
```

![image desc](https://labex.io/upload/R/D/H/jOgy5OVVhJWG.png)

Applications on Linux are generally installed in the /usr/local directory. Input the following command to zip the downloaded installation package into the /usr/local directory.

```
tar -zxf jdk-8u181-linux-x64.tar.gz -C /usr/local/
```

![image desc](https://labex.io/upload/R/D/F/ylMi2h6BnK6e.png)

Input the ``vim /etc/profile`` command to open this fie and then add the following code to the end of this file.

```
vim /etc/profile
```

```
export JAVA_HOME=/usr/local/jdk1.8.0_181
export PATH=$PATH:$JAVA_HOME/bin
```

![image desc](https://labex.io/upload/R/G/I/OR7QSXHn4MiT.jpg)

Input the ``source /etc/profile`` command to make your changes take effect.

```
source /etc/profile
```

Execute command ``java -version`` to verify the JDK installation.

```
java -version
```

![image desc](https://labex.io/upload/V/P/N/yVvqlWA7h47i.png)

### 4.6 Kafka environment preparation

Go to the Alibaba Cloud Kafka console and you can see the Kafka instance created by Terraform just now.

![image desc](https://labex.io/upload/C/O/W/8oWC6LZwVudB.jpg)

Click the instance name to view the connection address of the Kafka instance.

![image desc](https://labex.io/upload/H/V/L/1F0J0Ao4PSxH.jpg)

### 4.7 RDS environment preparation

Back to the Alibaba Cloud RDS console, you can see the RDS instance created by Terraform just now.

![image desc](https://labex.io/upload/N/O/M/R7kGQuNcRbeU.jpg)

You can see the database accounts that have been created.

![image desc](https://labex.io/upload/N/P/N/5q124Y59fpOS.jpg)

The database created.

![image desc](https://labex.io/upload/I/X/P/JJfEGQiPVDCs.jpg)

Intranet address of the database.

![image desc](https://labex.io/upload/G/X/K/eJFEbzsC2lZB.jpg)

Back to the ECS command line,

Enter the following command to connect to the RDS database. ***Please pay attention to replace YOUR-RDS-PRIVATE-ADDR with the user's own RDS intranet address***.

```
mysql -hYOUR-RDS-PRIVATE-ADDR -ulabex -pAliyun-test
```

![image desc](https://labex.io/upload/P/R/A/Mib6kGpznj5J.jpg)

Enter the following command to create a table in the "labex" database.

```
use labex;

create table user(
	id          varchar(5),
	username    varchar(20),
	password    varchar(20),
	addr        varchar(40),
	phone       varchar(11),
	nickname    varchar(12)
);

```

![image desc](https://labex.io/upload/T/C/P/OGVylQ7A70ZM.jpg)

Enter the following command to view the current database status. ***Please remember the log file and location here, it will be used when configuring Canal later***

```
show master status;
```

![image desc](https://labex.io/upload/U/R/A/xhfuPVdD3D2l.jpg)

Enter the ``exit`` command to exit the database.

![image desc](https://labex.io/upload/U/A/Y/uQb3eh7dlLGQ.jpg)

## 5. Install Canal

Enter the following command to download the canal installation package.

```
cd && wget https://labex-ali-data.oss-us-west-1.aliyuncs.com/canal/canal.deployer-1.1.5.tar.gz
```

![image desc](https://labex.io/upload/J/T/H/9NJh7N0ensKw.jpg)

Enter the following command to create a canal directory and download the canal installation package to this directory.

```
mkdir canal

tar -zxvf canal.deployer-1.1.5.tar.gz -C canal
```

![image desc](https://labex.io/upload/Q/O/P/N6z3CFzK5ZaN.jpg)

Enter the following command to view the files in the canal directory.

```
cd canal && ls 
```

![image desc](https://labex.io/upload/C/V/V/Q4bq9PpNRIJP.jpg)

Enter the command ``vim conf/canal.properties`` and modify the relevant configuration referring to the following. ***Please pay attention to replace YOUR-KAFKA-ADDR with the user's own Kafka connection address***.

```
vim conf/canal.properties
```

```
# tcp, kafka, RocketMQ choose kafka mode here
canal.serverMode = kafka
# The number of threads of the parser. If this configuration is turned on, it will block or fail to parse if it is not turned on
canal.instance.parser.parallelThreadSize = 16
# Configure the service address of MQ, here is the address and port corresponding to kafka
kafka.bootstrap.servers = YOUR-KAFKA-ADDR
# Configure instance, there must be a directory with the same name as example in the conf directory, and you can configure multiple
canal.destinations = example
```

![image desc](https://labex.io/upload/R/O/B/x0AgP3A8YtyV.jpg)

![image desc](https://labex.io/upload/S/W/O/9Cf9u8S0Y5cv.jpg)

![image desc](https://labex.io/upload/L/Y/R/9d55tPvfkAwY.jpg)

![image desc](https://labex.io/upload/W/H/G/pMfJPJ95difh.jpg)

Enter the command ``vim conf/example/instance.properties``, refer to the following to modify the relevant configuration. ***Please pay attention to replace YOUR-RDS-ADDR with the user's own RDS connection address***.

```
vim conf/example/instance.properties
```

```
## mysql serverId , v1.0.26+ will autoGen
# canal.instance.mysql.slaveId=0

# position info
canal.instance.master.address=YOUR-RDS-ADDR
# Execute SHOW MASTER STATUS in MySQL; view the binlog of the current database
canal.instance.master.journal.name=mysql-bin.000003
canal.instance.master.position=181545
# account password
canal.instance.dbUsername=labex
canal.instance.dbPassword=Aliyun-test
canal.instance.connectionCharset = UTF-8
# MQ queue name
canal.mq.topic=canaltopic
# Partition index of single-queue mode
canal.mq.partition=0
```

![image desc](https://labex.io/upload/E/K/K/jNARRNTl8Aqw.jpg)

![image desc](https://labex.io/upload/C/J/E/LZGP3Kz5HR4s.jpg)

![image desc](https://labex.io/upload/U/E/L/kTmDAg87t1YD.jpg)

Enter the following command to start the canal service.

```
bin/startup.sh
```

![image desc](https://labex.io/upload/P/M/R/Rgp8QH7oY3fg.jpg)

Go back to the Alibaba Cloud Kafka console and view the topic information.

![image desc](https://labex.io/upload/M/G/T/LHqxhqMQYCdw.jpg)

You can see that the topic on Kafka has started to have messages, indicating that Canal is synchronizing RDS log data to Kafka.

![image desc](https://labex.io/upload/N/G/Q/Elo38E3pNjQf.jpg)

Back to the ECS command line,

Enter the following command to download a sample jar package, which will be responsible for synchronizing the data in Kafka to Redis.

```
cd && wget https://labex-ali-data.oss-us-west-1.aliyuncs.com/canal/canal-0.0.1-SNAPSHOT.jar
```

![image desc](https://labex.io/upload/S/M/M/outrCl5ttQKq.jpg)

Enter the following command to start the synchronization process, ***Please pay attention to replace YOUR-KAFKA-ADDR, YOUR-REDIS-ADDR with the user's own Kafka and Redis connection address***

```
java -cp canal-0.0.1-SNAPSHOT.jar canal.SyncKafkaRedis YOUR-KAFKA-ADDR topic1 group1 YOUR-REDIS-ADDR Aliyun-test
```

Such as:

```
java -cp canal-0.0.1-SNAPSHOT.jar canal.SyncKafkaRedis 172.16.4.16:9092,172.16.4.14:9092,172.16.4.15:9092 topic1 group1 r-3nsa4cc5c3d04814.redis.rds.aliyuncs.com Aliyun-test
```

![image desc](https://labex.io/upload/H/X/S/5adH01c2CXrf.jpg)

You can see that the data is being synchronized, and the output messages are consumed from Kafka.

The message being consumed here is that Canal synchronizes RDS binlog file data to Kafka, which is aimed at RDS
The default "mysql" database message in the example. When the sample jar package consumes these messages, it will be output directly.
 
When the message of the target database "labex" is consumed, the data in Redis will be updated.

## 6. Data synchronization test

Next, create two new ECS command line interfaces.

The one that is executing the synchronization process is called command line 1.

The newly created ones are called command line 2 and command line 3, respectively.

### 6.1 Insert data

At the command line 2.

Enter the following command to log in to the RDS database. ***Please replace YOUR-RDS-PRIVATE-ADDR with the user's own RDS intranet address***.

```
mysql -ulabex -hYOUR-RDS-PRIVATE-ADDR -pAliyun-test -D labex
```

Such as:

```
mysql -ulabex -hrm-3ns7ry11cc5qiq5nj.mysql.rds.aliyuncs.com -pAliyun-test -D labex
```

![image desc](https://labex.io/upload/P/T/X/FB1quKVJPEjD.jpg)

Enter the following command to insert data into the user table.

```
insert into user values("19832", "name1", "ddsdfdfd", "addr1", "17138141002", "nickname1");
insert into user values("20122", "name2", "xdfdsafd", "addr2", "13877686321", "nickname2");
```

![image desc](https://labex.io/upload/H/Q/D/Nhxr9jzQ9cso.jpg)

On the command line 1,

You can see that there are two records of data synchronization that were inserted just now.

![image desc](https://labex.io/upload/M/J/K/YIipRg98kI1p.jpg)

On the command line 3,

Enter the following command to check whether there is data in redis. ***Please pay attention to replace YOUR-REDIS-ADDR with the user's own Redis address***

```
redis-cli -h YOUR-REDIS-ADDR -a Aliyun-test
```

Such as:

```
redis-cli -h r-3nsa4cc5c3d04814.redis.rds.aliyuncs.com -a Aliyun-test
```

![image desc](https://labex.io/upload/Q/F/B/Z4QvUE2kvxSe.jpg)

You can see that there are already two pieces of data.

Enter the following command, you can see that the data has been synchronized successfully.

```
get 19832

get 20122
```

![image desc](https://labex.io/upload/C/V/A/m6lnoY701klI.jpg)

### 6.2 Update data

Next we update the data in RDS.

On the command line 2,

Enter the following command to update the data with id = "19832".

```
update user set username = "nanzhao" where id = "19832";
```

![image desc](https://labex.io/upload/W/E/L/GdhV75am5LUt.jpg)

On the command line 3,

Enter the following command, you can see that the data has been updated.

```
get 19832
```

![image desc](https://labex.io/upload/D/I/T/Bu5BqdpcXNP1.jpg)

### 6.3 Delete data

Next we delete the data in RDS.

On the command line 2,

Enter the following command to delete the data with id = "19832".

```
delete from user where id = "19832";
```

![image desc](https://labex.io/upload/A/F/W/6lqm2ES0A5Kl.jpg)

On the command line 3,

Enter the following command, you can see that the Redis Key no longer exists, indicating that the synchronization is successful.

```
get 19832
```

![image desc](https://labex.io/upload/P/V/Y/06YU8izzoNnn.jpg)

## 7. Demo with Rendering the Redis data on Web App

### 7.1 Install the Apache environment

Next, remotely log in to the "labex2" instance.

![image desc](https://labex.io/upload/W/Q/L/GjrkQBMKuXoX.jpg)

```
ssh root@<labex2-ECS-public-IP>
```

> The default account name and password of the ECS instance:
> 
> Account name: root
> 
> Password: Aliyun-test

Enter the following command to install apache2.

```
apt update && apt install -y apache2 python3-pip
```

![image desc](https://labex.io/upload/V/A/B/U1Pp6dnOgqat.jpg)

Enter the following command to install the redis dependency of python.

```
export LC_ALL=C

pip3 install redis
```

![image desc](https://labex.io/upload/E/A/T/NlW7iMzcvsKX.jpg)

Run the following command to create a folder:

```
mkdir /var/www/python
```

![image desc](https://labex.io/upload/O/N/F/N62MgNHHO9H7.jpg)

Run the following command to disable the event module and enable the prefork module:

```
a2dismod mpm_event

a2enmod mpm_prefork cgi
```

![image desc](https://labex.io/upload/P/Y/X/aX3pfOQAFlKc.jpg)

Run the ``vim /etc/apache2/sites-enabled/000-default.conf`` command to open the Apache configuration file. Replace all the contents of the file with the following configuration. Save the settings and exit.

```
vim /etc/apache2/sites-enabled/000-default.conf
```

```
<VirtualHost *:80>
        DocumentRoot /var/www/python
        <Directory /var/www/python>
                Options +ExecCGI
                DirectoryIndex leaderboards.py
        </Directory>
        AddHandler cgi-script .py
        ErrorLog ${APACHE_LOG_DIR}/error.log
        CustomLog ${APACHE_LOG_DIR}/access.log combined
</VirtualHost>
```

![image desc](https://labex.io/upload/R/T/G/al4760jTze87.png)

Run the ``vim /var/www/python/showRedis.py`` command to create a new file. Copy the following content to the file. Save the settings and exit.***Replace YOUR-REDIS-ADDR with the address of your Redis instance.***

```
vim /var/www/python/showRedis.py
```

```
#!/usr/bin/python3

import cgitb
import redis
import json

r = redis.StrictRedis(host='YOUR-REDIS-ADDR', port=6379, db=0, password='Aliyun-test')

cgitb.enable()
print("Content-Type: text/html;charset=utf-8")
print()

keys = r.keys()

print ("<h1>Data is fetched from Redis:</h1>")
print ("<table border=1><tr><td><b>id</b></td><td><b>username</b></td><td><b>password</b></td><td><b>iphone</b></td><td><b>addr</b></td></tr>")

for key in keys:
    print("<tr>")
    dic1 = json.loads(r.get(key).decode("utf-8"))
    print ("<td>", dic1.get("id", "null"), '</td>')
    print ("<td>", dic1.get("username", "null"), '</td>')
    print ("<td>", dic1.get("password", "null"), '</td>')
    print ("<td>", dic1.get("phone","null"), '</td>')
    print ("<td>", dic1.get("addr", "null"), '</td>')
    print("<tr/>")
print ("</table><br/><br/>")
```

![image desc](https://labex.io/upload/L/E/J/J383zVyNQY0N.jpg)

Run the following command to grant the file execution permission:

```
chmod 755 /var/www/python/showRedis.py
```

![image desc](https://labex.io/upload/L/T/A/ghyaBWO1AjOc.jpg)

Run the following command to restart Apache to make the preceding configurations take effect:

```
service apache2 restart
```

![image desc](https://labex.io/upload/M/B/X/OWUVMhXZrUZq.jpg)

Access from the browser with "labex2" ECS in this tutorial. ***Please pay attention to replace the IP address with the user's own ECS public network address***

![image desc](https://labex.io/upload/B/F/S/lT6f02XJ7p6C.jpg)

Next, we repeat the operations of inserting, updating, and deleting data in MySQL in the section [Data synchronization test](http://gitlab.alibaba-inc.com/alibabacloud-labs/solution-mysql-redis-canal-kafka-sync#6-data-synchronization-test), and then refresh the browser to see the data in Redis.

![image desc](https://labex.io/upload/X/Q/R/QWjtOXCItV1g.jpg)

