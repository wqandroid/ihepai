package com.wq.letpapa.ui;

import java.util.HashMap;
import java.util.Set;

import net.tsz.afinal.http.AjaxParams;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.utils.UIHandler;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;

import com.wq.letpapa.R;
import com.wq.letpapa.customview.PopToast;
import com.wq.letpapa.ui.base.BaseActivity;

public class AuthActivty extends BaseActivity implements Callback ,PlatformActionListener{

	public static final int OAUTH_ERROR = 101;
	public static final int OAUTH_SUCCESS = 102;
	public static final int OAUTH_CANCLE = 103;
	public static final int OAUTH_OVER = 104;
	Handler handler;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		handler=new Handler(this);
	}
	@Override
	public boolean handleMessage(Message msg) {
		return false;
	}
	
	protected void authorize(Platform plat) {
		if (plat == null) {
			return;
		}
		if (plat.isValid()) {
			String userId = plat.getDb().getUserId();
			if (userId != null) {
				Message message = new Message();
				message.obj = plat;
				message.what = OAUTH_OVER;
				UIHandler.sendMessage(message, this);
				return;
			}
		}
		plat.setPlatformActionListener(this);
		plat.showUser(null);
	}
	
	
	public void starMainActivity(){
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				startActivity(SquarActivity.class);
				overridePendingTransition(R.anim.act_in, R.anim.act_exit);
				finish();
				PopToast.dismiss();
			}
		}, 1200);
	}

	AjaxParams params = new AjaxParams();
	protected void Login(Platform platform) {
		String uid = platform.getDb().getUserId();
		String username = platform.getDb().getUserName();
		String icont = platform.getDb().getUserIcon();
		String sex = platform.getDb().getUserGender();
		if (isNull(sex) || isNull(uid)) {
			removeAccount();
			return;
		}
		params.put("openid", uid);
		params.put("name", username);
		if (sex.equals("m") || sex.equals("f")) {
			if (sex.equals("m")) {
				sex = "1";
			} else {
				sex = "0";
			}
		}
		params.put("sex", sex);
		if (!icon.equals("")) {
			params.put("image", this.icon);
		} else {
			params.put("image1", icont);
		}
		params.put("plat", platform.getName());
		logi(params.toString());
		sendPost(LOGIN_URL, params);
	}
	
	@Override
	public void onCancel(Platform plat, int action) {
		Message msg = new Message();
		msg.what = OAUTH_CANCLE;
		msg.arg2 = action;
		msg.obj = plat;
		UIHandler.sendMessage(msg, this);
	}
	
	String icon = "";
	@Override
	public void onComplete(Platform plat, int action,
			HashMap<String, Object> res) {
		if(plat.getName().equals(QZone.NAME)||plat.getName().equals(QQ.NAME)){
			Set<String> key = res.keySet();
			savePlat(plat.getName());
			for (String string : key) {
				if (string.equals("figureurl_qq_2")) {
					this.icon= res.get(string).toString();
					params.put("image", res.get(string).toString());
				} else if (string.equals("figureurl_qq_1")) {
					params.put("image1", res.get(string).toString());
				}
			} // figureurl_qq_2
		}else{//如果是新浪微博登陆在设置头像
			
		}
		Message msg = new Message();
		msg.what = OAUTH_SUCCESS;
		msg.arg2 = action;
		msg.obj = plat;
		UIHandler.sendMessage(msg, this);
	}

	@Override
	public void onError(Platform plat, int action, Throwable t) {
		Message msg = new Message();
		msg.what = OAUTH_ERROR;
		msg.arg2 = action;
		msg.obj = plat;
		UIHandler.sendMessage(msg, this);
	}

	
	
	
}
