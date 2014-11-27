package com.wq.letpapa.ui.base;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.wq.letpapa.cache.PhotoFileManger;

public class CameraBaseActivity extends Activity {
	public static final int PHOTO_REQUEST_GALLERY = 11;
	public static final int PHOTO_REQUEST_EDITE = 12;
	public Display display;
	protected Uri phoUri;
	protected String photo_path;
	
	public static final int PIC_WINDTH=720;
	

	public static final int GONE=View.GONE;
	public static final int VISIBLE=View.VISIBLE;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE); // 获取当前屏幕管理器对象
		display = wm.getDefaultDisplay(); // 获取屏幕信息的描述类
	}

	public void getPictureFromGallery() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				"image/*");
		startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
	}
	protected void zoomPicToSize(Uri uri, int size) {
		Intent zoomIntent = new Intent("com.android.camera.action.CROP");
		zoomIntent.setDataAndType(uri, "image/*");
		zoomIntent.putExtra("crop", true);
		// 缩放
		zoomIntent.putExtra("aspectX", 1);
		zoomIntent.putExtra("aspectY", 1);
		// 大小
//		zoomIntent.putExtra("outputX", size);
//		zoomIntent.putExtra("outputY", size);
		zoomIntent.putExtra("return-data", false);
		zoomIntent.putExtra("scale", true);
		photo_path=PhotoFileManger.getCapturePath();
		phoUri = Uri.fromFile(new File(photo_path));
		zoomIntent.putExtra(MediaStore.EXTRA_OUTPUT, phoUri);
		zoomIntent.putExtra("outputFormat",
				Bitmap.CompressFormat.JPEG.toString());
		zoomIntent.putExtra("noFaceDetection", true);
		startActivityForResult(zoomIntent, PHOTO_REQUEST_EDITE);
	}
	
	
	public void ToastInfo(String msg){
		Toast.makeText(this, msg, 1).show();
	}
	
}
