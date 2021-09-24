package org.example.individual;

public class AtOfficeAfterAlert {
	private int participantID;
	
	private String start_time_event;
	private String end_time_event;
	
	private int afterTime;
	
	
    public AtOfficeAfterAlert(int participantID, String start_time_event, String end_time_event, int afterTime) {
        this.participantID = participantID;
        this.start_time_event = start_time_event;
        this.end_time_event = end_time_event;
        this.afterTime = afterTime;
    }

    public AtOfficeAfterAlert() {
    	this(0, "empty start", "empty end", 0);
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
    
	public int getAfterTime() {
		return afterTime;
	}
	public void setAfterTime(int afterTime) {
		this.afterTime = afterTime;
	}
	
	
	@Override
    public boolean equals(Object obj) {
        if (obj instanceof AtOfficeAfterAlert) {
        	AtOfficeAfterAlert other = (AtOfficeAfterAlert) obj;
            return participantID == other.participantID
            		&& start_time_event.equals(other.start_time_event) 
            		&& end_time_event.equals(other.end_time_event)
            		&& afterTime == other.afterTime;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "The participant: " + getParticipantID() 
        + " is present at the office after " + Integer.toString(getAfterTime())
        + " segment time interval is starting at " + getStart_time_event() 
        + " and ending at " + getEnd_time_event();
    }
}
