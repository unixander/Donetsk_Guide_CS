package ua.org.unixander.donetsk_guide.routes;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Timer;

import ua.org.unixander.donetsk_guide.R;
import ua.org.unixander.donetsk_guide.adapter.StationAdapter;
import ua.org.unixander.donetsk_guide.connection.DBConnection;
import ua.org.unixander.donetsk_guide.entity.Route;
import ua.org.unixander.donetsk_guide.entity.Station;
import ua.org.unixander.donetsk_guide.map.ShowMapInfo;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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

public class RouteInfo extends Activity {
	private Route route;
	private DBConnection db;
	private TextView title;
	private TextView cost;
	private TextView interval;
	private TextView work;
	private ImageView image;
	private ListView StationList;
	private Button button;
	private List<Station> list;
	private Context context;
	private Activity activity;
	private Dialog dialog;
	private Timer timer=new Timer();
	//TODO: add timer for connection wait
	
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		Intent i=this.getIntent();
		
		route=new Route();
		if(i.getStringExtra("RouteId") != null) route.id=Integer.parseInt(i.getStringExtra("RouteId"));
		this.setContentView(R.layout.routeinfo);
		
		activity=this;
		context=this;
		dialog=new Dialog(context);
		dialog.setContentView(R.layout.loading);
		dialog.setTitle("Please wait");
		dialog.show();
		
		new dbLoading().execute(new String());
		
		title=(TextView) findViewById(R.id.routeText);
		cost=(TextView) findViewById(R.id.costText);
		interval=(TextView) findViewById(R.id.intervalText);
		work=(TextView) findViewById(R.id.workTime);
		image=(ImageView) findViewById(R.id.imageView1);
		StationList=(ListView)findViewById(R.id.listRouteStation);
		button=(Button)findViewById(R.id.ShowRouteMap);

		this.button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(list.isEmpty()) {
					Toast.makeText(getApplicationContext(), "Sorry. There are no stations in the DB", Toast.LENGTH_LONG).show();
					return;
				}
				ArrayList<String> latitudes,longtitudes,names;
				latitudes=getLatitudesList(list);
				longtitudes=getLongtitudesList(list);
				names=getNamesList(list);
				if(latitudes.isEmpty()||longtitudes.isEmpty()||names.isEmpty()){
					Toast.makeText(getApplicationContext(), "You should mark stations to display", Toast.LENGTH_SHORT).show();
					return;
				}
				Intent StationIntent=new Intent(v.getContext(),ShowMapInfo.class);
				StationIntent.putStringArrayListExtra("Latitudes",latitudes);
				StationIntent.putStringArrayListExtra("Longtitudes",longtitudes);
				StationIntent.putStringArrayListExtra("Names", names);
				int value=-1;
				if(route.type.contains("tram")) value=R.drawable.tram;
				else if(route.type.contains("bus")) value=R.drawable.bus;
				else if(route.type.contains("trolley")) value=R.drawable.trolley;
				StationIntent.putExtra("Icon", value);
				StationIntent.putExtra("PlaceIcon", value);
        		startActivityForResult(StationIntent,0);
			}
		});
	}
	public static ArrayList<String> getNamesList(List<Station> list) {
		ArrayList<String> result=new ArrayList<String>();
		ListIterator<Station> it=list.listIterator();
		Station temp;
		do{
			temp=it.next();
			if(temp.isSelected())result.add(temp.title);
		}while(it.hasNext());
		return result;
	}
	public static ArrayList<String> getLatitudesList(List<Station> list){
		ArrayList<String> result=new ArrayList<String>();
		ListIterator<Station> it=list.listIterator();
		Station temp;
		do{
			temp=it.next();
			if(temp.isSelected())result.add(Double.toString(temp.latitude));
		}while(it.hasNext());
		return result;
	}
	public static ArrayList<String> getLongtitudesList(List<Station> list){
		ArrayList<String> result=new ArrayList<String>();
		ListIterator<Station> it=list.listIterator();
		Station temp;
		do{
			temp=it.next();
			if(temp.isSelected())result.add(Double.toString(temp.longtitude));
		}while(it.hasNext());
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
			route=db.getRouteWithId(route.id);
			list=db.getStationsFromRouteId(route.id);
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
				title.setText("Route number "+route.number);
				cost.setText("Cost: "+route.cost+" UAH");
				interval.setText("Interval: "+route.interval+" min.");
				work.setText("Works from "+route.time_begin+" till "+route.time_end);
				String s=route.type;
				if (s.startsWith("tram")) {
					image.setImageResource(R.drawable.tram);
				} else if (s.startsWith("trolley")){
					image.setImageResource(R.drawable.trolley);
				} else {
					image.setImageResource(R.drawable.bus);
				}
				if(list==null) list= new ArrayList<Station>();
				ArrayAdapter<Station> adapter = new StationAdapter(activity,
						list);
				StationList.setAdapter(adapter);
				dialog.cancel();
			} else {
				((TextView) dialog.findViewById(R.id.textView2))
						.setText(message);
			}
		}

	}
}
