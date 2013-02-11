package ua.org.unixander.donetsk_guide;

import java.util.ArrayList;
import java.util.Calendar;

import ua.org.unixander.donetsk_guide.connection.DBConnection;
import ua.org.unixander.donetsk_guide.map.ChooseMapActivity;
import ua.org.unixander.donetsk_guide.map.ShowMapInfo;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.ToggleButton;

public class searchRouteActivity extends Activity {
	Button btnMapPointOne, btnMapPointTwo, btnSearch;
	RadioButton btnYes, btnNo;
	ToggleButton toggleOne, toggleTwo;
	Context context;
	DBConnection db;
	boolean useEnhansed = false;
	double[] one, two;

	@Override
	public void onCreate(Bundle savedInstanceData) {
		super.onCreate(savedInstanceData);
		setContentView(R.layout.searchroutelayout);
		context = this;
		db = new DBConnection(this);

		btnMapPointOne = (Button) findViewById(R.id.btnMapPointOne);
		btnMapPointTwo = (Button) findViewById(R.id.btnMapPointTwo);
		btnSearch = (Button) findViewById(R.id.btnSearch);
		btnYes = (RadioButton) findViewById(R.id.radioButton1);
		btnNo = (RadioButton) findViewById(R.id.radioButton2);
		toggleOne = (ToggleButton) findViewById(R.id.toggleMPOne);
		toggleTwo = (ToggleButton) findViewById(R.id.toggleMPTwo);
		toggleOne.setEnabled(false);
		toggleTwo.setEnabled(false);

		btnNo.setChecked(true);
		btnNo.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				useEnhansed = false;
				btnYes.setChecked(false);
			}
		});

		btnYes.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				useEnhansed = true;
				btnNo.setChecked(false);
			}
		});

		btnMapPointOne.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(v.getContext(), ChooseMapActivity.class);
				i.putExtra("valueName", "mapPointOne");
				if (one != null && one.length != 0) {
					i.putExtra("Coordinates", one);
				}
				startActivityForResult(i, 1);
			}
		});

		btnMapPointTwo.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(v.getContext(), ChooseMapActivity.class);
				i.putExtra("valueName", "mapPointTwo");
				if (two != null && two.length != 0) {
					i.putExtra("Coordinates", two);
				}
				startActivityForResult(i, 2);

			}
		});

		btnSearch.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (one==null||two==null||one.length != 2 || two.length != 2
						|| !toggleOne.isChecked() || !toggleTwo.isChecked()) {
					Toast.makeText(
							context,
							"You should fill in all the fields and choose points",
							Toast.LENGTH_LONG).show();
					return;
				}
				Calendar c = Calendar.getInstance();
				int minutes = c.get(Calendar.MINUTE);
				int hours = c.get(Calendar.HOUR_OF_DAY);
				int period = getPeriodNumber(hours, minutes);
				if(btnNo.isChecked())
					period=-1;
				ArrayList<String>[] list = null;
				try {
					if (db.open()) {
						list = db.searchRouteTwoPoints(one, two, period);
						db.close();
					} else {
						Toast.makeText(getApplicationContext(),
								"Server inaccesible", Toast.LENGTH_LONG).show();
						return;
					}
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(),
							"Something went wrong. Try again",
							Toast.LENGTH_LONG).show();
					return;
				}
				if(list==null){
					Toast.makeText(getApplicationContext(), "No routes found. Please clarify your request", Toast.LENGTH_SHORT).show();
					return;
				}
				Intent intent = new Intent(v.getContext(), ShowMapInfo.class);
				list[1].add(Double.toString(one[1]));
				list[2].add(Double.toString(one[0]));
				list[1].add(Double.toString(two[1]));
				list[2].add(Double.toString(two[0]));
				list[0].add("Map Point #1");
				list[0].add("Map Point #2");
				intent.putStringArrayListExtra("Latitudes", list[1]);
				intent.putStringArrayListExtra("Longtitudes", list[2]);
				intent.putStringArrayListExtra("Names", list[0]);
				intent.putExtra("PlaceIcon", R.drawable.other);
				intent.putExtra("Route", 1);
				startActivityForResult(intent, 0);
			};
		});
	}

	private int getPeriodNumber(int hours, int minutes) {
		int p = 0;
		if (hours >= 0 && hours < 6) {
			p = 1;
		} else if (hours >= 6 && hours < 10) {
			p = 2;
		} else if (hours >= 10 && hours < 15) {
			p = 3;
		} else if (hours >= 15 && hours < 20) {
			p = 4;
		} else if (hours >= 20 && hours <= 23) {
			p = 5;
		}
		return p;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 1:
				one = data.getDoubleArrayExtra("Coordinates");
				if (one != null) {
					toggleOne.setChecked(true);
				} else {
					toggleOne.setChecked(false);
				}
				break;
			case 2:
				two = data.getDoubleArrayExtra("Coordinates");
				if (two != null) {
					toggleTwo.setChecked(true);
				} else {
					toggleTwo.setChecked(false);
				}
				break;
			}
			Toast.makeText(this.getApplicationContext(),
					"Coordinates received.", Toast.LENGTH_SHORT).show();
		}
	}
}
