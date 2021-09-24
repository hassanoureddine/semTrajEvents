package org.example.events;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.annotations.SerializedName;

public class SemTrajSegment {
	
	@SerializedName("participant_id")
	private int participantID;
	
	@SerializedName("start_datetime")
	private String start_datetime;
	
	@SerializedName("end_datetime")
	private String end_datetime;
	
	@SerializedName("place_id")
	private int place_id;
	
	@SerializedName("osm_type")
	private String osm_type;
    
	@SerializedName("osm_id")
	private long osm_id;
	
	@SerializedName("place_rank")
    private int place_rank;
    
	@SerializedName("category")
    private String category;
    
	@SerializedName("type")
    private String type;
    
	@SerializedName("importance")
    private float importance;
    
	@SerializedName("addresstype")
    private String addresstype;
    
	@SerializedName("name")
    private String name;
    
	@SerializedName("display_name")
    private String display_name;
	
	@SerializedName("house_number")
    private String house_number;
    
	@SerializedName("road")
    private String road;
    
	@SerializedName("neighbourhood")
    private String neighbourhood;
    
	@SerializedName("suburb")
    private String suburb;
    
	@SerializedName("town")
    private String town;
    
	@SerializedName("county")
    private String county;
    
	@SerializedName("state")
    private String state;
    
	@SerializedName("country")
    private String country;
    
	@SerializedName("postcode")
    private int postcode;
    
	@SerializedName("country_code")
    private String country_code;
    
	@SerializedName("bbox")
    private String bbox;
    
	@SerializedName("wkt")
    private String wkt;
    
	@SerializedName("partition_id")
    private int partition_id;
    
	@SerializedName("partition_name")
    private String partition_name;
    
	@SerializedName("partition_geom")
    private String partition_geom;
    
	@SerializedName("floor_id")
    private int floor_id;
    
	@SerializedName("floor_name")
    private String floor_name;
    
	@SerializedName("building_id") 				
    private int building_id;
    
	@SerializedName("state_bbox")
    private String state_bbox;
    
	@SerializedName("county_bbox")
    private String county_bbox;
    
	@SerializedName("city_bbox")
    private String city_bbox;
    
	@SerializedName("street_bbox")
    private String street_bbox;
    
	@SerializedName("NO2_semantics")
    private String NO2_semantics;
    
	@SerializedName("PM10_semantics")
    private String PM10_semantics;
    
	@SerializedName("PM25_semantics")
    private String PM25_semantics; //PM2.5
    
	@SerializedName("PM1_semantics")
    private String PM1_semantics; //PM1.0
    
	@SerializedName("BC_semantics")
    private String BC_semantics;
    
	@SerializedName("temperature_semantics")
    private String temperature_semantics;
    
	@SerializedName("humidity_semantics")
    private String humidity_semantics;
    
	@SerializedName("activity_semantics")
    private String activity_semantics;
    
	@SerializedName("event_semantics")
    private String event_semantics;

	public int getParticipantID() {
		return participantID;
	}

	public String getStart_datetime() {
		return start_datetime;
	}

	public String getEnd_datetime() {
		return end_datetime;
	}

	public int getPlace_id() {
		return place_id;
	}

	public String getOsm_type() {
		return osm_type;
	}

	public long getOsm_id() {
		return osm_id;
	}

	public int getPlace_rank() {
		return place_rank;
	}

	public String getCategory() {
		return category;
	}

	public String getType() {
		return type;
	}

	public float getImportance() {
		return importance;
	}

	public String getAddresstype() {
		return addresstype;
	}

	public String getName() {
		return name;
	}

	public String getDisplay_name() {
		return display_name;
	}

	public String getHouse_number() {
		return house_number;
	}

	public String getRoad() {
		return road;
	}

	public String getNeighbourhood() {
		return neighbourhood;
	}

	public String getSuburb() {
		return suburb;
	}

	public String getTown() {
		return town;
	}

	public String getCounty() {
		return county;
	}

	public String getState() {
		return state;
	}

	public String getCountry() {
		return country;
	}

	public int getPostcode() {
		return postcode;
	}

	public String getCountry_code() {
		return country_code;
	}

	public String getBbox() {
		return bbox;
	}

	public String getWkt() {
		return wkt;
	}

	public int getPartition_id() {
		return partition_id;
	}

	public String getPartition_name() {
		return partition_name;
	}

	public String getPartition_geom() {
		return partition_geom;
	}

	public int getFloor_id() {
		return floor_id;
	}

	public String getFloor_name() {
		return floor_name;
	}

	public int getBuilding_id() {
		return building_id;
	}

	public String getState_bbox() {
		return state_bbox;
	}

	public String getCounty_bbox() {
		return county_bbox;
	}

	public String getCity_bbox() {
		return city_bbox;
	}

	public String getStreet_bbox() {
		return street_bbox;
	}

	public String getNO2_semantics() {
		return NO2_semantics;
	}

	public String getPM10_semantics() {
		return PM10_semantics;
	}

	public String getPM25_semantics() {
		return PM25_semantics;
	}

	public String getPM1_semantics() {
		return PM1_semantics;
	}

	public String getBC_semantics() {
		return BC_semantics;
	}

	public String getTemperature_semantics() {
		return temperature_semantics;
	}

	public String getHumidity_semantics() {
		return humidity_semantics;
	}

	public String getActivity_semantics() {
		return activity_semantics;
	}

	public String getEvent_semantics() {
		return event_semantics;
	}
	
	public void setParticipantID(int participantID) {
		this.participantID = participantID;
	}

	public void setStart_datetime(String start_datetime) {
		this.start_datetime = start_datetime;
	}

	public void setEnd_datetime(String end_datetime) {
		this.end_datetime = end_datetime;
	}

	public void setPlace_id(int place_id) {
		this.place_id = place_id;
	}

	public void setOsm_type(String osm_type) {
		this.osm_type = osm_type;
	}

	public void setOsm_id(long osm_id) {
		this.osm_id = osm_id;
	}

	public void setPlace_rank(int place_rank) {
		this.place_rank = place_rank;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setImportance(float f) {
		this.importance = f;
	}

	public void setAddresstype(String addresstype) {
		this.addresstype = addresstype;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDisplay_name(String display_name) {
		this.display_name = display_name;
	}

	public void setHouse_number(String house_number) {
		this.house_number = house_number;
	}

	public void setRoad(String road) {
		this.road = road;
	}

	public void setNeighbourhood(String neighbourhood) {
		this.neighbourhood = neighbourhood;
	}

	public void setSuburb(String suburb) {
		this.suburb = suburb;
	}

	public void setTown(String town) {
		this.town = town;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public void setPostcode(int postcode) {
		this.postcode = postcode;
	}

	public void setCountry_code(String country_code) {
		this.country_code = country_code;
	}

	public void setBbox(String bbox) {
		this.bbox = bbox;
	}

	public void setWkt(String wkt) {
		this.wkt = wkt;
	}

	public void setPartition_id(int partition_id) {
		this.partition_id = partition_id;
	}

	public void setPartition_name(String partition_name) {
		this.partition_name = partition_name;
	}

	public void setPartition_geom(String partition_geom) {
		this.partition_geom = partition_geom;
	}

	public void setFloor_id(int floor_id) {
		this.floor_id = floor_id;
	}

	public void setFloor_name(String floor_name) {
		this.floor_name = floor_name;
	}

	public void setBuilding_id(int building_id) {
		this.building_id = building_id;
	}

	public void setState_bbox(String state_bbox) {
		this.state_bbox = state_bbox;
	}

	public void setCounty_bbox(String county_bbox) {
		this.county_bbox = county_bbox;
	}

	public void setCity_bbox(String city_bbox) {
		this.city_bbox = city_bbox;
	}

	public void setStreet_bbox(String street_bbox) {
		this.street_bbox = street_bbox;
	}

	public void setNO2_semantics(String nO2_semantics) {
		NO2_semantics = nO2_semantics;
	}

	public void setPM10_semantics(String pM10_semantics) {
		PM10_semantics = pM10_semantics;
	}

	public void setPM25_semantics(String pM25_semantics) {
		PM25_semantics = pM25_semantics;
	}

	public void setPM1_semantics(String pM1_semantics) {
		PM1_semantics = pM1_semantics;
	}

	public void setBC_semantics(String bC_semantics) {
		BC_semantics = bC_semantics;
	}

	public void setTemperature_semantics(String temperature_semantics) {
		this.temperature_semantics = temperature_semantics;
	}

	public void setHumidity_semantics(String humidity_semantics) {
		this.humidity_semantics = humidity_semantics;
	}

	public void setActivity_semantics(String activity_semantics) {
		this.activity_semantics = activity_semantics;
	}

	public void setEvent_semantics(String event_semantics) {
		this.event_semantics = event_semantics;
	}

	public String toString(){
		String message = new String("This is a stream for participant: " + Integer.toString(this.getParticipantID()));
		message = message + " start_datetime: " + this.getStart_datetime() + " end_datetime: " + this.getEnd_datetime();
		message = message + " Activity: " + this.getActivity_semantics();
		message = message + " Humidity: " + this.getHumidity_semantics();
        return message;
	}
	
	public long getWatermarkTimestamp() {
		
		DateTimeFormatter formatDateTime = DateTimeFormatter.ISO_DATE_TIME;
		LocalDateTime localDateTime = LocalDateTime.from(formatDateTime.parse(this.getStart_datetime().replace(' ', 'T')));
		Timestamp ts = Timestamp.valueOf(localDateTime);
		//System.out.println(String.valueOf(ts.getTime()));
		
		return ts.getTime();
	}
	
	public String getPlaceAccordingToHierarchy(String hierarchy) {
		//name, display_name, road, suburb, town, county, state, country, partition(room), floor, building
		
		switch(hierarchy) {
		case "name":
			return this.getName();
		case "display_name":
			return this.getDisplay_name();
		case "road":
			return this.getRoad();
		case "suburb":
			return this.getSuburb();
		case "town":
			return this.getTown();
		case "county":
			return this.getCounty();
		case "state":
			return this.getState();
		case "country":
			return this.getCountry();
		case "partition":
			return this.getPartition_name();
		case "floor":
			return this.getFloor_name();
		case "building":
			return Integer.toString(this.getBuilding_id());
		}
		return null;
	}
}
