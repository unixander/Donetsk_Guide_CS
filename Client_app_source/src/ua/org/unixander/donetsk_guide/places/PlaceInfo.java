package ua.org.unixander.donetsk_guide.places;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import ua.org.unixander.donetsk_guide.R;
import ua.org.unixander.donetsk_guide.adapter.StationAdapter;
import ua.org.unixander.donetsk_guide.connection.DBConnection;
import ua.org.unixander.donetsk_guide.entity.Place;
import ua.org.unixander.donetsk_guide.entity.Route;
import ua.org.unixander.donetsk_guide.entity.Station;
import ua.org.unixander.donetsk_guide.map.ShowMapInfo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PlaceInfo extends Activity {
	private Place place;
	private DBConnection db;
	private int icon=0;
	private List<Station> Stationslist;
	private ArrayAdapter<Station> adapter;
	private TextView name,type,description;
	private Button btn,map;
	private ListView list;
	private Dialog dialog;
	private Context context;
	private Activity activity;
	
	
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		context=this;
		activity=this;
		
		dialog = new Dialog(this);
		dialog.setContentView(R.layout.loading);
		dialog.setTitle("Please wait");
		dialog.show();
		
		new dbLoading().execute(new String());
		
		Intent i=this.getIntent();
		place=new Place();
		if(i.getStringExtra("PlaceID") != null) place.id=Integer.parseInt(i.getStringExtra("PlaceID"));
		if(i.getIntExtra("PlaceIcon",-1)!=-1) this.icon=i.getIntExtra("PlaceIcon", 0);
		this.setContentView(R.layout.placeinfo);
		name=(TextView) findViewById(R.id.textPlaceName);
		type=(TextView) findViewById(R.id.textPlaceType);
		description=(TextView) findViewById(R.id.textPlaceDescription);
		btn=(Button) findViewById(R.id.btnShowImage);
		map=(Button) findViewById(R.id.btnShowPlaceMap);
		list=(ListView) findViewById(R.id.plcsStationsList);
		
		btn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				Context mContext = v.getContext();
				if(place.image==null){
					Toast.makeText(mContext, "There is no image for "+place.name, Toast.LENGTH_LONG).show();
				} else {
					Dialog dialog = new Dialog(mContext);
	
					dialog.setContentView(R.layout.showimage);
					dialog.setTitle(place.name);
					ImageView img=(ImageView)dialog.findViewById(R.id.imageView1);
					img.setImageBitmap(BitmapFactory.decodeByteArray(place.image, 0, place.image.length));
					dialog.show();
					dialog.setCanceledOnTouchOutside(true);
				}
			}
		});
		
		map.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent StationIntent=new Intent(v.getContext(),ShowMapInfo.class);
				if(place.latitude==-1) 
					Toast.makeText(v.getContext(), "There are no coordinates for this place", Toast.LENGTH_SHORT).show();
				StationIntent.putStringArrayListExtra("Latitudes",getLatitudesList(Stationslist));
				StationIntent.putStringArrayListExtra("Longtitudes",getLongtitudesList(Stationslist));
				StationIntent.putStringArrayListExtra("Names", getNamesList(Stationslist));
				StationIntent.putExtra("PlaceIcon", icon);
				StationIntent.putExtra("Icon", R.drawable.bus);
        		startActivityForResult(StationIntent,0);
			}
		});
	}

	/**
	 * Get array of name of the stations
	 * @param list
	 * @return
	 */
	private ArrayList<String> getNamesList(List<Station> list) {
		ArrayList<String> result=new ArrayList<String>();
		ListIterator<Station> it=list.listIterator();
		String add;
		result.add(place.name);
		if(!it.hasNext()) return result;
		Station temp;
		do{
			temp=it.next();
			add=getRouteStation(temp.id);
			if(add.length()>0) temp.title+=add;
			if(temp.isSelected())result.add(temp.title);
		}while(it.hasNext());
		return result;
	}
	/**
	 * Get array of latitudes of the stations
	 * @param list
	 * @return
	 */
	private ArrayList<String> getLatitudesList(List<Station> list){
		ArrayList<String> result=new ArrayList<String>();
		result.add(Double.toString(place.latitude));
		ListIterator<Station> it=list.listIterator();
		if(!it.hasNext()) return result;
		Station temp;
		do{
			temp=it.next();
			if(temp.isSelected())result.add(Double.toString(temp.latitude));
		}while(it.hasNext());
		return result;
	}
	/**
	 * Get array pf longtitudes of the stations
	 * @param list
	 * @return
	 */
	private ArrayList<String> getLongtitudesList(List<Station> list){
		ArrayList<String> result=new ArrayList<String>();
		ListIterator<Station> it=list.listIterator();
		result.add(Double.toString(place.longtitude));
		if(!it.hasNext()) return result;
		Station temp;
		do{
			temp=it.next();
			if(temp.isSelected())result.add(Double.toString(temp.longtitude));
		}while(it.hasNext());
		return result;
	}
	/**
	 * Get information of routes related to the station
	 * @param stationid
	 * @return
	 */
	private String getRouteStation(int stationid){
		String bus="",trolleybus="",tram="",type="",number="";
		String result="";
		List<Route> routes=db.getRoutesFromStationId(stationid);
		if(!routes.isEmpty()){
			for(int i=0;i<routes.size();i++){
				type=routes.get(i).type;
				number=routes.get(i).number;
				if(type.contentEquals("bus")) bus+=number+"; ";
				else if(type.contains("roll")) trolleybus+=number+"; ";
				else if(type=="tram") tram+=number+"; ";
			}
		} else return result;

		if(bus!="") result+="\r\nBus: "+bus;
		if(trolleybus!="") result+="\r\nTrolleybus: "+trolleybus;
		if(tram!="")result+="\r\nTram: "+tram;
		return result;
	}
	
	private class dbLoading extends AsyncTask<String, Void, String> {
		private int error = -1;
		private boolean conn = true;
		private String message = "Server is inaccessible";

		@Override
		protected String doInBackground(String... params) {
			db = new DBConnection(context);
			conn = db.open();
			if (!conn) {
				error = 1;
				return db.getError();
			};
			place=db.getPlaceWithId(place.id);
			if(place==null){
				place=new Place();
			}
			
			Stationslist=db.getStationsFromPlaceId(place.id);
			if(Stationslist==null){
				Stationslist=new ArrayList<Station>();
			}
			
			db.close();
			return "Success";
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (error < 0) {
				name.setText(place.name);
				type.setText(place.type);
				description.setText(place.description);
				adapter = new StationAdapter(activity,
						Stationslist);
				list.setAdapter(adapter);
				dialog.cancel();
			} else {
				((TextView) dialog.findViewById(R.id.textView2))
						.setText(message);
			}
		}

	}
}
