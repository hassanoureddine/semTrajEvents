package org.example.individual;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.flink.cep.PatternSelectFunction;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.SimpleCondition;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.example.events.SemTrajSegment;

//4- Trajectory having different transportation modes for "arrive at the office" and "leave the office"
public class ArriveLeaveOfficeDifferentMode {
	
	static HashMap<Integer,String> transportationModeById = new HashMap<>();
	public static Pattern<SemTrajSegment, ?> arriveLeaveOfficeDifferentMode() {
		Pattern<SemTrajSegment, ?> p = Pattern.<SemTrajSegment>begin("begin")
				.where(new SimpleCondition<SemTrajSegment>() {

					@Override
					public boolean filter(SemTrajSegment value) throws Exception {
						// && value.getActivity_semantics().matches("Rue|Bus|Train|Voiture|Vélo|Métro|Tramway|Moto|Deux Roues")
						//value.getActivity_semantics().equals("Rue") || 
						if(value.getActivity_semantics() != null
								&& (value.getActivity_semantics().equals("Bus")
										|| value.getActivity_semantics().equals("Train") || value.getActivity_semantics().equals("Voiture")
										|| value.getActivity_semantics().equals("Vélo") || value.getActivity_semantics().equals("Métro")
										|| value.getActivity_semantics().equals("Tramway") || value.getActivity_semantics().equals("Moto")
										|| value.getActivity_semantics().equals("Deux Roues"))
								) {
							transportationModeById.put(value.getParticipantID(), value.getActivity_semantics());
							//transportationMode = value.getActivity_semantics();
							
							return true;
						}else {
							return false;
						}
					}
				})
				.next("office")
				.where(new SimpleCondition<SemTrajSegment>() {

					@Override
					public boolean filter(SemTrajSegment value) throws Exception {
						if(value.getActivity_semantics() != null && value.getActivity_semantics().equals("Bureau")) {
							return true;
						}
						return false;
					}
				}).oneOrMore()
				.next("end")
				.where(new SimpleCondition<SemTrajSegment>() {

					@Override
					public boolean filter(SemTrajSegment value) throws Exception {
						
						//System.out.println(Integer.toString(value.getParticipantID()) + " " + transportationModeById.get(value.getParticipantID()));
						
						if(value.getActivity_semantics() != null 
								&& !value.getActivity_semantics().equals("Bureau")
								&& !value.getActivity_semantics().equals(transportationModeById.get(value.getParticipantID()))) {
							return true;
						}
						return false;
					}
				}).times(1);
		return p;
	}
	
	public static DataStream<ArriveLeaveOfficeDifferentModeAlert> arriveLeaveOfficeDifferentModeAlertStream(PatternStream<SemTrajSegment> patternStream){
		DataStream<ArriveLeaveOfficeDifferentModeAlert> alerts = patternStream.select(new PatternSelectFunction<SemTrajSegment, ArriveLeaveOfficeDifferentModeAlert>() {

			@Override
			public ArriveLeaveOfficeDifferentModeAlert select(Map<String, List<SemTrajSegment>> pattern) throws Exception {
				return new ArriveLeaveOfficeDifferentModeAlert(
						((SemTrajSegment)pattern.get("begin").get(0)).getParticipantID(),
						((SemTrajSegment)pattern.get("office").get(0)).getStart_datetime(),
						((SemTrajSegment)pattern.get("end").get(0)).getStart_datetime(),
						((SemTrajSegment)pattern.get("begin").get(0)).getActivity_semantics(),
						((SemTrajSegment)pattern.get("end").get(0)).getActivity_semantics()
						);
			}
		});
		return alerts;
	}
}
