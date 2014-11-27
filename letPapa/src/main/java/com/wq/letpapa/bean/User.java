package com.wq.letpapa.bean;

import org.json.JSONException;
import org.json.JSONObject;

public class User extends LBObject {

	/**
	 *  "id": "34",
            "inper": "",
            "inp_time": "2014-08-06 22:10:30",
            "del": "0",
            "sort": "0",
            "user_id": "2",
            "pic": "",
            "pic1": "",
            "sex": "0",
            "describe": "0",
            "lon": "116.42784",
            "lat": "40.083467",
            "con_num": "0",
            "share_num": "0",
            "praise_num": "0",
            "image": "http://hp.ccmima.com/Uploads/photo/53e23756cd917.jpg",
            "laver": "3",
            "areas": "北京市",
            "user": {
                "id": "2",
                "inper": "",
                "inp_time": "0000-00-00 00:00:00",
                "del": "0",
                "sort": "0",
                "openid": "853AC5F798DEB42FF6A55F8B634E8DE2",
                "name": "王琼",
                "describe": "0",
                "sex": "1",
                "pic": "",
                "plat": "QZone",
                "device": "android",
                "reg_time": "2014-07-28 13:34:01",
                "login_time": "2014-08-04 14:31:04",
                "pic1": "0",
                "birthday": "0000-00-00",
                "image": "http://q.qlogo.cn/qqapp/1101848116/853AC5F798DEB42FF6A55F8B634E8DE2/40",
                "image1": "http://q.qlogo.cn/qqapp/1101848116/853AC5F798DEB42FF6A55F8B634E8DE2/40"
            },
            "time": "11小时前",
            "image1": "http://hp.ccmima.com/Uploads/photo/30_53e23756cd917.jpg"
	 */
	private static final long serialVersionUID = 4813182752631021381L;

	
	/**
	 * msg	Yes	返回错误信息
id	No	用户id
name	No	用户昵称
sex	No	用户头像
image	No	头像地址
image1	No	头像缩略图地址

	 */
	
	private String uid;
	private String name;
	private String icon;
	private String sex;
	private String thumb;

	public User() {
	}

	public User(JSONObject jsonObject) {
		try {
			uid = jsonObject.getString("id");
			name = jsonObject.getString("name");
			thumb = jsonObject.getString("image1");
			icon = jsonObject.getString("image");
			if(icon.length()<10){
				icon=thumb;
			}
			sex = jsonObject.getString("sex");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	
	
	
	public String getThumb() {
		return thumb;
	}

	public void setThumb(String thumb) {
		this.thumb = thumb;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}


	

}
