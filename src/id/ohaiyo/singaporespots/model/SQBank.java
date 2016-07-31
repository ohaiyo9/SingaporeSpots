package id.ohaiyo.singaporespots.model;

public class SQBank extends SQPOI{

	private String operating_hours;
	private String phone;
	private String fax;
	
	public SQBank(int id, String name, String address, String operating_hours, String phone, String fax, double latitude, double longitude, double dist){
		super(id, name, address, latitude, longitude, dist);
		this.operating_hours = operating_hours;
		this.phone = phone;
		this.fax = fax;
		this.distance = dist;
	}

	public String getOperating_hours() {
		return operating_hours;
	}

	public String getPhone() {
		return phone;
	}

	public String getFax() {
		return fax;
	}
	
}
