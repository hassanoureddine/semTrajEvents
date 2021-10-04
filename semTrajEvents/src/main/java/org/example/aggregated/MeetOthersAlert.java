package org.example.aggregated;

import java.util.HashSet;

public class MeetOthersAlert {
	
	private int participantId;
	private HashSet<Integer> meetWithparticipantsIds;
	private String start_time_event;
	private String end_time_event;
	private String hierarchy;
	
    public MeetOthersAlert(int participantId, HashSet<Integer> meetWithparticipantsIds, String start_time_event, String end_time_event, String hierarchy) {
    	this.setParticipantId(participantId);
        this.setMeetWithparticipantsIds(meetWithparticipantsIds);
        this.start_time_event = start_time_event;
        this.end_time_event = end_time_event;
        this.hierarchy = hierarchy;
    }

    public int getParticipantId() {
		return participantId;
	}

	public void setParticipantId(int participantId) {
		this.participantId = participantId;
	}
	
    public MeetOthersAlert() {
    	this(0, new HashSet<Integer>(), "empty start", "empty end",  "empty hierarchy");
    }

	public HashSet<Integer> getMeetWithparticipantsIds() {
		return meetWithparticipantsIds;
	}

	public void setMeetWithparticipantsIds(HashSet<Integer> meetWithparticipantsIds) {
		this.meetWithparticipantsIds = meetWithparticipantsIds;
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
	
	@Override
    public boolean equals(Object obj) {
        if (obj instanceof MeetOthersAlert) {
        	MeetOthersAlert other = (MeetOthersAlert) obj;
            return participantId == other.participantId
            		&& meetWithparticipantsIds.equals(other.meetWithparticipantsIds)
            		&& start_time_event.equals(other.start_time_event) 
            		&& end_time_event.equals(other.end_time_event)
            		&& hierarchy.equals(other.hierarchy);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "The alert Meet minimum nb of people within a time interval is detected for participant: " + getParticipantId()
        		+ " that have met participants: " + stringOfParticipantsIds()
        		+ " at hierarchy: " + getHierarchy()
        		+ " starting at " + getStart_time_event() + " and ending at " + getEnd_time_event();
    }

    public String stringOfParticipantsIds() {
    	String toReturn = "[";
    	for(Integer id : meetWithparticipantsIds) {
    		toReturn = toReturn + Integer.toString(id) + ", ";
    	}
    	toReturn = toReturn + "]";
    	return toReturn;
    }

	
}
