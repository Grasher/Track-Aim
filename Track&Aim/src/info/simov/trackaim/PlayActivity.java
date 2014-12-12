package info.simov.trackaim;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class PlayActivity extends Activity {
	 private Camera mCamera;
	    private CameraPreview mPreview;

	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	            WindowManager.LayoutParams.FLAG_FULLSCREEN);
	        setContentView(R.layout.activity_play);
	        
	        checkCameraHardware(this);
	        // Create an instance of Camera
	        mCamera = getCameraInstance();

	        // Create our Preview view and set it as the content of our activity.
	        mPreview = new CameraPreview(this, mCamera);
	        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
	        preview.addView(mPreview);
	    }
	    
	    /** A safe way to get an instance of the Camera object. */
	    public static Camera getCameraInstance(){
	        Camera c = null;
	        try {
	            c = Camera.open(); // attempt to get a Camera instance
	        }
	        catch (Exception e){
	            // Camera is not available (in use or does not exist)
	        }
	        return c; // returns null if camera is unavailable
	    }
	    
	    private boolean checkCameraHardware(Context context) {
	        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
	            // this device has a camera
	            return true;
	        } else {
	            // no camera on this device
	            return false;
	        }
	    }
	    
	    
}
