# Jellyfish-http-spring-boot-starter

Jellyfish series provides client APIs let current application quietly send monitoring data packet to remote Jellyfish console. The data packet contains some monitoring metrics like http-request-time, http-request-concurrency,http-response-status-code

## Install

Modfiy pom.xml and add dependency to current classpath

``` xml
<dependency>
<artifactId>jellyfish-http-spring-boot-starter</artifactId>
<groupId>com.github.paganini2008.atlantis</groupId>
<version>1.0-RC1</version>
</dependency>
```

## Configure

Add Jellyfish Console server location to <code>application.properties</code>

``` properties
atlantis.framework.jellyfish.brokerUrl=http://localhost:6100  #Jellyfish Console server location
```

##  Core API

* <code>EnableJellyfishApiWatcher</code>
* <code>ApiWatcher</code>
* <code>ApiStatisticWatcher</code>
* <code>ApiQpsWatcher</code>

## Quick Start

``` java
@EnableJellyfishApiWatcher
@SpringBootApplication
public class TestApplication {

	public static void main(String[] args) {
		SpringApplication.run(TestApplication.class, args);
	}

}
```

