package info.simov.trackaim;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class DrawSurfaceView extends View {
	Paint mPaint;
	private double OFFSET = 0d;
	private float screenWidth, screenHeight;
	private Drawable d;

	public DrawSurfaceView(Context c, Paint paint) {
		super(c);
	}

	public DrawSurfaceView(Context context, AttributeSet set) {
		super(context, set);
		mPaint = new Paint();
		mPaint.setStrokeWidth(DpiUtils.getPxFromDpi(getContext(), 200));
		mPaint.setAntiAlias(true);
		screenWidth = -0f;
		screenHeight = 0f;

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		Log.d("onSizeChanged", "in here w=" + w + " h=" + h);
		screenWidth = (float) w;
		screenHeight = (float) h;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// Setting the color of the circle
		mPaint.setColor(Color.GREEN);

		// Draw the circle at (x,y) with radius 15
		canvas.drawCircle(screenWidth, screenHeight, 15, mPaint);

		// Redraw the canvas
		invalidate();
	}

	public void setOffset(float offset) {
		this.OFFSET = offset;
	}

	public void setMyLocation(float latitude, float longitude) {

		this.screenWidth = latitude;
		this.screenHeight = longitude;

	}

}
