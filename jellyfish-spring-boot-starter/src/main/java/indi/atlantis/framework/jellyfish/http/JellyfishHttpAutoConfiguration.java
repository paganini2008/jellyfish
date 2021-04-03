package indi.atlantis.framework.jellyfish.http;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import indi.atlantis.framework.vortex.Handler;
import indi.atlantis.framework.vortex.metric.FullSynchronizationExecutor;
import indi.atlantis.framework.vortex.metric.IncrementalSynchronizationExecutor;
import indi.atlantis.framework.vortex.metric.SynchronizationExecutor;
import indi.atlantis.framework.vortex.metric.Synchronizer;

/**
 * 
 * JellyfishHttpAutoConfiguration
 * 
 * @author Jimmy Hoff
 *
 * @version 1.0
 */
@Configuration(proxyBeanMethods = false)
public class JellyfishHttpAutoConfiguration {

	@Bean
	public Environment primaryEnvironment() {
		return new Environment();
	}

	@Bean
	public Environment secondaryEnvironment() {
		return new Environment();
	}

	@Bean
	public Handler apiStatisticHandler() {
		return new ApiStatisticHandler();
	}

	@Bean
	public Handler apiQpsHandler() {
		return new ApiQpsHandler();
	}

	@Bean
	public Handler apiStatisticSynchronizationHandler(@Qualifier("secondaryEnvironment") Environment environment) {
		return new ApiStatisticSynchronizationHandler("statistic-", environment, false);
	}

	@Bean
	public Handler incrementalApiStatisticSynchronizationHandler(@Qualifier("secondaryEnvironment") Environment environment) {
		return new ApiStatisticSynchronizationHandler("statistic+", environment, true);
	}

	@Bean
	public Handler apiSummarySynchronizationHandler(@Qualifier("secondaryEnvironment") Environment environment) {
		return new ApiSummarySynchronizationHandler("summary-", environment, false);
	}

	@Bean
	public Handler incrementalApiSummarySynchronizationHandler(@Qualifier("secondaryEnvironment") Environment environment) {
		return new ApiSummarySynchronizationHandler("summary+", environment, true);
	}

	@Bean
	public Synchronizer apiStatisticSynchronizer(@Qualifier("secondaryEnvironment") Environment environment) {
		return new ApiStatisticSynchronizer("statistic-", environment, false);
	}

	@Bean
	public Synchronizer incrementalApiStatisticSynchronizer(@Qualifier("primaryEnvironment") Environment environment) {
		return new ApiStatisticSynchronizer("statistic+", environment, true);
	}

	@Bean
	public Synchronizer apiSummarySynchronizer(@Qualifier("secondaryEnvironment") Environment environment) {
		return new ApiSummarySynchronizer("summary-", environment, false);
	}

	@Bean
	public Synchronizer incrementalApiSummarySynchronizer(@Qualifier("primaryEnvironment") Environment environment) {
		return new ApiSummarySynchronizer("summary+", environment, true);
	}

	@Bean
	public SynchronizationExecutor fullSynchronizationExecutor(@Qualifier("apiSummarySynchronizer") Synchronizer apiSummarySynchronizer,
			@Qualifier("apiStatisticSynchronizer") Synchronizer apiStatisticSynchronizer) {
		FullSynchronizationExecutor executor = new FullSynchronizationExecutor();
		executor.addSynchronizers(apiSummarySynchronizer, apiStatisticSynchronizer);
		return executor;
	}

	@Bean
	public SynchronizationExecutor incrementalSynchronizationExecutor(
			@Qualifier("incrementalApiSummarySynchronizer") Synchronizer apiSummarySynchronizer,
			@Qualifier("incrementalApiStatisticSynchronizer") Synchronizer apiStatisticSynchronizer) {
		IncrementalSynchronizationExecutor executor = new IncrementalSynchronizationExecutor();
		executor.addSynchronizers(apiSummarySynchronizer, apiStatisticSynchronizer);
		return executor;
	}

}
