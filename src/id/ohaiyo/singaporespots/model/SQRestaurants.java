package id.ohaiyo.singaporespots.model;

public class SQRestaurants extends SQPOI{

	private String phone;
	private String website;
	private String place_type;
	private String cuisine_type;
	private String recommended_for;
	private String opening_hours;
	private String city;
	
	public SQRestaurants(int id, String name, String address, String phone, String website, String place_type, String cuisine_type, String recommended_for, String opening_hours, String city, double latitude, double longitude, double distance){
		super(id, name, address, latitude, longitude, distance);
		this.phone = phone;
		this.website = website;
		this.place_type = place_type;
		this.cuisine_type = cuisine_type;
		this.recommended_for = recommended_for;
		this.opening_hours = opening_hours;
		this.city = city;
		this.distance = distance;
	}

	public String getPhone() {
		return phone;
	}

	public String getWebsite() {
		return website;
	}

	public String getPlace_type() {
		return place_type;
	}

	public String getCuisine_type() {
		return cuisine_type;
	}

	public String getRecommended_for() {
		return recommended_for;
	}

	public String getOpening_hours() {
		return opening_hours;
	}

	public String getCity() {
		return city;
	}
	
}
