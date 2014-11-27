package com.wq.letpapa.bean;

import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONObject;

public class LBObject implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String getJsonValue(JSONObject obj, String key) {
		return String.valueOf(getJsonValue(obj, key, ""));
	}

	protected JSONArray getJsonArray(JSONObject obj, String key) {
		try {
			return (JSONArray) getJsonValue(obj, key, new JSONArray());
		} catch (Exception e) {
			return new JSONArray();
		}
	}

	protected Object getJsonValue(JSONObject obj, String key,
			Object defaultValue) {
		if ((boolean) obj.has(key)) {
			try {
				Object o = obj.get(key);
				if(defaultValue instanceof Boolean){
					return (Boolean) o;
				} else if(defaultValue instanceof Integer){
					return (Integer) o;
				} else if(defaultValue instanceof JSONArray){
					return (JSONArray) o;
				}
				return o;
			} catch (Exception e) {
				return defaultValue;
			}
		} else {
			return defaultValue;
		}
	}
}
