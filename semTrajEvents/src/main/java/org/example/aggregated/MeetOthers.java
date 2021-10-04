package org.example.aggregated;

import java.util.HashMap;
import java.util.HashSet;
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

//7- Trajectory that meet a minimum number of people within a time interval
public class MeetOthers {
	
	static HashMap<Integer,ParticipantStop> participantsStopsById = new HashMap<>();
	
	static HashMap<Integer, HashSet<Integer>> participantMeets = new HashMap<Integer, HashSet<Integer>>();
	
	public static Pattern<SemTrajSegment, ?> meetOthers(int minimumNbOfPeople, String hierarchy, long withinTime){
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
										&& stop_1st.OverlappedSecondsWith(stop_2nd) > 1) {
									
									if(participantMeets.containsKey(ev.getParticipantID())) {
										participantMeets.get(ev.getParticipantID()).add(value.getParticipantID());
									}else {
										HashSet<Integer> toAdd =  new HashSet<Integer>();
										toAdd.add(value.getParticipantID());
										participantMeets.put(ev.getParticipantID(), toAdd);
									}
									
									if(participantMeets.get(ev.getParticipantID()).size() > minimumNbOfPeople) {
										return true;
									}
								}	
							}
						}
						
						return false;
					}			
				}).within(Time.seconds(withinTime));
		
		return p;
	}
	
	
	public static DataStream<MeetOthersAlert> meetOthersAlertStream(PatternStream<SemTrajSegment> patternStream){
		DataStream<MeetOthersAlert> alerts = patternStream.select(new PatternSelectFunction<SemTrajSegment, MeetOthersAlert>() {

			@Override
			public MeetOthersAlert select(Map<String, List<SemTrajSegment>> pattern) throws Exception {
				
				int participantID = ((SemTrajSegment)pattern.get("1st").get(0)).getParticipantID();
				HashSet<Integer> withParticipantsID = participantMeets.get(participantID);
				withParticipantsID.remove(participantID);
				
				return new MeetOthersAlert(participantID, withParticipantsID, "", "", "town");
			}
		});
		
		return alerts;
	}
}
