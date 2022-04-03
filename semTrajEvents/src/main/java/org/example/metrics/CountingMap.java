package org.example.metrics;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.metrics.Counter;
import org.example.events.SemTrajSegment;

public class CountingMap extends RichMapFunction<SemTrajSegment, Integer>{

	private transient Counter eventCounter;
	
	
	public void open(Configuration parameters) {
		eventCounter = getRuntimeContext().getMetricGroup().addGroup("MyMetricsGroup").counter("events");
	}
	
	@Override
	public Integer map(SemTrajSegment value) throws Exception {
		eventCounter.inc();
		return value.getParticipantID();
	}

}
