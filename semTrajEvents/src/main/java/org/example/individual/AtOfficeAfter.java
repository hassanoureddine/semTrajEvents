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

//5- A trajectory present at the office after 20h
public class AtOfficeAfter {
	public static Pattern<SemTrajSegment, ?> atOfficeAfter(int afterHourTime){
		Pattern<SemTrajSegment, ?> p = Pattern.<SemTrajSegment>begin("begin")
				.where(new SimpleCondition<SemTrajSegment>() {

					@Override
					public boolean filter(SemTrajSegment value) throws Exception {
						if(value.getActivity_semantics() != null && value.getActivity_semantics().equals("Bureau")
								&& isAfterTime(value.getStart_datetime(), afterHourTime)) {
							return true;
						}
						return false;
					}
				});
		
		return p;
	}
	
	public static DataStream<AtOfficeAfterAlert> atOfficeAfterStream(PatternStream<SemTrajSegment> patternStream, int afterHourTime){
		DataStream<AtOfficeAfterAlert> alerts = patternStream.select(new PatternSelectFunction<SemTrajSegment, AtOfficeAfterAlert>() {

			@Override
			public AtOfficeAfterAlert select(Map<String, List<SemTrajSegment>> pattern) throws Exception {
				
				return new AtOfficeAfterAlert(
						((SemTrajSegment)pattern.get("begin").get(0)).getParticipantID(),
						((SemTrajSegment)pattern.get("begin").get(0)).getStart_datetime(),
						((SemTrajSegment)pattern.get("begin").get(0)).getEnd_datetime(),
						afterHourTime);
			}
		});
		
		return alerts;
	}
	
	public static boolean isAfterTime(String timeToCompare, int afterHourTime) {
		DateTimeFormatter formatDateTime = DateTimeFormatter.ISO_DATE_TIME;
		LocalDateTime localDateTime = LocalDateTime.from(formatDateTime.parse(timeToCompare.replace(' ', 'T')));
		Timestamp ts = Timestamp.valueOf(localDateTime);
		
		int hour = ts.getHours();
		int minutes = ts.getMinutes();
		int seconds = ts.getSeconds();
		
		if(hour > afterHourTime) {
			return true;
		}
		else 
			if(hour == afterHourTime && (minutes > 0 || seconds > 0) ) {
				return true;
			}
		return false;
		
	}
}
