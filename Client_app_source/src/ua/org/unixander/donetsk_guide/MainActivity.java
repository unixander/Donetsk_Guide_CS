package ua.org.unixander.donetsk_guide;

import ua.org.unixander.donetsk_guide.settings.LocalDB;
import ua.org.unixander.donetsk_guide.settings.SettingsActivity;
import ua.org.unixander.donetsk_guide.connection.DBConnection;
import ua.org.unixander.donetsk_guide.places.PlacesList;
import ua.org.unixander.donetsk_guide.routes.RoutesList;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends Activity {
	ImageButton ExitButton, SettingsButton, MapButton, ScheduleButton,
			PlacesButton, InstructionsButton;
	private LocalDB dbHelper;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dbHelper=new LocalDB(this);
		try{
			dbHelper.createDataBase();
		} catch (Exception e){
			e.printStackTrace();
		}
		dbHelper.close();
		
		DBConnection db = new DBConnection(this);
		try {
			if (db.open()) {
				db.close();
			} else {
				Toast.makeText(getApplicationContext(), "Server is inaccesible", Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), "Something went wrong. Try again", Toast.LENGTH_SHORT).show();
		}
		setContentView(R.layout.activity_main);
		// Get Buttons from form
		this.ExitButton = (ImageButton) findViewById(R.id.ExitButton);
		this.SettingsButton = (ImageButton) findViewById(R.id.SettingsButton);
		this.MapButton = (ImageButton) findViewById(R.id.MapButton);
		this.PlacesButton = (ImageButton) findViewById(R.id.PlacesButton);
		this.ScheduleButton = (ImageButton) findViewById(R.id.ScheduleButton);
		this.InstructionsButton = (ImageButton) findViewById(R.id.InstructionsButton);
		// Set action for Buttons on click
		this.InstructionsButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent InstructionsIntent = new Intent(v.getContext(),
						Instructions.class);
				startActivityForResult(InstructionsIntent, 0);
			}
		});
		this.ExitButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				terminate();
			}
		});
		this.ScheduleButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent ScheduleIntent = new Intent(v.getContext(),
						RoutesList.class);
				startActivityForResult(ScheduleIntent, 0);
			}
		});
		this.PlacesButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent PlaceIntent = new Intent(v.getContext(),
						PlacesList.class);
				startActivityForResult(PlaceIntent, 0);
			}
		});
		this.SettingsButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent SettingsIntent = new Intent(v.getContext(),
						SettingsActivity.class);
				startActivityForResult(SettingsIntent, 0);
			}
		});
		this.MapButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent ScheduleIntent = new Intent(v.getContext(),
						searchRouteActivity.class);
				startActivityForResult(ScheduleIntent, 0);
			}
		});

	}

	/** Called on terminating current activity */
	public void terminate() {
		super.onDestroy();
		this.finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}
