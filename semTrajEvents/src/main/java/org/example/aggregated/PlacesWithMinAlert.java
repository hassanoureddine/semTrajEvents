package org.example.aggregated;

import java.util.HashSet;

public class PlacesWithMinAlert {
	private HashSet<Integer> participantsIds;
	private String start_time_event;
	private String end_time_event;
	private String place;
	private String hierarchy;
	
    public PlacesWithMinAlert(HashSet<Integer> participantsIds, String start_time_event, String end_time_event, String place, String hierarchy) {
        this.setParticipantsIds(participantsIds);
        this.start_time_event = start_time_event;
        this.end_time_event = end_time_event;
        this.place = place;
        this.hierarchy = hierarchy;
    }

    public PlacesWithMinAlert() {
    	this(new HashSet<Integer>(), "empty start", "empty end", "empty place", "empty hierarchy");
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
    
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	
	public String getHierarchy() {
		return hierarchy;
	}
	public void setHierarchy(String hierarchy) {
		this.hierarchy = hierarchy;
	}
	
	@Override
    public boolean equals(Object obj) {
        if (obj instanceof PlacesWithMinAlert) {
        	PlacesWithMinAlert other = (PlacesWithMinAlert) obj;
            return participantsIds.equals(other.participantsIds)
            		&& start_time_event.equals(other.start_time_event) 
            		&& end_time_event.equals(other.end_time_event)
            		&& place.equals(other.place)
            		&& hierarchy.equals(other.hierarchy);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "The alert places with minimum number of participants is detected for participants: " + stringOfParticipantsIds()
        + " at place: " + getPlace() + " at hierarchy: " + getHierarchy()
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
