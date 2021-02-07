package indi.atlantis.framework.jellyfish;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;
import org.springtribe.framework.cluster.EnableApplicationCluster;

import indi.atlantis.framework.gearless.EnableGearless;
import indi.atlantis.framework.jellyfish.ui.JellyfishUIAutoConfiguration;

/**
 * 
 * EnableJellyfishServer
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
public @interface EnableJellyfishServer {
}
