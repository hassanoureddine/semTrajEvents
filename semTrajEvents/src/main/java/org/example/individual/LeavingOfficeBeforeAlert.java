package org.example.individual;

public class LeavingOfficeBeforeAlert {
	private int participantID;
	
	private String start_time_event;
	private String end_time_event;
	
	private int beforeTime;
	
	
    public LeavingOfficeBeforeAlert(int participantID, String start_time_event, String end_time_event, int beforeTime) {
        this.participantID = participantID;
        this.start_time_event = start_time_event;
        this.end_time_event = end_time_event;
        this.beforeTime = beforeTime;
    }

    public LeavingOfficeBeforeAlert() {
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
    
	public int getBeforeTime() {
		return beforeTime;
	}
	public void setBeforeTime(int beforeTime) {
		this.beforeTime = beforeTime;
	}
	
	
	@Override
    public boolean equals(Object obj) {
        if (obj instanceof LeavingOfficeBeforeAlert) {
        	LeavingOfficeBeforeAlert other = (LeavingOfficeBeforeAlert) obj;
            return participantID == other.participantID
            		&& start_time_event.equals(other.start_time_event) 
            		&& end_time_event.equals(other.end_time_event)
            		&& beforeTime == other.beforeTime;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "The participant: " + getParticipantID() 
        + " left the office before " + Integer.toString(getBeforeTime())
        + " segment time interval is starting at " + getStart_time_event() 
        + " and ending at " + getEnd_time_event();
    }
}
