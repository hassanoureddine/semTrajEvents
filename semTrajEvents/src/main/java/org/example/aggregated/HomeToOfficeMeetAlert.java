package org.example.aggregated;

public class HomeToOfficeMeetAlert {
	private int participantID;
	private int meetWithParticipantID;
	
	private String start_time_event;
	private String end_time_event;
	
	private String atPlace;
	
	private String hierarchy;
	
	private long duration;
	
    public HomeToOfficeMeetAlert(int participantID, int meetWithParticipantID, String start_time_event, String end_time_event, String atPlace, long duration, String hierarchy) {
        this.setParticipantID(participantID);
        this.setMeetWithParticipantID(meetWithParticipantID);
        this.start_time_event = start_time_event;
        this.end_time_event = end_time_event;
        this.setAtPlace(atPlace);
        this.setDuration(duration);
        this.hierarchy = hierarchy;
    }

    public HomeToOfficeMeetAlert() {
    	this(0, 0, "empty start", "empty end", "empty place", 0 ,"empty hierarchy");
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
    
	
	public String getHierarchy() {
		return hierarchy;
	}
	public void setHierarchy(String hierarchy) {
		this.hierarchy = hierarchy;
	}
	
	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}
	
	public int getParticipantID() {
		return participantID;
	}

	public void setParticipantID(int participantID) {
		this.participantID = participantID;
	}

	public int getMeetWithParticipantID() {
		return meetWithParticipantID;
	}

	public void setMeetWithParticipantID(int meetWithParticipantID) {
		this.meetWithParticipantID = meetWithParticipantID;
	}

	public String getAtPlace() {
		return atPlace;
	}

	public void setAtPlace(String atPlace) {
		this.atPlace = atPlace;
	}
	
	@Override
    public boolean equals(Object obj) {
        if (obj instanceof HomeToOfficeMeetAlert) {
        	HomeToOfficeMeetAlert other = (HomeToOfficeMeetAlert) obj;
            return participantID == other.participantID
            		&& meetWithParticipantID == other.meetWithParticipantID
            		&& start_time_event.equals(other.start_time_event) 
            		&& end_time_event.equals(other.end_time_event)
            		&& atPlace.equals(other.atPlace)
            		&& hierarchy.equals(other.hierarchy);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "The alert Home To Office Meet is detected for participant: " + getParticipantID() 
        + ". Have met participant: " + getMeetWithParticipantID()
        + " at place: " + getAtPlace() + " at hierarchy: " + getHierarchy() + " with duration = " + Long.toString(duration) + " secs"
        + " starting at " + getStart_time_event() + " and ending at " + getEnd_time_event();
    }

	

}
