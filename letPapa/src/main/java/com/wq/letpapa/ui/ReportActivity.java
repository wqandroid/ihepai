package com.wq.letpapa.ui;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import net.tsz.afinal.http.AjaxParams;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.wq.letpapa.R;
import com.wq.letpapa.utils.JsonUtil;

public class ReportActivity extends SwipeBackActivity {

	EditText editText1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_report_layout);
		editText1 = (EditText) findViewById(R.id.editText1);

		findViewById(R.id.button1).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (editText1.getText().toString().trim().length() < 10) {
					t("请写吓你的意见或建议,大于10个字");
				} else {
                     sendReport("", "", editText1.getText().toString());
				}
			}
		});
	}

	public void sendReport(String xuser_id, String xid, String content) {
		AjaxParams ajaxParams = new AjaxParams();
		ajaxParams.put("uid", getUid());
		ajaxParams.put("xuser_id", xuser_id);
		ajaxParams.put("xid", xid);
		ajaxParams.put("content", content);
		ajaxParams.put("type", "feedback");
		sendPost(REPORT_URL, ajaxParams);
	}
	@Override
	public void onSuccess(String url, Object obj) {
		super.onSuccess(url, obj);
		if(JsonUtil.isSuccess(obj)){
			t("谢谢你的反馈");
			editText1.setText("");
		}
	}

}
