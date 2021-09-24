package org.example.semTrajEvents;


import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.PatternSelectFunction;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.core.fs.FileSystem.WriteMode;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.example.events.*;
import org.example.individual.*;
import org.example.patternsForTest.*;
import org.example.social.SocialStop;
import org.example.social.SocialStopAlert;




public class CEPTraj {
	
	public static void main(String[] args) throws Exception{
		
		ParameterTool parameterTool = ParameterTool.fromArgs(args);
		Properties props=parameterTool.getProperties();
	    props.setProperty("auto.offset.reset", "earliest"); 
	    
		// Set up the execution environment
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();	
		
		env.enableCheckpointing(1000).
		setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
		
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
		
		//------------------Individual------------------
		//(1)
		//PatternStream<SemTrajSegment> patternStream = CEP.pattern(partitionedInput, IndividualStop.individualStop("suburb", 5));		 
		//DataStream<IndividualStopAlert> alerts = IndividualStop.individualStopAlertStream(patternStream);
		
		//(2)
		//PatternStream<SemTrajSegment> patternStream = CEP.pattern(partitionedInput, SportWithHighPollution.sportWithHighPollution());		 
		//DataStream<SportWithHighPollutionAlert> alerts = SportWithHighPollution.sportWithHighPollutionAlertStream(patternStream);
		
		//(3)
		//PatternStream<SemTrajSegment> patternStream = CEP.pattern(partitionedInput, HomeToOfficeHighPollution.homeToOfficeHighPollution(25));		 
		//DataStream<HomeToOfficeHighPollutionAlert> alerts = HomeToOfficeHighPollution.homeToOfficeHighPollutionAlertStream(patternStream);
		
		//(4)
		//PatternStream<SemTrajSegment> patternStream = CEP.pattern(partitionedInput, ArriveLeaveOfficeDifferentMode.arriveLeaveOfficeDifferentMode());		 
		//DataStream<ArriveLeaveOfficeDifferentModeAlert> alerts = ArriveLeaveOfficeDifferentMode.arriveLeaveOfficeDifferentModeAlertStream(patternStream);
		
		//(5)
		//PatternStream<SemTrajSegment> patternStream = CEP.pattern(partitionedInput, AtOfficeAfter.atOfficeAfter(20));
		//DataStream<AtOfficeAfterAlert> alerts = AtOfficeAfter.atOfficeAfterStream(patternStream, 20);
		
		//(6)
		PatternStream<SemTrajSegment> patternStream = CEP.pattern(partitionedInput, LeavingOfficeBefore.leavingOfficeBefore(17));
		DataStream<LeavingOfficeBeforeAlert> alerts = LeavingOfficeBefore.leavingOfficeBeforeStream(patternStream, 17);
		
		
		
		//------------------Aggregated------------------
		/*//(1)
		PatternStream<SemTrajSegment> patternStream = CEP.pattern(nonPartitionedInput, SocialStop.socialStop("county", 5, 2));
		DataStream<SocialStopAlert> alerts = SocialStop.socialStopAlerttStream(patternStream);
		*/
		
		
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
		
		alerts.map(v -> v.toString()).writeAsText(parameterTool.getRequired("out"), WriteMode.OVERWRITE).setParallelism(1);	
		alerts.map(v -> v.toString()).print();
		
		
        env.execute("Flink CEP semantic trajectories");
	}
	
	
	
	
	
	/*public static class CustomSelectFunction implements PatternSelectFunction<SemTrajSegment, Alert> {
		public Alert select(Map<String, List<SemTrajSegment>> pattern) throws Exception {
			return new Alert(((SemTrajSegment)pattern.get("start")).getParticipantID(),
					((SemTrajSegment)pattern.get("start")).getStart_datetime(),
					((SemTrajSegment)pattern.get("start")).getEnd_datetime());
		}
		
	}*/
	
	
}
