package com.wq.letpapa.bean;

import java.util.ArrayList;

import android.graphics.Bitmap;

public class DataFactory {

//	static ArrayList<NewPhotoBeans> beans;
	public static Bitmap bitmap;// 选择合拍试图view

	/*** 获取最新发布定数据集合 */
//	public static ArrayList<NewPhotoBeans> getNewPhotoLists() {
//		if (beans == null) {
//			beans = new ArrayList<NewPhotoBeans>();
//		}
//		return beans;
//	}

	
	static ArrayList<ThemeTopic> ThemeTopic;
	
	/** 获取最新合拍的数据集合 **/
	public static ArrayList<ThemeTopic> getThemeTopicLists() {
		if (ThemeTopic == null) {
			ThemeTopic = new ArrayList<ThemeTopic>();
		}
		return ThemeTopic;
	}
	

}
