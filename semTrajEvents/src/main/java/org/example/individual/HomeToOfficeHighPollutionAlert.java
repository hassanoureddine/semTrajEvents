package org.example.individual;

public class HomeToOfficeHighPollutionAlert {
	private int participantID;
	
	private String start_time_event;
	private String end_time_event;
	
	private String place;
	private String hierarchy;
	
    public HomeToOfficeHighPollutionAlert(int participantID, String start_time_event, String end_time_event, String place) {
        this.participantID = participantID;
        this.start_time_event = start_time_event;
        this.end_time_event = end_time_event;
        this.place = place;
    }

    public HomeToOfficeHighPollutionAlert() {
    	this(0, "empty start", "empty end", "empty place");
    }
    
	public int getParticipantID() {
		return participantID;
	}
	public void setParticipantID(int participantID) {
		this.participantID = participantID;
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
	
	
	@Override
    public boolean equals(Object obj) {
        if (obj instanceof HomeToOfficeHighPollutionAlert) {
        	HomeToOfficeHighPollutionAlert other = (HomeToOfficeHighPollutionAlert) obj;
            return participantID == other.participantID
            		&& start_time_event.equals(other.start_time_event) 
            		&& end_time_event.equals(other.end_time_event)
            		&& place.equals(other.place);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "The alert Home to Office High Pollution is detected for participant: " + getParticipantID()
        + " at place: " + getPlace()
        + " starting at " + getStart_time_event() 
        + " and ending at " + getEnd_time_event();
    }
}
