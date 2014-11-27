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
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wq.letpapa.MyApplication;
import com.wq.letpapa.R;
import com.wq.letpapa.adapter.PhotoBaseAdapter;
import com.wq.letpapa.adapter.XphotAdapter;
import com.wq.letpapa.bean.User;
import com.wq.letpapa.bean.XPhotos;
import com.wq.letpapa.customview.StickyScrollView;
import com.wq.letpapa.customview.StickyScrollView.ScrollChanged;
import com.wq.letpapa.utils.DensityUtil;
import com.wq.letpapa.utils.JsonUtil;

public class UsersDetailActivity extends SwipeBackActivity {

	GridView gridview;
	Handler handle = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case WHAT_PARISE:
				XPhotos bean = (XPhotos) msg.obj;
				sendParise(bean.getId(),bean.getUser().getUid());
				break;
			default:
				break;
			}
		}
		
	};
	LinearLayout fram_layout;
	StickyScrollView scrollView1;

	User user;

	ImageView iv_icon;
	TextView tv_name;
	TextView tv_sex_name, photo, merge,like;
	ArrayList<XPhotos> datas = new ArrayList<XPhotos>();

	ProgressBar progressBar1;
	XphotAdapter mergeAdapter;
	int height = 0;
	boolean iscanLoad = true;

	String nowType = TYPE_MERGE_PHOTO;
	ProgressBar load_more_parogressbar;
	ImageButton bt_settings;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.act_in, R.anim.act_exit);
		setContentView(R.layout.act_user_detail_layout);
		user = (User) getIntent().getSerializableExtra("user");
		if (user == null) {
			finish();
			return;
		}
		bt_settings = (ImageButton) findViewById(R.id.bt_settings);
		tv_sex_name = (TextView) findViewById(R.id.tv_sex_name);
		photo = (TextView) findViewById(R.id.photo);
		merge = (TextView) findViewById(R.id.merge);
		like=(TextView) findViewById(R.id.like);
		load_more_parogressbar = (ProgressBar) findViewById(R.id.load_more_parogressbar);
		progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
		gridview = (GridView) findViewById(R.id.hepaiview);
		iv_icon = (ImageView) findViewById(R.id.iv_icon);
		tv_name = (TextView) findViewById(R.id.tv_name);
		scrollView1 = (StickyScrollView) findViewById(R.id.scrollView1);
		fram_layout = (LinearLayout) findViewById(R.id.framlayout);
		displayImage(iv_icon, user.getIcon(), getRoundedBitmapDisplayer(-1));
		tv_name.setText(user.getName());
		mergeAdapter = new XphotAdapter(this, datas, handle);
		mergeAdapter.setTheme(PhotoBaseAdapter.THEME_SHOW_HEADTXT, false);
		gridview.setAdapter(mergeAdapter);
		scrollView1.setonScrollChanged(new ScrollChanged() {

			@Override
			public void onScrollChanged(int l, int t, int oldl, int oldt) {
				// Trace.i("allpage"+allpage+"nowpage"+nowpage+"oldl"+oldl+"oldt"+oldt+"height"+height);
				if (allpage > nowpage && oldt > height && iscanLoad) {
					iscanLoad = false;
					nowpage++;
					load_more_parogressbar.setVisibility(View.VISIBLE);
					refrashDate(nowType);
				}
			}
		});
		if (user.getUid().equals(getUid())) {
			bt_settings.setVisibility(View.VISIBLE);
			tv_sex_name.setTextColor(R.color.red);
			tv_sex_name.setText("我");
			photo.setText("我的合拍");
			merge.setText("我参与的");
			bt_settings.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					showChoseArea(v);
				}
			});
		} else {// 别人的主页
			bt_settings.setVisibility(View.GONE);
			if (user.getSex().equals("1")) {// 男
				tv_sex_name.setTextColor(Color.parseColor("#616161"));
				tv_sex_name.setText("他");
				photo.setText("他的合拍");
				merge.setText("他参与的");
			} else {
				tv_sex_name.setTextColor(Color.parseColor("#b0120a"));
				tv_sex_name.setText("她");
				photo.setText("她的合拍");
				merge.setText("她参与的");
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		refrashDate(TYPE_MERGE_PHOTO);
	}

	public void clickchose(View v) {
		like.setTextColor(Color.parseColor("#9e9e9e"));
		photo.setTextColor(Color.parseColor("#9e9e9e"));
		merge.setTextColor(Color.parseColor("#9e9e9e"));
		nowpage = 1;
		switch (v.getId()) {
		case R.id.photo:
			photo.setTextColor(Color.parseColor("#ca0000"));
			mergeAdapter.setTheme(PhotoBaseAdapter.THEME_SHOW_HEADTXT, false);
			nowType = TYPE_PHOTO;
			refrashDate(TYPE_PHOTO);
			break;
		case R.id.merge:
			merge.setTextColor(Color.parseColor("#ca0000"));
			mergeAdapter.setTheme(PhotoBaseAdapter.THEME_SHOW_HEADTXT, false);
			nowType = TYPE_MERGE_PHOTO;
			refrashDate(TYPE_MERGE_PHOTO);
			break;
		case R.id.parise:
			like.setTextColor(Color.parseColor("#ca0000"));
			mergeAdapter.setTheme(PhotoBaseAdapter.THEME_SHOWICON, false);
			nowType = TYPE_PARISE;
			refrashDate(TYPE_PARISE);
			break;
		}
	}

	PopupWindow pop_chosearea;
	// 筛选区域
	public void showChoseArea(View v) {
		if (pop_chosearea == null) {
			View popview = getLayoutInflater().inflate(R.layout.popsetting,
					null);
			pop_chosearea = new PopupWindow(popview, LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			pop_chosearea.setFocusable(true);
			pop_chosearea.setBackgroundDrawable(new BitmapDrawable());
			popview.findViewById(R.id.pop_set_user).setOnClickListener(
					new areClickListener());
			popview.findViewById(R.id.pop_set_cleracache).setOnClickListener(
					new areClickListener());
			popview.findViewById(R.id.pop_set_report).setOnClickListener(
					new areClickListener());
			
			if(getValue("isUpdate", true)){//false 没有更新提示更新
			TextView tv=	(TextView) popview.findViewById(R.id.pop_set_aboutus);
			tv.setOnClickListener(
						new areClickListener());
			tv.setCompoundDrawables(null, null, null, null);
			}else{
				popview.findViewById(R.id.pop_set_aboutus).setOnClickListener(
						new areClickListener());	
			}
			popview.findViewById(R.id.pop_set_logiout).setOnClickListener(
					new areClickListener());
		}
		if (pop_chosearea != null && pop_chosearea.isShowing()) {
			pop_chosearea.dismiss();
		} else {
			pop_chosearea.showAsDropDown(v, 30,
					DensityUtil.dip2px(mContext, 18));
		}
	}

	class areClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.pop_set_user:
				break;
			case R.id.pop_set_aboutus:
                startActivity(ProductAbout.class);
				break;
			case R.id.pop_set_logiout:
				exitlogin();
				break;
			case R.id.pop_set_cleracache:
                MyApplication.getInstance().getLoader().clearDiscCache();
                t("清除完毕");
				break;
			case R.id.pop_set_report:
              startActivity(ReportActivity.class);
				break;
			}
			pop_chosearea.dismiss();
		}
	}

	/***
	 * page 否 当前页码 默认1 pagesize 否 每页显示条数 默认5 sex 否 性别 lon 否 经度 lat 否 纬度
	 */
	int nowpage = 1;// 当前页数
	int allcount = 0;// 一共多少条
	int allpage = 0;// 中共多少页

	public void refrashDate(String type) {
		progressBar1.setVisibility(View.VISIBLE);
		bt_settings.setFocusable(false);
		AjaxParams params = new AjaxParams();
		params.put("page", nowpage + "");
		params.put("pagesize", 10 + "");
		params.put("type", type);
		params.put("user_id", user.getUid());// 要查询人的id
		params.put("user1_id", getUid());
		sendGet(USER_PHOTO_DETAIL_URL + "&" + params.getParamString(), type,
				null);
	}
	/**
	 * 赞
	 */
	public void sendParise(long id, String xuid) {
		AjaxParams ajaxParams = new AjaxParams();
		ajaxParams.put("uid", getUid());
		ajaxParams.put("type", "mphoto");
		ajaxParams.put("xid", id + "");
		ajaxParams.put("xuser_id", xuid);
		sendPost(SEND_PRAISE, ajaxParams);
	}

	@Override
	public void onSuccess(String url, String tag, Object obj) {
		super.onSuccess(url, tag, obj);
		if (url.startsWith(USER_PHOTO_DETAIL_URL)) {
			progressBar1.setVisibility(View.GONE);
			bt_settings.setFocusable(true);
			load_more_parogressbar.setVisibility(View.GONE);
			if (JsonUtil.isSuccess(obj)) {
				if (nowpage == 1) {
					datas.clear();
				}
				try {
					iscanLoad = true;
					JSONObject object = new JSONObject(obj.toString());
					// addCache(getClass(), object);
					JsonUtil.parseXPhotosBeans(datas, object);
					allcount = JsonUtil.getCount(object);
					allpage = JsonUtil.getPagenum(object);
					mergeAdapter.notifyDataSetChanged();
					// if(tag.equals(TYPE_PARISE)){
					// mergeAdapter.setTheme(PhotoBaseAdapter.THEME_SHOWICON,
					// true);
					// }else{
					// mergeAdapter.setTheme(PhotoBaseAdapter.THEME_SHOW_HEADTXT,
					// true);
					// }
					resetFramHight();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} else if (url.startsWith(SEND_PRAISE)) {
			if (JsonUtil.isSuccess(obj)) {

			} else {
				t(JsonUtil.getMessage(obj));
			}
		}
	}

	public void resetFramHight() {
		LinearLayout.LayoutParams params = (LayoutParams) fram_layout
				.getLayoutParams();
		int w = getScreenWidth(UsersDetailActivity.this);
		int h = (w / 2 - DensityUtil.dip2px(this, 10) + DensityUtil.dip2px(
				this, 94));
		params.width = w;
		int size = datas.size();
		int item = 0;
		if (size > 2) {// 小于2条
			item = size % 2 == 0 ? size / 2 : size / 2 + 1;
		} else {
			item = 1;
		}
		height = h * item - getScreenHeight(this);
		params.height = h * item;
		// if (size == 0) {
		// emptyview.setVisibility(View.VISIBLE);
		// params.height = DensityUtil.dip2px(this, 200);
		// } else {
		// emptyview.setVisibility(View.GONE);
		// }
		int px8 = DensityUtil.dip2px(this, 8);
		params.setMargins(0, 0, 0, px8);
		fram_layout.setLayoutParams(params);
		// scrollView1.smoothScrollTo(0, -1);
	}
}
