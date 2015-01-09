package info.simov.trackaim;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class DrawSurfaceView extends View {
	Point center = new Point(90d, 110.8000, 0);
	Point me = new Point(-3.870932d, 11.204727d, 0);
	Paint mPaint;
	private double OFFSET = 0d;
	private float screenWidth, screenHeight;
	private Bitmap mTarget;
	private float size;
	private boolean draw;

	public DrawSurfaceView(Context c, Paint paint) {
		super(c);
	}

	public DrawSurfaceView(Context context, AttributeSet set) {
		super(context, set);
		mPaint = new Paint();
		mPaint.setStrokeWidth(DpiUtils.getPxFromDpi(getContext(), 200));
		mPaint.setAntiAlias(true);
		mTarget = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.target);

		;

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		Log.d("onSizeChanged", "in here w=" + w + " h=" + h);
		screenWidth = (float) w;
		screenHeight = (float) h;
	}

	@SuppressLint({ "DrawAllocation", "NewApi" })
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (draw) {
			// Point location = new Point(0, 0, "POint");
			int targetCentreX = mTarget.getWidth() / 2;
			int targetCentreY = mTarget.getHeight() / 2;
			double dist = distInMetres(me, center);
			double angle = bearing(me.latitude, me.longitude, center.latitude,
					center.longitude) - OFFSET;
			double xPos, yPos;
			if (angle < 0)
				angle = (angle + 360) % 360;

			xPos = Math.sin(Math.toRadians(angle)) * dist;
			yPos = Math.sqrt(Math.pow(dist, 2) - Math.pow(xPos, 2));
			if (angle > 90 && angle < 270)
				yPos *= -1;
			double posInPx = angle * (screenWidth / 90d);
			xPos = posInPx - targetCentreX;
			yPos = yPos + targetCentreY;

			if (angle <= 45)
				center.x = (float) ((screenWidth / 2) + xPos);

			else if (angle >= 315)
				center.x = (float) ((screenWidth / 2) - ((screenWidth * 4) - xPos));

			else
				center.x = (float) (float) (screenWidth * 9); // somewhere off
																// the
																// screen

			center.y = (float) screenHeight / 2 + targetCentreY;
			canvas.drawBitmap(mTarget, center.x, (float) me.altitude, mPaint);
			// Redraw the canvas
			invalidate();
		}
	}

	public void setOffset(float offset) {
		this.OFFSET = offset;
	}

	protected double distInMetres(Point me, Point u) {

		double lat1 = me.latitude;
		double lng1 = me.longitude;

		double lat2 = u.latitude;
		double lng2 = u.longitude;

		double earthRadius = 6371;
		double dLat = Math.toRadians(lat2 - lat1);
		double dLng = Math.toRadians(lng2 - lng1);
		double sindLat = Math.sin(dLat / 2);
		double sindLng = Math.sin(dLng / 2);
		double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2) * Math.cos(lat1)
				* Math.cos(lat2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double dist = earthRadius * c;

		return dist * 1000;
	}

	protected static double bearing(double lat1, double lon1, double lat2,
			double lon2) {
		double longDiff = Math.toRadians(lon2 - lon1);
		double la1 = Math.toRadians(lat1);
		double la2 = Math.toRadians(lat2);
		double y = Math.sin(longDiff) * Math.cos(la2);
		double x = Math.cos(la1) * Math.sin(la2) - Math.sin(la1)
				* Math.cos(la2) * Math.cos(longDiff);

		double result = Math.toDegrees(Math.atan2(y, x));
		return (result + 360.0d) % 360.0d;
	}

	public void setMyLocation(double latitude, double longitude, double altitude) {
		me.latitude = latitude;
		me.longitude = longitude;
		me.altitude = altitude;
	}

	public void Draw(boolean draw) {
		this.draw = draw;
	}

	public boolean isInCenter() {
		System.out.println("Center: " + center.x);
		boolean hit= false;
		if (center.x > -30 && center.x < 30) {
			hit =true;
		}
		return hit;
	}

	public void setSize(float size) {
		this.size = size;
	}
}
