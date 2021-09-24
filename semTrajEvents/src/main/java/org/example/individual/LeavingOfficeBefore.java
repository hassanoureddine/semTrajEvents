package org.example.individual;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.apache.flink.cep.PatternSelectFunction;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.SimpleCondition;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.example.events.SemTrajSegment;

//6- A trajectory leaving the office before 17h
public class LeavingOfficeBefore {
	public static Pattern<SemTrajSegment, ?> leavingOfficeBefore(int beforeHourTime){
		Pattern<SemTrajSegment, ?> p = Pattern.<SemTrajSegment>begin("begin")
				.where(new SimpleCondition<SemTrajSegment>() {

					@Override
					public boolean filter(SemTrajSegment value) throws Exception {
						if(value.getActivity_semantics() != null && value.getActivity_semantics().equals("Bureau")) {
							return true;
						}
						return false;
					}
				
				})
				.next("leave")
				.where(new SimpleCondition<SemTrajSegment>() {

					@Override
					public boolean filter(SemTrajSegment value) throws Exception {
						if(value.getActivity_semantics() != null && !value.getActivity_semantics().equals("Bureau")
								&& isBeforeTime(value.getStart_datetime(), beforeHourTime)) {
							return true;
						}
						return false;
					}
				});
		
		return p;
	}
	
	public static DataStream<LeavingOfficeBeforeAlert> leavingOfficeBeforeStream(PatternStream<SemTrajSegment> patternStream, int beforeHourTime){
		DataStream<LeavingOfficeBeforeAlert> alerts = patternStream.select(new PatternSelectFunction<SemTrajSegment, LeavingOfficeBeforeAlert>() {

			@Override
			public LeavingOfficeBeforeAlert select(Map<String, List<SemTrajSegment>> pattern) throws Exception {
				return new LeavingOfficeBeforeAlert(
						((SemTrajSegment)pattern.get("begin").get(0)).getParticipantID(),
						((SemTrajSegment)pattern.get("leave").get(0)).getStart_datetime(),
						((SemTrajSegment)pattern.get("leave").get(0)).getEnd_datetime(),
						beforeHourTime);
			}
		});
		
		return alerts;
	}
	
	
	public static boolean isBeforeTime(String timeToCompare, int beforeHourTime) {
		DateTimeFormatter formatDateTime = DateTimeFormatter.ISO_DATE_TIME;
		LocalDateTime localDateTime = LocalDateTime.from(formatDateTime.parse(timeToCompare.replace(' ', 'T')));
		Timestamp ts = Timestamp.valueOf(localDateTime);
		
		int hour = ts.getHours();
		int minutes = ts.getMinutes();
		int seconds = ts.getSeconds();
		
		if(hour < beforeHourTime) {
			return true;
		}
		
		return false;
		
	}
}
