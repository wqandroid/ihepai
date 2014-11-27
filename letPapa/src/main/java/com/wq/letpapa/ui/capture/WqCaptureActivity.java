package com.wq.letpapa.ui.capture;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.wq.letpapa.R;
import com.wq.letpapa.ui.base.BaseActivity;

public class WqCaptureActivity extends BaseActivity {

	PreviewControl control;

	LinearLayout capture_parent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_wqcapture_layout);
		capture_parent=(LinearLayout) findViewById(R.id.capture_parent);
		control=new PreviewControl(capture_parent, this);
	}
	
	@Override
	public void onResume() {
		super.onResume();

		control.OnResume();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		control.onPause();
	}

}
