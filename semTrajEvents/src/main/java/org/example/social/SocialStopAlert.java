package org.example.social;


public class SocialStopAlert {
	private int participantID1;
	private int participantID2;
	
	private String start_time_event;
	private String end_time_event;
	
	private String place;
	private String hierarchy;
	
    public SocialStopAlert(int participantID1, int participantID2, String start_time_event, String end_time_event, String place, String hierarchy) {
        this.participantID1 = participantID1;
        this.participantID2 = participantID2;
        this.start_time_event = start_time_event;
        this.end_time_event = end_time_event;
        this.place = place;
        this.hierarchy = hierarchy;
    }

    public SocialStopAlert() {
    	this(0, 0, "empty start", "empty end", "empty place", "empty hierarchy");
    }
    
	public int getParticipantID1() {
		return participantID1;
	}
	public void setParticipantID1(int participantID1) {
		this.participantID1 = participantID1;
	}
	
	public int getParticipantID2() {
		return participantID2;
	}
	public void setParticipantID2(int participantID2) {
		this.participantID2 = participantID2;
	}

	public String getStart_time_event() {
		return start_time_event;
	}
	public void setStart_time_event(String start_time_event) {
		this.start_time_event = start_time_event;
	}


	public String getEnd_time_event() {
		return end_time_event;
	}
	public void setEnd_time_event(String end_time_event) {
		this.end_time_event = end_time_event;
	}
    
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	
	public String getHierarchy() {
		return hierarchy;
	}
	public void setHierarchy(String hierarchy) {
		this.hierarchy = hierarchy;
	}
	
	@Override
    public boolean equals(Object obj) {
        if (obj instanceof SocialStopAlert) {
        	SocialStopAlert other = (SocialStopAlert) obj;
            return participantID1 == other.participantID1
            		&& participantID2 == other.participantID2
            		&& start_time_event.equals(other.start_time_event) 
            		&& end_time_event.equals(other.end_time_event)
            		&& place.equals(other.place)
            		&& hierarchy.equals(other.hierarchy);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "The alert stop is detected for participant1: " + getParticipantID1() 
        + " and participant2: " + getParticipantID2()
        + " at place: " + getPlace() + " at hierarchy: " + getHierarchy()
        + " starting at " + getStart_time_event() + " and ending at " + getEnd_time_event();
    }
}
