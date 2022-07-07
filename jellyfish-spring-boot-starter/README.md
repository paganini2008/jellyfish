# jellyfish-spring-boot-starter

The Core class of Jellyfish Series, which provides all functions of whole framework.

## Install

``` xml
<dependency>
      <artifactId>jellyfish-spring-boot-starter</artifactId>
     <groupId>com.github.paganini2008.atlantis</groupId>
     <version>1.0-RC3</version>
</dependency>
```



## Functions

* Application Log Collecting
* Http API Statistic and Monitoring, including  http-request-time, http-request-concurrency,http-response-status-code



## Core API

* EnableJellyfishServer
* Slf4jHandler
* ApiStatisticHandler
* ApiQpsHandler



## Quick Start

``` java
@EnableJellyfishServer
@SpringBootApplication
public class JellyfishServerConsoleMain {

	public static void main(String[] args) {
		SpringApplication.run(JellyfishServerConsoleMain.class, args);
	}

}
```









