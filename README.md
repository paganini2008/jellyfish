# Jellyfish Series
## High performance distributed microservice monitoring system

Jellyfish is a lightweight distributed real-time monitoring system written in Java, which can seamlessly connect with spring boot or spring cloud project, besides, using jellyfish API can easily customize your applications

### The monitoring function provided by jellyfish is mainly divided into two parts:

1. Unified collection and query of application log
2. Statistics and monitoring of three indicators of HTTP interface of <code>SpringBoot</code> application: request time, error rate, concurrency

### How to deploy jellyfish?
Jellyfish is divided into **server side** and **agent side**

**The Server Side** generally is an independent <code>SpringBoot</code> application or cluster. The cluster mode is implemented by [tridenter](https://github.com/paganini2008/tridenter-spring-boot-starter.git), another distributed cooperation framework for microservices. Besides, the server side of jellyfish needs to deploy <code>redis</code> and <code>elasticsearch</code>

**The Agent Side** is usually another group of <code>SpringBoot</code> applications or cluster with jellyfish related jar packages, including jellyfish-http-spring-boot-starter or jellyfish-slf4j, which send real-time packets to **the server side**.

### Features of Jellyfish

1. The jellyfish server side is based on the streaming computing framework [vortex](https://github.com/paganini2008/vortex.git), which has low latency and high concurrency. It supports two transport protocols, TCP and HTTP

2. The network communication layer of the jellyfish server side relies on vortex framework, supports dynamic horizontal expansion and supports the rule of final data consistency

3. Jellyfish's application interface statistics are quasi real time, and the pure memory calculation. The default statistical time window is 1 minute (support customized). However, the increase of the number of interfaces can affect the real-time performance. After testing, the maximum delay is kept in 1 minute, and the average delay is in seconds.

### There are four sub projects in the jellyfish series:

**1. jellyfish-console**
The jellyfish web console can run independently, usually as a server. Of course, you can customize the server side. By using the annotation <code>@EnableJellyFishserver</code>

**2. jellyfish-http-spring-boot-starter**
Jar package, agent package for data statistics of application http APIs

**3. jellyfish-slf4j**
Jar package, jellyfish connecting slf4j (only <code>logback</code> is supported at present), and unified application of log collection agent package

**4.  jellyfish-spring-boot-starter** 
The core jar of jellyfish series, which realizes all the core functions

### How to access slf4j and collect logs uniformly?
Your Application **(the agent side)** need to: 

**1. Install**

``` xml
<dependency>
<artifactId>jellyfish-slf4j</artifactId>
<groupId>indi.atlantis.framework</groupId>
<version>1.0-RC1</version>
</dependency>
```

**2. Modify logback.xml**

``` xml
<?xml version="1.0" encoding="utf-8" ?>
<configuration>
<!-- Define a new appender -->
<appender name="logTracker" class="indi.atlantis.framework.jellyfish.slf4j.logback.HttpTransportClientAppender">
<applicationName>tester5</applicationName><!-- modify the applicationName -->
<brokerUrl>http://192.168.159.1:10010</brokerUrl> <!-- modify the location of Jellyfish server side -->
</appender>
<!-- omit other configuration -->

<root level="info">
<appender-ref ref="STDOUT" />
<appender-ref ref="INFO" />
<appender-ref ref="ERROR" />
<appender-ref ref="logTracker" />  <!-- refer the new appender -->
</root>
</configuration>
```

**3. Start Jellyfish Console**
Command：<code>java -jar jellyfish-console-1.0-RC1.jar</code>     
Location:  http://localhost:6100/jellyfish/log/

**4. Start your application**
Then start your application, and the logs will flow into the jellyfish console constantly. 
By the way, each line of log data received by the jellyfish server side from the agent side is saved in <code>ElasticSearch</code>



### How to access spring boot projects and count HTTP APIs indicators?
First, the statistics section of http API calls by jellyfish reference the SDK of the metric module of vortex. Therefore, it is based on the time series. The default statistical time window is 1 minute. The first 60 data are saved by rolling. The historical data cannot be stored in default settings, but it can be extended by implementing the <code>MetricEvictionHandler</code> and <code>MetricSequencerFactory</code> to achieve the function.

Your application need to: 

**1. Install**
``` xml
<dependency>
<artifactId>jellyfish-http-spring-boot-starter</artifactId>
<groupId>indi.atlantis.framework</groupId>
<version>1.0-RC1</version>
</dependency>
```

**<code>2. application.properties</code>**

``` properties
atlantis.framework.jellyfish.brokerUrl=http://localhost:6100  #Jellyfish console server location
```

**3.  Restart your application**
