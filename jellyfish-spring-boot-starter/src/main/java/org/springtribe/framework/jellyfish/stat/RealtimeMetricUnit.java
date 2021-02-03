package org.springtribe.framework.jellyfish.stat;

import org.springtribe.framework.jellyfish.stat.MetricUnits.LongMetricUnit;

/**
 * 
 * RealtimeMetricUnit
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class RealtimeMetricUnit extends LongMetricUnit {

	RealtimeMetricUnit(long value, boolean failed, boolean timeout) {
		super(value);
		this.failedCount = failed ? 1 : 0;
		this.timeoutCount = timeout ? 1 : 0;
	}

	RealtimeMetricUnit(long highestValue, long lowestValue, long totalValue, int count, long timestamp, int failedCount, int timeoutCount) {
		super(highestValue, lowestValue, totalValue, count, timestamp);
		this.failedCount = failedCount;
		this.timeoutCount = timeoutCount;
	}

	private int failedCount;
	private int timeoutCount;

	public int getFailedCount() {
		return failedCount;
	}

	public int getTimeoutCount() {
		return timeoutCount;
	}

	@Override
	public MetricUnit merge(MetricUnit anotherUnit) {
		long highestValue = Long.max(getHighestValue().longValue(), anotherUnit.getHighestValue().longValue());
		long lowestValue = Long.min(getLowestValue().longValue(), anotherUnit.getLowestValue().longValue());
		long totalValue = getTotalValue().longValue() + anotherUnit.getTotalValue().longValue();
		int count = getCount() + anotherUnit.getCount();
		long timestamp = anotherUnit.getTimestamp();

		RealtimeMetricUnit realtimeMetricUnit = (RealtimeMetricUnit) anotherUnit;
		int failedCount = getFailedCount() + realtimeMetricUnit.getFailedCount();
		int timeoutCount = getTimeoutCount() + realtimeMetricUnit.getTimeoutCount();
		return new RealtimeMetricUnit(highestValue, lowestValue, totalValue, count, timestamp, failedCount, timeoutCount);
	}

	public static RealtimeMetricUnit valueOf(long value, boolean failed, boolean timeout) {
		return new RealtimeMetricUnit(value, failed, timeout);
	}

}
