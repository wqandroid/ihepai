package com.wq.letpapa.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;

public class BitmapUtil {

	
	/**
	 *  ALPHA_8:数字为8，图形参数应该由一个字节来表示,应该是一种8位的位图 
     * ARGB_4444:4+4+4+4=16，图形的参数应该由两个字节来表示,应该是一种16位的位图. 
     * ARGB_8888:8+8+8+8=32，图形的参数应该由四个字节来表示,应该是一种32位的位图. 
     * RGB_565:5+6+5=16，图形的参数应该由两个字节来表示,应该是一种16位的位图. 
     *  
     * ALPHA_8，ARGB_4444，ARGB_8888都是透明的位图，也就是所字母A代表透明。 
     * ARGB_4444:意味着有四个参数,即A,R,G,B,每一个参数由4bit表示. 
     * ARGB_8888:意味着有四个参数,即A,R,G,B,每一个参数由8bit来表示. 
     * RGB_565:意味着有三个参数,R,G,B,三个参数分别占5bit,6bit,5bit. 
     *  
     *  
     * BitmapFactory.Options.inPurgeable; 
     *  
     * 如果 inPurgeable 设为True的话表示使用BitmapFactory创建的Bitmap 
     * 用于存储Pixel的内存空间在系统内存不足时可以被回收， 
     * 在应用需要再次访问Bitmap的Pixel时（如绘制Bitmap或是调用getPixel）， 
     * 系统会再次调用BitmapFactory decoder重新生成Bitmap的Pixel数组。  
     * 为了能够重新解码图像，bitmap要能够访问存储Bitmap的原始数据。 
     *  
     * 在inPurgeable为false时表示创建的Bitmap的Pixel内存空间不能被回收， 
     * 这样BitmapFactory在不停decodeByteArray创建新的Bitmap对象， 
     * 不同设备的内存不同，因此能够同时创建的Bitmap个数可能有所不同， 
     * 200个bitmap足以使大部分的设备重新OutOfMemory错误。 
     * 当isPurgable设为true时，系统中内存不足时， 
     * 可以回收部分Bitmap占据的内存空间，这时一般不会出现OutOfMemory 错误。 */
	
	public static String TAG = "BitmapUtil";
	public static Bitmap adjustPhotoRotation(Bitmap bm,
			final int orientationDegree, int x) {
		Matrix m = new Matrix();
		m.setRotate(orientationDegree, (float) bm.getWidth() / 2,
				(float) bm.getHeight() / 2);
		m.postScale(1, -1); // 镜像垂直翻转
		// m.postScale(-1, 1); //镜像水平翻转
		try {
			Bitmap bm1 = Bitmap.createBitmap(bm, 0, 0, bm.getHeight(),
					bm.getHeight(), m, true);
			bm1=Bitmap.createBitmap(bm1, x, 0, bm1.getHeight(), bm1.getHeight());
			return bm1;
		} catch (OutOfMemoryError ex) {
		}
		return null;
	}

	
	/**
	 * 
	 * @param file
	 * @param size 最小宽度
	 * @return
	 */
	public  static Bitmap decodeWithSize(String file,int size){
		Options opts=new Options();
		opts.inJustDecodeBounds=true;
		BitmapFactory.decodeFile(file, opts);
		int nw=opts.outWidth;
		int nh=opts.outHeight;
		opts.inJustDecodeBounds=false;
		opts.outHeight=size;		
		opts.outWidth=nw*size/nh;
		return BitmapFactory.decodeFile(file, opts);
	}
	
	public static Bitmap Rotation(Bitmap bm, final int orientationDegree) {
		Matrix m = new Matrix();
		m.setRotate(orientationDegree, (float) bm.getWidth() / 2,
				(float) bm.getHeight() / 2);
		// m.postScale(1, -1); // 镜像垂直翻转
		// m.postScale(-1, 1); //镜像水平翻转
		try {
			Bitmap bm1 = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
					bm.getHeight(), m, true);
			return bm1;
		} catch (OutOfMemoryError ex) {
		}
		return null;
	}
	
	
	public static Bitmap PhotoRotationMirror(Bitmap bm, final int orientationDegree) {
		Matrix m = new Matrix();
		 m.postScale(1, -1); // 镜像垂直翻转
//		 m.postScale(-1, 1); //镜像水平翻转
		m.postRotate(orientationDegree);
		try {
			Bitmap bm1 = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
					bm.getHeight(), m, true);
			return bm1;
		} catch (OutOfMemoryError ex) {
		}
		return null;
	}
	
	public static Bitmap PhotoRotation(Bitmap bm, final int orientationDegree) {
		Matrix m = new Matrix();
		m.postRotate(orientationDegree);
		try {
			Bitmap bm1 = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
					bm.getHeight(), m, true);
			return bm1;
		} catch (OutOfMemoryError ex) {
		}
		return null;
	}

	/** 时间更短 */
	public static Bitmap adjustPhotoRotation(Bitmap bm,
			final int orientationDegree) {

		Matrix m = new Matrix();
		m.setRotate(orientationDegree, (float) bm.getWidth() / 2,
				(float) bm.getHeight() / 2);
		float targetX, targetY;
		if (orientationDegree == 90) {
			targetX = bm.getHeight();
			targetY = 0;
		} else {
			targetX = bm.getHeight();
			targetY = bm.getWidth();
		}
		final float[] values = new float[9];
		m.getValues(values);
		float x1 = values[Matrix.MTRANS_X];
		float y1 = values[Matrix.MTRANS_Y];
		m.postTranslate(targetX - x1, targetY - y1);
		Bitmap bm1 = Bitmap.createBitmap(bm.getHeight(), bm.getWidth(),
				Bitmap.Config.ARGB_8888);
		Paint paint = new Paint();
		Canvas canvas = new Canvas(bm1);
		canvas.drawBitmap(bm, m, paint);
		return bm1;
	}

	public static Bitmap decodeUriAsBitmap(Context context, Uri uri, int degree) {
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(context.getContentResolver()
					.openInputStream(uri));
			bitmap = PhotoRotation(bitmap, degree);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}

	public static Bitmap decodeUriAsBitmap(Context context, Uri uri) {
		Bitmap bitmap = null;
		try {
			// BitmapFactory.Options opts=new Options();
			// opts.inJustDecodeBounds=true;
			bitmap = BitmapFactory.decodeStream(context.getContentResolver()
					.openInputStream(uri));
			// bitmap = BitmapFactory.decodeStream(context.getContentResolver()
			// .openInputStream(uri), new Rect(), opts);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}

	public static Bitmap getViewBitmap(View v) {

		v.clearFocus();
		v.setPressed(false);

		// 能画缓存就返回false
		boolean willNotCache = v.willNotCacheDrawing();
		v.setWillNotCacheDrawing(false);
		int color = v.getDrawingCacheBackgroundColor();
		v.setDrawingCacheBackgroundColor(0);
		if (color != 0) {
			v.destroyDrawingCache();
		}
		v.buildDrawingCache();
		Bitmap cacheBitmap = v.getDrawingCache();
		if (cacheBitmap == null) {
			Log.e(TAG, "failed getViewBitmap(" + v + ")",
					new RuntimeException());
			return null;
		}
		Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
		// Restore the view
		v.destroyDrawingCache();
		v.setWillNotCacheDrawing(willNotCache);
		v.setDrawingCacheBackgroundColor(color);
		return bitmap;
	}

	/**
	 * create the bitmap from a byte array 生成水印图片
	 * 
	 * @param src
	 *            the bitmap object you want proecss
	 * @param watermark
	 *            the water mark above the src
	 * @return return a bitmap object ,if paramter's length is 0,return null
	 */
	public static Bitmap createBitmap(Bitmap src, Bitmap watermark) {
		String tag = "createBitmap";
		Log.d(tag, "create a new bitmap");
		if (src == null) {
			return null;
		}
		int w = src.getWidth();
		int h = src.getHeight();
		int ww = watermark.getWidth();
		int wh = watermark.getHeight();
		// create the new blank bitmap
		Bitmap newb = Bitmap.createBitmap(w, h, Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
		Canvas cv = new Canvas(newb);
		// draw src into
		cv.drawBitmap(src, 0, 0, null);// 在 0，0坐标开始画入src
		// draw watermark into
		cv.drawBitmap(watermark, w - ww -25, h - wh - 25, null);// 在src的右下角画入水印
		// save all clip
		cv.save(Canvas.ALL_SAVE_FLAG);// 保存
		// store
		cv.restore();// 存储
		return newb;
	}

	/**
	 * 获得带倒影的图片方法
	 */
	public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
		final int reflectionGap = 4;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2,
				width, height / 2, matrix, false);

		Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
				(height + height / 2), Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);
		canvas.drawBitmap(bitmap, 0, 0, null);
		Paint deafalutPaint = new Paint();
		canvas.drawRect(0, height, width, height + reflectionGap, deafalutPaint);

		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
				bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
				0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		// Set the Transfer mode to be porter duff and destination in
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		// Draw a rectangle using the paint with our linear gradient
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
				+ reflectionGap, paint);
		return bitmapWithReflection;
	}

	/**
	 * 获得圆角图片的方法
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	/**
	 * 将Drawable转化为Bitmap
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, drawable
				.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		return bitmap;

	}

	/**
	 * 放大缩小图片
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidht = ((float) w / width);
		float scaleHeight = ((float) h / height);
		matrix.postScale(scaleWidht, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		return newbmp;
	}

	/**
	 * 图片透明度处理
	 * 
	 * @param sourceImg
	 *            原始图片
	 * @param number
	 *            透明度
	 * @return
	 */
	public static Bitmap setAlpha(Bitmap sourceImg, int number) {
		int[] argb = new int[sourceImg.getWidth() * sourceImg.getHeight()];
		sourceImg.getPixels(argb, 0, sourceImg.getWidth(), 0, 0,
				sourceImg.getWidth(), sourceImg.getHeight());// 获得图片的ARGB值
		number = number * 255 / 100;
		for (int i = 0; i < argb.length; i++) {
			// argb = (number << 24) | (argb & 0x00FFFFFF);// 修改最高2位的值
		}
		sourceImg = Bitmap.createBitmap(argb, sourceImg.getWidth(),
				sourceImg.getHeight(), Config.ARGB_8888);
		return sourceImg;
	}

	/***
	 * 绘制带有边框的文字
	 * 
	 * @param strMsg
	 *            ：绘制内容
	 * @param g
	 *            ：画布
	 * @param paint
	 *            ：画笔
	 * @param setx
	 *            ：：X轴起始坐标
	 * @param sety
	 *            ：Y轴的起始坐标
	 * @param fg
	 *            ：前景色
	 * @param bg
	 *            ：背景色
	 */
	public void drawText(String strMsg, Canvas g, Paint paint, int setx,
			int sety, int fg, int bg) {
		paint.setColor(bg);
		g.drawText(strMsg, setx + 1, sety, paint);
		g.drawText(strMsg, setx, sety - 1, paint);
		g.drawText(strMsg, setx, sety + 1, paint);
		g.drawText(strMsg, setx - 1, sety, paint);
		paint.setColor(fg);
		g.drawText(strMsg, setx, sety, paint);
		g.restore();
	}

	public static Bitmap drawbitmap(int color,int w,int h){
		Bitmap bitmap=Bitmap.createBitmap(w, h, Config.ARGB_8888);
		Canvas canvas=new Canvas(bitmap);
		canvas.drawColor(color);
		return bitmap;
	}
	public static Bitmap drawbitmap(int color,int w,int h,float density){
		Bitmap bitmap=Bitmap.createBitmap(w, h, Config.ARGB_8888);
		bitmap.setDensity((int)density);
		Canvas canvas=new Canvas(bitmap);
		canvas.drawColor(color);
		return bitmap;
	}
	
	
	
	/**
	 * 将彩色图转换为灰度图
	 * 
	 * @param img
	 *            位图
	 * @return 返回转换好的位图
	 */
	public static Bitmap convertGreyImg(Bitmap img) {
		int width = img.getWidth(); // 获取位图的宽
		int height = img.getHeight(); // 获取位图的高

		int[] pixels = new int[width * height]; // 通过位图的大小创建像素点数组

		img.getPixels(pixels, 0, width, 0, 0, width, height);
		int alpha = 0xFF << 24;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				int grey = pixels[width * i + j];

				int red = ((grey & 0x00FF0000) >> 16);
				int green = ((grey & 0x0000FF00) >> 8);
				int blue = (grey & 0x000000FF);

				grey = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);
				grey = alpha | (grey << 16) | (grey << 8) | grey;
				pixels[width * i + j] = grey;
			}
		}
		Bitmap result = Bitmap.createBitmap(width, height, Config.RGB_565);
		result.setPixels(pixels, 0, width, 0, 0, width, height);
		return result;
	}

	// 压缩图片大小
	public static Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}
	
	
	
	public static boolean saveBitmap(String path,Bitmap bitmap){
		File f=new File(path);
		try {
			bitmap.compress(CompressFormat.JPEG, 100, new FileOutputStream(f));
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}
}
