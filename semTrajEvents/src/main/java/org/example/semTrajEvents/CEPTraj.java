package org.example.semTrajEvents;


import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.PatternSelectFunction;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.core.fs.FileSystem.WriteMode;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.example.events.*;
import org.example.individual.*;
import org.example.metrics.CountingMap;
import org.example.metrics.MyMapper;
import org.example.aggregated.*;
import org.example.patternsForTest.*;




public class CEPTraj {
	
	public static void main(String[] args) throws Exception{
		
		ParameterTool parameterTool = ParameterTool.fromArgs(args);
		Properties props=parameterTool.getProperties();
		//props.setProperty("auto.offset.reset", "earliest"); 
		props.setProperty("auto.offset.reset", "latest"); 
		props.setProperty("flink.partition-discovery.interval-millis", "500");
	    //parameterTool.getRequired("topic")
		// Set up the execution environment
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();	
		//env.setParallelism(8);
		//env.enableCheckpointing(1000).setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
		
        // Input stream of monitoring events
		DataStream<SemTrajSegment> messageStream = env
				.addSource(new FlinkKafkaConsumer<>(
						parameterTool.getRequired("topic"), 
						new SemTrajSegmDeserializer(),
						props)).
				assignTimestampsAndWatermarks(new SemTrajWatermarks());

		
		DataStream<SemTrajSegment> partitionedInput = messageStream.keyBy(new KeySelector<SemTrajSegment, Integer>(){
			@Override
			public Integer getKey(SemTrajSegment value) throws Exception {
				return value.getParticipantID();
			}
		});		
		
		DataStream<SemTrajSegment> nonPartitionedInput = messageStream;
		
		//partitionedInput.map(v -> v.toString()).print();
		//nonPartitionedInput.map(v -> v.toString()).print();
		
		
		//nonPartitionedInput.map(new MyMapper()).writeAsText("perf.txt", WriteMode.OVERWRITE);
		//nonPartitionedInput.map(new CountingMap()).writeAsText("perf.txt", WriteMode.OVERWRITE);
		env.getConfig().setLatencyTrackingInterval(1000L);
		
		
		
		//------------------Individual------------------
		//IE1 (1)
		PatternStream<SemTrajSegment> patternStreamIE1 = CEP.pattern(partitionedInput, IndividualStop.individualStop("suburb", 2));		 
		DataStream<IndividualStopAlert> alertsIE1 = IndividualStop.individualStopAlertStream(patternStreamIE1);
		
		//IE2 (2)
		PatternStream<SemTrajSegment> patternStreamIE2 = CEP.pattern(partitionedInput, SportWithHighPollution.sportWithHighPollution());		 
		DataStream<SportWithHighPollutionAlert> alertsIE2 = SportWithHighPollution.sportWithHighPollutionAlertStream(patternStreamIE2);
		
		//IE3 (3)
		PatternStream<SemTrajSegment> patternStreamIE3 = CEP.pattern(partitionedInput, HomeToOfficeHighPollution.homeToOfficeHighPollution(3));		 
		DataStream<HomeToOfficeHighPollutionAlert> alertsIE3 = HomeToOfficeHighPollution.homeToOfficeHighPollutionAlertStream(patternStreamIE3);
		
		//(4)
		//PatternStream<SemTrajSegment> patternStream = CEP.pattern(partitionedInput, ArriveLeaveOfficeDifferentMode.arriveLeaveOfficeDifferentMode());		 
		//DataStream<ArriveLeaveOfficeDifferentModeAlert> alerts = ArriveLeaveOfficeDifferentMode.arriveLeaveOfficeDifferentModeAlertStream(patternStreamIE4);
		
		//IE4 (5)
		PatternStream<SemTrajSegment> patternStreamIE4 = CEP.pattern(partitionedInput, AtOfficeAfter.atOfficeAfter(20));
		DataStream<AtOfficeAfterAlert> alertsIE4 = AtOfficeAfter.atOfficeAfterStream(patternStreamIE4, 20);
		
		//(6)
		//PatternStream<SemTrajSegment> patternStream = CEP.pattern(partitionedInput, LeavingOfficeBefore.leavingOfficeBefore(17));
		//DataStream<LeavingOfficeBeforeAlert> alerts = LeavingOfficeBefore.leavingOfficeBeforeStream(patternStream, 17);
		
		
		
		//------------------Aggregated------------------
		//AE1 (1)
		PatternStream<SemTrajSegment> patternStreamAE1 = CEP.pattern(nonPartitionedInput, AggegatedStop.aggregatedStop("road", 1, 2));
		DataStream<AggregatedStopAlert> alertsAE1 = AggegatedStop.aggregatedStopAlertStream(patternStreamAE1);
		
		//(2)
		
		
		//(3)
		//PatternStream<SemTrajSegment> patternStream = CEP.pattern(nonPartitionedInput, PlacesWithMin.placesWithMin("town", 2));
		//DataStream<PlacesWithMinAlert> alerts = PlacesWithMin.placesWithMinAlertStream(patternStream);
		
		//(4)
		
		
		//AE2 (5)
		PatternStream<SemTrajSegment> patternStreamAE2 = CEP.pattern(partitionedInput, HomeToOfficeMeet.homeToOfficeMeet(1, "road"));
		DataStream<HomeToOfficeMeetAlert> alertsAE2 = HomeToOfficeMeet.homeToOfficeMeetAlertStream(patternStreamAE2);
		
		
		//(6)
		//PatternStream<SemTrajSegment> patternStream = CEP.pattern(nonPartitionedInput, SameActivityDifferentRegion.sameActivityDifferentRegion("town"));
		//DataStream<SameActivityDifferentRegionAlert> alerts = SameActivityDifferentRegion.sameActivityDifferentRegionAlertStream(patternStream);	
		//AE3 (6)
		PatternStream<SemTrajSegment> patternStreamAE3 = CEP.pattern(partitionedInput, SportBehaviorDifferentRegion.sportBehaviorDifferentRegion("town"));
		DataStream<SportBehaviorDifferentRegionAlert> alertsAE3 = SportBehaviorDifferentRegion.sportBehaviorDifferentRegionAlertStream(patternStreamAE3);
		
		
		//AE4 (7)
		PatternStream<SemTrajSegment> patternStreamAE4 = CEP.pattern(nonPartitionedInput, MeetOthers.meetOthers(2, "town", 4000));
		DataStream<MeetOthersAlert> alertsAE4 = MeetOthers.meetOthersAlertStream(patternStreamAE4);
		
		
		/*PatternStream<SemTrajSegment> patternStream = CEP.pattern(partitionedInput, sequence.arriveLeaveBureau());	
		DataStream<ArriveLeaveBureauAlert> alerts = patternStream.select(new PatternSelectFunction<SemTrajSegment, ArriveLeaveBureauAlert>() {
			@Override
			public ArriveLeaveBureauAlert select(Map<String, List<SemTrajSegment>> pattern) throws Exception {
				return new ArriveLeaveBureauAlert(
						((SemTrajSegment)pattern.get("arriveBy").get(0)).getParticipantID(),
						((SemTrajSegment)pattern.get("arriveBy").get(0)).getStart_datetime(),
						((SemTrajSegment)pattern.get("leaveBy").get(0)).getEnd_datetime(),
						((SemTrajSegment)pattern.get("arriveBy").get(0)).getActivity_semantics(),
						((SemTrajSegment)pattern.get("middle").get(0)).getActivity_semantics(),
						((SemTrajSegment)pattern.get("leaveBy").get(0)).getActivity_semantics());
			}
		});*/
		
		/*DataStream<Alert> alerts = patternStream.select(new PatternSelectFunction<SemTrajSegment, Alert>() {
			@Override
			public Alert select(Map<String, List<SemTrajSegment>> pattern) throws Exception {
				return new Alert(((SemTrajSegment)pattern.get("start").get(0)).getParticipantID(),
						((SemTrajSegment)pattern.get("start").get(0)).getStart_datetime(),
						((SemTrajSegment)pattern.get("start").get(0)).getEnd_datetime());
			}
		});*/
		
		
		
		
		//-----------------------------------------------------------------------------------------
		//patternStream.select(new CustomSelectFunction()).writeAsText(parameterTool.getRequired("out"), WriteMode.OVERWRITE);
		
		//alertsQuery1.map(v -> v.toString()).writeAsText("outQuery1.txt", WriteMode.OVERWRITE);	
		//alertsQuery1.map(v -> v.toString()).print();
		
		//alertsQuery2.map(v -> v.toString()).writeAsText("outQuery2.txt", WriteMode.OVERWRITE);	
		//alertsQuery2.map(v -> v.toString()).print();
		
		//alertsQuery3.map(v -> v.toString()).writeAsText("outQuery3.txt", WriteMode.OVERWRITE);	
		//alertsQuery3.map(v -> v.toString()).print();
		
		
		//alerts1.map(v -> v.toString()).writeAsText(parameterTool.getRequired("out"), WriteMode.OVERWRITE).setParallelism(1);	
		//alerts1.map(v -> v.toString()).print();
		
		
		//alertsIE1.map(v -> v.toString()).print();
		//alertsIE2.map(v -> v.toString()).print();
		//alertsIE3.map(v -> v.toString()).print();
		//alertsIE4.map(v -> v.toString()).print();
		
		//alertsAE1.map(v -> v.toString()).print();
		//alertsAE2.map(v -> v.toString()).print();
		//alertsAE3.map(v -> v.toString()).print();
		//alertsAE4.map(v -> v.toString()).print();
		
		env.execute("Flink CEP semantic trajectories");
		//JobExecutionResult result  = env.execute("Flink CEP semantic trajectories");
		//System.out.println("The job took " + result.getNetRuntime(TimeUnit.SECONDS) + " to execute");
	}
	
	
	
	
	
	/*public static class CustomSelectFunction implements PatternSelectFunction<SemTrajSegment, Alert> {
		public Alert select(Map<String, List<SemTrajSegment>> pattern) throws Exception {
			return new Alert(((SemTrajSegment)pattern.get("start")).getParticipantID(),
					((SemTrajSegment)pattern.get("start")).getStart_datetime(),
					((SemTrajSegment)pattern.get("start")).getEnd_datetime());
		}
		
	}*/
	
	
}
