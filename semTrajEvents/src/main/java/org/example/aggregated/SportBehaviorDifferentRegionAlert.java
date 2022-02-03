package org.example.aggregated;

import java.util.HashSet;

public class SportBehaviorDifferentRegionAlert {
	private HashSet<Integer> participantsIds;
	private String start_time_event;
	private String end_time_event;
	
	private String hierarchy;
	
	private String event;
	
    public SportBehaviorDifferentRegionAlert(HashSet<Integer> participantsIds, String start_time_event, String end_time_event, String hierarchy, String event) {
        this.setParticipantsIds(participantsIds);
        this.start_time_event = start_time_event;
        this.end_time_event = end_time_event;
        this.hierarchy = hierarchy;
        this.setEvent(event);
    }

    public SportBehaviorDifferentRegionAlert() {
    	this(new HashSet<Integer>(), "empty start", "empty end", "empty hierarchy", "empty activity");
    }

	public HashSet<Integer> getParticipantsIds() {
		return participantsIds;
	}

	public void setParticipantsIds(HashSet<Integer> participantsIds) {
		this.participantsIds = participantsIds;
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
	
	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}
	
	@Override
    public boolean equals(Object obj) {
        if (obj instanceof SportBehaviorDifferentRegionAlert) {
        	SportBehaviorDifferentRegionAlert other = (SportBehaviorDifferentRegionAlert) obj;
            return participantsIds.equals(other.participantsIds)
            		&& start_time_event.equals(other.start_time_event) 
            		&& end_time_event.equals(other.end_time_event)
            		&& hierarchy.equals(other.hierarchy)
            		&& event.equals(other.event);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "The alert Same Activity with Different Regions is detected for participants: " + stringOfParticipantsIds()
        + " for activity: " + getEvent()
        + " at hierarchy: " + getHierarchy()
        + " starting at " + getStart_time_event() + " and ending at " + getEnd_time_event();
    }

    public String stringOfParticipantsIds() {
    	String toReturn = "[";
    	for(Integer id : participantsIds) {
    		toReturn = toReturn + Integer.toString(id) + ", ";
    	}
    	toReturn = toReturn + "]";
    	return toReturn;
    }


}
