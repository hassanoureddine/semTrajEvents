package org.example.events;

import java.sql.Timestamp;

import org.apache.flink.streaming.api.functions.AssignerWithPunctuatedWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;

public class SemTrajWatermarks implements AssignerWithPunctuatedWatermarks<SemTrajSegment>{

	@Override
	public long extractTimestamp(SemTrajSegment element, long previousElementTimestamp) {
		// TODO Auto-generated method stub		
		return element.getWatermarkTimestamp();
	}

	@Override
	public Watermark checkAndGetNextWatermark(SemTrajSegment lastElement, long extractedTimestamp) {
		// TODO Auto-generated method stub
		return new Watermark(extractedTimestamp);
	}
	
}
