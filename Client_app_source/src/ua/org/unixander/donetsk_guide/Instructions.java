package ua.org.unixander.donetsk_guide;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class Instructions extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.instructions);
		TextView about=(TextView) findViewById(R.id.about);
		about.setMovementMethod(LinkMovementMethod.getInstance());
	}

}
