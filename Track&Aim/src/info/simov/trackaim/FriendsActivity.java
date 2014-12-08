package info.simov.trackaim;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

public class FriendsActivity extends Activity {

	private EditText userName;
	private Button search;
	private ListView userFoundList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends);
		
		userName = (EditText) findViewById(R.id.findUser);
		userFoundList = (ListView) findViewById(R.id.usersList);
		search = (Button) findViewById(R.id.search);
		search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				String name = userName.getText().toString();
				Toast.makeText(getApplicationContext(), "User: " + name,
						Toast.LENGTH_LONG).show();

			}
		});

	}
}
