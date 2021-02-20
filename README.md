# A light java application monitoring system based on spring boot framework

### It is made up of two parts: 

- Application Log Console
- Application API Watcher

## What is the Log Console?

Log console can collect and show log data from applications that are configured with **jellyfish-slf4j**. 

It means you need to use slf4j in your application. Only support logback and log4j now

<img src="https://raw.githubusercontent.com/paganini2008/material/main/image/jellyfish-log1.png" style="width: 800px;" />

If you want to scan past log, It also provide historical log query for you.

<img src="https://raw.githubusercontent.com/paganini2008/material/main/image/jellyfish-log2.png" style="width: 800px;" />



# How to configure log console?

Step 1: 

Add jar dependency in your pom.xml

```
		<dependency>
			<artifactId>jellyfish-slf4j</artifactId>
			<groupId>indi.atlantis.framework</groupId>
			<version>1.0-RC1</version>
		</dependency>
```

Step 2:

Open your **logback.xml** and  append following code:

```
<?xml version="1.0" encoding="utf-8" ?>
<configuration>
	<appender name="logTracker" class="indi.atlantis.framework.jellyfish.slf4j.logback.HttpTransportClientAppender">
		<applicationName>tester5</applicationName> <!-- Your application name -->
		<brokerUrl>http://192.168.159.1:10010</brokerUrl> <!-- Your Jellyfish server location -->
	</appender>
	
	<!-- omit other configuration -->
	
	<root level="info">
		<appender-ref ref="STDOUT" />
		<appender-ref ref="INFO" />
		<appender-ref ref="ERROR" />
		<appender-ref ref="logTracker" />  <!-- Reference your new appender -->
	</root>
</configuration>
```

It's done. Very easy.

## And then what is the API Watcher?

Watch and make a statistic of Web Application API, including these metrics:

- Response Time
- Concurrency
- QPS
- Count (Success Count/Failed Count/Timeout Count)
- Count of per http status code (1xx/2xx/3xx/4xx/5xx)

Look this:







