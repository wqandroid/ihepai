package com.wq.letpapa.ui;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.wq.letpapa.R;

@SuppressWarnings("deprecation")
public class WQTabActivity extends TabActivity implements
		OnCheckedChangeListener, OnClickListener {

	TabHost tabHost;

	ImageView camera;
	RadioButton home, user;
	RelativeLayout home_layout, user_layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_tabs_layout);
		tabHost = getTabHost();
		TabSpec spec = tabHost.newTabSpec("home")
				.setContent(new Intent(this, SquarActivity.class))
				.setIndicator("home");
		tabHost.addTab(spec);
		TabSpec userspec = tabHost.newTabSpec("user")
				.setContent(new Intent(this, UserInfoActivity.class))
				.setIndicator("user");
		tabHost.addTab(userspec);

		camera = (ImageView) findViewById(R.id.camera);
		home = (RadioButton) findViewById(R.id.home);
		user = (RadioButton) findViewById(R.id.user);
		user_layout = (RelativeLayout) findViewById(R.id.user_layout);
		home_layout = (RelativeLayout) findViewById(R.id.home_layout);
		user_layout.setOnClickListener(this);
		home_layout.setOnClickListener(this);
		home.setOnCheckedChangeListener(this);
		user.setOnCheckedChangeListener(this);
		
		
		
		camera.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent in=new Intent(WQTabActivity.this, CaptureActivity.class);
				startActivity(in);
			}
		});
		
		
	}

	@Override
	public void onCheckedChanged(CompoundButton button, boolean isChecked) {

		if (isChecked) {
			if (button.getId() == R.id.home) {
				tabHost.setCurrentTabByTag("home");
				user.setChecked(false);
		
			} else {
				tabHost.setCurrentTabByTag("user");
				home.setChecked(false);
		
			}
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.home_layout:
			tabHost.setCurrentTabByTag("home");
			user.setChecked(false);
			home.setChecked(true);
			break;
		case R.id.user_layout:
			tabHost.setCurrentTabByTag("user");
			home.setChecked(false);
			user.setChecked(true);
			break;
		default:
			break;
		}

	}

}
