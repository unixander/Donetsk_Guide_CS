package ua.org.unixander.donetsk_guide.settings;

import java.io.IOException;

import ua.org.unixander.donetsk_guide.R;
import ua.org.unixander.donetsk_guide.connection.DBConnection;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends Activity {
	
	private Context context;
	private EditText hostText,portText; 
	private Button btnSave,btnCheck;
	private LocalDB dbHelper;
	
	@Override
	public void onCreate(Bundle savedInstanceData) {
		super.onCreate(savedInstanceData);
		context=this;
		dbHelper=new LocalDB(context);
		setContentView(R.layout.settings);
		dbHelper.openDataBase();
		String h=dbHelper.getHost();
		String p=dbHelper.getPort();
		dbHelper.close();
		hostText=(EditText) findViewById(R.id.editText1);
		portText=(EditText) findViewById(R.id.editText2);
		btnSave=(Button) findViewById(R.id.button1);
		btnCheck=(Button) findViewById(R.id.btnCheckConnection);
		
		hostText.setText(h);
		portText.setText(p);
		
		btnCheck.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				String host=hostText.getText().toString();
				if(host.isEmpty()){
					Toast.makeText(getApplicationContext(), "Enter server host", Toast.LENGTH_SHORT);
					return;
				}
				int port=Integer.parseInt(portText.getText().toString());
				if(port<=0){
					Toast.makeText(getApplicationContext(), "Enter correct server port", Toast.LENGTH_SHORT).show();
					return;
				}
				
				DBConnection db = new DBConnection(host,port);
				try {
					if (db.open()) {
						db.close();
						Toast.makeText(getApplicationContext(), "Connection to server established", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(getApplicationContext(), "Host or port is invalid. Or server is inaccesible", Toast.LENGTH_LONG).show();
					}
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), "Something went wrong. Try again", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		btnSave.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				dbHelper.openDataBaseforWrite();
				String host=hostText.getText().toString();
				dbHelper.updateHost(host);
				String port = portText.getText().toString();
				dbHelper.updatePort(port);
				dbHelper.close();
				Toast.makeText(getApplicationContext(), "Settings saved", Toast.LENGTH_SHORT).show();
			}
		});
	}
	
}
