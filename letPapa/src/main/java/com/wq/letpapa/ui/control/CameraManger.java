package com.wq.letpapa.ui.control;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.wq.letpapa.R;
import com.wq.letpapa.customview.ControlPanelLayout;
import com.wq.letpapa.utils.log.Trace;

public class CameraManger implements SurfaceHolder.Callback, ShutterCallback,
		MyCameraCallback {

	int mCameraId = CameraInfo.CAMERA_FACING_BACK;
	Camera camera;
	boolean isFront = false;// 是否是前置摄像头
	boolean isPreviewing = false;
	RelativeLayout fram_layout;
	SurfaceView surfaceView;
	Activity activity;
	int sc_width = 0;

	SurfaceHolder holder;

	public CameraManger(Activity activity, RelativeLayout famlayout) {
		this.fram_layout = famlayout;
		this.activity = activity;
	}

	@SuppressWarnings("deprecation")
	public void initSurfaceView() {
		surfaceView = (SurfaceView) fram_layout.findViewById(R.id.surfaceView1);
		surfaceView.getHolder()
				.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		surfaceView.getHolder().addCallback(this);
	}

	@Override
	public void onPause() {
		closeCamera();
		surfaceView.getHolder().removeCallback(this);
		surfaceView = null;
		holder = null;
	}

	@Override
	public boolean onResume() {
		if (!openCamera()) {
			return false;
		}
		initSurfaceView();
		if(!isPreviewing){
			startPreview();		
		}
		return true;
	}

	/**
	 * 
	 * */
	@SuppressWarnings("deprecation")
	public void resverlayout(Size size) { // 1080 1920
		// 12:57:44.294: I/System.out(555): 获取最图片大小(w:3984h:2988)
		System.out.println("设定照片尺寸:" + size.width + "h:" + size.height);
		WindowManager wm = (WindowManager) activity
				.getSystemService(Context.WINDOW_SERVICE); // 获取当前屏幕管理器对象
		Display display = wm.getDefaultDisplay(); // 获取屏幕信息的描述类
		sc_width = display.getWidth();// 1196 //1920 1920 1080
		int yw = size.width;// 预览的宽 1920
		int yh = size.height;// 预览的高 1080
		int lh = display.getHeight();// surface 的宽度等于屏幕的宽度 1080
		int lw = yh * yw / yh;
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(lw, lh);
		surfaceView.setLayoutParams(params);
		System.out.println("从新设置surfaceView的宽高" + lw + "h:" + lh);
	}

	@SuppressWarnings("deprecation")
	private void setCameraParameters() {
		Camera.Parameters parameters = camera.getParameters();
		List<Integer> d = parameters.getSupportedPreviewFormats();
		parameters.setPreviewFrameRate(d.get(0));
		/* 设置照片的输出格式:jpg */
		parameters.setPictureFormat(PixelFormat.JPEG);
		/* 照片质量 */
		parameters.set("jpeg-quality", 100);
		/* 设置照片的大小：此处照片大小等于屏幕大小 */
		// parameters.setPictureSize(display.getWidth(),
		// display.getHeight());
		// 设置白平衡
		if (mCameraId == CameraInfo.CAMERA_FACING_BACK) {
			if (parameters.getSupportedWhiteBalance().contains(
					Camera.Parameters.WHITE_BALANCE_AUTO)) {
				parameters
						.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
			}
			// 是否支持自动对焦
			if (parameters.getSupportedFocusModes().contains(
					Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
				parameters
						.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
			}
		}
		parameters.set("orientation", "landscape");
		/* 将参数对象赋予到 camera 对象上 */
		camera.setParameters(parameters);
		Size size = getBestPictureSize(parameters);
		if (size != null) {
			parameters.setPictureSize(size.width, size.height);
			Size preSize = getBestPreviwSize(size, parameters);
			List<Size> sizess = parameters.getSupportedPreviewSizes();
			for (Size size2 : sizess) {
				System.out.println("w" + size2.width + "h" + size2.height);
			}
			// preSize=sizess.get(sizess.size())
			if (preSize != null) {
				parameters.setPreviewSize(preSize.width, preSize.height);
				System.out.println("设定预览w" + preSize.width + "h"
						+ preSize.height);
				// parameters.setPreviewSize(2560,1440);
			}
		}
		// Size size = parameters.getPreviewSize();
		// parameters.setPreviewSize(size.width, size.height);
		camera.setParameters(parameters);
		// 从新计算surfaceview的大小
		// resverlayout(parameters.getPictureSize());
		resverlayout(parameters.getPreviewSize());
	}

	
	
	/**
	 * size=w:h(5312x 2988) size=w:h(3984x 2988) size=w:h(3264x 2448)
	 * size=w:h(3264x 1836) size=w:h(2560x 1920) size=w:h(2048x 1152)
	 * size=w:h(1920x 1080) size=w:h(1280x 960) size=w:h(1280x 720)
	 * size=w:h(800x 480) size=w:h(640x 480) 获取最接近16:9的比例的图片尺寸1.7777777
	 * @param parameters
	 * @return
	 */

	public Size getBestPictureSize(Camera.Parameters parameters) {
		List<Size> sizes = parameters.getSupportedPictureSizes();
		double bestbl = 1.777777777778d;
		double smart = 0.3d;
		Size newsSize = null;
		for (Size size : sizes) {
			double currentbl = ((double) size.width) / ((double) size.height);
			if (smart > Math.abs(currentbl - bestbl) &&size.height >= 720) {
				smart = Math.abs(currentbl - bestbl);
				newsSize = size;
			}
		}
		if (newsSize == null) {
			newsSize = sizes.get(sizes.size() - 2);
			System.out.println("默认设置的最大尺寸");
		}
		System.out.println(" 获取到最佳照片尺寸是w" + newsSize.width + "h:"
				+ newsSize.height);
		return newsSize;
	}

	public Size getBestPreviwSize(Size pSize, Camera.Parameters parameters) {
		List<Size> sizes = parameters.getSupportedPreviewSizes();
		double bestbl = ((double) pSize.width) / ((double) pSize.height);
		double smart = 0.3d;
		Size newsSize = null;
		for (Size size : sizes) {
			if(size.equals(pSize)){
				return size;
			}
			double currentbl = ((double) size.width) / ((double) size.height);
			if (smart > Math.abs(currentbl - bestbl)) {
				smart = Math.abs(currentbl - bestbl);
				newsSize = size;
			}
		}
		return newsSize;
	}

	public boolean openCamera() {
		try {
			camera = Camera.open(mCameraId);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				camera.reconnect();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		if (camera == null) {
			showCameraErrorAndFinish();
			return false;
		}
		return true;
	}

	public boolean isFrontCamera() {
		return mCameraId == CameraInfo.CAMERA_FACING_FRONT ? true : false;
	}

	public void startPreview() {
		if (isPreviewing) {
			stopPreview();
		}
		if (camera == null)
			return;
		setCameraParameters();
		try {
			camera.setPreviewDisplay(holder);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			camera.startPreview();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		isPreviewing = true;
	}

	public void stopPreview() {
		if (camera != null) {
			camera.stopPreview();
			isPreviewing = false;
		}
	}

	public void switchCamera() {
		if (camera == null) {
			showCameraErrorAndFinish();
			return;
		}
		// 当前是后置摄像头
		if (mCameraId == CameraInfo.CAMERA_FACING_BACK) {
			mCameraId = CameraInfo.CAMERA_FACING_FRONT;
			fram_layout.findViewById(R.id.iv_flash).setVisibility(View.GONE);
		} else {
			mCameraId = CameraInfo.CAMERA_FACING_BACK;
			fram_layout.findViewById(R.id.iv_flash).setVisibility(View.VISIBLE);
		}
		closeCamera();
		openCamera();
		startPreview();
	}

	public void changeFlash(boolean isopen) {
		Camera.Parameters cameraParameters = camera.getParameters();
		if (!isopen) {
			cameraParameters.setFlashMode(Parameters.FLASH_MODE_OFF);
		} else {
			cameraParameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
		}
		camera.setParameters(cameraParameters);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Trace.i("surfaceChanged---width:"+width+"height:"+height);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		this.holder = holder;
			startPreview();	
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {

	}

	private void closeCamera() {
		if (camera == null) {
			return;
		}
		camera.setErrorCallback(null);
		if (isPreviewing) {
			stopPreview();
		}
		camera.release();
		camera=null;
	}

	@Override
	public void takePicture(PictureCallback pictureCallback) {
		camera.takePicture(this, null, pictureCallback);
	}

	@Override
	public void onShutter() {

	}

	private void showCameraErrorAndFinish() {
		DialogInterface.OnClickListener buttonListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				activity.finish();
			}
		};
		new AlertDialog.Builder(activity).setCancelable(false).setTitle("出大事了")
				.setMessage("艾玛，你摄像头呢？").setNeutralButton("关闭", buttonListener)
				.show();
	}

}
