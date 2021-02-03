package org.springtribe.framework.jellyfish;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;
import org.springtribe.framework.cluster.EnableApplicationCluster;
import org.springtribe.framework.gearless.EnableGearless;
import org.springtribe.framework.jellyfish.ui.JellyfishUIAutoConfiguration;

/**
 * 
 * EnableJellyfishClient
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@EnableGearless
@EnableApplicationCluster(enableLeaderElection = true, enableMonitor = true)
@Import({ JellyfishAutoConfiguration.class, JellyfishUIAutoConfiguration.class })
public @interface EnableJellyfishClient {
}
