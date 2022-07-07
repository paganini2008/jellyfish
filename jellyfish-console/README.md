# Jellyfish-console

Jellyfish series provides an independent  web application for collecting log data and monitoring metrics data from other applications,  displaying log items and HTTP API statistic result in real-time.

## Functions

* Application log collecting （based on logback or log4j2）
* Http API metrics data statistic, including  http-request-time, http-request-concurrency,http-response-status-code and qps

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


## Run

<code>java -jar jellyfish-console-1.0-RC1.jar</code>     
 Location:  http://localhost:6100/jellyfish/log/