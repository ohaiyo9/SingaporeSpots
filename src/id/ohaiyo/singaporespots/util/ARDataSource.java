package id.ohaiyo.singaporespots.util;

import id.ohaiyo.singaporespots.R;
import id.ohaiyo.singaporespots.model.SQPOI;

import java.util.ArrayList;
import java.util.List;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import com.jwetherell.augmented_reality.data.DataSource;
import com.jwetherell.augmented_reality.ui.IconMarker;
import com.jwetherell.augmented_reality.ui.Marker;

public class ARDataSource extends DataSource {
	
    private List<Marker> cachedMarkers = new ArrayList<Marker>();
    private List<? extends SQPOI> poiList = new ArrayList<SQPOI>();
    private static Bitmap icon = null;
    
    public ARDataSource(Resources res, List<? extends SQPOI> poiList){
    	if (res == null) throw new NullPointerException();

        createIcon(res);
        this.poiList = poiList;
    }
	
	protected void createIcon(Resources res) {
        if (res == null) throw new NullPointerException();

        icon = BitmapFactory.decodeResource(res, R.drawable.marker_orange);
    }

	@Override
	public List<Marker> getMarkers() {
		for(int i = 0; i < poiList.size(); i++){
			SQPOI poi = poiList.get(i);
			
			Marker marker = new IconMarker(poi.getName(), poi.getLatitude(), poi.getLongitude(), 0, Color.DKGRAY, icon);
			cachedMarkers.add(marker);
		}
		return cachedMarkers;
	}

}
