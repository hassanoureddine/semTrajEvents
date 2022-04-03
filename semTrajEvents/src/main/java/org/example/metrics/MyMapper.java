package org.example.metrics;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.dropwizard.metrics.DropwizardMeterWrapper;
import org.apache.flink.metrics.Meter;
import org.example.events.SemTrajSegment;


public class MyMapper extends RichMapFunction<SemTrajSegment, Long> {
	  private transient Meter meter;

	  @Override
	  public void open(Configuration config) {
	    com.codahale.metrics.Meter dropwizardMeter = new com.codahale.metrics.Meter();

	    this.meter = getRuntimeContext()
	      .getMetricGroup()
	      .addGroup("MyMetricsGroup")
	      .meter("myMeter", new DropwizardMeterWrapper(dropwizardMeter));
	  }

	@Override
	public Long map(SemTrajSegment value) throws Exception {
		// TODO Auto-generated method stub
		this.meter.markEvent();
		return value.getWatermarkTimestamp();
	}
}