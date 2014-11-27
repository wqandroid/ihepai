package com.wq.letpapa.ui.base;

import org.json.JSONObject;

import net.tsz.afinal.http.AjaxParams;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.wq.letpapa.R;
import com.wq.letpapa.bean.User;
import com.wq.letpapa.ui.FirstActivity;
import com.wq.letpapa.ui.WelcomeActivity;
import com.wq.letpapa.utils.ACache;
import com.wq.letpapa.utils.Constant;
import com.wq.letpapa.utils.HttpUtil;
import com.wq.letpapa.utils.HttpUtil.LoadCallBack;
import com.wq.letpapa.utils.log.Trace;

/**
 * 该父类封装了网络请求部分{@link HttpUtil} post get 请求等 只需要调用请求 下面分别为请求回调 void
 * onStar(String url); void onSuccess(String url, Object obj); void
 * onLoading(String url, long count, long current); void onError(String url,
 * String msg); 图片异步加载部分 Aache 是用来做文件缓存 一般缓存网络请求数据json ...
 * 
 * @author wangqiong
 * 
 */
public class BaseActivity extends FragmentActivity implements LoadCallBack,
		Constant {

	public ProgressDialog Basedialog;
	protected Context mContext;
	int screenWidth;
	int screenHeight;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		mContext = this;
		WindowManager windowManager = getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		screenWidth = display.getWidth();
		screenHeight = display.getHeight();
	}

	public boolean isVisible(View v) {
		if (v.getVisibility() == View.VISIBLE) {
			return true;
		}
		return false;
	}

	public void onResume() {
		super.onResume();
	}

	public void onPause() {
		super.onPause();
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

	/** 短暂显示Toast提示(来自String) **/
	protected void showShortToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	/** 长时间显示Toast提示(来自String) **/
	protected void showLongToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	}

	/** Debug输出Log日志 **/
	protected void logi(String msg) {
		Trace.i(msg);
	}

	/** Debug输出Log日志 **/
	protected void logi(String tag, String msg) {
		Trace.i(tag, msg);
	}

	/** Debug输出Log日志 **/
	protected void logd(String tag, String msg) {
		Trace.d(msg);
	}

	/** Error输出Log日志 **/
	protected void loge(String msg) {
		Trace.e(msg);
	}

	/** Error输出Log日志 **/
	protected void loge(String msg, Exception exception) {
		Trace.e("laiba", msg, exception);
	}

	/** Error输出Log日志 **/
	protected void loge(String tag, String msg) {
		Trace.e(tag, msg);
	}

	/** 通过Class跳转界面 **/
	protected void startActivity(Class<?> cls) {
		startActivity(cls, null);
	}

	/** 含有Bundle通过Class跳转界面 **/
	protected void startActivity(Class<?> cls, Bundle bundle) {
		Intent intent = new Intent();
		intent.setClass(this, cls);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		startActivity(intent);
	}

	/**
	 * toast资源字符串
	 * 
	 * @param resId
	 */
	public void t(int resId) {
		try {
			t(this.getString(resId));
		} catch (Exception e) {
			t("" + resId);
		}
	}

	/**
	 * toast字符串
	 * 
	 * @param resId
	 */
	public void t(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	public void HideDialog() {
		if (Basedialog != null) {
			Basedialog.cancel();
		}
		Basedialog = null;
	}

	public void showDialod(String msg) {
		createDialog(msg);
	}

	/**
	 * 创建弹窗
	 */
	public void createDialog(String msg) {
		Basedialog = new ProgressDialog(mContext, R.style.custom_alert_dialog);
		Basedialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		Basedialog.setMessage(msg);
		Basedialog.setIndeterminate(false);
		Basedialog.setCancelable(false);
		Basedialog.show();
	}

	/**
	 * 发起get请求 并显示状态对话框
	 * 
	 * @param url
	 *            请求地址
	 * @param params
	 *            参数
	 */
	public void sendGet(String url, AjaxParams params) {
		if (checkNet(mContext)) {
			HttpUtil.sendGet(url, params, this);
		}else{
			onError(url, "无法连接网络");
		}
	}

	public void sendGet(String url, String tag, AjaxParams params) {
		if (checkNet(mContext)) {
			HttpUtil.sendGet(url, tag, params, this);
		}else{
			onError(url, "无法连接网络");
		}
	}

	/**
	 * 发起post请求 并显示状态对话框
	 * 
	 * @param url
	 *            请求地址
	 * @param params
	 *            参数
	 */
	public void sendPost(String url, AjaxParams params) {
		if (checkNet(mContext)) {
			HttpUtil.sendPost(url, params, this);
		}else{
			onError(url, "无法连接网络");
		}
	}

	public void sendPost(String url, String tag, AjaxParams params) {
		if (checkNet(mContext)) {
			HttpUtil.sendPost(url, tag, params, this);
		}else{
			onError(url, "无法连接网络");
		}
	}

	/**
	 * 发起get请求 并显示状态对话框
	 * 
	 * @param url
	 *            请求地址
	 * @param params
	 *            参数
	 * @param dialogtxt
	 *            对话框内容
	 */
	public void sendGetWhithDialod(String url, AjaxParams params,
			String dialogtxt) {
		sendGet(url, params);
		createDialog(dialogtxt);
	}

	/**
	 * 发起post请求 并显示状态对话框
	 * 
	 * @param url
	 *            请求地址
	 * @param params
	 *            参数
	 * @param dialogtxt
	 *            对话框内容
	 */
	public void sendPostWhithDialod(String url, AjaxParams params,
			String dialogtxt) {
		sendPost(url, params);
		createDialog(dialogtxt);
	}

	public void downloadFile(String url, String filename) {
		System.out.println(url + "\nfile:" + filename);
		if(checkNet(this)){
			HttpUtil.downLoadFile(url, filename, this);
		}else{
			onError(url, "无法连接网络");
		}
	}
	public boolean checkNet(Context context) {
		ConnectivityManager con = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (con == null) {
			Toast.makeText(context, "无网络连接,请检查网络..", Toast.LENGTH_SHORT).show();
			return false;
		} else {
			NetworkInfo info = con.getActiveNetworkInfo();
			if (info != null) {
				if (info.getState() == NetworkInfo.State.CONNECTED) {
					return true;
				}
			}
		}
		Toast.makeText(context, "无网络连接,请检查网络..", Toast.LENGTH_SHORT).show();
		return false;

	}

	/**
	 * Get the screen height.
	 * 
	 * @param context
	 * @return the screen height
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static int getScreenHeight(Activity context) {

		Display display = context.getWindowManager().getDefaultDisplay();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			Point size = new Point();
			display.getSize(size);
			return size.y;
		}
		return display.getHeight();
	}

	/**
	 * Get the screen width.
	 * 
	 * @param context
	 * @return the screen width
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public int getScreenWidth(Activity context) {
		Display display = context.getWindowManager().getDefaultDisplay();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			Point size = new Point();
			display.getSize(size);
			return size.x;
		}
		return display.getWidth();
	}

	public float getDensity(Context context) {
		return context.getResources().getDisplayMetrics().density;
	}

	ImageLoader imageLoader;

	protected ImageLoader getImageLoader() {
		if (imageLoader == null) {
			imageLoader = ImageLoader.getInstance();
		}
		return imageLoader;
	}

	protected void displayImage(ImageView imageView, String uri) {
		getImageLoader().displayImage(uri, imageView);
	}

	protected void displayImage(ImageView imageView, String uri,
			ImageLoadingListener listener) {
		getImageLoader().displayImage(uri, imageView, listener);
	}

	protected void displayImage(ImageView imageView, String uri,
			DisplayImageOptions options) {
		getImageLoader().displayImage(uri, imageView, options);
	}

	/**
	 * 获取一个支持渐入的动画DisplayImageOptions
	 * 
	 * @param duratuion
	 *            渐入时间
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
	 * 
	 * @param radious
	 *            如果等于-1 则是圆形图片
	 * @return
	 */
	public DisplayImageOptions getRoundedBitmapDisplayer(int radious) {
		int resous = R.drawable.white_bg;
		if (radious == -1) {
			resous = R.drawable.white_circle;
		}
		// 使用DisplayImageOptions.Builder()创建DisplayImageOptions
		return new DisplayImageOptions.Builder().showStubImage(resous) // 设置图片下载期间显示的图片
				.showImageForEmptyUri(resous) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(resous) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
				.displayer(new RoundedBitmapDisplayer(radious)) // 设置成圆角图片
				.build(); // 创建配置过得DisplayImageOption对象
	}

	@Override
	public void onStar(String url) {

	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public void onSuccess(String url, Object obj) {
		logi("url:" + url + "\n 结果:" + obj.toString());
		HideDialog();
	}

	@Override
	public void onLoading(String url, long count, long current) {

	}

	@Override
	public void onError(String url, String msg) {
		t(msg);
		HideDialog();
		loge("onError:" + msg);
	}

	public boolean isNull(String str) {
		if (str != null && !str.trim().equals("") && !str.equals("null")) {
			return false;
		}
		return true;
	}

	/****
	 * -------------------cache------------------------------------ 添加缓存 获取缓存
	 * 移除缓存
	 */
	public void addCache(Class<?> cls, JSONObject data) {
		ACache.get(this).put(cls.getName(), data);
	}

	public JSONObject getCache(Class<?> cls) {
		return ACache.get(this).getAsJSONObject(cls.getName());
	}

	public void removeCache(Class<?> cls) {
		ACache.get(this).remove(cls.getName());
	}

	public void removeAllCache() {
		ACache.get(this).clear();
	}

	/*** --------------------user setting----------------------------- */
	public SharedPreferences getSettingSP() {
		SharedPreferences preferences = getSharedPreferences("setting.spf",
				MODE_PRIVATE);
		return preferences;
	}

	public void saveBool(String key, boolean value) {
		getSettingSP().edit().putBoolean(key, value).commit();
	}

	public boolean getValue(String key, Boolean defaultvalue) {
		return getSettingSP().getBoolean(key, defaultvalue);
	}

	public void saveint(String key, int value) {
		getSettingSP().edit().putInt(key, value).commit();
	}

	public int getValue(String key, int defaultvalue) {
		return getSettingSP().getInt(key, defaultvalue);
	}

	public void saveString(String key, long value) {
		getSettingSP().edit().putLong(key, value).commit();
	}

	public long getValue(String key, Long defaultvalue) {
		return getSettingSP().getLong(key, defaultvalue);
	}

	public void saveString(String key, String value) {
		getSettingSP().edit().putString(key, value).commit();
	}

	public String getValue(String key, String defaultvalue) {
		return getSettingSP().getString(key, defaultvalue);
	}

	/*** --------------------user----------------------------- */

	public SharedPreferences getSharedPreferences() {
		SharedPreferences preferences = getSharedPreferences("User.spf",
				MODE_PRIVATE);
		return preferences;
	}

	public void savePlat(String plat) {
		getSharedPreferences().edit().putString("plat", plat).commit();
	}

	public String getPlat() {
		return getSharedPreferences().getString("plat", SinaWeibo.NAME);
	}

	public String getUid() {
		if (getUser() == null) {
			return null;
		}
		return getUser().getUid();
	}

	public User getUser() {
		User user = new User();
		SharedPreferences sp = getSharedPreferences();
		if (sp.getString("id", null) == null) {
			return null;
		}
		/** 用户登录失败 */
		user.setUid(sp.getString("id", null));
		user.setIcon(sp.getString("icon", null));
		user.setSex(sp.getString("sex", null));
		user.setThumb(sp.getString("thumb", null));
		user.setName(sp.getString("name", null));
		return user;
	}

	public void saveUser(User user) {
		SharedPreferences sp = getSharedPreferences();
		sp.edit().putString("name", user.getName()).commit();
		sp.edit().putString("sex", user.getSex()).commit();
		sp.edit().putString("id", user.getUid()).commit();
		sp.edit().putString("thumb", user.getThumb()).commit();
		sp.edit().putString("icon", user.getIcon()).commit();
	}

	/** 注销登录 */
	public void removeAccount() {
		getSharedPreferences().edit().putString("id", null).commit();
		ShareSDK.getPlatform(this, getPlat()).removeAccount();
		getSharedPreferences().edit().clear().commit();
	}

	public void exitlogin() {
		removeAccount();
		ACache.get(mContext).clear();
		Intent ins = new Intent(this, WelcomeActivity.class);
		ins.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(ins);
		finish();
	}

	@Override
	public void onSuccess(String url, String tag, Object obj) {

	}

	/*** -------------------------------- */

	public void dofinish(View v) {
		this.finish();
		overridePendingTransition(R.anim.roll, R.anim.roll_down);
	}
}
