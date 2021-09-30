package org.example.aggregated;

import java.util.HashSet;

public class SameActivityDifferentRegionAlert {
	private HashSet<Integer> participantsIds;
	private String start_time_event;
	private String end_time_event;
	
	private String hierarchy;
	
	private String activity;
	
    public SameActivityDifferentRegionAlert(HashSet<Integer> participantsIds, String start_time_event, String end_time_event, String hierarchy, String activity) {
        this.setParticipantsIds(participantsIds);
        this.start_time_event = start_time_event;
        this.end_time_event = end_time_event;
        this.hierarchy = hierarchy;
        this.setActivity(activity);
    }

    public SameActivityDifferentRegionAlert() {
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
	
	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}
	
	@Override
    public boolean equals(Object obj) {
        if (obj instanceof SameActivityDifferentRegionAlert) {
        	SameActivityDifferentRegionAlert other = (SameActivityDifferentRegionAlert) obj;
            return participantsIds.equals(other.participantsIds)
            		&& start_time_event.equals(other.start_time_event) 
            		&& end_time_event.equals(other.end_time_event)
            		&& hierarchy.equals(other.hierarchy)
            		&& activity.equals(other.activity);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "The alert Same Activity with Different Regions is detected for participants: " + stringOfParticipantsIds()
        + " for activity: " + getActivity()
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
