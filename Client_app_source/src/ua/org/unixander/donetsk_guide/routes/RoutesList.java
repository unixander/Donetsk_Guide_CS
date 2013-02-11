package ua.org.unixander.donetsk_guide.routes;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ua.org.unixander.donetsk_guide.R;
import ua.org.unixander.donetsk_guide.adapter.RoutesListAdapter;
import ua.org.unixander.donetsk_guide.connection.DBConnection;
import ua.org.unixander.donetsk_guide.entity.Route;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class RoutesList extends ListActivity {
	private DBConnection db;
	private String[] names;
	private int ids[];
	private List<Route> routesList = null;
	private Context context;
	private ListActivity current;
	private Dialog dialog;
	private String numberRoute = "", typeRoute = "";
	private Spinner transports;
	private Timer timer=new Timer(); 
	//TODO: add timer for connection wait

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		current = this;
		context = this;
		dialog = new Dialog(this);
		dialog.setContentView(R.layout.loading);
		dialog.setTitle("Please wait");
		dialog.show();
		this.setContentView(R.layout.routeslist);
		transports = (Spinner) findViewById(R.id.typeSpinner);
		new dbLoading().execute(new String());

		Button search = (Button) findViewById(R.id.searchschedulebtn);
		search.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				EditText search = (EditText) findViewById(R.id.scheduleSearch);
				String s = search.getText().toString();
				String t = transports.getSelectedItem().toString();
				if (t.contains("Any"))
					t = "";
				numberRoute = s;
				typeRoute = t;
				new dbLoading().execute(new String());

			}
		});
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent RouteIntent = new Intent(v.getContext(), RouteInfo.class);
		RouteIntent.putExtra("RouteId", Integer.toString(ids[(int) id]));
		startActivityForResult(RouteIntent, 0);
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
			}
			;
			if (numberRoute.isEmpty() && typeRoute.isEmpty()) {
				routesList = db.getRoutesList();
				message = "Server is inaccessible";
			} else {
				routesList = db.searchRoute(numberRoute, typeRoute);
				message = "Nothing found";
			}

			int num = 0;
			if (routesList != null) {
				num = routesList.size();
			}
			names = new String[num];
			ids = new int[num];

			for (int i = 0; i < num; i++) {
				names[i] = "Number: " + routesList.get(i).number;
				ids[i] = routesList.get(i).id;
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
				current.setListAdapter(new RoutesListAdapter(current, names,
						routesList));
				dialog.cancel();
			} else {
				((TextView) dialog.findViewById(R.id.textView2))
						.setText(message);
			}
		}

	}
}
