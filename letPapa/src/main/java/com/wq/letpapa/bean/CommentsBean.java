package com.wq.letpapa.bean;

import org.json.JSONException;
import org.json.JSONObject;

public class CommentsBean {
/**
 *   "id": "3",
            "inper": "",
            "inp_time": "2014-08-13 18:01:17",
            "del": "0",
            "sort": "0",
            "type": "mphoto",
            "xid": "1",
            "user_id": "2",
            "content": "dsadadsadads",
            ruser_id: "3",
            rid: "23",
            ruser: "坐井观天的遐想",
            user{
            ...
            }
 */
	
	
	private long id;
	private String inp_time;
	private String type;
	private long xid;
	private long user_id;
	private String content;
	private User user;
	private long ruser_id;
	private String ruser;//被回复人名字
	private long rid;
	
	public CommentsBean(){}
	public CommentsBean(JSONObject object){
		try {
			this.content=object.getString("content");
			this.inp_time=object.getString("inp_time");
			this.type=object.getString("type");
			this.id=object.getLong("id");
			this.xid=object.getLong("xid");
			this.ruser_id=object.getLong("ruser_id");
			this.user_id=object.getLong("user_id");
			this.ruser=object.getString("ruser");
			JSONObject obj=object.getJSONObject("user");
			this.user=new User(obj);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getInp_time() {
		return inp_time;
	}
	public void setInp_time(String inp_time) {
		this.inp_time = inp_time;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public long getXid() {
		return xid;
	}
	public void setXid(long xid) {
		this.xid = xid;
	}
	public long getUser_id() {
		return user_id;
	}
	public void setUser_id(long user_id) {
		this.user_id = user_id;
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
	public long getRuser_id() {
		return ruser_id;
	}
	public void setRuser_id(long ruser_id) {
		this.ruser_id = ruser_id;
	}
	public String getRuser() {
		return ruser;
	}
	public void setRuser(String ruser) {
		this.ruser = ruser;
	}
	public long getRid() {
		return rid;
	}
	public void setRid(long rid) {
		this.rid = rid;
	}
	
	
	
}
