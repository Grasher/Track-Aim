package com.example.teste;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements SensorEventListener  {
	Button btnShowLocation;

	// GPSTracker class
	GPSTracker gps;
	
	Location l ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		l = new Location("");
	    l.setLatitude(41.208605000000006);
	    l.setLongitude(-8.5961100000000001);
		btnShowLocation = (Button) findViewById(R.id.btnShowLocation);

		// show location button click event
		btnShowLocation.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// create class object
				gps = new GPSTracker(MainActivity.this);

				// check if GPS enabled
				if (gps.canGetLocation()) {

					double latitude = gps.getLatitude();
					double longitude = gps.getLongitude();
					Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();	
					Location loc2 = new Location("");
					loc2.setLatitude(latitude);
					loc2.setLongitude(longitude);
					float distanceInMeters = l.distanceTo(loc2);
					if(distanceInMeters<=1){
					// \n is for new line
					Toast.makeText(
							getApplicationContext(),
							"PERTO", Toast.LENGTH_LONG)
							.show();
					}
				} else {
					// can't get location
					// GPS or Network is not enabled
					// Ask user to enable GPS/network in settings
					gps.showSettingsAlert();
				}

			}
		});
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
