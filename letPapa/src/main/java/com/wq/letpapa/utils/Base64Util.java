package com.wq.letpapa.utils;

import android.util.Base64;

public class Base64Util {

	
	
	/**
	 * base64 加密字符串
	 * @param str
	 * @return
	 */
	public static String encode(String str){
		  byte [] encode = Base64.encode(str.getBytes(), Base64.DEFAULT);  
	      return new String(encode);  
	}
	/**
	 * base64 解密
	 * @param str
	 * @return
	 */
	public static String decode(String str){
	    try {
			byte [] result = Base64.decode(str, Base64.DEFAULT);  
			return new String(result);
		} catch (Exception e) {
			 return str;
		}  
	}
}
