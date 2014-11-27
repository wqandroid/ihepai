package com.wq.letpapa.ui;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.wq.letpapa.R;

public class CropImageActivity extends Activity{

	
//	CropImageView cropImageView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_crop_layout);
//		cropImageView=(CropImageView) findViewById(R.id.CropImageView);
		Bitmap bitmap=BitmapFactory.decodeFile(uriToPath(getIntent().getData()));
		if(bitmap==null){
//			Toast.makeText(this, "获取图片失败", 0).show();
			System.out.println("获取图片失败");
			finish();
		}
//		cropImageView.setImageBitmap(bitmap);
	}

	public String uriToPath(Uri uri) {
		Cursor c = getContentResolver().query(uri, null, null, null, null);
		c.moveToFirst();
		String img_path = c.getString(c
				.getColumnIndex(MediaStore.Images.Media.DATA));
		c.close();
		return img_path;
	}


}
