package com.wq.letpapa.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.wq.letpapa.R;
import com.wq.letpapa.bean.User;

public class LBBaseAdapter extends BaseAdapter {

	private ImageLoader imageLoader;
	protected DisplayImageOptions options;

	protected Context context;
	protected LayoutInflater inflater;
	protected int GONE = View.GONE;
	protected int VISIBLE = View.VISIBLE;
	protected int INVISIBLE = View.INVISIBLE;

	public LBBaseAdapter(Context context) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		screenWidth = getScreenWidth((Activity) context);
	}

	int screenWidth;
	ArrayList<?> datas;

	public LBBaseAdapter(Context context, ArrayList<?> datas) {
		// this(context);
		this.datas = datas;
		this.context = context;
		inflater = LayoutInflater.from(context);
		screenWidth = getScreenWidth((Activity) context);
	}

	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Object getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return null;
	}

	protected ImageLoader getImageLoader() {
		if (imageLoader == null) {
			imageLoader = ImageLoader.getInstance();
		}
		return imageLoader;
	}

	protected void displayImage(ImageView imageView, String uri) {
		getImageLoader().displayImage(uri, imageView);
	}
	
	protected void displayImage(ImageView imageView, String uri,DisplayImageOptions options) {
		getImageLoader().displayImage(uri, imageView, options);
	}


	protected void displayImage(ImageView imageView, String uri,
			ImageLoadingListener listener) {
		getImageLoader().displayImage(uri, imageView, listener);
	}

	protected File getCacheFile(String url) {
		return getImageLoader().getDiscCache().get(url);
	}

	protected Bitmap getMemoryCacheFile(String url) {
		return getImageLoader().getMemoryCache().get(url);
	}

	/**
	 * 显示一批View
	 * 
	 * @param views
	 */
	public void showView(View... views) {
		for (int i = 0; i < views.length; i++) {
			View view = views[i];
			if (view != null) {
				view.setVisibility(View.VISIBLE);
			}
		}
	}

	/**
	 * 隐藏一批View
	 * 
	 * @param views
	 */
	public void hideView(View... views) {
		for (int i = 0; i < views.length; i++) {
			View view = views[i];
			if (view != null) {
				view.setVisibility(View.GONE);
			}
		}
	}

	public int getScreenWidth(Activity context) {
		Display display = context.getWindowManager().getDefaultDisplay();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			Point size = new Point();
			display.getSize(size);
			return size.x;
		}
		return display.getWidth();
	}

	/**
	 * 获取一个支持渐入的动画DisplayImageOptions
	 * @param duratuion 渐入时间
	 * @return
	 */
	public DisplayImageOptions getFadin(int duratuion) {
		// 使用DisplayImageOptions.Builder()创建DisplayImageOptions
		return new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.white_bg) // 设置图片下载期间显示的图片
				.showImageForEmptyUri(R.drawable.white_bg) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.white_bg) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
				// .displayer(new RoundedBitmapDisplayer(14)) // 设置成圆角图片
				.displayer(new FadeInBitmapDisplayer(duratuion)).build(); // 创建配置过得DisplayImageOption对象
	}
	/**
	 * 获取圆角参数的options
	 * @param radious 如果等于-1 则是圆形图片
	 * @return
	 */
	public DisplayImageOptions getRoundedBitmapDisplayer(int radious) {
		// 使用DisplayImageOptions.Builder()创建DisplayImageOptions
		int resous=R.drawable.white_bg;
		if(radious==-1){
			resous=R.drawable.white_circle;
		}
		return new DisplayImageOptions.Builder()
				.showStubImage(resous) // 设置图片下载期间显示的图片
				.showImageForEmptyUri(resous) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(resous) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
				 .displayer(new RoundedBitmapDisplayer(radious)) // 设置成圆角图片
				.build(); // 创建配置过得DisplayImageOption对象
	}

	
	/**
	 * 图片加载第一次显示监听器
	 * 
	 * @author Administrator
	 * 
	 */
	private static class AnimateFirstDisplayListener extends
			SimpleImageLoadingListener {
		static final List<String> displayedImages = Collections
				.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				// 是否第一次显示
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					// 图片淡入效果
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}

}
