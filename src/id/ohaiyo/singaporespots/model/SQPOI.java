package id.ohaiyo.singaporespots.model;

public class SQPOI {
	
	protected int id;
	protected String name;
	protected String address;
	protected double latitude;
	protected double longitude;
	protected double distance;
	
	public SQPOI(int id, String name, String address, double latitude, double longitude, double dist){
		this.id = id;
		this.name = name;
		this.address = address;
		this.latitude = latitude;
		this.longitude = longitude;
		this.distance = dist;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public double getDistance() {
		return distance;
	}
	
	
}
