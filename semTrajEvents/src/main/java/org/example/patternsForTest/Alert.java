package org.example.patternsForTest;


public class Alert {

	private int participantID;
	private String start_time_event;
	private String end_time_event;
	
	
    public Alert(int participantID, String start_time_event, String end_time_event) {
        this.participantID = participantID;
        this.start_time_event = start_time_event;
        this.end_time_event = end_time_event;
    }

    public Alert() {
    	this(0, "empty start", "empty end");
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
    
	@Override
    public boolean equals(Object obj) {
        if (obj instanceof Alert) {
        	Alert other = (Alert) obj;
            return participantID == other.participantID 
            		&& start_time_event.equals(other.start_time_event) 
            		&& end_time_event.equals(other.end_time_event);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "The alert is detected for participant: " + getParticipantID() + " starting at " + getStart_time_event() + " and ending at " + getEnd_time_event();
    }
    
}
