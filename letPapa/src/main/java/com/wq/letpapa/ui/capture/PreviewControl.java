package com.wq.letpapa.ui.capture;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.wq.letpapa.customview.CustomDialog;
import com.wq.letpapa.ui.capture.preview.CameraPreview;

public class PreviewControl {
	private static boolean DEBUGGING = false;
	public static final String TAG = "lbcamera";
	private static final String CAMERA_PARAM_ORIENTATION = "orientation";
	private static final String CAMERA_PARAM_LANDSCAPE = "landscape";
	private static final String CAMERA_PARAM_PORTRAIT = "portrait";

	protected boolean cameraError = false;
	protected Camera mCamera;
	protected List<Camera.Size> mPreviewSizeList;
	protected List<Camera.Size> mPictureSizeList;
	protected Camera.Size mPreviewSize;
	protected Camera.Size mPictureSize;
	private int mCameraId;
	LinearLayout capture_parent;
	int screenWidth, screenHeight;
	Activity activity;

	public PreviewControl(LinearLayout capture_parent, Activity activity) {
		this.capture_parent = capture_parent;
		this.activity = activity;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			if (Camera.getNumberOfCameras() > 1) {
				mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
			} else {
				mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
			}
		} else {
			mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
		}
		if (!checkCameraHardware(activity)) {
			showCameraError(activity);
			cameraError = true;
			return;
		}
		if (!safeCameraOpen(mCameraId)) {
			showCameraError(activity);
			cameraError = true;
			return;
		}
		Camera.Parameters cameraParams = mCamera.getParameters();
		WindowManager windowManager = activity.getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		screenWidth = display.getWidth();
		screenHeight = display.getHeight();
		mPreviewSizeList = cameraParams.getSupportedPreviewSizes();
		mPictureSizeList = cameraParams.getSupportedPictureSizes();
	}

	public void OnResume() {
		if (!cameraError) {
			if(mCamera==null){
				safeCameraOpen(mCameraId);
			}
			setCameraParameters();
			capture_parent.removeAllViews();
			CameraPreview cameraPreview = new CameraPreview(activity, mCamera);
			capture_parent.addView(cameraPreview);
		}
	}

	public void onPause() {
		if (!cameraError) {
			stopPreviewAndFreeCamera();
		}
	}

	@SuppressWarnings("deprecation")
	private void setCameraParameters() {
		Camera.Parameters parameters = mCamera.getParameters();
		List<Integer> d = parameters.getSupportedPreviewFormats();
		parameters.setPreviewFrameRate(d.get(0));
		/* 设置照片的输出格式:jpg */
		parameters.setPictureFormat(PixelFormat.JPEG);
		/* 照片质量 */
		parameters.set("jpeg-quality", 100);
		// 设置白平衡
		if (mCameraId == CameraInfo.CAMERA_FACING_BACK) {
			if (parameters.getSupportedWhiteBalance().contains(
					Camera.Parameters.WHITE_BALANCE_AUTO)) {
				parameters
						.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
			}
		}
		// 是否支持自动对焦
		if (parameters.getSupportedFocusModes().contains(
				Camera.Parameters.FLASH_MODE_AUTO)) {
			parameters.setFocusMode(Camera.Parameters.FLASH_MODE_AUTO);
		}
		parameters.set(CAMERA_PARAM_ORIENTATION, CAMERA_PARAM_PORTRAIT);
		configureCameraParameters(parameters);
		// 获取最适合当前屏幕的预览
		mPreviewSize=determinePreviewSize(true, screenWidth, screenHeight);
		mPictureSize = determinePictureSize(mPreviewSize);
		resverlayout(mPreviewSize);
		parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
		parameters.setPictureSize(mPictureSize.width, mPictureSize.height);
		/* 将参数对象赋予到 camera 对象上 */
		mCamera.setParameters(parameters);
	}

	/**
	 * 
	 * */
	@SuppressWarnings("deprecation")
	public void resverlayout(Size size) { // 1080 1920

		int yw = size.width;// 预览的宽 1920
		int yh = size.height;// 预览的高 1080

		int lh = screenWidth * yw / yh;// surface 的宽度等于屏幕的宽度 1080
		int lw = screenWidth;

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(lw, lh);
		capture_parent.setLayoutParams(params);
		Log.i(TAG,"从新设置surfaceView的宽高" + lw + "h:" + lh);

	}

	/** Check if this device has a camera */
	private boolean checkCameraHardware(Context context) {
		if (context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			// this device has a camera
			return true;
		} else {
			// no camera on this device
			return false;
		}
	}

	private boolean safeCameraOpen(int id) {
		boolean qOpened = false;
		try {
			stopPreviewAndFreeCamera();
			if (id >= 0) {
				mCamera = Camera.open(id);
			} else {
				mCamera = Camera.open();
			}
			qOpened = (mCamera != null);
		} catch (Exception e) {
			Log.e(TAG, "failed to open Camera");
			e.printStackTrace();
		}
		return qOpened;
	}

	/**
	 * When this function returns, mCamera will be null.
	 */
	private void stopPreviewAndFreeCamera() {
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
	}

	public void showCameraError(Context context) {
		CustomDialog.Build build = new CustomDialog.Build();
		build.setTitle("相机故障");
		build.setContent("相机出现故障无法打开");
		build.setRight("关闭").build(context).show();
	}
	
	 /**
     * @param cameraParams
     * @param portrait
     * @param reqWidth must be the value of the parameter passed in surfaceChanged
     * @param reqHeight must be the value of the parameter passed in surfaceChanged
     * @return Camera.Size object that is an element of the list returned from Camera.Parameters.getSupportedPreviewSizes.
     */
    protected Camera.Size determinePreviewSize(boolean portrait, int reqWidth, int reqHeight) {
        // Meaning of width and height is switched for preview when portrait,
        // while it is the same as user's view for surface and metrics.
        // That is, width must always be larger than height for setPreviewSize.
        int reqPreviewWidth; // requested width in terms of camera hardware
        int reqPreviewHeight; // requested height in terms of camera hardware
        if (portrait) {
            reqPreviewWidth = reqHeight;
            reqPreviewHeight = reqWidth;
        } else {
            reqPreviewWidth = reqWidth;
            reqPreviewHeight = reqHeight;
        }
        if (DEBUGGING) {
            Log.v(TAG, "Listing all supported preview sizes");
            for (Camera.Size size : mPreviewSizeList) {
                Log.v(TAG, "  w: " + size.width + ", h: " + size.height);
            }
            Log.v(TAG, "Listing all supported picture sizes");
            for (Camera.Size size : mPictureSizeList) {
                Log.v(TAG, "  w: " + size.width + ", h: " + size.height);
            }
        }
        // Adjust surface size with the closest aspect-ratio
        float reqRatio = ((float) reqPreviewWidth) / reqPreviewHeight;
        float curRatio, deltaRatio;
        float deltaRatioMin = Float.MAX_VALUE;
        Camera.Size retSize = null;
        for (Camera.Size size : mPreviewSizeList) {
            curRatio = ((float) size.width) / size.height;
            deltaRatio = Math.abs(reqRatio - curRatio);
            if (deltaRatio < deltaRatioMin&&size.height>720) {
                deltaRatioMin = deltaRatio;
                retSize = size;
            }
        }
        
    	if (retSize == null) {
			Log.i(TAG, "无法找到最适合当前预览的照片大小");
		} else {
			Log.i(TAG, "最适合当前屏幕预览大小" + retSize.width + "h" + retSize.height);
		}
        
        return retSize;
    }


	protected Camera.Size determinePictureSize(Camera.Size previewSize) {
		Camera.Size retSize = null;
		for (Camera.Size size : mPictureSizeList) {
			if (size.equals(previewSize)) {
				Log.i(TAG, "Same picture size  found.");
				return size;
			}
		}
		if (true) {
			Log.i(TAG, "Same picture size not found.");
		}
		// if the preview size is not supported as a picture size
		float reqRatio = ((float) previewSize.width) / previewSize.height;
		float curRatio, deltaRatio;
		float deltaRatioMin = Float.MAX_VALUE;
		for (Camera.Size size : mPictureSizeList) {
			curRatio = ((float) size.width) / size.height;
			deltaRatio = Math.abs(reqRatio - curRatio);
			if (deltaRatio < deltaRatioMin && size.height >= 720
					& size.width >= 720) {
				deltaRatioMin = deltaRatio;
				retSize = size;
			}
		}
		if (retSize == null) {
			Log.i(TAG, "无法找到最适合当前预览的照片大小");
		} else {
			Log.i(TAG, "最适合当前预览的照片大小" + retSize.width + "h" + retSize.height);
		}
		return retSize;
	}

	public boolean isPortrait() {
		return (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
	}

	protected void configureCameraParameters(Camera.Parameters cameraParams) {
		int angle;
		Display display = activity.getWindowManager().getDefaultDisplay();
		switch (display.getRotation()) {
		case Surface.ROTATION_0: // This is display orientation
			angle = 90; // This is camera orientation
			break;
		case Surface.ROTATION_90:
			angle = 0;
			break;
		case Surface.ROTATION_180:
			angle = 270;
			break;
		case Surface.ROTATION_270:
			angle = 180;
			break;
		default:
			angle = 90;
			break;
		}
		Log.v(TAG, "angle: " + angle);
		mCamera.setDisplayOrientation(angle);
		mCamera.setParameters(cameraParams);
	}

}
