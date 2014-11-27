package com.wq.letpapa.bean;

import org.json.JSONException;
import org.json.JSONObject;

public class ThemeTopic {

	/**
	 * id: "3",
title: "寻找另一半",
describe: "寻找另一半脸？",
inp_time: "0",
del: "0",
weight: "1",
sort: "1",
photo_num: "0",
mphoto_num: "0",
color: "aa00ff",
image: "0"
	 */
	
	private String id;
	private String title;
	private String describe;
	private int weight; 
	private int sort;
	private int photo_num;
	private int mphoto_num;
	private String color;
	private String image;
	
	
	public ThemeTopic(){}
	public ThemeTopic(JSONObject jsonObject){
		try {
			this.id=jsonObject.getString("id");
			this.title=jsonObject.getString("title");
			this.describe=jsonObject.getString("describe");
			this.color=jsonObject.getString("color");
			this.image=jsonObject.getString("image");
			this.weight=jsonObject.getInt("weight");
			this.sort=jsonObject.getInt("sort");
			this.photo_num=jsonObject.getInt("photo_num");
			this.mphoto_num=jsonObject.getInt("mphoto_num");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescribe() {
		return describe;
	}
	public void setDescribe(String describe) {
		this.describe = describe;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	public int getSort() {
		return sort;
	}
	public void setSort(int sort) {
		this.sort = sort;
	}
	public int getPhoto_num() {
		return photo_num;
	}
	public void setPhoto_num(int photo_num) {
		this.photo_num = photo_num;
	}
	public int getMphoto_num() {
		return mphoto_num;
	}
	public void setMphoto_num(int mphoto_num) {
		this.mphoto_num = mphoto_num;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	
	
	
	
}
