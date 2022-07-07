# Jellyfish-slf4j

Jellyfish series provides APIs for sending log data packet in background to Jellyfish console. It means there is an extra <code>Appender Component</code> defined in slf4j configuration file. Currently, logback and log4j2 is supported. 

## Install

Modify <code>pom.xml</code> and add dependency to current classpath

``` xml
<dependency>
      <artifactId>jellyfish-slf4j</artifactId>
     <groupId>com.github.paganini2008.atlantis</groupId>
     <version>1.0-RC3</version>
</dependency>
```

## Configure

Modify <code>logback.xml</code> and add an appender and make it works

``` xml
<?xml version="1.0" encoding="utf-8" ?>
<configuration>
    
<appender name="logTracker" class="io.atlantisframework.jellyfish.slf4j.logback.TransportClientAppender">
  <applicationName>tester</applicationName> <!-- Modify applicationName -->
  <brokerUrl>http://192.168.159.1:6100</brokerUrl> <!-- Modify Jellyfish Console Server Location -->
</appender>
    
<!-- omit other configuration -->
    
<root level="info">
  <appender-ref ref="STDOUT" />
  <appender-ref ref="INFO" />
  <appender-ref ref="ERROR" />
  <appender-ref ref="logTracker" />  <!-- Refer the appender -->
</root>
    
</configuration>
```

## Core API

* <code>io.atlantisframework.jellyfish.slf4j.log4j2.TransportClientAppender</code>
* <code>io.atlantisframework.jellyfish.slf4j.logback.TransportClientAppender</code>

