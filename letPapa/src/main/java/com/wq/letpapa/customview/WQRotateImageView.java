package com.wq.letpapa.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.widget.ImageView;

public class WQRotateImageView extends ImageView implements Rotatable {

	public WQRotateImageView(Context context) {
		super(context);
	}

	public WQRotateImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public WQRotateImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	int orientationDegree;
	@Override
	public void setOrientation(int orientation) {
		this.orientationDegree = orientation;
		Matrix m = new Matrix();
		m.setRotate(orientationDegree, (float) bitmap.getWidth() / 2,
				(float) bitmap.getHeight() / 2);
		bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getHeight(),
				bitmap.getWidth(), m, true);
		setImageBitmap(bitmap);
		System.out.println("图片宽高···········~~~~"+bitmap.getWidth());
	}
	Bitmap bitmap;
	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);
		this.bitmap = bm;
	}

	@Override
	public void setImageResource(int resId) {
		super.setImageResource(resId);
	}

}
