package org.example.aggregated;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import org.apache.flink.streaming.api.datastream.DataStream;
import org.example.events.SemTrajSegment;

//3- Places with a minimum number of persons at the same time
public class PlacesWithMin {
	
	static HashMap<Integer,ParticipantStop> participantsStopsById = new HashMap<>();
	
	static HashSet<Integer> overlappedSegmentsIdsOfSamePlace = new HashSet<Integer>();
	
	static int MinimumNbParticipants = 2; // this is a default value and it can be changed by the pattern function 'placesWithMin'

	public static Pattern<SemTrajSegment, ?> placesWithMin(String hierarchy, int minimumNbParticipants){
		
		MinimumNbParticipants = minimumNbParticipants;
		
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
												
							overlappedSegmentsIdsOfSamePlace = findOverlappedSegmentsIdsOfSamePlace();							
							
							if(overlappedSegmentsIdsOfSamePlace.size()>=MinimumNbParticipants) {
								return true;
							}
							
						}
						
						return false;
					}			
				});
		
		
		return p;
	}
	
	
	public static DataStream<PlacesWithMinAlert> placesWithMinAlertStream (PatternStream<SemTrajSegment> patternStream){
		DataStream<PlacesWithMinAlert> alerts = patternStream.select(new PatternSelectFunction<SemTrajSegment,PlacesWithMinAlert>(){

			@Override
			public PlacesWithMinAlert select(Map<String, List<SemTrajSegment>> pattern) throws Exception {
				return new PlacesWithMinAlert(overlappedSegmentsIdsOfSamePlace, 
						"",
						"",
						((SemTrajSegment)pattern.get("2nd").get(0)).getPlaceAccordingToHierarchy("town"),
						"town");
			}
			
		});
		
		return alerts;
	}
	
	private static HashSet<Integer> findOverlappedSegmentsIdsOfSamePlace(){
		HashSet<ParticipantStop> overlappedIds =  new HashSet<>();
		
		ArrayList<ParticipantStop> overlappedArray =  new ArrayList<>();
		
		Set<Integer> keys = participantsStopsById.keySet();
		Integer[] allKeys =keys.toArray(new Integer[keys.size()]);
		
		for(int i = 0; i<keys.size() - 1; i++) {
			overlappedArray.clear();
			overlappedArray.add(participantsStopsById.get(allKeys[i]));
			
			//add all the overlapped ParticipantStop with the i element in participantsStopsById (with the same place)
			for(int j = i+1; j<keys.size(); j++) {
				if(participantsStopsById.get(allKeys[i]).getPlace().equals(participantsStopsById.get(allKeys[j]).getPlace())
						&& participantsStopsById.get(allKeys[i]).OverlappedSecondsWith(participantsStopsById.get(allKeys[j])) > 0) {
					
					overlappedArray.add(participantsStopsById.get(allKeys[j]));
				}
			}
			
			//check for the overlapped between the overlapped with the i element in participantsStopsById
			
			for(int k = 1; k<overlappedArray.size()-1; k++) {
				for(int t = k+1; t<overlappedArray.size(); t++) {
					if(overlappedArray.get(k).OverlappedSecondsWith(overlappedArray.get(t))>0) {
						overlappedIds.add(overlappedArray.get(k));
						overlappedIds.add(overlappedArray.get(t));
					}
				}
				overlappedIds.add(overlappedArray.get(0)); //add the first element of overlappedArray (this element overlap with all overlappedArray elements)
			}
			
			if(overlappedIds.size() >= MinimumNbParticipants) {
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
	
	
	public static void printHashMap(HashMap<Integer,ParticipantStop> stopsById) {
		for (Map.Entry me : stopsById.entrySet()) {
	          System.out.println(me.getValue().toString());
	        }
	}
}
