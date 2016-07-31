package id.ohaiyo.singaporespots;

import id.ohaiyo.singaporespots.model.SQPOI;
import id.ohaiyo.singaporespots.util.ARDataSource;
import id.ohaiyo.singaporespots.util.DBHelper;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jwetherell.augmented_reality.activity.AugmentedReality;
import com.jwetherell.augmented_reality.data.ARData;
import com.jwetherell.augmented_reality.ui.Marker;
import com.jwetherell.augmented_reality.widget.VerticalTextView;

public class ARActivity extends AugmentedReality {
	
	private static Toast myToast = null;
    private static VerticalTextView text = null;
    
    private int curType;
    private int lat;
    private int lon;
    private double curRad;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        
        curType = getIntent().getExtras().getInt("type");
        lat = getIntent().getExtras().getInt("lat");
        lon = getIntent().getExtras().getInt("lon");
        curRad = getIntent().getExtras().getDouble("rad");
        
        myToast = new Toast(getApplicationContext());
        myToast.setGravity(Gravity.CENTER, 0, 0);
        text = new VerticalTextView(getApplicationContext());
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        text.setLayoutParams(params);
        text.setBackgroundResource(android.R.drawable.toast_frame);
        text.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_Small);
        text.setShadowLayer(2.75f, 0f, 0f, Color.parseColor("#BB000000"));
        myToast.setView(text);
        myToast.setDuration(Toast.LENGTH_SHORT);
        
        List<? extends SQPOI> poiList = new ArrayList<SQPOI>();
		if(curType == DBHelper.TYPE_RESTAURANT || curType == DBHelper.TYPE_CAFE){
			String type = "Restaurant";
			if(curType == DBHelper.TYPE_CAFE) type = "Cafe";
			
			poiList = LauncherActivity.dbHelper.getRestaurants(lat, lon, curRad, type);
			
		} else if(curType == DBHelper.TYPE_BANK){
			poiList = LauncherActivity.dbHelper.getBanks(lat, lon, curRad);
		} else if(curType == DBHelper.TYPE_ATM){
			poiList = LauncherActivity.dbHelper.getATMs(lat, lon, curRad);
		}
		
		ARDataSource ardata = new ARDataSource(this.getResources(), poiList);
        ARData.addMarkers(ardata.getMarkers());
		
        zoomLayout.setVisibility(LinearLayout.GONE);
        
	}
	
	@Override
    protected void markerTouched(Marker marker) {
        text.setText(marker.getName());
        myToast.show();
    }
	
	@Override
    protected void updateDataOnZoom() {
        super.updateDataOnZoom();
        ARData.setZoomProgress(myZoomBar.getProgress() / 10);
        curRad = myZoomBar.getProgress() / 10.0;
        endLabel.setText(curRad + " KM");
        
        updateData();
	}
	
	private void updateData(){
		
	}

}
