package id.ohaiyo.singaporespots;

import id.ohaiyo.singaporespots.util.DBHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;

public class LauncherActivity extends Activity {
	
	public static final String PREFS_NAME = "SingaporeSpotsPrefsFile";
	
	public static SharedPreferences prefs;
	public static DBHelper dbHelper;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		dbHelper = new DBHelper(this);
		dbHelper.open();
        
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        
        File path = Environment.getExternalStorageDirectory();
		File sgMap = new File(path, "osmdroid/singapore.sqlite");
		if(!sgMap.exists()){
			try{
				int count;
				InputStream input = getResources().getAssets().open("singapore.sqlite");
				File osmPath = new File(path, "osmdroid/");
				if(!osmPath.exists()) osmPath.mkdir();
				OutputStream output = new FileOutputStream(sgMap);
				byte data[] = new byte[1024];
				
				while ((count = input.read(data)) != -1) {
					output.write(data, 0, count);
				}
				output.flush();
	
				output.close();
				input.close();
	
			} catch(IOException e){
				e.printStackTrace();
			}
		}
		
		showMap();
    }
	
	public void showMap(){
		finish();
		
	    Intent offMapIntent = new Intent(getBaseContext(), OffMapActivity.class);
		startActivity(offMapIntent);
    }

}
