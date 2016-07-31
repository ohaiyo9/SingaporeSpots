package id.ohaiyo.singaporespots.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import id.ohaiyo.singaporespots.model.SQATM;
import id.ohaiyo.singaporespots.model.SQBank;
import id.ohaiyo.singaporespots.model.SQRestaurants;

public class DBHelper extends SQLiteAssetHelper {

	public static final int TYPE_POI = 1001;
	public static final int TYPE_RESTAURANT = 1002;
	public static final int TYPE_CAFE = 1003;
	public static final int TYPE_BANK = 1004;
	public static final int TYPE_ATM = 1005;
	
	private static final String DATABASE_NAME = "pois";
	private static final String TABLE_RESTAURANT = "restaurants";
	private static final String TABLE_BANK = "bank_branch";
	private static final String TABLE_ATM = "bank_atm";
	private static final int DATABASE_VERSION = 1;
	
	private SQLiteDatabase db;
	
	public DBHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public void open(){
		db = getReadableDatabase();
	}
	
	public List<SQRestaurants> getRestaurants(int lat, int lon, double radius, String type){
		double curLat = lat / 1E6;
		double curLon = lon / 1E6;
		ArrayList<SQRestaurants> resList = new ArrayList<SQRestaurants>();
		
		String query = "SELECT *,(((latitude - "+curLat+
				") * (latitude - "+curLat+")) + (longitude - ("
				+curLon+")) * (longitude - ("+curLon+
				"))) * (110 * 110) AS dist FROM "+TABLE_RESTAURANT+" WHERE dist <= "
				+radius+" AND place_type LIKE '"+type+"'";
		Cursor cursor = db.rawQuery(query, null);
		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			int id = cursor.getInt(cursor.getColumnIndex("_id"));
			String name = cursor.getString(cursor.getColumnIndex("name"));
			String address = cursor.getString(cursor.getColumnIndex("address"));
			String phone = cursor.getString(cursor.getColumnIndex("phone"));
			String website = cursor.getString(cursor.getColumnIndex("website"));
			String place_type = cursor.getString(cursor.getColumnIndex("place_type"));
			String cuisine_type = cursor.getString(cursor.getColumnIndex("cuisine_type"));
			String recommended_for = cursor.getString(cursor.getColumnIndex("recommended_for"));
			String opening_hours = cursor.getString(cursor.getColumnIndex("opening_hours"));
			String city = cursor.getString(cursor.getColumnIndex("city"));
			double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
			double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
			double distance = cursor.getDouble(cursor.getColumnIndex("dist"));
			
			SQRestaurants restaurant = new SQRestaurants(id, name, address, phone, website, place_type, cuisine_type, recommended_for, opening_hours, city, latitude, longitude, distance);
			resList.add(restaurant);
			
			cursor.moveToNext();
		}
		
		cursor.close();
		
		return resList;
	}
	
	public List<SQBank> getBanks(int lat, int lon, double radius){
		double curLat = lat / 1E6;
		double curLon = lon / 1E6;
		
		ArrayList<SQBank> bankList = new ArrayList<SQBank>();
		
		String query = "SELECT *,(((latitude - "+curLat+
				") * (latitude - "+curLat+")) + (longitude - ("
				+curLon+")) * (longitude - ("+curLon+
				"))) * (110 * 110) AS dist FROM "+TABLE_BANK+" WHERE dist <= "
				+radius;
		Cursor cursor = db.rawQuery(query, null);
		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			int id = cursor.getInt(cursor.getColumnIndex("_id"));
			String name = cursor.getString(cursor.getColumnIndex("name"));
			String address = cursor.getString(cursor.getColumnIndex("address"));
			String operating_hours = cursor.getString(cursor.getColumnIndex("operating_hours"));
			String phone = cursor.getString(cursor.getColumnIndex("telephone"));
			String fax = cursor.getString(cursor.getColumnIndex("fax"));
			double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
			double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
			double dist = cursor.getDouble(cursor.getColumnIndex("dist"));
			
			SQBank bank = new SQBank(id, name, address, operating_hours, phone, fax, latitude, longitude, dist);
			bankList.add(bank);
			
			cursor.moveToNext();
		}
		
		cursor.close();
		
		return bankList;
	}
	
	public List<SQATM> getATMs(int lat, int lon, double radius){
		double curLat = lat / 1E6;
		double curLon = lon / 1E6;
		
		ArrayList<SQATM> atmList = new ArrayList<SQATM>();
		
		String query = "SELECT *,(((latitude - "+curLat+
				") * (latitude - "+curLat+")) + (longitude - ("
				+curLon+")) * (longitude - ("+curLon+
				"))) * (110 * 110) AS dist FROM "+TABLE_ATM+" WHERE dist <= "
				+radius;
		Cursor cursor = db.rawQuery(query, null);
		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			int id = cursor.getInt(cursor.getColumnIndex("_id"));
			String name = cursor.getString(cursor.getColumnIndex("name"));
			String address = cursor.getString(cursor.getColumnIndex("address"));
			double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
			double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
			double dist = cursor.getDouble(cursor.getColumnIndex("dist"));
			
			SQATM atm = new SQATM(id, name, address, latitude, longitude, dist);
			atmList.add(atm);
			
			cursor.moveToNext();
		}
		
		cursor.close();
		
		return atmList;
	}
	
	public SQRestaurants getRestByID(int id){
		String query = "SELECT * FROM "+TABLE_RESTAURANT+" WHERE _id = "+id;
		
		Cursor cursor = db.rawQuery(query, null);
		if(cursor != null){
			cursor.moveToFirst();
			String name = cursor.getString(cursor.getColumnIndex("name"));
			String address = cursor.getString(cursor.getColumnIndex("address"));
			String phone = cursor.getString(cursor.getColumnIndex("phone"));
			String website = cursor.getString(cursor.getColumnIndex("website"));
			String place_type = cursor.getString(cursor.getColumnIndex("place_type"));
			String cuisine_type = cursor.getString(cursor.getColumnIndex("cuisine_type"));
			String recommended_for = cursor.getString(cursor.getColumnIndex("recommended_for"));
			String opening_hours = cursor.getString(cursor.getColumnIndex("opening_hours"));
			String city = cursor.getString(cursor.getColumnIndex("city"));
			double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
			double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
			
			SQRestaurants restaurant = new SQRestaurants(id, name, address, phone, website, place_type, cuisine_type, recommended_for, opening_hours, city, latitude, longitude, 0);
			return restaurant;
			
		} else{
			return new SQRestaurants(-1, "", "", "", "", "", "", "", "", "", 0, 0, 0);
		}
	}
	
	public SQBank getBankByID(int id){
		String query = "SELECT * FROM "+TABLE_BANK+" WHERE _id = "+id;
		
		Cursor cursor = db.rawQuery(query, null);
		if(cursor != null){
			cursor.moveToFirst();
			String name = cursor.getString(cursor.getColumnIndex("name"));
			String address = cursor.getString(cursor.getColumnIndex("address"));
			String operating_hours = cursor.getString(cursor.getColumnIndex("operating_hours"));
			String phone = cursor.getString(cursor.getColumnIndex("telephone"));
			String fax = cursor.getString(cursor.getColumnIndex("fax"));
			double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
			double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
			
			SQBank bank = new SQBank(id, name, address, operating_hours, phone, fax, latitude, longitude, 0);
			return bank;
			
		} else{
			return new SQBank(-1, "", "", "", "", "", 0, 0, 0);
		}
	}
	
	public SQATM getATMByID(int id){
		String query = "SELECT * FROM "+TABLE_ATM+" WHERE _id = "+id;
		
		Cursor cursor = db.rawQuery(query, null);
		if(cursor != null){
			cursor.moveToFirst();
			String name = cursor.getString(cursor.getColumnIndex("name"));
			String address = cursor.getString(cursor.getColumnIndex("address"));
			double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
			double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
			
			SQATM atm = new SQATM(id, name, address, latitude, longitude, 0);
			return atm;
			
		} else{
			return new SQATM(-1, "", "", 0, 0, 0);
		}
	}

}
