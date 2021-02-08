package indi.atlantis.framework.jellyfish.metrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import indi.atlantis.framework.vortex.Handler;
import indi.atlantis.framework.vortex.common.Tuple;

/**
 * 
 * SummarySynchronizer
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class SummarySynchronizer implements Handler {

	public static final String TOPIC_NAME = SummarySynchronizer.class.getName();

	@Qualifier("secondaryCatalogContext")
	@Autowired
	private CatalogContext catalogContext;

	@Override
	public void onData(Tuple tuple) {

		Catalog catalog = Catalog.of(tuple);
		Summary summary = catalogContext.getSummary(catalog);
		summary.clear();

		long count = tuple.getField("count", Long.class);
		long timeoutCount = tuple.getField("timeoutCount", Long.class);
		long failedCount = tuple.getField("failedCount", Long.class);
		Counter counter = new Counter(count, failedCount, timeoutCount);
		summary.merge(counter);

		long countOf1xx = tuple.getField("countOf1xx", Long.class);
		long countOf2xx = tuple.getField("countOf2xx", Long.class);
		long countOf3xx = tuple.getField("countOf3xx", Long.class);
		long countOf4xx = tuple.getField("countOf4xx", Long.class);
		long countOf5xx = tuple.getField("countOf5xx", Long.class);

		HttpStatusCounter httpStatusCounter = new HttpStatusCounter(countOf1xx, countOf2xx, countOf3xx, countOf4xx, countOf5xx);
		summary.merge(httpStatusCounter);
	}

	@Override
	public String getTopic() {
		return TOPIC_NAME;
	}
}
