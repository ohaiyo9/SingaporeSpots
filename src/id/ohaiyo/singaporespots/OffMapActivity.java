package id.ohaiyo.singaporespots;

import id.ohaiyo.singaporespots.model.SQATM;
import id.ohaiyo.singaporespots.model.SQBank;
import id.ohaiyo.singaporespots.model.SQRestaurants;
import id.ohaiyo.singaporespots.util.CustomInfoWindow;
import id.ohaiyo.singaporespots.util.DBHelper;
import id.ohaiyo.singaporespots.util.VerticalSeekBar;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.overlays.ExtendedOverlayItem;
import org.osmdroid.bonuspack.overlays.ItemizedOverlayWithBubble;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MyLocationOverlay;
import org.osmdroid.views.overlay.OverlayItem.HotspotPlace;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;


@SuppressWarnings("deprecation")
public class OffMapActivity extends Activity implements OnClickListener, OnSeekBarChangeListener {
	
	private ToggleButton btnFood;
	private ToggleButton btnCafe;
	private ToggleButton btnBank;
	private ToggleButton btnATM;
	private ToggleButton btnAR;
	private ToggleButton btnMore;
	private TextView tvSeekValue;
	private VerticalSeekBar verSeekBar;
	private MapView mMapView;
	private IMapController mMapController;
	
	private int curLat = 1286816;
	private int curLon = 103854403;
	private double curRad;
	private int curType;
	
	ItemizedOverlayWithBubble<ExtendedOverlayItem> poiNodes;
	
	private MyLocationOverlay myLocOver;
	private GeoPoint curGP = new GeoPoint(curLat, curLon);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_off_map);
		
		btnFood = (ToggleButton) findViewById(R.id.btnFood);
		btnCafe = (ToggleButton) findViewById(R.id.btnCafe);
		btnBank = (ToggleButton) findViewById(R.id.btnBank);
		btnATM = (ToggleButton) findViewById(R.id.btnATM);
		btnAR = (ToggleButton) findViewById(R.id.btnAR);
		btnMore = (ToggleButton) findViewById(R.id.btnMore);
		mMapView = (MapView) findViewById(R.id.mapOffView);
		verSeekBar = (VerticalSeekBar) findViewById(R.id.verSeekBar);
		tvSeekValue = (TextView) findViewById(R.id.tvSeekValue);
		
		btnFood.setOnClickListener(this);
		btnCafe.setOnClickListener(this);
		btnBank.setOnClickListener(this);
		btnATM.setOnClickListener(this);
		btnAR.setOnClickListener(this);
		btnMore.setOnClickListener(this);
		verSeekBar.setOnSeekBarChangeListener(this);
		
		curRad = 1.0;
		curType = DBHelper.TYPE_RESTAURANT;
		btnFood.setChecked(true);
		btnFood.setEnabled(false);
		tvSeekValue.setText(""+curRad);
		
		mMapView.setTileSource(TileSourceFactory.MAPNIK);
		mMapView.setUseDataConnection(false);
		mMapView.setClickable(true);
		mMapView.setBuiltInZoomControls(true);
		mMapView.setMultiTouchControls(true);
		mMapController = mMapView.getController();
		mMapController.setZoom(12);
		mMapController.setCenter(curGP);
		
		myLocOver = new CustomMyLocOverlay(this, mMapView);
		myLocOver.enableMyLocation();
		myLocOver.enableFollowLocation();
		//myLocOver.enableCompass();
		//myLocOver.setLocationUpdateMinTime(5000);
		//myLocOver.setLocationUpdateMinDistance(10);
		myLocOver.setDrawAccuracyEnabled(true);
		myLocOver.runOnFirstFix(new Runnable() {
			
			@Override
			public void run() {
				mMapController.animateTo(myLocOver.getMyLocation());
			}
		});
		mMapView.getOverlays().add(myLocOver);
		
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	  // ignore orientation/keyboard change
	  super.onConfigurationChanged(newConfig);
	}
	
	public void resetButtons(){
		btnFood.setChecked(false);
		btnFood.setEnabled(true);
		btnCafe.setChecked(false);
		btnCafe.setEnabled(true);
		btnBank.setChecked(false);
		btnBank.setEnabled(true);
		btnATM.setChecked(false);
		btnATM.setEnabled(true);
		btnAR.setChecked(false);
		btnAR.setEnabled(true);
		btnMore.setChecked(false);
		btnMore.setEnabled(true);
	}
	
	class CustomMyLocOverlay extends MyLocationOverlay{
		
		int lat;
		int lon;
		Context ctx;
		
		public CustomMyLocOverlay(Context context, MapView mapView){
			super(context, mapView);
			ctx = context;
		}
		
		@Override
		public void onLocationChanged(Location location){
			lat = (int)(location.getLatitude() * 1E6);
			lon = (int)(location.getLongitude() * 1E6);
			
			curLat = lat;
			curLon = lon;

			ArrayList<ExtendedOverlayItem> poiOvers = new ArrayList<ExtendedOverlayItem>();

			mMapView.getOverlays().remove(poiNodes);
			poiNodes = new ItemizedOverlayWithBubble<ExtendedOverlayItem>(
					ctx, poiOvers, mMapView, new CustomInfoWindow(mMapView));

			if(curType == DBHelper.TYPE_RESTAURANT || curType == DBHelper.TYPE_CAFE){
				String type = "Restaurant";
				if(curType == DBHelper.TYPE_CAFE) type = "Cafe";

				List<SQRestaurants> resList = LauncherActivity.dbHelper.getRestaurants(lat, lon, curRad, type);
				for(int i = 0; i < resList.size(); i++){
					SQRestaurants res = resList.get(i);
					
					GeoPoint resGP = new GeoPoint(res.getLatitude(), res.getLongitude());
					ExtendedOverlayItem poiMarker = new ExtendedOverlayItem(res.getName(), res.getAddress(),
							resGP, ctx);
					poiMarker.setMarkerHotspot(HotspotPlace.CENTER);
					Drawable curMark = getResources().getDrawable(R.drawable.marker_orange);
					poiMarker.setMarker(curMark);
					poiMarker.setRelatedObject(res);
					poiNodes.addItem(poiMarker);
				}
			} else if(curType == DBHelper.TYPE_BANK){
				List<SQBank> bankList = LauncherActivity.dbHelper.getBanks(lat, lon, curRad);
				for(int i = 0; i < bankList.size(); i++){
					SQBank bank = bankList.get(i);
					
					GeoPoint bankGP = new GeoPoint(bank.getLatitude(), bank.getLongitude());
					ExtendedOverlayItem poiMarker = new ExtendedOverlayItem(bank.getName(), bank.getAddress(),
							bankGP, ctx);
					poiMarker.setMarkerHotspot(HotspotPlace.CENTER);
					Drawable curMark = getResources().getDrawable(R.drawable.marker_orange);
					poiMarker.setMarker(curMark);
					poiMarker.setRelatedObject(bank);
					poiNodes.addItem(poiMarker);
				}
			} else if(curType == DBHelper.TYPE_ATM){
				List<SQATM> atmList = LauncherActivity.dbHelper.getATMs(lat, lon, curRad);
				for(int i = 0; i < atmList.size(); i++){
					SQATM atm = atmList.get(i);
					
					GeoPoint bankGP = new GeoPoint(atm.getLatitude(), atm.getLongitude());
					ExtendedOverlayItem poiMarker = new ExtendedOverlayItem(atm.getName(), atm.getAddress(),
							bankGP, ctx);
					poiMarker.setMarkerHotspot(HotspotPlace.CENTER);
					Drawable curMark = getResources().getDrawable(R.drawable.marker_orange);
					poiMarker.setMarker(curMark);
					poiMarker.setRelatedObject(atm);
					poiNodes.addItem(poiMarker);
				}
			}
			
			mMapView.getOverlays().add(poiNodes);
			super.onLocationChanged(location);
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		curRad = ((double)progress/10);
		tvSeekValue.setText(""+curRad);
		if(myLocOver.getLastFix() != null){
			myLocOver.onLocationChanged(myLocOver.getLastFix());
		}
		
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btnCafe:
			curType = DBHelper.TYPE_CAFE;
			resetButtons();
			btnCafe.setEnabled(false);
			btnCafe.setChecked(true);
			break;
		case R.id.btnFood:
			curType = DBHelper.TYPE_RESTAURANT;
			resetButtons();
			btnFood.setEnabled(false);
			btnFood.setChecked(true);
			break;
		case R.id.btnBank:
			curType = DBHelper.TYPE_BANK;
			resetButtons();
			btnBank.setEnabled(false);
			btnBank.setChecked(true);
			break;
		case R.id.btnATM:
			curType = DBHelper.TYPE_ATM;
			resetButtons();
			btnATM.setEnabled(false);
			btnATM.setChecked(true);
			break;
		case R.id.btnMore:
			btnMore.setChecked(false);
			Intent intent = new Intent(getBaseContext(), MoreActivity.class);
			startActivityForResult(intent, 0);
			break;
		case R.id.btnAR:
			btnAR.setChecked(false);
			Intent arIntent = new Intent(this, ARActivity.class);
			arIntent.putExtra("type", curType);
			arIntent.putExtra("lat", curLat);
			arIntent.putExtra("lon", curLon);
			arIntent.putExtra("rad", curRad);
			startActivity(arIntent);
			break;

		default:
			break;
		}
		if(myLocOver.getLastFix() != null){
			myLocOver.onLocationChanged(myLocOver.getLastFix());
		}
	}
	
}
