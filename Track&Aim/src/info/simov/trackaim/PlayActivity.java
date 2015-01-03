package info.simov.trackaim;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;

public class PlayActivity extends Activity implements SensorEventListener {

	// record the compass picture angle turned
	private float currentDegree = 0f;

	// device sensor manager
	private ImageView mPointer;
	private Button map;
	private SensorManager sensorManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play);

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mPointer = (ImageView) findViewById(R.id.pointer);
		map = (Button) findViewById(R.id.map);
		

	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {

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

	@Override
	protected void onResume() {
		super.onResume();

		// for the system's orientation sensor registered listeners
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
				SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	protected void onPause() {
		super.onPause();

		// to stop the listener and save battery
		sensorManager.unregisterListener(this);
	}

}
