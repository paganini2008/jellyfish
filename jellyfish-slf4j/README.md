# Jellyfish-slf4j

A logging plugin that send log entry to one server

## Install

Modfiy pom.xml and add dependency to current classpath

``` xml
<dependency>
      <artifactId>jellyfish-slf4j</artifactId>
     <groupId>com.github.paganini2008.atlantis</groupId>
     <version>1.0-RC3</version>
</dependency>
```

## Configure

Modify logback.xml and add appender and make it works

``` xml
<?xml version="1.0" encoding="utf-8" ?>
<configuration>
    
<appender name="logTracker" class="indi.atlantis.framework.jellyfish.slf4j.logback.HttpTransportClientAppender">
  <applicationName>tester5</applicationName> <!-- Modify applicationName -->
  <brokerUrl>http://192.168.159.1:10010</brokerUrl> <!-- Modify Jellyfish Server Location -->
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

