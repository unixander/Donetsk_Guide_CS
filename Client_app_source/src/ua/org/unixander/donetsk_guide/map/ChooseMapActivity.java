package ua.org.unixander.donetsk_guide.map;

import java.util.List;
import java.util.Locale;

import ua.org.unixander.donetsk_guide.R;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ChooseMapActivity extends MapActivity {
	// Controls in the layout
	private MapController mapController;
	private MapView mapView;
	private Button savebtn;
	private List<Overlay> listOfOverlays;
	private MapOverlay mapOverlay;

	private int icon; // icon for the map
	private double longtitude = 48.014132; // default longtitude
	private double latitude = 37.802761; // default latitude
	private GeoPoint p;

	@Override
	public void onCreate(Bundle savedInstanceData) {
		super.onCreate(savedInstanceData);
		Locale.setDefault(new Locale("en","US"));
		setContentView(R.layout.editmap);

		mapView = (MapView) findViewById(R.id.EditMapView);
		savebtn = (Button) findViewById(R.id.saveCoordinates);
		icon = R.drawable.location;
		Intent i = this.getIntent();
		double[] temp = null;
		if ((temp = i.getDoubleArrayExtra("Coordinates")) != null) {
			latitude = temp[1];
			longtitude = temp[0];
		}

		// Init mapView settings
		mapView.setBuiltInZoomControls(true);
		mapView.displayZoomControls(true);
		mapView.setSatellite(true);
		mapController = mapView.getController();

		p = new GeoPoint((int) (longtitude * 1e6), (int) (latitude * 1e6));
		mapController.setCenter(new GeoPoint((int) (longtitude * 1e6),
				(int) (latitude * 1e6)));
		mapController.setZoom(14);
		mapOverlay = new MapOverlay();
		listOfOverlays = mapView.getOverlays();
		listOfOverlays.clear();
		listOfOverlays.add(mapOverlay);

		mapView.invalidate();
		savebtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				latitude = p.getLatitudeE6() / 1e6;
				longtitude = p.getLongitudeE6() / 1e6;
				Intent data = new Intent();
				data.putExtra("Coordinates", new double[] { latitude,
						longtitude });
				if (getParent() == null) {
					setResult(Activity.RESULT_OK, data);
				} else {
					getParent().setResult(Activity.RESULT_OK, data);
				}
				finish();
			}
		});
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	/*
	 * Overlay for using with Google maps layout onTouchEvent retrieves
	 * coordinates of the selected place
	 */
	class MapOverlay extends com.google.android.maps.Overlay {

		private long lastTouchTime = -1;
		private final static long MAX_INTERVAL = 1000;

		@Override
		public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
				long when) {
			super.draw(canvas, mapView, shadow);
			Point screenPts = new Point();
			mapView.getProjection().toPixels(p, screenPts);

			Bitmap bmp = BitmapFactory.decodeResource(getResources(), icon);
			canvas.drawBitmap(bmp, screenPts.x - bmp.getWidth() / 2,
					screenPts.y - bmp.getHeight() / 2, null);
			return true;
		}

		@Override
		public boolean onTouchEvent(MotionEvent event, MapView mapView) {
			final long thisTime = System.currentTimeMillis();
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if (thisTime - lastTouchTime < MAX_INTERVAL) {
					lastTouchTime = -1;
					p = mapView.getProjection().fromPixels((int) event.getX(),
							(int) event.getY());
					mapOverlay = new MapOverlay();
					listOfOverlays = mapView.getOverlays();
					listOfOverlays.clear();
					listOfOverlays.add(mapOverlay);

					mapView.invalidate();
				} else {
					lastTouchTime = thisTime;
				}
			}
			return super.onTouchEvent(event, mapView);
		}

	}

}
