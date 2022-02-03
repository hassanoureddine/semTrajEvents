package org.example.aggregated;

import java.util.HashMap;

import org.apache.flink.cep.nfa.aftermatch.AfterMatchSkipStrategy;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.IterativeCondition;
import org.apache.flink.cep.pattern.conditions.SimpleCondition;
import org.apache.flink.cep.pattern.conditions.IterativeCondition.Context;
import org.example.events.SemTrajSegment;

public class MeetThenSamePath {

	static HashMap<Integer,ParticipantStop> participantsStopsById = new HashMap<>();
	
	public static Pattern<SemTrajSegment, ?> meetThenSamePath(String hierarchy, int samePathSegmentsNumber){
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
								
								if(stop_1st.getPlace().equals(stop_2nd.getPlace()) && stop_1st.OverlappedSecondsWith(stop_2nd) > 1) {
									
									
									
									return true;
								}	
							}
						}
						
						return false;
					}							
				});
		
		return p;
	}
}
