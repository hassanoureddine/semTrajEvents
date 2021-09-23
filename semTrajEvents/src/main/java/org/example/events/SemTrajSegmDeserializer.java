package org.example.events;

import java.io.IOException;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.util.serialization.KeyedDeserializationSchema;
import com.google.gson.Gson;


public class SemTrajSegmDeserializer implements KeyedDeserializationSchema <SemTrajSegment> {
	private Gson gson;
	
	@Override
	public TypeInformation<SemTrajSegment> getProducedType() {
		return TypeInformation.of(SemTrajSegment.class);
	}

	@Override
	public boolean isEndOfStream(SemTrajSegment nextElement) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public SemTrajSegment deserialize(byte[] messageKey, byte[] message, String topic, int partition, long offset)
			throws IOException {
		if (gson == null) {
            gson = new Gson();
        }
		
		String strMessage = new String(message);
		strMessage = strMessage.replace("\\", "");
		strMessage = strMessage.substring(1, strMessage.length() - 1);
		//System.out.println(strMessage);
		
		SemTrajSegment seg = gson.fromJson(strMessage, SemTrajSegment.class);
		return seg;
	}
	
}
