package social;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.flink.cep.PatternSelectFunction;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.nfa.aftermatch.AfterMatchSkipStrategy;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.IterativeCondition;
import org.apache.flink.cep.pattern.conditions.IterativeCondition.Context;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.example.events.SemTrajSegment;

public class SocialStop {
	static long stopTime = 0;
	static List<Integer> uniqueParticipantsIds = new ArrayList<Integer>();
	
	static String start1;
	static String end1;
	static String start2;
	static String end2;
	
	static String place;
	
	//1- A minimum number of trajectories/persons are present (or meet) at the same place for a minimum time interval (the place can belong to different hierarchies)
	// NOT FINISHED YET
	public static Pattern<SemTrajSegment, ?> socialStop(String hierarchy, long duration, int minimumNbParticipants) {
		//duration in seconds or minutes??
		
		Pattern<SemTrajSegment, ?> p = Pattern.<SemTrajSegment>begin("begin", AfterMatchSkipStrategy.skipPastLastEvent())
				.subtype(SemTrajSegment.class)
				.next("firstPresence")
				.subtype(SemTrajSegment.class)
				.where(new IterativeCondition<SemTrajSegment>() {

					@Override
					public boolean filter(SemTrajSegment value, Context<SemTrajSegment> ctx) throws Exception {
						for(SemTrajSegment ev : ctx.getEventsForPattern("begin")) {
							
							System.out.println(Integer.toString(ev.getParticipantID()) + "  " + ev.getStart_datetime());
							System.out.println(Integer.toString(value.getParticipantID()) + "  " + value.getStart_datetime());
							
							
							System.out.println(ev.getPlaceAccordingToHierarchy(hierarchy) + " AND " + value.getPlaceAccordingToHierarchy(hierarchy));
							
							if(ev.getParticipantID() != value.getParticipantID()
									&& ev.getPlaceAccordingToHierarchy(hierarchy) != null
									&& value.getPlaceAccordingToHierarchy(hierarchy) != null
									&& ev.getPlaceAccordingToHierarchy(hierarchy).equals(value.getPlaceAccordingToHierarchy(hierarchy))
									) {
								
								//&& isOverlappingDateTime(ev.getStart_datetime(), ev.getEnd_datetime(), value.getStart_datetime(), value.getEnd_datetime())>0
								place = value.getPlaceAccordingToHierarchy(hierarchy);
								if(!place.equals(value.getPlaceAccordingToHierarchy(hierarchy)) 
										|| !place.equals(ev.getPlaceAccordingToHierarchy(hierarchy))
										|| (start1 == null && end1 == null) 
										){
									start1 = ev.getStart_datetime();
									end1 = ev.getEnd_datetime();
									//start2 = value.getStart_datetime();
									//end2 = value.getEnd_datetime();
								}
								
								
								
								System.out.println("HII");
								
								AddUniqueParticipantsIds(ev.getParticipantID());
								AddUniqueParticipantsIds(value.getParticipantID());
								
								stopTime += isOverlappingDateTime(start1, end1, value.getStart_datetime(), value.getEnd_datetime());

								System.out.println("----------stopTime: " + Long.toString(stopTime));
								
								break;
							}else {
								return false;
							}
						}
						if(stopTime >= duration && uniqueParticipantsIds.size() >= minimumNbParticipants) {
							stopTime = 0;
							ClearUniqueParticipantsIds();
							return true;
							}else {
								return false;
							}
						}
				})
				.oneOrMore().until(new IterativeCondition<SemTrajSegment>() {

					@Override
					public boolean filter(SemTrajSegment value, Context<SemTrajSegment> ctx) throws Exception {
						for(SemTrajSegment ev : ctx.getEventsForPattern("begin")) {
							if(//ev.getParticipantID() != value.getParticipantID() && 
									ev.getPlaceAccordingToHierarchy(hierarchy) != null
									&& value.getPlaceAccordingToHierarchy(hierarchy) != null
									&& !ev.getPlaceAccordingToHierarchy(hierarchy).equals(value.getPlaceAccordingToHierarchy(hierarchy))
									) {
								System.out.println("UNTIL ACTIVATED");
								stopTime = 0;
								ClearUniqueParticipantsIds();
								return true;
							}
						}
						return false;
					}
				});
		return p;
	}
	
	
	public static DataStream<SocialStopAlert> socialStopAlerttStream (PatternStream<SemTrajSegment> patternStream){		
		DataStream<SocialStopAlert> alerts = patternStream.select(new PatternSelectFunction<SemTrajSegment, SocialStopAlert>() {

			@Override
			public SocialStopAlert select(Map<String, List<SemTrajSegment>> pattern) throws Exception {
				System.out.println("--------------ACHEIVED---------------");
				return new SocialStopAlert(((SemTrajSegment)pattern.get("begin").get(0)).getParticipantID(),
						((SemTrajSegment)pattern.get("firstPresence").get(0)).getParticipantID(), 
						((SemTrajSegment)pattern.get("begin").get(0)).getStart_datetime(), 
						((SemTrajSegment)pattern.get("firstPresence").get(0)).getEnd_datetime(), 
						((SemTrajSegment)pattern.get("firstPresence").get(0)).getPlaceAccordingToHierarchy("county"), 
						"county");
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
		
		return min;
	}
	
	public static long isOverlappingDateTime(String start1, String end1, String start2, String end2) {
		DateTimeFormatter formatDateTime = DateTimeFormatter.ISO_DATE_TIME;
		LocalDateTime localDateTimeS1 = LocalDateTime.from(formatDateTime.parse(start1.replace(' ', 'T')));
		Timestamp s1 = Timestamp.valueOf(localDateTimeS1);
		LocalDateTime localDateTimE1 = LocalDateTime.from(formatDateTime.parse(end1.replace(' ', 'T')));
		Timestamp e1 = Timestamp.valueOf(localDateTimE1);
		
		LocalDateTime localDateTimeS2 = LocalDateTime.from(formatDateTime.parse(start2.replace(' ', 'T')));
		Timestamp s2 = Timestamp.valueOf(localDateTimeS2);
		LocalDateTime localDateTimE2 = LocalDateTime.from(formatDateTime.parse(end2.replace(' ', 'T')));
		Timestamp e2 = Timestamp.valueOf(localDateTimE2);
		
		
		Timestamp overlapStart = MaxTimestamp(s1, s2);
		Timestamp overlapEnd = MinTimestamp(e1, e2);
		
		
		if((s1.before(e2) && s2.before(e1)) || (s2.before(e1) && s1.before(e2))) {
			long diffMs = overlapEnd.getTime() - overlapStart.getTime();
			long diffSec = diffMs / 1000;
			long min = diffSec / 60;
			return min;
		}else {
			return 0;
		}
	}
	
	public static Timestamp MaxTimestamp(Timestamp a, Timestamp b) {
		return a.compareTo(b) > 0? a : b;
	}
	
	public static Timestamp MinTimestamp(Timestamp a, Timestamp b) {
		return a.compareTo(b) > 0? b : a;
	}
		
	private static void AddUniqueParticipantsIds(int number) {
		  if (!uniqueParticipantsIds.contains(number)) {
			  uniqueParticipantsIds.add(number);
		  }
		}
	
	private static void ClearUniqueParticipantsIds() {
		uniqueParticipantsIds.clear();
	}
}
