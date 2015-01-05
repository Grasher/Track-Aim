package info.simov.trackaim;

import java.text.DecimalFormat;
import java.text.NumberFormat;

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
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;

public class PlayActivity extends Activity implements SensorEventListener {

	// record the compass picture angle turned
	private float currentDegree = 0f, targetDegree;
	private GeomagneticField geoField;
	// device sensor manager
	private ImageView mPointer, mSeta;
	private Button map;
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

	private LocationManager locationManager;

	private static final NumberFormat nf = new DecimalFormat("##.########");

	private static final String PROX_ALERT_INTENT = "com.example.sensorgps.lbs.ProximityAlert";

	private static final long POINT_RADIUS = 1000; // in Meters
	private static final long PROX_ALERT_EXPIRATION = -1;

	private static final String POINT_LATITUDE_KEY = "POINT_LATITUDE_KEY";
	private static final String POINT_LONGITUDE_KEY = "POINT_LONGITUDE_KEY";

	private static final long MINIMUM_DISTANCECHANGE_FOR_UPDATE = 1; // in
																		// Meters
	private static final long MINIMUM_TIME_BETWEEN_UPDATE = 1000; // in
																	// Milliseconds

	private float azimut, bearing;
	private info.simov.trackaim.DrawSurfaceView mDrawView;
	private Location myLocation;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play);

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		accelerometer = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		magnetometer = sensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		orientation = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

		mPointer = (ImageView) findViewById(R.id.pointer);
		mSeta = (ImageView) findViewById(R.id.seta);
		map = (Button) findViewById(R.id.map);

		mDrawView = (DrawSurfaceView) findViewById(R.id.drawSurfaceView);

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		locationManager.requestLocationUpdates(

		LocationManager.GPS_PROVIDER,

		MINIMUM_TIME_BETWEEN_UPDATE,

		MINIMUM_DISTANCECHANGE_FOR_UPDATE,

		new MyLocationListener()

		);
		myLocation = new Location("");
		myLocation.setLatitude(41.20869333);
		myLocation.setLongitude(-8.5841670);
		saveProximityAlertPoint();

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
		RotateAnimation ra = new RotateAnimation(normalizeDegree(targetDegree),
				-azimuthInDegress, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);

		ra.setDuration(250);

		ra.setFillAfter(true);

		mSeta.startAnimation(ra);
		mDrawView.setOffset(targetDegree);
		mDrawView.setMyLocation(myLocation.getLatitude(),
				myLocation.getLongitude());
		targetDegree = bearing - (bearing + targetDegree) + azimuthInDegress;
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

	private void saveProximityAlertPoint() {

		saveCoordinatesInPreferences((float) myLocation.getLatitude(),
				(float) myLocation.getLongitude());
		addProximityAlert(myLocation.getLatitude(), myLocation.getLongitude());

	}

	private void addProximityAlert(double latitude, double longitude) {
		Intent intent = new Intent(PROX_ALERT_INTENT);
		PendingIntent proximityIntent = PendingIntent.getBroadcast(this, 0,
				intent, 0);
		locationManager.addProximityAlert(latitude, // the latitude of the
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
			/*
			 * Toast.makeText(PlayExActivity.this,
			 * 
			 * "Distance from Point:" + distance + ", bearing: " + bearing,
			 * Toast.LENGTH_LONG).show();
			 */
			geoField = new GeomagneticField(Double.valueOf(
					location.getLatitude()).floatValue(), Double.valueOf(
					location.getLongitude()).floatValue(), Double.valueOf(
					location.getAltitude()).floatValue(),
					System.currentTimeMillis());

			targetDegree += geoField.getDeclination();
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
