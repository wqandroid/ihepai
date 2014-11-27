package com.wq.letpapa.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.wq.letpapa.R;
import com.wq.letpapa.bean.DataFactory;
import com.wq.letpapa.cache.PhotoFileManger;
import com.wq.letpapa.customview.ControlPanelLayout;
import com.wq.letpapa.customview.CustomDialog;
import com.wq.letpapa.customview.RotateImageView;
import com.wq.letpapa.customview.TouchOverlayView;
import com.wq.letpapa.gpu.ActivityGallery;
import com.wq.letpapa.ui.MyDialogListener.OnLeftClick;
import com.wq.letpapa.ui.base.CameraBaseActivity;
import com.wq.letpapa.ui.control.CameraManger;
import com.wq.letpapa.ui.control.Util;
import com.wq.letpapa.utils.BitmapUtil;
import com.wq.letpapa.utils.Constant;

public class CaptureActivity extends CameraBaseActivity implements
		OnClickListener, Constant {

	ControlPanelLayout controlPanelLayout;
	RelativeLayout fram_layout;
	CameraManger cameraManger;

	// View filer_view;
	TouchOverlayView overlayView;
	RotateImageView iv_flash, iv_changecamera, iv_chose, iv_chosefilter;
	boolean isOpenFlash = false;
	ScrollView scrollView;

	boolean isPhotoOver = false;
	boolean ischoseFromGallery = false;
	Bitmap currentbitmap = null;

	boolean isHEPAI = false;
	int or = 0;
	long photo_id = 0;
	int topic_id = 0;
	Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.surface_layout);
		iv_flash = (RotateImageView) findViewById(R.id.iv_flash);
		// filer_view = (View) findViewById(R.id.filer_view);
		scrollView = (ScrollView) findViewById(R.id.ScrollView);
		iv_changecamera = (RotateImageView) findViewById(R.id.iv_changecamera);
		overlayView = (TouchOverlayView) findViewById(R.id.touchOverlayView1);
		iv_chose = (RotateImageView) findViewById(R.id.iv_chose);
		controlPanelLayout = (ControlPanelLayout) findViewById(R.id.control_panel);
		fram_layout = (RelativeLayout) findViewById(R.id.frame_layout);
		// 旋转或者保存按钮
		iv_chosefilter = (RotateImageView) findViewById(R.id.iv_chosefilter);
		CheckHepai(getIntent());
		cameraManger = new CameraManger(this, fram_layout);
		// 看看闪光灯 是否可用
		if (getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA_FLASH)) {
			iv_flash.setOnClickListener(this);
		} else {
			iv_flash.setVisibility(View.INVISIBLE);
		}
		findViewById(R.id.take_capture).setOnClickListener(this);
		if (Camera.getNumberOfCameras() > 1) {
			iv_changecamera.setOnClickListener(this);
		} else {
			iv_changecamera.setVisibility(View.GONE);
		}
		iv_chose.setOnClickListener(this);
		iv_chosefilter.setOnClickListener(this);
		// 取消拍摄按钮
		findViewById(R.id.cancle_recoder).setOnClickListener(this);
	}

	/** 判断是合拍还是发布 */
	private void CheckHepai(Intent intent) {
		// 判断是否是参与合拍
		if (intent.getIntExtra("code", -1) == CODE_TAKE_HEPAI) {
			isHEPAI = true; // photo_id
			photo_id = getIntent().getLongExtra("photo_id", -1);
			topic_id = getIntent().getIntExtra("topic_id", -1);
			or = getIntent().getIntExtra("or", 0);
			overlayView.setNoTouch();
			overlayView.setNoAnim();
			overlayView.setoverlayIv(DataFactory.bitmap, or);
			iv_chosefilter.setVisibility(View.INVISIBLE);
		}
	}

	public void setPhotoOver(boolean isCaptureOver) {
		isPhotoOver = isCaptureOver;
		if (isCaptureOver) {
			iv_changecamera.setVisibility(View.GONE);
			iv_flash.setVisibility(View.GONE);
			iv_chose.setImageResource(R.drawable.camera_done);
		} else {
			iv_changecamera.setVisibility(View.VISIBLE);
			iv_flash.setVisibility(View.VISIBLE);
			iv_chose.setImageResource(R.drawable.camera_library);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK) {
			cameraManger.onResume();
			// finish();
			ToastInfo("获取图片失败");
			return;
		}
		if (requestCode == PHOTO_REQUEST_GALLERY) {
			if (data == null) {
				return;
			}
			if (data.getData() == null) {
				return;
			}
			zoomPicToSize(data.getData(), 720);
		} else if (requestCode == PHOTO_REQUEST_EDITE) {
			Bitmap bitmap = null;
			ischoseFromGallery = true;
			if (isHEPAI) {
				bitmap = BitmapUtil.decodeUriAsBitmap(this, phoUri, -90);
			} else {
				if (ischoseFromGallery) {
					bitmap = BitmapUtil.decodeUriAsBitmap(this, phoUri,-90);
					overlayView.setImage(bitmap);
				} else {
					bitmap = BitmapUtil.decodeUriAsBitmap(this, phoUri, -90);
					overlayView.setImage(bitmap);
				}
			}
			// Bitmap bitmap = BitmapUtil.decodeUriAsBitmap(this, phoUri);
			currentbitmap = BitmapUtil.zoomBitmap(bitmap, 720, 720);
			System.out.println("图片大小****w:" + currentbitmap.getWidth() + "h:"
					+ currentbitmap.getHeight());
			cameraManger.stopPreview();
			overlayView.setImage(currentbitmap);
			photo_path = PhotoFileManger.getCapturePath();
					try {
				currentbitmap.compress(CompressFormat.JPEG, 100,
						new FileOutputStream(new File(photo_path)));
				iv_chosefilter.setImageResource(R.drawable.camera_sdcard);
				iv_chosefilter.setVisibility(View.VISIBLE);
				setPhotoOver(true);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	class JPEGPictureCallBack implements PictureCallback {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			Options options = new Options();
			// options.inSampleSize = 2;
			options.inJustDecodeBounds = true;
			Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,
					options);
			int scale = options.outHeight / 720;
			if (scale > 1 && ((options.outHeight / scale) >= 720)) {
				options.inSampleSize = scale;
			}
			options.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,
					options);
			// 获取正常的宽度
			new Handler().post(new CaptureRunable(bitmap));
			camera.stopPreview();
		}
	}

	class SaveRunbale implements Runnable {
		Bitmap bitmap;

		public SaveRunbale(Bitmap bitmap) {
			this.bitmap = Bitmap.createBitmap(bitmap);
		}

		@Override
		public void run() {
			File file = new File(PhotoFileManger.getSavepath());
			try {
				if (isHEPAI) {
					if (!ischoseFromGallery) {
						bitmap = BitmapUtil.PhotoRotation(bitmap, 90);
					}
					bitmap = createBitmap(bitmap, DataFactory.bitmap, or);
				} else {
					bitmap = BitmapUtil.PhotoRotation(bitmap, 90);
				}
				bitmap.compress(CompressFormat.JPEG, 100, new FileOutputStream(
						file));
				MediaScannerConnection.scanFile(CaptureActivity.this,
						new String[] { file.toString() }, null,
						new MediaScannerConnection.OnScanCompletedListener() {
							@Override
							public void onScanCompleted(final String path,
									final Uri uri) {
								handler.post(new Runnable() {
									@Override
									public void run() {
										Toast.makeText(CaptureActivity.this,
												"保存成功" + path, 0).show();
										overlayView.setImageNone();
										cameraManger.startPreview();
										setPhotoOver(false);
										iv_chosefilter
												.setImageResource(R.drawable.camera_rotated);
										if (isHEPAI) {
											iv_chosefilter
													.setVisibility(View.INVISIBLE);
										}
									}
								});
							}
						});
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public void savebitmap(Bitmap bitmap) {
		new Thread(new SaveRunbale(bitmap)).start();
	}

	class CaptureRunable implements Runnable {
		Bitmap bitmap;
		int width, height;
		int x = 0;

		public CaptureRunable(Bitmap bitmap) {
			this.bitmap = bitmap;
			int width = View.MeasureSpec.makeMeasureSpec(0,
					View.MeasureSpec.UNSPECIFIED);
			int height = View.MeasureSpec.makeMeasureSpec(0,
					View.MeasureSpec.UNSPECIFIED);
			View title_view = findViewById(R.id.title_view);
			title_view.measure(width, height);
			this.height = title_view.getMeasuredHeight();
			this.width = title_view.getMeasuredWidth();
			// 计算出等比bitmao截取的 顶部距离
			x = (bitmap.getHeight() * this.width) / display.getHeight();
			double bl = ((double) this.width) / ((double) display.getWidth());
			double cropwindth = bl * bitmap.getWidth();
			x = (int) cropwindth;
		}

		@Override
		public void run() {
			int h = bitmap.getHeight();
			Bitmap bitmap2 = null;
			// 判断是否是前置摄像头
			if (cameraManger.isFrontCamera()) {
				// 翻转 并设置镜像模式
				bitmap2 = BitmapUtil.PhotoRotationMirror(bitmap, 180);
				bitmap2 = Bitmap.createBitmap(bitmap2, x, 0, h, h, null, false);
			} else {
				bitmap2 = Bitmap.createBitmap(bitmap, x, 0, h, h, null, false);
			}
			bitmap2 = ThumbnailUtils.extractThumbnail(bitmap2, 720, 720);
			currentbitmap = bitmap2;
			overlayView.setImage(currentbitmap);
			ischoseFromGallery = false;
			photo_path = PhotoFileManger.getCapturePath();
			try {
				currentbitmap.compress(CompressFormat.JPEG, 100,
						new FileOutputStream(new File(photo_path)));
				iv_chosefilter.setImageResource(R.drawable.camera_sdcard);
				iv_chosefilter.setVisibility(View.VISIBLE);
				setPhotoOver(true);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!cameraManger.onResume()) {
			ToastInfo("连接相机失败");
			finish();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		cameraManger.onPause();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.take_capture:
			if (isPhotoOver) {
				overlayView.setImageNone();
				cameraManger.startPreview();
				setPhotoOver(false);
			} else {
				cameraManger.takePicture(new JPEGPictureCallBack());
			}
			break;
		case R.id.iv_flash:
			if (!isOpenFlash) {
				cameraManger.changeFlash(true);
				isOpenFlash = true;
				iv_flash.setImageResource(R.drawable.flash_off);
			} else {
				cameraManger.changeFlash(false);
				isOpenFlash = false;
				iv_flash.setImageResource(R.drawable.flash_on);
			}
			break;
		case R.id.iv_changecamera:
			cameraManger.switchCamera();
			break;
		case R.id.iv_chose:
			if (!isPhotoOver) {
				getPictureFromGallery();
			} else {
				if (isHEPAI) {
					new MergeTask().execute(photo_path);
				} else {
					Intent in = new Intent(CaptureActivity.this,
							PhotoEditeActivity.class);
					in.putExtra("location", overlayView.getLocation());// 覆盖位置
					in.putExtra("path", photo_path);
					in.putExtra("ischoseFromGallery", ischoseFromGallery);
					startActivityForResult(in, 90);
					finish();
				}
			}
			break;
		case R.id.iv_chosefilter:
			if (isPhotoOver) {// 拍照完毕
				new Thread(new SaveRunbale(currentbitmap)).start();
				;
			} else {
				overlayView.rotateNext();
			}
			break;
		case R.id.cancle_recoder:
			finish();
			break;
		}
	}

	public class MergeTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... arg0) {
			Bitmap bitmap = BitmapFactory.decodeFile(photo_path);
			if (!ischoseFromGallery) {
				bitmap = BitmapUtil.PhotoRotation(bitmap, 90);
			}
			bitmap = createBitmap(bitmap, DataFactory.bitmap, or);
			try {
				String path = PhotoFileManger.getCapturePath();
				bitmap.compress(CompressFormat.JPEG, 100, new FileOutputStream(
						new File(path)));
				new File(photo_path).delete();
				photo_path = path;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			return photo_path;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Intent in = new Intent(CaptureActivity.this, ActivityGallery.class);
			in.putExtra("path", photo_path);
			in.putExtra("location", or);
			in.putExtra("doflag", FLAG_TAKE_HEPAI);
			in.putExtra("photo_id", photo_id);
			in.putExtra("topic_id", topic_id);
			startActivity(in);
			finish();
		}
	}

	public Bitmap createBitmap(Bitmap src, Bitmap watermark, int or) {
		if (src == null) {
			return null;
		}
		int ww = watermark.getWidth();
		int wh = watermark.getHeight();
		// create the new blank bitmap
		Bitmap newb = Bitmap.createBitmap(PIC_WINDTH, PIC_WINDTH,
				Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
		System.out.println("createBitmap水印" + "w:" + ww + ":" + wh);
		Canvas cv = new Canvas(newb);
		// draw src into
		if (isHEPAI&&ischoseFromGallery) {
			src = BitmapUtil.PhotoRotation(src, 90);
		}
		cv.drawBitmap(src, 0, 0, null);// 在 0，0坐标开始画入src
		switch (or) {
		case TOP:
			cv.drawBitmap(watermark, 0, PIC_WINDTH / 2, null);// 在src的右下角画入水印
			break;
		case DOWN:
			cv.drawBitmap(watermark, 0, 0, null);// 在src的右下角画入水印
			break;
		case LEFT:
			// draw watermark into
			watermark = BitmapUtil.Rotation(watermark, -90);
			cv.drawBitmap(watermark, PIC_WINDTH / 2, 0, null);// 在src的右下角画入水印
			break;
		case RIGHT:
			watermark = BitmapUtil.Rotation(watermark, -90);
			cv.drawBitmap(watermark, 0, 0, null);// 在src的右下角画入水印
			break;
		}
		// save all clip
		cv.save(Canvas.ALL_SAVE_FLAG);// 保存
		// store
		cv.restore();// 存储
		return newb;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			showExit();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void showExit() {
		CustomDialog.Build build = new CustomDialog.Build();
		build.setTitle("退出?").setContent("确定不拍了?").setCancleAble(false);
		build.setLeft("确定").setOnLeftClick(new OnLeftClick() {
			@Override
			public void onItemClick(CustomDialog dialog) {
				dialog.dismiss();
				finish();
			}
		});
		build.setRight("取消").setOnRightClikc(null);
		build.build(CaptureActivity.this).show();
	}
}
