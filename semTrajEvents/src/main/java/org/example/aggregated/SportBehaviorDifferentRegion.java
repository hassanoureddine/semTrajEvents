package org.example.aggregated;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.flink.cep.PatternSelectFunction;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.nfa.aftermatch.AfterMatchSkipStrategy;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.IterativeCondition;
import org.apache.flink.cep.pattern.conditions.SimpleCondition;
import org.apache.flink.cep.pattern.conditions.IterativeCondition.Context;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.example.events.SemTrajSegment;

//8- Trajectories having Sport Behavior (or event), at the same time, but with different regions (or places)
//different regions are according to a specified hierarchy specified by the pattern function 'sportBehaviorDifferentRegion' parameter

public class SportBehaviorDifferentRegion {
static HashMap<Integer,ParticipantStop> participantsStopsById = new HashMap<>();
	
	static HashSet<Integer> overlappedSegmentsIdsOfSportBehaviorDifferentPlace = new HashSet<Integer>();

	public static Pattern<SemTrajSegment, ?> sportBehaviorDifferentRegion(String hierarchy){
		Pattern<SemTrajSegment, ?> p = Pattern.<SemTrajSegment>begin("1st", AfterMatchSkipStrategy.skipPastLastEvent())
				.subtype(SemTrajSegment.class)
				.where(new SimpleCondition<SemTrajSegment>() {

					@Override
					public boolean filter(SemTrajSegment value) throws Exception {
						
						if(value.getEvent_semantics() == null || !value.getEvent_semantics().equals("Sport")) return false;
						
						if(participantsStopsById.containsKey(value.getParticipantID())) {
							if(participantsStopsById.get(value.getParticipantID()).getPlace().equals(value.getPlaceAccordingToHierarchy(hierarchy))
									&& participantsStopsById.get(value.getParticipantID()).getActivity().equals(value.getEvent_semantics())) {
								participantsStopsById.put(value.getParticipantID(),
										new ParticipantStop(value.getParticipantID(),
												value.getPlaceAccordingToHierarchy(hierarchy),
												participantsStopsById.get(value.getParticipantID()).getStart_time_stop(),
												value.getEnd_datetime(), 
												value.getEvent_semantics()));
								
							}else {
								participantsStopsById.put(value.getParticipantID(),
										new ParticipantStop(value.getParticipantID(),
												value.getPlaceAccordingToHierarchy(hierarchy),
												value.getStart_datetime(),
												value.getEnd_datetime(), 
												value.getEvent_semantics()));
							}
						}else {
							participantsStopsById.put(value.getParticipantID(),
									new ParticipantStop(value.getParticipantID(),
											value.getPlaceAccordingToHierarchy(hierarchy),
											value.getStart_datetime(),
											value.getEnd_datetime(), 
											value.getEvent_semantics())); //we put event in place of activity in ParticipantStop Object just to not ruin the project functions. 	
																	      //To be fixed later by adding event attribute to ParticipantStop Class
																		  //Then need to change participantStop.getActivity() to participantStop.getEvet()
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

						if(value.getEvent_semantics() == null || !value.getEvent_semantics().equals("Sport")) return false;
						
						for(SemTrajSegment ev : ctx.getEventsForPattern("1st")) {
							if(participantsStopsById.containsKey(value.getParticipantID())) {
								if(participantsStopsById.get(value.getParticipantID()).getPlace().equals(value.getPlaceAccordingToHierarchy(hierarchy))
										&& participantsStopsById.get(value.getParticipantID()).getActivity().equals(value.getEvent_semantics())) {
									participantsStopsById.put(value.getParticipantID(),
											new ParticipantStop(value.getParticipantID(),
													value.getPlaceAccordingToHierarchy(hierarchy),
													participantsStopsById.get(value.getParticipantID()).getStart_time_stop(),
													value.getEnd_datetime(), 
													value.getEvent_semantics()));
									
								}else {
									participantsStopsById.put(value.getParticipantID(),
											new ParticipantStop(value.getParticipantID(),
													value.getPlaceAccordingToHierarchy(hierarchy),
													value.getStart_datetime(),
													value.getEnd_datetime(), 
													value.getEvent_semantics()));
								}
							}else {
								participantsStopsById.put(value.getParticipantID(),
										new ParticipantStop(value.getParticipantID(),
												value.getPlaceAccordingToHierarchy(hierarchy),
												value.getStart_datetime(),
												value.getEnd_datetime(), 
												value.getEvent_semantics())); //we put event in place of activity in ParticipantStop Object just to not ruin the project functions. 	
							      											  //To be fixed later by adding event attribute to ParticipantStop Class
																			 //Then need to change participantStop.getActivity() to participantStop.getEvet()
							}
													
							/////////////////////////////////////
							//printHashMap(participantsStopsById);
							//System.out.println("HASHMAP size = " + Integer.toString(participantsStopsById.size()));
							/////////////////////////////////////
												
							overlappedSegmentsIdsOfSportBehaviorDifferentPlace = findOverlappedSegmentsIdsOfSportBehaviorDifferentPlace();							
							
							if(overlappedSegmentsIdsOfSportBehaviorDifferentPlace.size()>=2) {
								return true;
							}
							
						}
						
						return false;
					}			
				});
		
		
		return p;
	}
	
	public static DataStream<SportBehaviorDifferentRegionAlert> sportBehaviorDifferentRegionAlertStream (PatternStream<SemTrajSegment> patternStream){
		DataStream<SportBehaviorDifferentRegionAlert> alerts = patternStream.select(new PatternSelectFunction<SemTrajSegment, SportBehaviorDifferentRegionAlert>() {

			@Override
			public SportBehaviorDifferentRegionAlert select(Map<String, List<SemTrajSegment>> pattern) throws Exception {
				return new SportBehaviorDifferentRegionAlert(overlappedSegmentsIdsOfSportBehaviorDifferentPlace,
						"",
						"",
						"town",
						((SemTrajSegment)pattern.get("2nd").get(0)).getEvent_semantics());
			}
		});
		
		return alerts;
	}
	
	
	private static HashSet<Integer> findOverlappedSegmentsIdsOfSportBehaviorDifferentPlace(){
		HashSet<ParticipantStop> overlappedIds =  new HashSet<>();
		
		ArrayList<ParticipantStop> overlappedArray =  new ArrayList<>();
		
		Set<Integer> keys = participantsStopsById.keySet();
		Integer[] allKeys =keys.toArray(new Integer[keys.size()]);
		
		for(int i = 0; i<keys.size() - 1; i++) {
			overlappedArray.clear();
			overlappedArray.add(participantsStopsById.get(allKeys[i]));
			
			//add all the overlapped ParticipantStop with the i element in participantsStopsById (with the same activity and different places)
			for(int j = i+1; j<keys.size(); j++) {
				if(participantsStopsById.get(allKeys[i]).getActivity().equals(participantsStopsById.get(allKeys[j]).getActivity())
						&& !participantsStopsById.get(allKeys[i]).getPlace().equals(participantsStopsById.get(allKeys[j]).getPlace())
						&& participantsStopsById.get(allKeys[i]).OverlappedSecondsWith(participantsStopsById.get(allKeys[j])) > 0) {
					
					overlappedArray.add(participantsStopsById.get(allKeys[j]));
				}
			}
			
			//check for the overlapped between the overlapped with the i element in participantsStopsById
			
			for(int k = 0; k<overlappedArray.size()-1; k++) {
				for(int t = k+1; t<overlappedArray.size(); t++) {
					if(overlappedArray.get(k).OverlappedSecondsWith(overlappedArray.get(t))>0) {
						overlappedIds.add(overlappedArray.get(k));
						overlappedIds.add(overlappedArray.get(t));
					}
				}
				//overlappedIds.add(overlappedArray.get(0)); //add the first element of overlappedArray (this element overlap with all overlappedArray elements)
			}
			
			if(overlappedIds.size() >= 2) {
				break;
			}
		}
		
		HashSet<Integer> setOfOverlappedIds = new HashSet<Integer>();
		
		if(overlappedArray.size() == 2) {
			for(ParticipantStop s : overlappedArray) {
				setOfOverlappedIds.add(s.getParticipantID());
			}
		}else {
			for(ParticipantStop s : overlappedIds) {
				setOfOverlappedIds.add(s.getParticipantID());
			}
		}
				
		return setOfOverlappedIds;
	}
}
