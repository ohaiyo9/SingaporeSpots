package id.ohaiyo.singaporespots.util;

import id.ohaiyo.singaporespots.DetailsActivity;
import id.ohaiyo.singaporespots.R;
import id.ohaiyo.singaporespots.model.SQATM;
import id.ohaiyo.singaporespots.model.SQBank;
import id.ohaiyo.singaporespots.model.SQRestaurants;

import org.osmdroid.bonuspack.overlays.DefaultInfoWindow;
import org.osmdroid.bonuspack.overlays.ExtendedOverlayItem;
import org.osmdroid.views.MapView;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

public class CustomInfoWindow extends DefaultInfoWindow {
	
	int curType;
	SQRestaurants selRest;
	SQBank selBank;
	SQATM selATM;
	Context ctx;

	public CustomInfoWindow(MapView mapView) {
		super(R.layout.bonuspack_bubble, mapView);

		ctx = mapView.getContext();
		Button btn = (Button) (mView.findViewById(R.id.bubble_moreinfo));
		btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(ctx, DetailsActivity.class);
				intent.putExtra("type", curType);
				if(curType == DBHelper.TYPE_RESTAURANT){
					intent.putExtra("id", selRest.getId());
					intent.putExtra("lat", (int) (selRest.getLatitude() * 1E6));
					intent.putExtra("lon", (int) (selRest.getLongitude() * 1E6));
				} else if(curType == DBHelper.TYPE_BANK){
					intent.putExtra("id", selBank.getId());
					intent.putExtra("lat", (int) (selBank.getLatitude() * 1E6));
					intent.putExtra("lon", (int) (selBank.getLongitude() * 1E6));
				} else if(curType == DBHelper.TYPE_ATM){
					intent.putExtra("id", selATM.getId());
					intent.putExtra("lat", (int) (selATM.getLatitude() * 1E6));
					intent.putExtra("lon", (int) (selATM.getLongitude() * 1E6));
				}
				ctx.startActivity(intent);
			}
		});
	}

	@Override
	public void onOpen(Object item) {
		super.onOpen(item);
		Object selItem = ((ExtendedOverlayItem)item).getRelatedObject();
		if(selItem.getClass() == SQRestaurants.class){
			selRest = (SQRestaurants) selItem;
			curType = DBHelper.TYPE_RESTAURANT;
		} else if(selItem.getClass() == SQBank.class){
			selBank = (SQBank) selItem;
			curType = DBHelper.TYPE_BANK;
		} else if(selItem.getClass() == SQATM.class){
			selATM = (SQATM) selItem;
			curType = DBHelper.TYPE_ATM;
		}

		Button moreInfo = (Button) mView.findViewById(R.id.bubble_moreinfo);
		moreInfo.setVisibility(View.VISIBLE);
	}
}
