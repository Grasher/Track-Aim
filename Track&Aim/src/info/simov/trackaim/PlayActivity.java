package info.simov.trackaim;

import java.util.ArrayList;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class PlayActivity extends Activity implements SensorEventListener {

	// record the compass picture angle turned
	private float currentDegree = 0f, targetDegree;
	private GeomagneticField geoField;
	private ImageView mPointer, mSeta;
	private Button map;
	private RelativeLayout layout;

	private SensorManager sensorManager;
	private Sensor accelerometer;
	private Sensor magnetometer;
	private Sensor orientation;

	private float[] mLastAccelerometer = new float[3];
	private float[] mLastMagnetometer = new float[3];
	private boolean mLastAccelerometerSet = false;
	private boolean mLastMagnetometerSet = false;
	private float[] mR = new float[9];
	private float[] mOrientation = new float[3];

	private ArrayList<LocationManager> locationManager;

	private static final String PROX_ALERT_INTENT = "com.example.sensorgps.lbs.ProximityAlert";

	private static final long POINT_RADIUS = 15; // in Meters
	private static final long PROX_ALERT_EXPIRATION = -1;

	private static final String POINT_LATITUDE_KEY = "POINT_LATITUDE_KEY";
	private static final String POINT_LONGITUDE_KEY = "POINT_LONGITUDE_KEY";

	private static final long MINIMUM_DISTANCECHANGE_FOR_UPDATE = 1; // in
																		// Meters
	private static final long MINIMUM_TIME_BETWEEN_UPDATE = 1000; // in
																	// Milliseconds

	private float bearing;
	private info.simov.trackaim.DrawSurfaceView mDrawView;
	private ArrayList<Location> locations;
	public boolean draw[];

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play);
		Intent i = getIntent();
		String username = i.getStringExtra("USERNAME");

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		accelerometer = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		magnetometer = sensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		orientation = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

		mPointer = (ImageView) findViewById(R.id.pointer);
		mSeta = (ImageView) findViewById(R.id.seta);
		map = (Button) findViewById(R.id.map);
		map.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate));
		map.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(PlayActivity.this, MapActivity.class);
				startActivity(i);

			}
		});

		mDrawView = (DrawSurfaceView) findViewById(R.id.drawSurfaceView);

		LocationManager loc1 = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		loc1.requestLocationUpdates(

		LocationManager.GPS_PROVIDER,

		MINIMUM_TIME_BETWEEN_UPDATE,

		MINIMUM_DISTANCECHANGE_FOR_UPDATE,

		new MyLocationListener()

		);
		locationManager = new ArrayList<LocationManager>();
		LocationManager loc2 = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		loc2.requestLocationUpdates(

		LocationManager.GPS_PROVIDER,

		MINIMUM_TIME_BETWEEN_UPDATE,

		MINIMUM_DISTANCECHANGE_FOR_UPDATE,

		new MyLocationListener()

		);
		LocationManager loc3 = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		loc3.requestLocationUpdates(

		LocationManager.GPS_PROVIDER,

		MINIMUM_TIME_BETWEEN_UPDATE,

		MINIMUM_DISTANCECHANGE_FOR_UPDATE,

		new MyLocationListener()

		);
		locationManager.add(loc1);
		locationManager.add(loc2);
		locationManager.add(loc3);
		locations = new ArrayList<Location>();
		draw = new boolean[3];
		Location l = new Location("");
		l.setLatitude(41.1780816666667);
		l.setLongitude(-8.608971666667);
		saveProximityAlertPoint((float) l.getLatitude(),
				(float) l.getLongitude(),0);
		locations.add(l);
		draw[0] = false;
		l.setLatitude(11.1780816666667);
		l.setLongitude(-18.608971666667);
		saveProximityAlertPoint((float) l.getLatitude(),
				(float) l.getLongitude(),1);
		locations.add(l);
		draw[1] = false;
		l.setLatitude(1.1780816666667);
		l.setLongitude(-1.608971666667);
		saveProximityAlertPoint((float) l.getLatitude(),
				(float) l.getLongitude(),2);
		locations.add(l);
		draw[2] = false;
		layout = (RelativeLayout) findViewById(R.id.mainlayout);
		layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean hit = mDrawView.isInCenter();
				Toast.makeText(PlayActivity.this, "Hit" + hit,
						Toast.LENGTH_LONG).show();
				// UpdateDb.UpdateScore(username, 1);
			}
		});

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
			onSensorChangeOrientation(event);
		} else {
			if (event.sensor == accelerometer) {
				System.arraycopy(event.values, 0, mLastAccelerometer, 0,
						event.values.length);
				mLastAccelerometerSet = true;
			} else if (event.sensor == magnetometer) {
				System.arraycopy(event.values, 0, mLastMagnetometer, 0,
						event.values.length);
				mLastMagnetometerSet = true;
			}
			if (mLastAccelerometerSet && mLastMagnetometerSet) {
				onSensorChangeAccelerometerAndMagnetic(event);
			}
		}
	}

	private float normalizeDegree(float value) {
		if (value >= 0.0f && value <= 180.0f) {
			return value;
		} else {
			return 180 - (180 + value);
		}
	}

	private void onSensorChangeAccelerometerAndMagnetic(SensorEvent event) {
		SensorManager.getRotationMatrix(mR, null, mLastAccelerometer,
				mLastMagnetometer);
		SensorManager.getOrientation(mR, mOrientation);
		float azimuthInRadians = mOrientation[0];
		float azimuthInDegress = (float) (Math.toDegrees(azimuthInRadians) + 360) % 360;
		for (int i = 0; i < draw.length; i++) {
			if (draw[i]) {
				RotateAnimation ra = new RotateAnimation(normalizeDegree(targetDegree),
						-azimuthInDegress, Animation.RELATIVE_TO_SELF, 0.5f,
						Animation.RELATIVE_TO_SELF, 0.5f);

				ra.setDuration(250);

				ra.setFillAfter(true);
				float y = mOrientation[1];
				mSeta.startAnimation(ra);
				mDrawView.Draw(true);
				mDrawView.setOffset(targetDegree);
				mDrawView.setMyLocation(locations.get(i).getLatitude(),
						locations.get(i).getLongitude(), y);
				targetDegree = bearing - (bearing + targetDegree) + azimuthInDegress;
			}
		}

		
	}

	private void onSensorChangeOrientation(SensorEvent event) {
		// get the angle around the z-axis rotated
		float degree = Math.round(event.values[0]);

		// create a rotation animation (reverse turn degree degrees)
		RotateAnimation ra = new RotateAnimation(currentDegree, -degree,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);

		// how long the animation will take place
		ra.setDuration(210);

		// set the animation after the end of the reservation status
		ra.setFillAfter(true);

		// Start the animation
		mPointer.startAnimation(ra);
		currentDegree = -degree;

	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();

		// for the system's orientation sensor registered listeners
		sensorManager.registerListener(this, accelerometer,
				SensorManager.SENSOR_DELAY_GAME);
		sensorManager.registerListener(this, magnetometer,
				SensorManager.SENSOR_DELAY_GAME);
		sensorManager.registerListener(this, orientation,
				SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	protected void onPause() {
		super.onPause();
		sensorManager.unregisterListener(this, accelerometer);
		sensorManager.unregisterListener(this, magnetometer);
		sensorManager.unregisterListener(this, orientation);
	}

	private void saveProximityAlertPoint(float latitude, float longitude, int i) {

		saveCoordinatesInPreferences((float) latitude, (float) longitude);
		addProximityAlert((float) latitude, (float) longitude,i);

	}

	private void addProximityAlert(double latitude, double longitude ,int i) {
		Intent intent = new Intent(PROX_ALERT_INTENT);
		PendingIntent proximityIntent = PendingIntent.getBroadcast(this, 0,
				intent, 0);
		locationManager.get(i).addProximityAlert(latitude, // the latitude of the
													// central point of the
													// alert region
				longitude, // the longitude of the central point of the alert
							// region
				POINT_RADIUS, // the radius of the central point of the alert
								// region, in meters
				PROX_ALERT_EXPIRATION, // time for this proximity alert, in
										// milliseconds, or -1 to indicate no
										// expiration

				proximityIntent // will be used to generate an Intent to fire
								// when entry to or exit from the alert region
								// is detected
				);
		IntentFilter filter = new IntentFilter(PROX_ALERT_INTENT);
		registerReceiver(new ProximityIntentReceiver(), filter);

	}

	private void saveCoordinatesInPreferences(float latitude, float longitude) {
		SharedPreferences prefs = this.getSharedPreferences(getClass()
				.getSimpleName(), Context.MODE_PRIVATE);

		SharedPreferences.Editor prefsEditor = prefs.edit();
		prefsEditor.putFloat(POINT_LATITUDE_KEY, latitude);
		prefsEditor.putFloat(POINT_LONGITUDE_KEY, longitude);
		prefsEditor.commit();
	}

	private Location retrievelocationFromPreferences() {
		SharedPreferences prefs = this.getSharedPreferences(getClass()
				.getSimpleName(), Context.MODE_PRIVATE);
		Location location = new Location("POINT_LOCATION");
		location.setLatitude(prefs.getFloat(POINT_LATITUDE_KEY, 0));
		location.setLongitude(prefs.getFloat(POINT_LONGITUDE_KEY, 0));
		return location;

	}

	public class MyLocationListener implements LocationListener {

		public void onLocationChanged(Location location) {

			Location pointLocation = retrievelocationFromPreferences();

			float distance = location.distanceTo(pointLocation);
			bearing = pointLocation.bearingTo(location);
			Toast.makeText(PlayActivity.this, "distance: " + distance,
					Toast.LENGTH_LONG).show();

			geoField = new GeomagneticField(Double.valueOf(
					location.getLatitude()).floatValue(), Double.valueOf(
					location.getLongitude()).floatValue(), Double.valueOf(
					location.getAltitude()).floatValue(),
					System.currentTimeMillis());

			targetDegree += geoField.getDeclination();

			for (int i = 0; i < locations.size(); i++) {
				if (locations.get(i).getLatitude() == pointLocation
						.getLatitude()
						&& locations.get(i).getLongitude() == pointLocation
								.getLongitude()) {
					Toast.makeText(PlayActivity.this, "distance: " + distance,
							Toast.LENGTH_LONG).show();
					if (distance < 10) {
						draw[i] = true;
						// distanceToPoint = distance;
					} else {
						draw[i] = false;
						// distanceToPoint = -1;
					}

				}
			}

		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

}
