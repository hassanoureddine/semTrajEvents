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
import org.apache.flink.cep.pattern.conditions.IterativeCondition.Context;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.example.events.SemTrajSegment;

//5- Trajectory going from home to office and meet another trajectory/person for a minimum time interval
public class HomeToOfficeMeet {
	
	static HashMap<Integer,ParticipantStop> participantsStopsById = new HashMap<>();
	
	static HashMap<Integer,ParticipantStop> participantsStopsByIdForMeet = new HashMap<>();
	
	//static String hierarchy = "name";
	//hierarchy indicates at what level of spatial granularity the meet is evaluated
	public static Pattern<SemTrajSegment, ?> homeToOfficeMeet(long minimumMeetDuration, String hierarchy){
		Pattern<SemTrajSegment, ?> p = Pattern.<SemTrajSegment>begin("home", AfterMatchSkipStrategy.skipToLast("home"))
				.where(new SimpleCondition<SemTrajSegment>() {

					@Override
					public boolean filter(SemTrajSegment value) throws Exception {
						if(value.getActivity_semantics() != null && value.getActivity_semantics().equals("Domicile")) {
							participantsStopsById.put(value.getParticipantID(), 
									new ParticipantStop(value.getParticipantID(), 
											value.getPlaceAccordingToHierarchy(hierarchy), 
											value.getStart_datetime(), 
											value.getEnd_datetime(), 
											value.getActivity_semantics(),
											false));
							return true;
						}
						return false;
					}
				})
				.followedBy("1stMetSomeone").subtype(SemTrajSegment.class)
				.where(new SimpleCondition<SemTrajSegment>() {

					@Override
					public boolean filter(SemTrajSegment value) throws Exception {
						
						if(value.getActivity_semantics() != null && !value.getActivity_semantics().equals("Domicile") && participantsStopsById.containsKey(value.getParticipantID())) {
							
							if(participantsStopsByIdForMeet.containsKey(value.getParticipantID())) {
								if(participantsStopsByIdForMeet.get(value.getParticipantID()).getPlace().equals(value.getPlaceAccordingToHierarchy(hierarchy))) {
									participantsStopsByIdForMeet.put(value.getParticipantID(),
											new ParticipantStop(value.getParticipantID(),
													value.getPlaceAccordingToHierarchy(hierarchy),
													participantsStopsByIdForMeet.get(value.getParticipantID()).getStart_time_stop(),
													value.getEnd_datetime()));
									
								}else {
									participantsStopsByIdForMeet.put(value.getParticipantID(),
											new ParticipantStop(value.getParticipantID(),
													value.getPlaceAccordingToHierarchy(hierarchy),
													value.getStart_datetime(),
													value.getEnd_datetime()));
								}
							}else {
								participantsStopsByIdForMeet.put(value.getParticipantID(),
										new ParticipantStop(value.getParticipantID(),
												value.getPlaceAccordingToHierarchy(hierarchy),
												value.getStart_datetime(),
												value.getEnd_datetime()));
							}

							/////////////////////////////////////
							printHashMap(participantsStopsByIdForMeet);
							System.out.println("HASHMAP size = " + Integer.toString(participantsStopsById.size()));
							/////////////////////////////////////
							
							return true;
							
						}else {
							return false;
						}
						
					}
				})
				.followedBy("2ndMetSomeone")
				.subtype(SemTrajSegment.class)
				.where(new IterativeCondition<SemTrajSegment>() {

					@Override
					public boolean filter(SemTrajSegment value, Context<SemTrajSegment> ctx) throws Exception {

						for(SemTrajSegment ev : ctx.getEventsForPattern("1stMetSomeone")) {
							if(participantsStopsByIdForMeet.containsKey(value.getParticipantID())) {
								if(participantsStopsByIdForMeet.get(value.getParticipantID()).getPlace().equals(value.getPlaceAccordingToHierarchy(hierarchy))) {
									participantsStopsByIdForMeet.put(value.getParticipantID(),
											new ParticipantStop(value.getParticipantID(),
													value.getPlaceAccordingToHierarchy(hierarchy),
													participantsStopsByIdForMeet.get(value.getParticipantID()).getStart_time_stop(),
													value.getEnd_datetime()));
									
								}else {
									participantsStopsByIdForMeet.put(value.getParticipantID(),
											new ParticipantStop(value.getParticipantID(),
													value.getPlaceAccordingToHierarchy(hierarchy),
													value.getStart_datetime(),
													value.getEnd_datetime()));
								}
							}else {
								participantsStopsByIdForMeet.put(value.getParticipantID(),
										new ParticipantStop(value.getParticipantID(),
												value.getPlaceAccordingToHierarchy(hierarchy),
												value.getStart_datetime(),
												value.getEnd_datetime()));
							}
							
							
							/////////////////////////////////////
							printHashMap(participantsStopsByIdForMeet);
							System.out.println("HASHMAP size = " + Integer.toString(participantsStopsById.size()));
							/////////////////////////////////////
							
							
							if(ev.getParticipantID() != value.getParticipantID()){
								ParticipantStop stop_1st = participantsStopsByIdForMeet.get(ev.getParticipantID());
								ParticipantStop stop_2nd = participantsStopsByIdForMeet.get(value.getParticipantID());
								
								if(stop_1st.getPlace().equals(stop_2nd.getPlace()) 
										&& stop_1st.OverlappedSecondsWith(stop_2nd) > minimumMeetDuration) {
									
									//here, 'ev' was at home --> met 'value' with > minimumMeetDuration
									participantsStopsById.get(ev.getParticipantID()).setMetSomeone(true);
									
									/*participantsStopsById.put(ev.getParticipantID(), 
											new ParticipantStop(ev.getParticipantID(), 
													value.getPlaceAccordingToHierarchy(hierarchy), 
													value.getStart_datetime(), 
													value.getEnd_datetime(), 
													"Domicile",
													true));*/
									
									/////////////////////////////////////
									//printHashMap(participantsStopsById);
									//System.out.println("HASHMAP size = " + Integer.toString(participantsStopsById.size()));
									/////////////////////////////////////
									
									return true;
								}	
							}
						}
						
						return false;
					}			
				}).oneOrMore()
				.followedBy("office")
				.where(new SimpleCondition<SemTrajSegment>() {

					@Override
					public boolean filter(SemTrajSegment value) throws Exception {
						if(participantsStopsById.containsKey(value.getParticipantID()) 
								&& participantsStopsById.get(value.getParticipantID()).getMetSomeone()
								&& value.getActivity_semantics() != null
								&& value.getActivity_semantics().equals("Bureau")) {
							return true;
						}
						return false;
					}
					
				}).within(Time.hours(1));
		
		return p;
	}
	
	public static DataStream<HomeToOfficeMeetAlert> homeToOfficeMeetAlertStream(PatternStream<SemTrajSegment> patternStream){
		DataStream<HomeToOfficeMeetAlert> alerts = patternStream.select(new PatternSelectFunction<SemTrajSegment,HomeToOfficeMeetAlert>(){

			@Override
			public HomeToOfficeMeetAlert select(Map<String, List<SemTrajSegment>> pattern) throws Exception {
				
				int participantID = ((SemTrajSegment)pattern.get("office").get(0)).getParticipantID();
				participantsStopsById.remove(participantID);
				
				int withParticipantID = ((SemTrajSegment)pattern.get("2ndMetSomeone").get(0)).getParticipantID();
				String atPlace = ((SemTrajSegment)pattern.get("2ndMetSomeone").get(0)).getPlaceAccordingToHierarchy("town");
				
				long duration = participantsStopsByIdForMeet.get(participantID).OverlappedSecondsWith(participantsStopsByIdForMeet.get(withParticipantID));
				
				
				return new HomeToOfficeMeetAlert(participantID, withParticipantID, "", "", atPlace, duration, "town");
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
