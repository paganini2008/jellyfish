# Jellyfish Series

A lightweight distributed microservice  monitoring system, which can seamlessly intergrate with spring boot or spring cloud framework and easily customize your applications. 

## Functions

* Application Log Collecting
* Http API Statistic and Monitoring, including  http-request-time, http-request-concurrency,http-response-status-code, qps

## Compatibility

*  Jdk8 (or later)
*  <code>SpringBoot</code> Framework 2.2.x (or later)
*  <code>Redis</code> 3.x (or later)
*  <code>Netty</code> 4.x (or later)
*  ElasticSearch 6.x (or later)

## Features

* Low latency and high availability
* TCP and HTTP protocol  supported
* Scrolling Time Window Statistics supported
* Pure memory calculating
* Customized historical metrics data  persistence policy

## Modules

####  jellyfish-console
An independent  web application for collecting log data and monitoring metrics data and  realtime display log items and HTTP API statistic result.

#### jellyfish-http-spring-boot-starter
Client APIs provided for  making current application quietly send monitoring data packet to remote Jellyfish console. The data packet contains some monitoring metrics like http-request-time, http-request-concurrency,http-response-status-code

**Install**

``` xml
<dependency>
<artifactId>jellyfish-http-spring-boot-starter</artifactId>
<groupId>com.github.paganini2008.atlantis</groupId>
<version>1.0-RC1</version>
</dependency>
```

#### jellyfish-slf4j
Client APIs provided for sending log data packet in background to Jellyfish console. It means there is an extra <code>Appender Component</code> defined in slf4j configuration file. Currently, logback and log4j2 is supported. 

**Install**

``` xml
<dependency>
<artifactId>jellyfish-slf4j</artifactId>
<groupId>com.github.paganini2008.atlantis</groupId>
<version>1.0-RC1</version>
</dependency>
```

**4.  jellyfish-spring-boot-starter** 
The core class of Jellyfish Series, which provides all functions of whole framework.

**Install**

``` xml
<dependency>
  <artifactId>jellyfish-spring-boot-starter</artifactId>
  <groupId>com.github.paganini2008.atlantis</groupId>
  <version>1.0-RC1</version>
</dependency>
```




