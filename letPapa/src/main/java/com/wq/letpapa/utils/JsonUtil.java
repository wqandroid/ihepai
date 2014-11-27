package com.wq.letpapa.utils;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.wq.letpapa.bean.NotifBean;
import com.wq.letpapa.bean.XPhotos;

public class JsonUtil {

	public static boolean isSuccess(Object object) {
		return isSuccess(object.toString());
	}

	public static boolean isSuccess(String object) {
		String code;
		try {
			JSONObject jsonObject = new JSONObject(object);
			code = jsonObject.getString("code");
			if (code.equals("1"))
				return true;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static int getPagenum(Object object) {
		int msg;
		try {
			JSONObject jsonObject = new JSONObject(object.toString());
			msg = jsonObject.getInt("pagenum");
			return msg;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static int getCount(Object object) {
		int msg;
		try {
			JSONObject jsonObject = new JSONObject(object.toString());
			msg = jsonObject.getInt("count");
			return msg;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static String getMessage(Object object) {
		String msg;
		try {
			JSONObject jsonObject = new JSONObject(object.toString());
			msg = jsonObject.getString("msg");
			return msg;
			// return Base64Util.decode(msg);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	
	
	public static void parseNotifBeans(ArrayList<NotifBean> beans,
			JSONObject object) {
		try {
			JSONArray array = object.getJSONArray("list");
			int n = array.length();
			for (int i = 0; i < n; i++) {
				beans.add(new NotifBean(array.getJSONObject(i)));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
	
	

	public static void parseXPhotosBeans(ArrayList<XPhotos> beans,
			JSONObject object,long id) {
		try {
			JSONArray array = object.getJSONArray("list");
			int n = array.length();
			for (int i = 0; i < n; i++) {
				XPhotos mergeBea = new XPhotos(array.getJSONObject(i));
				if(mergeBea.getId()==id){
					continue;
				}
				beans.add(mergeBea);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
	
	
	
	
	

	public static void parseXPhotosBeans(ArrayList<XPhotos> beans,
			JSONObject object) {
		try {
			JSONArray array = object.getJSONArray("list");
			int n = array.length();
			for (int i = 0; i < n; i++) {
				XPhotos mergeBea = new XPhotos(array.getJSONObject(i));
				beans.add(mergeBea);
//				if (id == -1) {
//					beans.add(mergeBea);
//				} else {
//					if (id != mergeBea.getId()) {
//						
//					}
//				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
	
	
	
	

}
