package org.example.individual;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.apache.flink.cep.PatternSelectFunction;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.nfa.aftermatch.AfterMatchSkipStrategy;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.IterativeCondition;
import org.apache.flink.cep.pattern.conditions.SimpleCondition;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.example.events.SemTrajSegment;

//1- Trajectory that stays at a place for a minimum time interval (e.g., gare, bureau) (the place can belong to different hierarchies) (we can call it a stop event)
public class IndividualStop {
	
	static long stopTime = 0;
	public static Pattern<SemTrajSegment, ?> individualStop(String hierarchy, long duration) {
		//duration in seconds or minutes??
		//It can't detect start and end time for the stop. It can detect only the exact end time of the stop 
		Pattern<SemTrajSegment, ?> p = Pattern.<SemTrajSegment>begin("firstPresence", AfterMatchSkipStrategy.skipPastLastEvent())
				.subtype(SemTrajSegment.class)
				.followedBy("secondPresence")
				.subtype(SemTrajSegment.class)
				.where(new IterativeCondition<SemTrajSegment>() {
					
					@Override
					public boolean filter(SemTrajSegment value, Context<SemTrajSegment> ctx) throws Exception {

						Long dur = Duration(value.getStart_datetime(), value.getEnd_datetime());
						
						for(SemTrajSegment ev : ctx.getEventsForPattern("firstPresence")) {
							if(ev.getParticipantID() == value.getParticipantID()
									&& ev.getPlaceAccordingToHierarchy(hierarchy) != null
									&& value.getPlaceAccordingToHierarchy(hierarchy) != null
									&& ev.getPlaceAccordingToHierarchy(hierarchy).equals(value.getPlaceAccordingToHierarchy(hierarchy))) {
								stopTime += dur;
								break;
							}else {
								return false;
							}
						}
						
						
						//System.out.println(Long.toString(dur));
						//System.out.println(Long.toString(stopTime));
						
						if(stopTime >= duration) {
							stopTime = 0;
							return true;
						}
						return false;
					}			
				}).oneOrMore().until(new IterativeCondition<SemTrajSegment>() {

					@Override
					public boolean filter(SemTrajSegment value, Context<SemTrajSegment> ctx) throws Exception {
						for(SemTrajSegment ev : ctx.getEventsForPattern("firstPresence")) {
							if(ev.getParticipantID() == value.getParticipantID()
									&& ev.getPlaceAccordingToHierarchy(hierarchy) != null
									&& value.getPlaceAccordingToHierarchy(hierarchy) != null
									&& !ev.getPlaceAccordingToHierarchy(hierarchy).equals(value.getPlaceAccordingToHierarchy(hierarchy))) {
								//System.out.println("UNTIL ACTIVATED");
								stopTime = 0;
								return true;
								}
							}
						return false;
					}
				});
				
				
		return p;
	}
	
	public static DataStream<IndividualStopAlert> individualStopAlertStream (PatternStream<SemTrajSegment> patternStream){
		DataStream<IndividualStopAlert> alerts = patternStream.select(new PatternSelectFunction<SemTrajSegment, IndividualStopAlert>(){

			@Override
			public IndividualStopAlert select(Map<String, List<SemTrajSegment>> pattern) throws Exception {
				int participantID = ((SemTrajSegment)pattern.get("firstPresence").get(0)).getParticipantID();
				String start_time_event = ((SemTrajSegment)pattern.get("firstPresence").get(0)).getStart_datetime();
				
				String end_time_event = ((SemTrajSegment)pattern.get("secondPresence").get(0)).getEnd_datetime(); 
				/*if(((SemTrajSegment)pattern.get("secondPresence").get(0) != null)){
					end_time_event = ((SemTrajSegment)pattern.get("secondPresence").get(0)).getEnd_datetime(); 
				}else {
					end_time_event = ((SemTrajSegment)pattern.get("firstPresence").get(0)).getEnd_datetime(); 
				}*/
				
				
				String place = ((SemTrajSegment)pattern.get("secondPresence").get(0)).getPlaceAccordingToHierarchy("suburb"); 
				String hierarchy = "suburb";
				
				
				return new IndividualStopAlert(participantID, start_time_event, end_time_event, place, hierarchy);
			}		
		});
		
		return alerts;
	}

	public static long Duration(String time1, String time2) {
		DateTimeFormatter formatDateTime = DateTimeFormatter.ISO_DATE_TIME;
		LocalDateTime localDateTime1 = LocalDateTime.from(formatDateTime.parse(time1.replace(' ', 'T')));
		Timestamp t1 = Timestamp.valueOf(localDateTime1);

		LocalDateTime localDateTime2 = LocalDateTime.from(formatDateTime.parse(time2.replace(' ', 'T')));
		Timestamp t2 = Timestamp.valueOf(localDateTime2);

		long diffMs = t2.getTime() - t1.getTime();
		long diffSec = diffMs / 1000;
		long min = diffSec / 60;
		//long sec = diffSec % 60;
		//System.out.println("The difference is "+min+" minutes and "+sec+" seconds.");
		
		return diffSec;
	}
}
