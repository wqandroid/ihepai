package com.wq.letpapa.ui.base;

import org.json.JSONObject;

import net.tsz.afinal.http.AjaxParams;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.wq.letpapa.R;
import com.wq.letpapa.bean.User;
import com.wq.letpapa.utils.ACache;
import com.wq.letpapa.utils.Constant;
import com.wq.letpapa.utils.HttpUtil;
import com.wq.letpapa.utils.HttpUtil.LoadCallBack;
import com.wq.letpapa.utils.log.Trace;

public class BaseFragment extends Fragment implements LoadCallBack, Constant {

	public ProgressDialog Basedialog;
	Context mContext;
	int screenWidth;
	int screenHeight;
	public int GONE = View.GONE;
	public int VISIBLE = View.VISIBLE;
	public int INVISIBLE = View.INVISIBLE;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		DisplayMetrics dm = new DisplayMetrics();
		// 取得窗口属性
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		// 窗口的宽度
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
	}

	public void onResume() {
		super.onResume();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = getActivity();
		return super.onCreateView(inflater, container, savedInstanceState);
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
		Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
	}

	/** 长时间显示Toast提示(来自String) **/
	protected void showLongToast(String text) {
		Toast.makeText(getActivity(), text, Toast.LENGTH_LONG).show();
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
	protected void loge(String msg) {
		Trace.e(msg);
	}

	/** Debug输出Log日志 **/
	protected void loge(String tag, String msg) {
		Trace.e(msg);
	}

	/** Error输出Log日志 **/
	protected void loge(String msg, Exception exception) {
		Trace.e("laiba", msg, exception);
	}

	/** 通过Class跳转界面 **/
	protected void startActivity(Class<?> cls) {
		startActivity(cls, null);
	}

	/** 含有Bundle通过Class跳转界面 **/
	protected void startActivity(Class<?> cls, Bundle bundle) {
		Intent intent = new Intent();
		intent.setClass(getActivity(), cls);
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
		Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
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
		Basedialog = new ProgressDialog(getActivity(),
				R.style.custom_alert_dialog);
		Basedialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		Basedialog.setMessage(msg);
		Basedialog.setIndeterminate(false);
		Basedialog.setCancelable(false);
		Basedialog.show();
	}

	/****
	 * -------------------cache------------------------------------
	 *  添加缓存 获取缓存 移除缓存 
	 */
	public void addCache(Class<?> cls, JSONObject data) {
		ACache.get(getActivity()).put(cls.getName(), data);
	}
	public JSONObject getCache(Class<?> cls) {
		return ACache.get(getActivity()).getAsJSONObject(cls.getName());
	}
	public void removeCache(Class<?> cls) {
		ACache.get(getActivity()).remove(cls.getName());
	}
	public void removeAllCache() {
		ACache.get(getActivity()).clear();
	}
	/**** -------------------cache------------------------------------ */

	/**
	 * 发起get请求 并显示状态对话框
	 * 
	 * @param url
	 *            请求地址
	 * @param params
	 *            参数
	 */
	public void sendGet(String url, AjaxParams params) {
		if (checkNet(getActivity())) {
			HttpUtil.sendGet(url, params, this);
		} else {
			t("连接网络异常");
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
		if (checkNet(getActivity())) {
			HttpUtil.sendPost(url, params, this);
		} else {
			t("连接网络异常");
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
		HttpUtil.downLoadFile(url, filename, this);
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

	public boolean checkNet(Context context) {
		ConnectivityManager con = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkinfo = con.getActiveNetworkInfo();
		if (networkinfo == null || !networkinfo.isAvailable()) {
			Toast.makeText(context, "无网络连接,请检查网络..", Toast.LENGTH_SHORT).show();
			return false;
		}
		boolean isWiFi = false;
		if (isWiFi) {
			boolean wifi = con.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
					.isConnectedOrConnecting();
			if (!wifi) { // 提示使用wifi
				Toast.makeText(context, "建议您使用WiFi以减少流量！", Toast.LENGTH_LONG)
						.show();
			}
		}
		return true;
	}

	ImageLoader imageLoader;

	protected ImageLoader getImageLoader() {
		if (imageLoader == null) {
			imageLoader = ImageLoader.getInstance();
		}
		return imageLoader;
	}

	protected void displayImage(ImageView imageView, String uri) {
		displayImage(imageView, uri, null);
	}

	protected void displayImage(ImageView imageView, String uri,
			ImageLoadingListener listener) {
		getImageLoader().displayImage(uri, imageView, listener);
	}

	@Override
	public void onStar(String url) {

	}

	@Override
	public void onSuccess(String url, Object obj) {
		HideDialog();
	}

	@Override
	public void onLoading(String url, long count, long current) {

	}

	@Override
	public void onError(String url, String msg) {
		t(msg);
		HideDialog();
		loge(msg);
	}
	/*** --------------------user setting----------------------------- */
	public SharedPreferences getSettingSP() {
		SharedPreferences preferences = getActivity().getSharedPreferences("setting.spf",
				Context.MODE_PRIVATE);
		return preferences;
	}
	public void saveBool(String key,boolean value){
		getSettingSP().edit().putBoolean(key, value).commit();
	}
	public boolean getValue(String key,Boolean defaultvalue){
	 return	getSettingSP().getBoolean(key, defaultvalue);
	}
	public void saveint(String key,int value){
		getSettingSP().edit().putInt(key, value).commit();
	}
	public int getValue(String key,int defaultvalue){
	 return	getSettingSP().getInt(key, defaultvalue);
	}
	public void saveString(String key,String value){
		getSettingSP().edit().putString(key, value).commit();
	}
	public String getValue(String key,String defaultvalue){
	 return	getSettingSP().getString(key, defaultvalue);
	}
	
	/*** --------------------user----------------------------- */

	public SharedPreferences getSharedPreferences() {
		SharedPreferences preferences = getActivity().getSharedPreferences("User.spf",
				Context.MODE_PRIVATE);
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
		ShareSDK.getPlatform(getActivity(), getPlat()).removeAccount();
		getSharedPreferences().edit().clear().commit();
	}
	/*** -------------------------------- */

	@Override
	public void onSuccess(String url, String tag, Object obj) {
		
	}
}
