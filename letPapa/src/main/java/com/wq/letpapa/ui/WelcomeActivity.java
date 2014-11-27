package com.wq.letpapa.ui;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;

import com.wq.letpapa.R;
import com.wq.letpapa.bean.User;
import com.wq.letpapa.customview.PopToast;
import com.wq.letpapa.utils.JsonUtil;

public class WelcomeActivity extends AuthActivty {

	LinearLayout login_layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.act_welcome_layout);
		login_layout = (LinearLayout) findViewById(R.id.login_layout);
		login_layout.setVisibility(View.INVISIBLE);
		checkLogin();
	}

	Platform platform;

	public void checkLogin() {
		if (getPlat().equals(SinaWeibo.NAME)) {
			platform = new SinaWeibo(this);
		} else {
			platform = new QZone(this);
		}
		if (platform.isValid() && getUid() != null) {
			// 跳转到主界面
			PopToast.dismiss();
			starMainActivity();
		} else {
			removeAccount();
			showLoginLayout();
		}
	}

	public void login(View v) {
		switch (v.getId()) {
		case R.id.sina_login:
			authorize(new SinaWeibo(mContext));
			break;
		case R.id.qq_login:
			authorize(new QZone(mContext));
			break;
		}
		PopToast.showText(mContext, login_layout.getRootView(), "请求登陆中....");
	}

	@Override
	public void onSuccess(String url, Object obj) {
		super.onSuccess(url, obj);
		if (url.equals(LOGIN_URL)) {
			if (JsonUtil.isSuccess(obj)) {
				JSONObject jsonObject;
				try {
					PopToast.showText(mContext, login_layout.getRootView(),
							"登陆成功");
					jsonObject = new JSONObject(obj.toString());
					User user = new User(jsonObject);
					t(JsonUtil.getMessage(obj));
					saveUser(user);
					checkLogin();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				logi("错误:" + JsonUtil.getMessage(obj));
				t("错误:" + JsonUtil.getMessage(obj));
			}
		}
	}

	public void showLoginLayout() {
		login_layout.setVisibility(View.VISIBLE);
		login_layout.startAnimation(AnimationUtils.loadAnimation(this,
				R.anim.view_up));
	}

	@Override
	public boolean handleMessage(Message msg) {
		super.handleMessage(msg);
		switch (msg.what) {
		case OAUTH_CANCLE:
			t(R.string.oauth_cancle);
			PopToast.dismiss(getString(R.string.oauth_cancle));
			break;
		case OAUTH_ERROR:
			t(R.string.oauth_error);
			PopToast.dismiss(getString(R.string.oauth_error));
			break;
		case OAUTH_SUCCESS:
			t(R.string.oauth_success);
			PopToast.showText(mContext, login_layout.getRootView(),
					"登陆中....");
			Platform platform = (Platform) msg.obj;
			Login(platform);
			break;
		case OAUTH_OVER:
			Platform plat = (Platform) msg.obj;
			Login(plat);
			break;
		}
		return false;
	}

}
