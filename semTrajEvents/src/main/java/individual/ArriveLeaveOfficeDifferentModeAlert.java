package individual;

public class ArriveLeaveOfficeDifferentModeAlert {
private int participantID;
	
	private String start_time_event;
	private String end_time_event;
	
	private String arriveMode;
	private String leaveMode;
	
    public ArriveLeaveOfficeDifferentModeAlert(int participantID, String start_time_event, String end_time_event, String arriveMode, String leaveMode) {
        this.participantID = participantID;
        this.start_time_event = start_time_event;
        this.end_time_event = end_time_event;
        this.arriveMode = arriveMode;
        this.leaveMode = leaveMode;
    }

    public ArriveLeaveOfficeDifferentModeAlert() {
    	this(0, "empty start", "empty end", "empty arriveMode", "empty leaveMode");
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
    
	public String getArriveMode() {
		return arriveMode;
	}
	public void setArriveMode(String arriveMode) {
		this.arriveMode = arriveMode;
	}
	
	public String getLeaveMode() {
		return leaveMode;
	}
	public void setLeaveMode(String leaveMode) {
		this.leaveMode = leaveMode;
	}
	
	
	@Override
    public boolean equals(Object obj) {
        if (obj instanceof ArriveLeaveOfficeDifferentModeAlert) {
        	ArriveLeaveOfficeDifferentModeAlert other = (ArriveLeaveOfficeDifferentModeAlert) obj;
            return participantID == other.participantID
            		&& start_time_event.equals(other.start_time_event) 
            		&& end_time_event.equals(other.end_time_event)
            		&& arriveMode.equals(other.arriveMode)
            		&& leaveMode.equals(other.leaveMode);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "The alert Arrive and Leave Office with Different transportation mode is detected for participant: " + getParticipantID()
        + " arriving with mode " + getArriveMode()
		+ " leaving with mode " + getLeaveMode()
        + " starting at " + getStart_time_event() 
        + " and ending at " + getEnd_time_event();
    }
}
