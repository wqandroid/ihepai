package com.wq.letpapa.bean;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class XPhotos implements Serializable {

	/** 合拍的实体类 */

	// "id": "1",
	// "inper": "",
	// "inp_time": "2014-08-09 15:20:01",
	// "del": "0",
	// "sort": "0",
	// "user_id": "2",
	// "user1_id": "3",
	// "photo_id": "12",
	// "pic": "",
	// "pic1": "",
	// "sex": "0",
	// "describe": "Q2Vz5ZCI5ouN55qE ",
	// "lon": "116.367719",
	// "lat": "40.078551",
	// "con_num": "0",
	// "share_num": "0",
	// "praise_num": "0",
	// "image": "http://hp.ccmima.com/Uploads/mphoto/53e5cba163360.jpg",
	// "areas": "北京市",
	// "user": {
	// "id": "2",
	// "inper": "",
	// "inp_time": "0000-00-00 00:00:00",
	// "del": "0",
	// "sort": "0",
	// "openid": "853AC5F798DEB42FF6A55F8B634E8DE2",
	// "name": "王琼",
	// "describe": "0",
	// "sex": "1",
	// "pic": "",
	// "plat": "QZone",
	// "device": "android",
	// "reg_time": "2014-07-28 13:34:01",
	// "login_time": "2014-08-10 22:08:55",
	// "pic1": "0",
	// "birthday": "0000-00-00",
	// "image":
	// "http://q.qlogo.cn/qqapp/1101848116/853AC5F798DEB42FF6A55F8B634E8DE2/100",
	// "image1":
	// "http://q.qlogo.cn/qqapp/1101848116/853AC5F798DEB42FF6A55F8B634E8DE2/40"
	// },
	// "time": "2014-08-09 15:20:01",
	// "image1": "http://hp.ccmima.com/Uploads/mphoto/30_53e5cba163360.jpg"

	private static final long serialVersionUID = 1L;

	long id;
	private String user_id;// 参与合拍人id
	private String user1_id;// 发起合拍人id
	private String describe;
	private String inp_time;
	private long lon, lat;
	private int con_num;// 评论数
	private int share_num;
	private int praise_num;
	private int re_num;// 参与合拍数
	private long photo_id;
	private String image;
	private String areas;// 城市
	private String time;
	private String image1;
	private boolean isparise;
	private boolean isMerge;
	private String type;
	private User user;
	private int laver;
	private int topic_id;
	private String topic_title;

	public XPhotos() {
	}

	public XPhotos(JSONObject object) {
		try {
			this.id = object.getLong("id");
			this.user_id = object.getString("user_id");
			if (object.has("user1_id")) {// 参与合拍
				isMerge = true;
				type = "mphoto";
				this.user1_id = object.getString("user1_id");
				this.photo_id = object.getLong("photo_id");
			} else {
				isMerge = false;
				type = "photo";
				this.user1_id = "";
				this.photo_id = object.getLong("id");
			}
			this.topic_id = object.getInt("topic_id");
			this.describe = object.getString("describe");
			this.inp_time = object.getString("inp_time");
			this.image = object.getString("image");
			this.areas = object.getString("areas");
			this.time = object.getString("time");
			this.image1 = object.getString("image1");
			this.lon = object.getLong("lon");
			this.lat = object.getLong("lat");
			this.con_num = object.getInt("con_num");
			this.share_num = object.getInt("share_num");
			this.praise_num = object.getInt("praise_num");

			if (object.has("is_praise") && object.getInt("is_praise") == 1) {
				this.isparise = true;
			} else {
				this.isparise = false;
			}
			if (object.has("laver")) {
				this.laver = object.getInt("laver");
			}
			if (object.has("re_num")) {
				this.re_num = object.getInt("re_num");
			}
			
			if (object.has("topic_title")) {
				this.topic_title = object.getString("topic_title");
			}
			this.user = new User(object.getJSONObject("user"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public int getRe_num() {
		return re_num;
	}

	public void setRe_num(int re_num) {
		this.re_num = re_num;
	}

	public String getTopic_title() {
		return topic_title;
	}

	public void setTopic_title(String topic_title) {
		this.topic_title = topic_title;
	}

	public int getTopic_id() {
		return topic_id;
	}

	public void setTopic_id(int topic_id) {
		this.topic_id = topic_id;
	}

	public boolean isMerge() {
		return isMerge;
	}

	public void setMerge(boolean isMerge) {
		this.isMerge = isMerge;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getLaver() {
		return laver;
	}

	public void setLaver(int laver) {
		this.laver = laver;
	}

	public long getPhoto_id() {
		return photo_id;
	}

	public void setPhoto_id(long photo_id) {
		this.photo_id = photo_id;
	}

	public boolean isIsparise() {
		return isparise;
	}

	public void setIsparise(boolean isparise) {
		this.isparise = isparise;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getUser1_id() {
		return user1_id;
	}

	public void setUser1_id(String user1_id) {
		this.user1_id = user1_id;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public String getInp_time() {
		return inp_time;
	}

	public void setInp_time(String inp_time) {
		this.inp_time = inp_time;
	}

	public long getLon() {
		return lon;
	}

	public void setLon(long lon) {
		this.lon = lon;
	}

	public long getLat() {
		return lat;
	}

	public void setLat(long lat) {
		this.lat = lat;
	}

	public int getCon_num() {
		return con_num;
	}

	public void setCon_num(int con_num) {
		this.con_num = con_num;
	}

	public int getShare_num() {
		return share_num;
	}

	public void setShare_num(int share_num) {
		this.share_num = share_num;
	}

	public int getPraise_num() {
		return praise_num;
	}

	public void setPraise_num(int praise_num) {
		this.praise_num = praise_num;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getAreas() {
		return areas;
	}

	public void setAreas(String areas) {
		this.areas = areas;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getImage1() {
		return image1;
	}

	public void setImage1(String image1) {
		this.image1 = image1;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
