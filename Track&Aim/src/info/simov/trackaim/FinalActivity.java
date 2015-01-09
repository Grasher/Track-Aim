package info.simov.trackaim;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class FinalActivity extends Activity {
	private TextView username;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent i = getIntent();
		String user = i.getStringExtra("USERNAME");
		username = (TextView) findViewById(R.id.vencedor);
		username.setText(user);
		setContentView(R.layout.activity_final);
	}
}
