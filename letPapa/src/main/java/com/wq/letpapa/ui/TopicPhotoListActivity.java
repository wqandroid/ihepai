package com.wq.letpapa.ui;

import java.util.ArrayList;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.wq.letpapa.MyApplication;
import com.wq.letpapa.R;
import com.wq.letpapa.adapter.XphotAdapter;
import com.wq.letpapa.bean.XPhotos;
import com.wq.letpapa.customview.CustomDialog;
import com.wq.letpapa.ui.UsersDetailActivity.areClickListener;
import com.wq.letpapa.utils.DensityUtil;
import com.wq.letpapa.utils.JsonUtil;

public class TopicPhotoListActivity extends SwipeBackActivity implements
		OnClickListener {

	TextView tv_title;
	int id;
	String name;

	Handler handler = new Handler();

	PullToRefreshGridView listView;
	XphotAdapter adapter;
	int nowpage = 1;// 当前页数
	int allcount = 0;// 一共多少条
	int allpage = 0;// 中共多少页
	ArrayList<XPhotos> beans = new ArrayList<XPhotos>();
	String nowtype = TYPE_MERGE_PHOTO;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.act_in, R.anim.act_exit);
		setContentView(R.layout.act_topicphoto_list_layout);
		tv_title = (TextView) findViewById(R.id.tv_title);
		id = getIntent().getIntExtra("id", 3);
		name = getIntent().getStringExtra("name");
		items = getResources().getStringArray(R.array.theme_data);
		listView = (PullToRefreshGridView) findViewById(R.id.pull_refresh_list);
		adapter = new XphotAdapter(this, beans, handler);
		listView.setAdapter(adapter);
		tv_title.setText(name);
		initListener();
		refrashDate();
	}

	public void initListener() {
		findViewById(R.id.tab_selection).setOnClickListener(this);
		listView.setOnRefreshListener(new OnRefreshListener2<GridView>() {
			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<GridView> refreshView) {
				nowpage = 1;
				refrashDate();
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<GridView> refreshView) {
				if (nowpage == allpage) {
					listView.onRefreshComplete();
					Toast.makeText(mContext, "已经到底了", 0).show();
					return;
				}
				nowpage++;
				refrashDate();
			}
		});
		tv_title.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showTopic();
			}
		});
	}

	private void refrashDate() {
		if (getValue(SP_TOPIC_TYPE, TYPE_MERGE_PHOTO).equals(TYPE_PHOTO)) {
			loadPhotos();
		} else {
			loadMergePhotos();
		}
	}

	public void loadMergePhotos() {
		AjaxParams params = new AjaxParams();
		params.put("topic_id", id + "");
		sendGet(THEME_MERGEPHOTO_LIST + "&" + params.getParamString(),
				TYPE_MERGE_PHOTO, null);
	}

	public void loadPhotos() {
		AjaxParams params = new AjaxParams();
		params.put("topic_id", id + "");
		sendGet(THEME_PHOTO_LIST + "&" + params.getParamString(), TYPE_PHOTO,
				null);
	}

	@Override
	public void onSuccess(String url, String tag, Object obj) {
		super.onSuccess(url, tag, obj);
		// if (tag.equals(TYPE_MERGE_PHOTO)) {
		//
		//
		// } else if (tag.equals(TYPE_PHOTO)) {
		//
		// }
		listView.onRefreshComplete();
		if (JsonUtil.isSuccess(obj)) {
			if (nowpage == 1) {
				beans.clear();
			}
			try {
				JSONObject object = new JSONObject(obj.toString());
				addCache(getClass(), object);
				JsonUtil.parseXPhotosBeans(beans, object);
				allcount = JsonUtil.getCount(object);
				allpage = JsonUtil.getPagenum(object);
				adapter.notifyDataSetChanged();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}

	String[] items;

	public void showTopic() {
		CustomDialog.Build build = new CustomDialog.Build().setTitle("选择主题")
				.setItems(items).setCancleAble(true);
		build.setOnitemClikc(new MyDialogListener.OnitemClikc() {
			@Override
			public void onItemClick(CustomDialog dialog, int which) {
				String txt = items[which];
				id = Integer.parseInt(txt.substring(0, 1));
				tv_title.setText(items[which].substring(1, txt.length()));
				dialog.dismiss();
				refrashDate();
			}
		});
		build.build(this).show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tab_selection:
			showChoseArea(v);
			break;
		default:
			break;
		}
	}

	PopupWindow pop_chostype;
	TextView mphoto, photo;

	// 筛选区域
	public void showChoseArea(View v) {
		if (pop_chostype == null) {
			View popview = getLayoutInflater().inflate(R.layout.pop_phototype,
					null);
			pop_chostype = new PopupWindow(popview, LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			pop_chostype.setFocusable(true);
			pop_chostype.setBackgroundDrawable(new BitmapDrawable());
			mphoto = (TextView) popview.findViewById(R.id.pop_set_mphoto);
			mphoto.setOnClickListener(new areClickListener());
			photo = (TextView) popview.findViewById(R.id.pop_set_photo);
			photo.setOnClickListener(new areClickListener());
		}
		if (pop_chostype != null && pop_chostype.isShowing()) {
			pop_chostype.dismiss();
		} else {
			pop_chostype
					.showAsDropDown(v, -10, DensityUtil.dip2px(mContext, 12));
			if (getValue(SP_TOPIC_TYPE, TYPE_MERGE_PHOTO).equals(TYPE_PHOTO)) {
				photo.setTextColor(Color.parseColor("#ca0000"));
				mphoto.setTextColor(Color.parseColor("#616161"));
			} else {
				mphoto.setTextColor(Color.parseColor("#ca0000"));
				photo.setTextColor(Color.parseColor("#616161"));
			}
		}
	}

	class areClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			nowpage = 1;
			switch (v.getId()) {
			case R.id.pop_set_mphoto:
				saveString(SP_TOPIC_TYPE, TYPE_MERGE_PHOTO);
				refrashDate();
				break;
			case R.id.pop_set_photo:
				saveString(SP_TOPIC_TYPE, TYPE_PHOTO);
				refrashDate();
				break;
			}
			pop_chostype.dismiss();
		}

	}
}
