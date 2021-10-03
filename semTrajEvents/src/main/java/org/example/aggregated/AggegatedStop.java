package org.example.aggregated;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.flink.cep.PatternSelectFunction;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.nfa.aftermatch.AfterMatchSkipStrategy;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.IterativeCondition;
import org.apache.flink.cep.pattern.conditions.SimpleCondition;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.example.events.SemTrajSegment;

//1- A minimum number of trajectories/persons are present (or meet) at the same place for a minimum time interval (the place can belong to different hierarchies)
//done to detect the meet for two participants.
//It can be enhanced for multiple participants.
//It can be enhanced to precise the start and end meet datetime.
public class AggegatedStop {

	static HashMap<Integer,ParticipantStop> participantsStopsById = new HashMap<>();
	
	//duration in seconds
	public static Pattern<SemTrajSegment, ?> aggregatedStop(String hierarchy, long duration, int minimumNbParticipants){
		Pattern<SemTrajSegment, ?> p = Pattern.<SemTrajSegment>begin("1st", AfterMatchSkipStrategy.skipPastLastEvent())
				.subtype(SemTrajSegment.class)
				.where(new SimpleCondition<SemTrajSegment>() {

					@Override
					public boolean filter(SemTrajSegment value) throws Exception {
						if(participantsStopsById.containsKey(value.getParticipantID())) {
							if(participantsStopsById.get(value.getParticipantID()).getPlace().equals(value.getPlaceAccordingToHierarchy(hierarchy))) {
								participantsStopsById.put(value.getParticipantID(),
										new ParticipantStop(value.getParticipantID(),
												value.getPlaceAccordingToHierarchy(hierarchy),
												participantsStopsById.get(value.getParticipantID()).getStart_time_stop(),
												value.getEnd_datetime()));
								
							}else {
								participantsStopsById.put(value.getParticipantID(),
										new ParticipantStop(value.getParticipantID(),
												value.getPlaceAccordingToHierarchy(hierarchy),
												value.getStart_datetime(),
												value.getEnd_datetime()));
							}
						}else {
							participantsStopsById.put(value.getParticipantID(),
									new ParticipantStop(value.getParticipantID(),
											value.getPlaceAccordingToHierarchy(hierarchy),
											value.getStart_datetime(),
											value.getEnd_datetime()));
						}
						
						

						/////////////////////////////////////
						//printHashMap(participantsStopsById);
						//System.out.println("HASHMAP size = " + Integer.toString(participantsStopsById.size()));
						/////////////////////////////////////
						
						return true;
					}
				})
				.followedBy("2nd")
				.subtype(SemTrajSegment.class)
				.where(new IterativeCondition<SemTrajSegment>() {

					@Override
					public boolean filter(SemTrajSegment value, Context<SemTrajSegment> ctx) throws Exception {

						for(SemTrajSegment ev : ctx.getEventsForPattern("1st")) {
							if(participantsStopsById.containsKey(value.getParticipantID())) {
								if(participantsStopsById.get(value.getParticipantID()).getPlace().equals(value.getPlaceAccordingToHierarchy(hierarchy))) {
									participantsStopsById.put(value.getParticipantID(),
											new ParticipantStop(value.getParticipantID(),
													value.getPlaceAccordingToHierarchy(hierarchy),
													participantsStopsById.get(value.getParticipantID()).getStart_time_stop(),
													value.getEnd_datetime()));
									
								}else {
									participantsStopsById.put(value.getParticipantID(),
											new ParticipantStop(value.getParticipantID(),
													value.getPlaceAccordingToHierarchy(hierarchy),
													value.getStart_datetime(),
													value.getEnd_datetime()));
								}
							}else {
								participantsStopsById.put(value.getParticipantID(),
										new ParticipantStop(value.getParticipantID(),
												value.getPlaceAccordingToHierarchy(hierarchy),
												value.getStart_datetime(),
												value.getEnd_datetime()));
							}
							
							
							/////////////////////////////////////
							//printHashMap(participantsStopsById);
							//System.out.println("HASHMAP size = " + Integer.toString(participantsStopsById.size()));
							/////////////////////////////////////
							
							
							if(ev.getParticipantID() != value.getParticipantID()){
								ParticipantStop stop_1st = participantsStopsById.get(ev.getParticipantID());
								ParticipantStop stop_2nd = participantsStopsById.get(value.getParticipantID());
								
								if(stop_1st.getPlace().equals(stop_2nd.getPlace()) 
										&& stop_1st.OverlappedSecondsWith(stop_2nd) > duration) {
									return true;
								}	
							}
						}
						
						return false;
					}			
				});
		
		return p;
	}
	
	
	
	public static DataStream<AggregatedStopAlert> aggregatedStopAlertStream (PatternStream<SemTrajSegment> patternStream){		
		DataStream<AggregatedStopAlert> alerts = patternStream.select(new PatternSelectFunction<SemTrajSegment, AggregatedStopAlert>() {

			@Override
			public AggregatedStopAlert select(Map<String, List<SemTrajSegment>> pattern) throws Exception {
				
				int participantID = ((SemTrajSegment)pattern.get("1st").get(0)).getParticipantID();
				int withParticipantID = ((SemTrajSegment)pattern.get("2nd").get(0)).getParticipantID();
				
				String startDateTime = " ";
				String endDateTime = " ";
				long duration = participantsStopsById.get(participantID).OverlappedSecondsWith(participantsStopsById.get(withParticipantID));
				String place = participantsStopsById.get(participantID).getPlace();
				
				//stopWithById.remove(participantID);
				
				return new AggregatedStopAlert(participantID, withParticipantID, 
						startDateTime, endDateTime, 
						place, duration, "road");
			}
		});
		return alerts;
	}
	
	
	public static void printHashMap(HashMap<Integer,ParticipantStop> stopsById) {
		for (Map.Entry me : stopsById.entrySet()) {
	          System.out.println(me.getValue().toString());
	        }
	}
	
}
