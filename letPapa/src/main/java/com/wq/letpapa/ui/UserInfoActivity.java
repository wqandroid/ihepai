package com.wq.letpapa.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wq.letpapa.R;
import com.wq.letpapa.ui.base.BaseActivity;

public class UserInfoActivity extends BaseActivity {

	TextView tv_fenscount, tv_attioncount, tv_picturecount, tv_newsfens_count,tv_name;
ImageView iv_icon;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_user_layout);
		tv_fenscount = (TextView) findViewById(R.id.tv_fenscount);
		tv_attioncount = (TextView) findViewById(R.id.tv_attioncount);
		tv_picturecount = (TextView) findViewById(R.id.tv_picture_count);
		tv_newsfens_count = (TextView) findViewById(R.id.tv_newfens_count);
		tv_name=(TextView) findViewById(R.id.tv_name);
		iv_icon = (ImageView) findViewById(R.id.iv_icon);
		
	}

	public void Doclick(View v) {
		switch (v.getId()) {
		case R.id.user_headbg:
			startActivity(UserPageActivity.class);
			break;
		case R.id.exit_login:// 退出登录
			exitlogin();
			break;
		case R.id.attion_dym://关注人动态
			break;
		case R.id.aboutme_dym://于我相关
			break;
		case R.id.clear_cache://晴空缓存
			getImageLoader().clearDiscCache();
			break;
		case R.id.about://
			break;
		}
	}

}
