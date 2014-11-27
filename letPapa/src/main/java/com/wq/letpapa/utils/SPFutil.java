package com.wq.letpapa.utils;

import android.content.Context;
import android.content.SharedPreferences;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qzone.QZone;

import com.wq.letpapa.bean.User;

public class SPFutil {

	public static String USER_NAME = "user.spf";

	public static void saveUser(Context context, User user) {
		SharedPreferences spf = context.getSharedPreferences(USER_NAME,
				Context.MODE_PRIVATE);
		spf.edit().putString("uid", user.getUid()).commit();
		spf.edit().putString("uname", user.getName()).commit();
		spf.edit().putString("icon", user.getIcon()).commit();
		spf.edit().putString("sex", user.getSex()).commit();
	}

	public static User getUser(Context context) {
		SharedPreferences spf = context.getSharedPreferences(USER_NAME,
				Context.MODE_PRIVATE);
		User user = null;
		String uid = spf.getString("uid", null);
		// if (uid.equals(""))
		// return null;
		user = new User();
		user.setUid(uid);
		user.setName(spf.getString("uname", null));
		user.setIcon(spf.getString("icon", null));
		user.setSex(spf.getString("sex", null));
		return user;
	}

	/**
	 * 设置未读系统消息 未读条数
	 * 
	 * @param context
	 * @param isclear
	 *            是清0还是 增加
	 */
	public static void setNotReadCount(Context context, boolean isclear) {
		SharedPreferences spf = context.getSharedPreferences("msg",
				Context.MODE_PRIVATE);
		if (isclear) {
			spf.edit().putInt("count", 0).commit();
		} else {
			int n = getNotReadCount(context);
			spf.edit().putInt("count", n++).commit();
		}

	}

	public static int getNotReadCount(Context context) {
		SharedPreferences spf = context.getSharedPreferences("msg",
				Context.MODE_PRIVATE);
		return spf.getInt("count", 0);
	}

	/***/
	public static void removeAccount(Context context) {
		SharedPreferences spf = context.getSharedPreferences(USER_NAME,
				Context.MODE_PRIVATE);
		spf.edit().putString("uid", "").commit();
		String plat = spf.getString("plat", QZone.NAME);
		Platform platform = ShareSDK.getPlatform(context, plat);
		if (platform.isValid()) {
			platform.removeAccount();
		}
	}
	public static void setChatBg(Context context, String key, String url) {
		SharedPreferences spf = context.getSharedPreferences("chat",
				Context.MODE_PRIVATE);
		spf.edit().putString(key, url).commit();
	}
	public static String getChatBg(Context context, String key){
		SharedPreferences spf = context.getSharedPreferences("chat",
				Context.MODE_PRIVATE);
		if(spf.contains(key)){
			return spf.getString(key, "");
		}
		return "";
	}
	
	
	
	

}
