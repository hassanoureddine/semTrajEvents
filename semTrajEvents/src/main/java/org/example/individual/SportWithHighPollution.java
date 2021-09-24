package org.example.individual;

import java.util.List;
import java.util.Map;

import org.apache.flink.cep.PatternSelectFunction;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.nfa.aftermatch.AfterMatchSkipStrategy;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.SimpleCondition;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.example.events.SemTrajSegment;

public class SportWithHighPollution {
	public static Pattern<SemTrajSegment, ?> sportWithHighPollution() {
		Pattern<SemTrajSegment, ?> p = Pattern.<SemTrajSegment>begin("begin")
				.where(new SimpleCondition<SemTrajSegment>() {

					@Override
					public boolean filter(SemTrajSegment value) throws Exception {
						if(value.getEvent_semantics() != null && value.getEvent_semantics().equals("Sport")
								&& (
										(value.getBC_semantics() != null && value.getBC_semantics().equals("High"))
										|| (value.getPM10_semantics() != null && value.getPM10_semantics().equals("High"))
										|| (value.getPM1_semantics() != null && value.getPM1_semantics().equals("High"))
										|| (value.getPM25_semantics() != null && value.getPM25_semantics().equals("High"))
										|| (value.getNO2_semantics() != null && value.getNO2_semantics().equals("High"))
										)
								) {
							return true;
						}else {
							return false;
						}
					}
				});
		return p;
	}
	
	public static DataStream<SportWithHighPollutionAlert> sportWithHighPollutionAlertStream(PatternStream<SemTrajSegment> patternStream){
		DataStream<SportWithHighPollutionAlert> alert = patternStream.select(new PatternSelectFunction<SemTrajSegment, SportWithHighPollutionAlert>() {

			@Override
			public SportWithHighPollutionAlert select(Map<String, List<SemTrajSegment>> pattern) throws Exception {
				return new SportWithHighPollutionAlert(
						((SemTrajSegment)pattern.get("begin").get(0)).getParticipantID(),
						((SemTrajSegment)pattern.get("begin").get(0)).getStart_datetime(),
						((SemTrajSegment)pattern.get("begin").get(0)).getEnd_datetime(),
						((SemTrajSegment)pattern.get("begin").get(0)).getPlaceAccordingToHierarchy("display_name")
						);
			}
		});
		
		return alert;
	}
}
