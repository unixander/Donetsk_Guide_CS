package ua.org.unixander.donetsk_guide.map;

import java.util.ArrayList;

import ua.org.unixander.donetsk_guide.R;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ShowMapInfo extends MapActivity {
	private MapController mapController;
	private MapView mapView;
	private mapPoint[] stations;
	private PointsOverlay itemizedoverlay;
	private View popup;
	private int placeicon = -1;
	private int icon = -1, flag = -1;
	private static boolean showed = false;
	private Drawable drawable;

	@Override
	public void onCreate(Bundle savedInstanceData) {
		super.onCreate(savedInstanceData);
		setContentView(R.layout.showmapinfo);

		Intent i = this.getIntent();
		ArrayList<String> titles = i.getStringArrayListExtra("Names");
		ArrayList<String> latitudes = i.getStringArrayListExtra("Latitudes");
		ArrayList<String> longtitudes = i
				.getStringArrayListExtra("Longtitudes");

		titles.add(titles.get(titles.size() - 1));
		latitudes.add(latitudes.get(latitudes.size() - 1));
		longtitudes.add(longtitudes.get(longtitudes.size() - 1));
		placeicon = i.getIntExtra("PlaceIcon", -1);
		flag = i.getIntExtra("Route", -1);
		if (placeicon == -1) {
			placeicon = R.drawable.other;
		}
		icon = i.getIntExtra("Icon", -1);
		if (icon == -1) {
			icon = R.drawable.bus;
		}

		if (titles != null)
			stations = getPoints(titles, latitudes, longtitudes);

		mapView = (MapView) findViewById(R.id.DGMapView);
		mapView.setBuiltInZoomControls(true);
		mapView.displayZoomControls(true);
		mapView.setSatellite(true);

		mapController = mapView.getController();
		mapController.setCenter(new GeoPoint((int) (Double
				.parseDouble(longtitudes.get(0)) * 1e6), (int) (Double
				.parseDouble(latitudes.get(0)) * 1e6)));
		mapController.setZoom(14);

		popup = getLayoutInflater().inflate(R.layout.map_popup, mapView, false);
		if (icon == -1)
			drawable = this.getResources().getDrawable(R.drawable.bus);
		else
			drawable = this.getResources().getDrawable(icon);
		itemizedoverlay = new PointsOverlay(this, drawable, titles.size());
		createMarker(stations);
	}

	private mapPoint[] getPoints(ArrayList<String> titles,
			ArrayList<String> latitudes, ArrayList<String> longtitudes) {
		mapPoint[] result = new mapPoint[titles.size()];
		for (int i = 0; i < titles.size(); i++) {
			result[i] = new mapPoint();
			result[i].title = titles.get(i);
			result[i].latitude = Double.parseDouble(latitudes.get(i));
			result[i].longtitude = Double.parseDouble(longtitudes.get(i));
		}
		return result;
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	private void createMarker(mapPoint[] points) {
		for (int i = 0; i < points.length; i++) {
			GeoPoint p = new GeoPoint((int) (points[i].longtitude * 1e6),
					(int) (points[i].latitude * 1e6));
			OverlayItem overlayitem = new OverlayItem(p, points[i].title,
					points[i].title);
			itemizedoverlay.addOverlay(overlayitem);
		}
		if (itemizedoverlay.size() > 0) {
			mapView.getOverlays().add(itemizedoverlay);
		}
	}

	// Overlay for markers
	private class PointsOverlay extends ItemizedOverlay<OverlayItem> {

		private int maxNum;
		private OverlayItem overlays[];
		private int index = 0;
		private boolean full = false;
		private Context context;
		private OverlayItem previousoverlay;

		public PointsOverlay(Context context, Drawable defaultMarker, int max) {
			super(boundCenterBottom(defaultMarker));
			this.maxNum = max + 1;
			this.overlays = new OverlayItem[maxNum];
			this.context = context;
		}

		@Override
		protected OverlayItem createItem(int i) {
			OverlayItem overlayitem = overlays[i];
			Drawable marker = null;
			if (placeicon != -1)
				marker = this.context.getResources().getDrawable(placeicon);
			Drawable marker2 = this.context.getResources().getDrawable(icon);
			if (i == 0 && placeicon != -1 && flag == -1) {
				boundCenterBottom(marker);
				overlayitem.setMarker(marker);
			} else if ((i == stations.length - 2 || i == stations.length - 3)
					&& flag == 1 && placeicon != -1) {
				boundCenterBottom(marker);
				overlayitem.setMarker(marker);
			} else {
				boundCenterBottom(marker2);
				overlayitem.setMarker(marker2);
			}
			return overlayitem;
		}

		@Override
		public int size() {
			if (full) {
				return overlays.length;
			} else {
				return index;
			}

		}

		public void addOverlay(OverlayItem overlay) {
			if (previousoverlay != null) {
				if (index < maxNum) {
					overlays[index] = previousoverlay;
				} else {
					index = 0;
					full = true;
					overlays[index] = previousoverlay;
				}
				index++;
				populate();
			}
			this.previousoverlay = overlay;
		}

		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			super.draw(canvas, mapView, shadow);
		}

		protected boolean onTap(int index) {

			OverlayItem overlayItem = overlays[index];
			MapView map = (MapView) findViewById(R.id.DGMapView);
			MapView.LayoutParams mapParams = new MapView.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT,
					overlayItem.getPoint(), 0, 0,
					MapView.LayoutParams.BOTTOM_CENTER);
			TextView text = (TextView) popup.findViewById(R.id.textPopup);
			text.setText(stations[index].title);

			if (!showed) {
				map.addView(popup, mapParams);
				showed = true;
			} else {
				map.removeView(popup);
				showed = false;
			}
			return true;
		};
	}
	
	private class mapPoint{
		public String title;
		public double latitude,longtitude;
	}
}
