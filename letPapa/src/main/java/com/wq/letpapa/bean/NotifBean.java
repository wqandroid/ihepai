package com.wq.letpapa.bean;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class NotifBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6788101780609243270L;
	// reply 回复 |mphoto 参与合拍|praise 赞 |comments 评论
	String data_type;
	long xid;// 合拍的id
	long inp_time;// 时间
	// 发布合拍或者是参与合拍
	String type; // （如果还是参合我的合拍则没有该字段）
	String content;// 回复内容 或者评论内容
	User user;
	String thumb;

	public NotifBean() {
	}

	public NotifBean(JSONObject object) {
		try {
			this.data_type = object.getString("data_type");
			this.inp_time = object.getLong("inp_time");
			if (object.has("xid")) {
				this.xid = object.getLong("xid");
			}else{
				this.xid=object.getLong("id");
			}
			if (object.has("type")) {
				this.type = object.getString("type");
			}
			if (object.has("content")) {
				this.content = object.getString("content");
			}
			this.thumb = object.getString("image1");
			JSONObject obj = object.getJSONObject("user");
			this.user = new User(obj);
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

	public void setXid(long xid) {
		this.xid = xid;
	}

	public String getData_type() {
		return data_type;
	}

	public void setData_type(String data_type) {
		this.data_type = data_type;
	}

	public Long getXid() {
		return xid;
	}

	public void setXid(Long xid) {
		this.xid = xid;
	}

	public long getInp_time() {
		return inp_time;
	}

	public void setInp_time(long inp_time) {
		this.inp_time = inp_time;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
