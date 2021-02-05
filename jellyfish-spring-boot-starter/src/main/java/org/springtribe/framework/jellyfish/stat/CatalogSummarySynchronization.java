package org.springtribe.framework.jellyfish.stat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springtribe.framework.gearless.Handler;
import org.springtribe.framework.gearless.common.Tuple;

/**
 * 
 * CatalogSummarySynchronization
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class CatalogSummarySynchronization implements Handler {

	public static final String TOPIC_NAME = CatalogSummarySynchronization.class.getName();

	@Qualifier("secondaryCatalogContext")
	@Autowired
	private CatalogContext catalogContext;

	@Override
	public void onData(Tuple tuple) {
		String clusterName = tuple.getField("clusterName", String.class);
		String applicationName = tuple.getField("applicationName", String.class);
		String host = tuple.getField("host", String.class);
		String category = tuple.getField("category", String.class);
		String path = tuple.getField("path", String.class);
		Catalog catalog = new Catalog(clusterName, applicationName, host, category, path);
		CatalogSummary catalogSummary = catalogContext.getSummary(catalog);

		long count = tuple.getField("count", Long.class);
		long timeoutCount = tuple.getField("timeoutCount", Long.class);
		long failedCount = tuple.getField("failedCount", Long.class);
		Counter counter = new Counter(count, failedCount, timeoutCount);
		catalogSummary.merge(counter);

		long countOf1xx = tuple.getField("countOf1xx", Long.class);
		long countOf2xx = tuple.getField("countOf2xx", Long.class);
		long countOf3xx = tuple.getField("countOf3xx", Long.class);
		long countOf4xx = tuple.getField("countOf4xx", Long.class);
		long countOf5xx = tuple.getField("countOf5xx", Long.class);
		HttpStatusCounter httpStatusCounter = new HttpStatusCounter(countOf1xx, countOf2xx, countOf3xx, countOf4xx, countOf5xx);
		catalogSummary.merge(httpStatusCounter);

	}

	@Override
	public String getTopic() {
		return TOPIC_NAME;
	}

}
