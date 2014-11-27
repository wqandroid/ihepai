package com.wq.letpapa.ui;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import net.tsz.afinal.http.AjaxParams;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.wq.letpapa.R;
import com.wq.letpapa.adapter.NotifAdapter;
import com.wq.letpapa.bean.NotifBean;
import com.wq.letpapa.ui.base.BaseActivity;
import com.wq.letpapa.utils.JsonUtil;

public class AboutMeActivity extends SwipeBackActivity {

	// http://hepai.dianjin169.com/api.php?m=Index&a=at_list&user_id=2&type=list&lasttime=0&pagesize=10

	PullToRefreshListView pullToRefreshListView1;

	NotifAdapter adapter;
	ArrayList<NotifBean> datas = new ArrayList<NotifBean>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_about_layout);
		pullToRefreshListView1 = (PullToRefreshListView) findViewById(R.id.pullToRefreshListView1);
		adapter = new NotifAdapter(this, datas);
		pullToRefreshListView1.setAdapter(adapter);
		pullToRefreshListView1.getRefreshableView().setDividerHeight(0);
		pullToRefreshListView1.setMode(Mode.BOTH);
		pullToRefreshListView1
				.setOnRefreshListener(new OnRefreshListener2<ListView>() {
					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						datas.clear();
						loadData(System.currentTimeMillis());
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						if (datas.size() == 0) {
							loadData(System.currentTimeMillis());
						}
						loadData(datas.get(datas.size() - 1).getInp_time());
					}
				});
		loadData(System.currentTimeMillis());

		pullToRefreshListView1
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {

						NotifBean bean = (NotifBean) arg0.getAdapter().getItem(arg2);

						// 别人参与我的合拍，这跳转到MergePhotoDetailActivity
						if (bean.getData_type().equals("mphoto")) {
							Intent in = new Intent(mContext,
									XPhotoDetailActivity.class);
							in.putExtra("xid", bean.getXid());
							in.putExtra("type", "mphoto");
							startActivity(in);
						} else {
							if (bean.getType().equals("mphoto")) {
								Intent in = new Intent(mContext,
										XPhotoDetailActivity.class);
								in.putExtra("xid", bean.getXid());
								in.putExtra("type", "mphoto");
								startActivity(in);
							} else {
								Intent in = new Intent(mContext,
										XPhotoDetailActivity.class);
								in.putExtra("xid", bean.getXid());
								in.putExtra("type", "photo");
								startActivity(in);
							}
						}
					}
				});
	}

	public void loadData(long lasttime) {
		AjaxParams params = new AjaxParams();
		params.put("user_id", getUid());
		params.put("type", "list");
		params.put("lasttime", lasttime + "");
		params.put("pagesize", "5");
		sendGet(ABOUT_ME + "&" + params.toString(), null);
	}

	@Override
	public void onSuccess(String url, Object obj) {
		super.onSuccess(url, obj);
		pullToRefreshListView1.onRefreshComplete();
		if (url.startsWith(ABOUT_ME) && JsonUtil.isSuccess(obj)) {
			logi(obj.toString());
			try {
				JSONObject array = new JSONObject(obj.toString());
				JsonUtil.parseNotifBeans(datas, array);
				adapter.notifyDataSetChanged();
				saveString(SP_LASTTIME, System.currentTimeMillis());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onError(String url, String msg) {
		super.onError(url, msg);
		pullToRefreshListView1.onRefreshComplete();
	}

}
