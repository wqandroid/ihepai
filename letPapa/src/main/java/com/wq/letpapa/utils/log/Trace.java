package com.wq.letpapa.utils.log;

import android.util.Log;

/**
 * 打印日志
 * 
 * @author 王琼
 * @date 2014-4-8
 */
public class Trace {

	static boolean isLog = true;
	static String TAG = "hepai";

	public static void d(String msg) {
		d("lebooo", msg);
	}

	public static void d(String tag, String msg) {
		if (isLog)
			Log.d(tag, msg);
	}

	public static void i(String msg) {
		i(TAG, msg);
	}

	public static void i(String tag, String msg) {
		if (isLog)
			Log.i(tag, msg);
	}

	public static void e(String msg) {
		e(TAG, msg);
	}

	public static void e(String tag, String msg) {
		if (isLog)
			Log.e(tag, msg);
	}

	public static void e(String tag, String msg, Exception e) {
		if (isLog)
			Log.e(tag, msg, e);
//		writetxt(msg + e.getMessage());
	}


}
