package org.example.patternsForTest;


public class ArriveLeaveBureauAlert {

	private int participantID;
	private String start_time_event;
	private String end_time_event;
	
	private String arriveActivity;
	private String activity;
	private String leaveActivity;
	
	
    public ArriveLeaveBureauAlert(int participantID, String start_time_event, String end_time_event,
    		String arriveActivity, String activity, String leaveActivity) {
        this.participantID = participantID;
        this.start_time_event = start_time_event;
        this.end_time_event = end_time_event;
        
        this.arriveActivity = arriveActivity;
        this.activity = activity;
        this.leaveActivity = leaveActivity;
    }

    public ArriveLeaveBureauAlert() {
    	this(0, "empty start", "empty end", "empty", "empty", "empty");
    }
    
    public String getArriveActivity() {
		return arriveActivity;
	}
	public void setArriveActivity(String arriveActivity) {
		this.arriveActivity = arriveActivity;
	}
    
	public String getActivity() {
		return activity;
	}
	public void setActivity(String activity) {
		this.activity = activity;
	}
    
	public String getLeaveActivity() {
		return leaveActivity;
	}
	public void setLeaveActivity(String leaveActivity) {
		this.leaveActivity = leaveActivity;
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
        if (obj instanceof ArriveLeaveBureauAlert) {
        	ArriveLeaveBureauAlert other = (ArriveLeaveBureauAlert) obj;
            return participantID == other.participantID 
            		&& start_time_event.equals(other.start_time_event) 
            		&& end_time_event.equals(other.end_time_event)
            		&& arriveActivity.equals(other.arriveActivity)
            		&& activity.equals(other.activity)
            		&& leaveActivity.equals(other.leaveActivity);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "The 'ArriveLeftBureau' alert is detected for participant: " 
        		+ getParticipantID() 
        		+ " Arrive Activity: " + getArriveActivity() + " at " + getStart_time_event() 
        		+ " Activity: " + getActivity()
        		+ " Left Activity: " + getLeaveActivity() + " at " + getEnd_time_event();
    }
    
}
