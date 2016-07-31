package id.ohaiyo.singaporespots;

import id.ohaiyo.singaporespots.model.SQATM;
import id.ohaiyo.singaporespots.model.SQBank;
import id.ohaiyo.singaporespots.model.SQRestaurants;
import id.ohaiyo.singaporespots.util.DBHelper;

import java.util.ArrayList;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class DetailsActivity extends Activity {
	
	private TextView detTitle;
	private TextView detName;
	private TextView detAddress;
	private TextView detPhone;
	private TextView detOpening;
	private TextView detType;
	private TextView detWeb;
	private TextView tvPhone;
	private TextView tvWeb;
	private MapView mMapView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_details);
		
		int type = getIntent().getExtras().getInt("type");
		int id = getIntent().getExtras().getInt("id");
		int lat = getIntent().getExtras().getInt("lat");
		int lon = getIntent().getExtras().getInt("lon");
		
		GeoPoint gp = new GeoPoint(lat, lon);
		
		SQRestaurants rest = new SQRestaurants(-1, "", "", "", "", "", "", "", "", "", 0, 0, 0);
		SQBank bank = new SQBank(-1, "", "", "", "", "", 0, 0, 0);
		SQATM atm = new SQATM(-1, "", "", 0, 0, 0);
		if(type == DBHelper.TYPE_RESTAURANT){
			rest = LauncherActivity.dbHelper.getRestByID(id);
		} else if(type == DBHelper.TYPE_BANK){
			bank = LauncherActivity.dbHelper.getBankByID(id);
		} else if(type == DBHelper.TYPE_ATM){
			atm  = LauncherActivity.dbHelper.getATMByID(id);
		}
		
		detTitle = (TextView) findViewById(R.id.detailTitle);
		detName = (TextView) findViewById(R.id.detailName);
		detAddress = (TextView) findViewById(R.id.detailAddress);
		detPhone = (TextView) findViewById(R.id.detailPhone);
		detOpening = (TextView) findViewById(R.id.detailOpening);
		detType = (TextView) findViewById(R.id.detailType);
		detWeb = (TextView) findViewById(R.id.detailWeb);
		tvPhone = (TextView) findViewById(R.id.tvPhone);
		tvWeb = (TextView) findViewById(R.id.tvWeb);
		mMapView = (MapView) findViewById(R.id.mapPreview);
		
		detName.setEnabled(false);
		detAddress.setEnabled(false);
		detPhone.setEnabled(false);
		detOpening.setEnabled(false);
		
		
		if(rest.getId() > -1){
			detTitle.setText(rest.getName());
			detName.setText(rest.getName());
			detAddress.setText(rest.getAddress());
			detPhone.setText(rest.getPhone());
			detOpening.setText(rest.getOpening_hours());
			detType.setText(rest.getCuisine_type());
			detWeb.setText(rest.getWebsite());
		} else if(bank.getId() > -1){
			detTitle.setText(bank.getName());
			detName.setText(bank.getName());
			detAddress.setText(bank.getAddress());
			detPhone.setText(bank.getPhone());
			detOpening.setText(bank.getOperating_hours());
			detType.setVisibility(View.INVISIBLE);
			tvWeb.setVisibility(View.INVISIBLE);
			detWeb.setVisibility(View.INVISIBLE);
		} else if(atm.getId() > -1){
			detTitle.setText(atm.getName());
			detName.setText(atm.getName());
			detAddress.setText(atm.getAddress());
			detOpening.setText("");
			detPhone.setText("");
			detType.setVisibility(View.INVISIBLE);
			tvPhone.setVisibility(View.INVISIBLE);
			tvWeb.setVisibility(View.INVISIBLE);
			detWeb.setVisibility(View.INVISIBLE);
		}

		mMapView.setTileSource(TileSourceFactory.MAPNIK);
		mMapView.setBuiltInZoomControls(false);
		mMapView.setMultiTouchControls(false);
		mMapView.setUseDataConnection(false);
		IMapController mMapController = mMapView.getController();
		mMapController.setZoom(16);
		mMapController.setCenter(gp);
		
		
		DefaultResourceProxyImpl mResourceProxy = new DefaultResourceProxyImpl(getApplicationContext());
		ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
        OverlayItem item = new OverlayItem(detTitle.getText().toString(), detAddress.getText().toString(), gp);
        item.setMarker(getResources().getDrawable(R.drawable.marker_orange));
        items.add(item);
		ItemizedIconOverlay<OverlayItem> mItems = new ItemizedIconOverlay<OverlayItem>(items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
            @Override
            public boolean onItemSingleTapUp(final int index,
                    final OverlayItem item) {
                return false;
            }
            @Override
            public boolean onItemLongPress(final int index,
                    final OverlayItem item) {
                return false;
            }
        }, mResourceProxy);
		mMapView.getOverlays().add(mItems);
	}
}
