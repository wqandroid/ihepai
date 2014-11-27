package com.wq.letpapa;

import java.io.File;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import cn.sharesdk.framework.ShareSDK;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
/**
//
//
//                       _oo0oo_
//                      o8888888o
//                      88" . "88
//                      (| -_- |)
//                      0\  =  /0
//                    ___/`---'\___
//                  .' \\|     |// '.
//                 / \\|||  :  |||// \
//                / _||||| -:- |||||- \
//               |   | \\\  -  /// |   |
//               | \_|  ''\---/''  |_/ |
//               \  .-\__  '-'  ___/-. /
//             ___'. .'  /--.--\  `. .'___
//          ."" '<  `.___\_<|>_/___.' >' "".
//         | | :  `- \`.;`\ _ /`;.`/ - ` : | |
//         \  \ `_.   \_ __\ /__ _/   .-` /  /
//     =====`-.____`.___ \_____/___.-`___.-'=====
//                       `=---='
//
//     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//
//               佛祖保佑         永无BUG
//
//
 */
public class MyApplication extends Application {

	private static MyApplication instance;
	@Override
	public void onCreate() {
		super.onCreate();
		initImageLoader(this);
		ShareSDK.initSDK(this);
	}
	
	public static MyApplication getInstance() {
		if (instance == null) {
			instance = new MyApplication();
		}
		return instance;
	}

	/**
	 * 获取缩略图缓存图片路径
	 * 
	 * @param url
	 * @return
	 */
	public String getCacheFile(String url) {
		File file = ImageLoader.getInstance().getDiscCache().get(url);
		if (file.exists()) {
			return file.getAbsolutePath();
		} else
			return null;
	}

	public ImageLoader getLoader(){
		return ImageLoader.getInstance();
	} 
	
	public static void initImageLoader(Context context) {
		int memoryCacheSize;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
			int memClass = ((ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE))
					.getMemoryClass();
			memoryCacheSize = (memClass / 8) * 1024 * 1024; // 1/8 of app memory
															// limit
		} else {
			memoryCacheSize = 2 * 1024 * 1024;
		}

		DisplayImageOptions defaultDisplayImageOptions = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.white_bg)
				.showImageForEmptyUri(R.drawable.white_bg)
				.showImageOnFail(R.drawable.white_bg).cacheInMemory(true)
				.cacheOnDisc(true).build();

		ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)
				.memoryCacheSize(memoryCacheSize)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.defaultDisplayImageOptions(defaultDisplayImageOptions).build();
		ImageLoader.getInstance().init(configuration);
	}

}
