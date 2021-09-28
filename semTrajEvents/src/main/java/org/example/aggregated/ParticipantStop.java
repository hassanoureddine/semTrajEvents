package org.example.aggregated;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ParticipantStop {
	private int participantID;
	private String place;
	private String start_time_stop;
	private String end_time_stop;
	
	
	public ParticipantStop(int participantID, String place, String start_time_stop, String end_time_stop) {
		this.participantID = participantID;
        this.place = place;
        this.start_time_stop = start_time_stop;
        this.end_time_stop = end_time_stop;
	}
	
	public int getParticipantID() {
		return participantID;
	}
	public void setParticipantID(int participantID) {
		this.participantID = participantID;
	}
	
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	public String getStart_time_stop() {
		return start_time_stop;
	}
	public void setStart_time_event(String start_time_stop) {
		this.start_time_stop = start_time_stop;
	}
	public String getEnd_time_stop() {
		return end_time_stop;
	}
	public void setEnd_time_stop(String end_time_stop) {
		this.end_time_stop = end_time_stop;
	}
	
	@Override
    public String toString() {
		return "Participant stop for: " + Integer.toString(getParticipantID())
		+ " at place: " + getPlace() + " at interval : [" + getStart_time_stop() + ", " + getEnd_time_stop() + "]";
	}
	
	
	public long OverlappedMinutesWith(ParticipantStop obj) {
		DateTimeFormatter formatDateTime = DateTimeFormatter.ISO_DATE_TIME;
		LocalDateTime localDateTimeS1 = LocalDateTime.from(formatDateTime.parse(this.start_time_stop.replace(' ', 'T')));
		Timestamp s1 = Timestamp.valueOf(localDateTimeS1);
		LocalDateTime localDateTimE1 = LocalDateTime.from(formatDateTime.parse(this.end_time_stop.replace(' ', 'T')));
		Timestamp e1 = Timestamp.valueOf(localDateTimE1);
		
		LocalDateTime localDateTimeS2 = LocalDateTime.from(formatDateTime.parse(obj.start_time_stop.replace(' ', 'T')));
		Timestamp s2 = Timestamp.valueOf(localDateTimeS2);
		LocalDateTime localDateTimE2 = LocalDateTime.from(formatDateTime.parse(obj.end_time_stop.replace(' ', 'T')));
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
	
	
	public long OverlappedSecondsWith(ParticipantStop obj) {
		DateTimeFormatter formatDateTime = DateTimeFormatter.ISO_DATE_TIME;
		LocalDateTime localDateTimeS1 = LocalDateTime.from(formatDateTime.parse(this.start_time_stop.replace(' ', 'T')));
		Timestamp s1 = Timestamp.valueOf(localDateTimeS1);
		LocalDateTime localDateTimE1 = LocalDateTime.from(formatDateTime.parse(this.end_time_stop.replace(' ', 'T')));
		Timestamp e1 = Timestamp.valueOf(localDateTimE1);
		
		LocalDateTime localDateTimeS2 = LocalDateTime.from(formatDateTime.parse(obj.start_time_stop.replace(' ', 'T')));
		Timestamp s2 = Timestamp.valueOf(localDateTimeS2);
		LocalDateTime localDateTimE2 = LocalDateTime.from(formatDateTime.parse(obj.end_time_stop.replace(' ', 'T')));
		Timestamp e2 = Timestamp.valueOf(localDateTimE2);
		
		
		Timestamp overlapStart = MaxTimestamp(s1, s2);
		Timestamp overlapEnd = MinTimestamp(e1, e2);
		
		
		if((s1.before(e2) && s2.before(e1)) || (s2.before(e1) && s1.before(e2))) {
			long diffMs = overlapEnd.getTime() - overlapStart.getTime();
			long diffSec = diffMs / 1000;
			long min = diffSec / 60;
			return diffSec;
		}else {
			return 0;
		}
	}
	
	private static Timestamp MaxTimestamp(Timestamp a, Timestamp b) {
		return a.compareTo(b) > 0? a : b;
	}
	
	private static Timestamp MinTimestamp(Timestamp a, Timestamp b) {
		return a.compareTo(b) > 0? b : a;
	}
	
	
}
