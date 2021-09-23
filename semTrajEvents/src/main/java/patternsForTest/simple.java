package patternsForTest;

import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.SimpleCondition;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.example.events.SemTrajSegment;

//Simple patterns created for test
public class simple {

	public static Pattern<SemTrajSegment, ?> pattern1() {
		Pattern<SemTrajSegment, ?> p = Pattern.<SemTrajSegment>begin("start")
				.where(
						new SimpleCondition<SemTrajSegment>() {
							@Override
							public boolean filter(SemTrajSegment value) throws Exception {
								if(value.getActivity_semantics() != null 
										&& value.getActivity_semantics() != "" 
										&& value.getActivity_semantics().equals("Rue")) {
									return true;
									}
								return false;
								}
							}
						).within(Time.seconds(1));
		return p;
	}
			
	
	
	public static Pattern<SemTrajSegment, ?> pattern2() {
		return Pattern.<SemTrajSegment>begin("start")
		.where(new SimpleCondition<SemTrajSegment>() {
			@Override
			public boolean filter(SemTrajSegment value) throws Exception {
				if(value.getActivity_semantics() != null 
						&& value.getActivity_semantics() != "" 
						&& value.getActivity_semantics().equals("Rue")) {
					return true;
					}
				return false;
			}					
		}).followedBy("end").where(new SimpleCondition<SemTrajSegment>() {
			@Override
			public boolean filter(SemTrajSegment value) throws Exception {
				if(value.getHumidity_semantics().equals("High")) {
					return true;
				}
				return false;
			}
		}).within(Time.seconds(3));
	}
			
}
