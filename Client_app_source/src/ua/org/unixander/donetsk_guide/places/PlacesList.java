package ua.org.unixander.donetsk_guide.places;

import java.util.ArrayList;
import java.util.List;

import ua.org.unixander.donetsk_guide.R;
import ua.org.unixander.donetsk_guide.adapter.PlacesListAdapter;
import ua.org.unixander.donetsk_guide.connection.DBConnection;
import ua.org.unixander.donetsk_guide.entity.Place;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

public class PlacesList extends ListActivity {
	private DBConnection db;
	private String[] names;
	private String[] types;
	private List<Place> placesList = null;
	private Context context;
	private ListActivity current;
	private Dialog dialog;
	private Spinner spinner;
	private ArrayAdapter<String> adapter,typesArrayAdapter;
	private String namePlace="",typePlace="";
	private List<String> typeslist;
	private int[] ids;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		
		current=this;
		context=this;
		dialog = new Dialog(context);
		dialog.setContentView(R.layout.loading);
		dialog.setTitle("Please wait");
		dialog.show();
		this.setContentView(R.layout.placeslist);
		
		spinner = (Spinner) findViewById(R.id.placeslistType);
		
		new dbLoading().execute(new String());
		
		Button SearchBtn = new Button(this);
		SearchBtn = (Button) findViewById(R.id.SearchPlace);
		SearchBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				EditText search = (EditText) findViewById(R.id.SearchPlaceInput);
				String s = search.getText().toString();
				String type = spinner.getSelectedItem().toString();
				if (type == "Show All")
					type = "";
				namePlace=s;
				typePlace=type;
				new dbLoading().execute(new String());
			}
		});
	}

	private int getIcon(String type) {
		return PlacesListAdapter.GetPlaceIcon(type);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent PlaceIntent = new Intent(v.getContext(), PlaceInfo.class);
		PlaceIntent.putExtra("PlaceID", Integer.toString(ids[(int) id]));
		PlaceIntent.putExtra("PlaceIcon", getIcon(types[(int) id]));
		startActivity(PlaceIntent);
	};
	
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
			}
			if(typePlace.isEmpty()&&namePlace.isEmpty()){
				placesList = db.getPlacesList();
				message = "Server is inaccessible";
			} else {
				placesList=db.searchPlace(namePlace, typePlace);
				message = "Nothing found";
			}
			int num=0;
			if(placesList!=null&&placesList.size()!=0){
				num= placesList.size();
			}
			names = new String[num];
			ids = new int[num];
			types = new String[num];
			for (int i = 0; i < num; i++) {
				names[i] = placesList.get(i).name;
				types[i] = placesList.get(i).type;
				ids[i] = placesList.get(i).id;
			}
			typeslist = db.getPlacesTypes();
			if(typeslist==null){
				typeslist=new ArrayList<String>();
			}
			typeslist.add(0, "Show All");
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
				adapter = new PlacesListAdapter(current, names,
						placesList);
				current.setListAdapter(adapter);
				typesArrayAdapter = new ArrayAdapter<String>(current,
						android.R.layout.simple_spinner_item, typeslist);
				typesArrayAdapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinner.setAdapter(typesArrayAdapter);
				dialog.cancel();
			} else {
				dialog.setTitle("Something went wrong");
				((TextView)dialog.findViewById(R.id.textView1)).setText("Error");
				((TextView) dialog.findViewById(R.id.textView2))
						.setText(message);
			}
		}

	}
}
