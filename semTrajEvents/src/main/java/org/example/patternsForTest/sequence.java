package org.example.patternsForTest;

import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.SimpleCondition;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.example.events.SemTrajSegment;

public class sequence {
	
	public static Pattern<SemTrajSegment, ?> arriveLeaveBureau() {
		Pattern<SemTrajSegment, ?> p = Pattern.<SemTrajSegment>begin("arriveBy")
				.where(
						new SimpleCondition<SemTrajSegment>() {
							@Override
							public boolean filter(SemTrajSegment value) throws Exception {
								if(value.getActivity_semantics() != null && value.getActivity_semantics() != ""
										&& !value.getActivity_semantics().equals("Bureau")) {
									return true;
									}
								return false;
								}
							})
				.next("middle").where(
						new SimpleCondition<SemTrajSegment>() {
							@Override
							public boolean filter(SemTrajSegment value) throws Exception {
								if(value.getActivity_semantics() != null && value.getActivity_semantics() != ""
										&& value.getActivity_semantics().equals("Bureau")) {
									return true;
									}
								return false;
								}
							})
				.followedBy("leaveBy").where(
						new SimpleCondition<SemTrajSegment>() {
							@Override
							public boolean filter(SemTrajSegment value) throws Exception {
								if(value.getActivity_semantics() != null && value.getActivity_semantics() != ""
										&& !value.getActivity_semantics().equals("Bureau")) {
									return true;
									}
								return false;
								}
							});
		return p;
	}
}
