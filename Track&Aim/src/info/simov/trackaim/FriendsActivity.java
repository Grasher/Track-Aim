package info.simov.trackaim;

import android.app.Activity;
import android.content.Intent;
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

	private Button searchFriends, friendsRequests;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends);
		
		searchFriends = (Button) findViewById(R.id.friendRequests);
		searchFriends.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(FriendsActivity.this,SearchFriendsActivity.class);
				startActivity(i);
			}
		});
		
		friendsRequests = (Button) findViewById(R.id.searchFriends);
		friendsRequests.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(FriendsActivity.this,FriendRequestActivity.class);
				startActivity(i);
			}
		});

	}
}
