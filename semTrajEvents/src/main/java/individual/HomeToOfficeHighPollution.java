package individual;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.apache.flink.cep.PatternSelectFunction;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.nfa.aftermatch.AfterMatchSkipStrategy;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.SimpleCondition;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.example.events.SemTrajSegment;

//3- A trajectory going from home to office and be exposed to a high level of pollution for a minimum time interval
public class HomeToOfficeHighPollution {
	static long exposureDuration = 0;
	
	public static Pattern<SemTrajSegment, ?> homeToOfficeHighPollution(long duration) {
		Pattern<SemTrajSegment, ?> p = Pattern.<SemTrajSegment>begin("home", AfterMatchSkipStrategy.skipToLast("home"))
				.where(new SimpleCondition<SemTrajSegment>() {

					@Override
					public boolean filter(SemTrajSegment value) throws Exception {
						if(value.getActivity_semantics() != null && value.getActivity_semantics().equals("Domicile")) {
							exposureDuration = 0;
							return true;
						}else {
							return false;
						}
					}
				})
				.followedBy("highPollution")
				.where(new SimpleCondition<SemTrajSegment>() {

					@Override
					public boolean filter(SemTrajSegment value) throws Exception {
						if( (value.getActivity_semantics() == null 
								|| (
										value.getActivity_semantics() != null 
										&& !value.getActivity_semantics().equals("Domicile")) 
										&& !value.getActivity_semantics().equals("Bureau")
									)  
								&&
									(
										(value.getBC_semantics() != null && value.getBC_semantics().equals("High"))
										|| (value.getPM10_semantics() != null && value.getPM10_semantics().equals("High"))
										|| (value.getPM1_semantics() != null && value.getPM1_semantics().equals("High"))
										|| (value.getPM25_semantics() != null && value.getPM25_semantics().equals("High"))
										|| (value.getNO2_semantics() != null && value.getNO2_semantics().equals("High"))
										)
								) {
							exposureDuration += Duration(value.getStart_datetime(), value.getEnd_datetime());
							System.out.println("exposure duration:" + Long.toString(exposureDuration) + " at " + value.getStart_datetime() + " end " + value.getEnd_datetime());
							return true;
						}
						return false;
					}
				}).oneOrMore()
				.followedBy("office")
				.where(new SimpleCondition<SemTrajSegment>() {

					@Override
					public boolean filter(SemTrajSegment value) throws Exception {
						if(value.getActivity_semantics() != null && value.getActivity_semantics().equals("Bureau")) {
							if(exposureDuration >= duration) {
								exposureDuration = 0;
								return true;
							}
						}
						return false;
					}
				}).within(Time.hours(1));
		return p;
	}
	
	
	public static DataStream<HomeToOfficeHighPollutionAlert> homeToOfficeHighPollutionAlertStream(PatternStream<SemTrajSegment> patternStream){
		DataStream<HomeToOfficeHighPollutionAlert> alerts = patternStream.select(new PatternSelectFunction<SemTrajSegment, HomeToOfficeHighPollutionAlert>() {

			@Override
			public HomeToOfficeHighPollutionAlert select(Map<String, List<SemTrajSegment>> pattern) throws Exception {
				return new HomeToOfficeHighPollutionAlert(
						((SemTrajSegment)pattern.get("home").get(0)).getParticipantID(),
						((SemTrajSegment)pattern.get("highPollution").get(0)).getStart_datetime(),
						((SemTrajSegment)pattern.get("highPollution").get(0)).getEnd_datetime(),
						((SemTrajSegment)pattern.get("highPollution").get(0)).getPlaceAccordingToHierarchy("display_name")
						);
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
	
}
