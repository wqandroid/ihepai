package com.wq.letpapa.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import net.tsz.afinal.http.AjaxParams;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wq.letpapa.R;
import com.wq.letpapa.cache.PhotoFileManger;
import com.wq.letpapa.customview.CustomDialog;
import com.wq.letpapa.customview.CustomDialog.Build;
import com.wq.letpapa.customview.PagerSlidingTabStrip;
import com.wq.letpapa.ui.MyDialogListener.OnLeftClick;
import com.wq.letpapa.ui.MyDialogListener.OnRightClick;
import com.wq.letpapa.ui.base.BaseActivity;
import com.wq.letpapa.ui.capture.WqCaptureActivity;
import com.wq.letpapa.ui.fragment.MergePhotoFragment;
import com.wq.letpapa.ui.fragment.NewPhotoFragment;
import com.wq.letpapa.ui.fragment.ThemeTopicFragment;
import com.wq.letpapa.utils.JsonUtil;
import com.wq.letpapa.wxapi.onekeyshare.CustomerLogo;

public class SquarActivity extends BaseActivity implements
		OnPageChangeListener, OnClickListener {

	private PagerSlidingTabStrip tabs;
	private ViewPager pager;
	private MyPagerAdapter adapter;
	List<Fragment> fragments = new ArrayList<Fragment>();
	ImageView tab_selection;

	ImageView iv_badage;
	ImageView layer_tab_selection;
	RelativeLayout layer;// 菜单层
	TextView tv_badagecount;//与我相关条数
    ImageView iv_update_badage;//new 更新
	
	public String isUpdate="isUpdate";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_squar_layout);
		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		tabs.setShouldExpand(true);
		iv_badage = (ImageView) findViewById(R.id.iv_badage);
		pager = (ViewPager) findViewById(R.id.pager);
		tab_selection = (ImageView) findViewById(R.id.tab_selection);
		layer_tab_selection = (ImageView) findViewById(R.id.layer_tab_selection);
		tv_badagecount = (TextView) findViewById(R.id.tv_badagecount);
		layer = (RelativeLayout) findViewById(R.id.layer);
		iv_update_badage=(ImageView) findViewById(R.id.iv_update_badage);
		adapter = new MyPagerAdapter(getSupportFragmentManager());
		pager.setAdapter(adapter);
		final int pageMargin = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
						.getDisplayMetrics());
		pager.setPageMargin(pageMargin);
		tabs.setViewPager(pager);

		tabs.setOnPageChangeListener(this);
		saveBool(SP_LIST, false);
		fragments.add(new MergePhotoFragment());
		fragments.add(new ThemeTopicFragment());
		fragments.add(new NewPhotoFragment());

		layer_tab_selection.setOnClickListener(this);
		tab_selection.setOnClickListener(this);
		findViewById(R.id.layer_tab_takecamera).setOnClickListener(this);
		findViewById(R.id.tab_takecamera).setOnClickListener(this);
		if(getValue(isUpdate, true)){
			checkUpdate();	
			iv_update_badage.setVisibility(View.GONE);
		}else{
			iv_update_badage.setVisibility(View.VISIBLE);
		}
	}

	public void showLayer() {
		showView(layer);
		layer.startAnimation(AnimationUtils.loadAnimation(this,
				R.anim.slide_left_in));
	}

	public void hidenLayer() {
		hideView(layer);
		layer.startAnimation(AnimationUtils.loadAnimation(this,
				R.anim.slide_left_out));
	}

	public void startCamera() {
		Intent in = new Intent(this, WqCaptureActivity.class);
		startActivity(in);
	}

	String[] items;
	public void check_menu(View v) {
		switch (v.getId()) {
		case R.id.lin_home:
			hidenLayer();
			break;
		case R.id.lin_notif:
			startActivity(AboutMeActivity.class);
			break;
		case R.id.lin_self:
			Intent in = new Intent(SquarActivity.this,
					UsersDetailActivity.class);
			in.putExtra("user", getUser());
			startActivity(in);
			break;
		default:
			break;
		}
		hideView(layer);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}
	@Override
	public void onResume() {
		super.onResume();
		loadData();
	}
	/**查询与我相关消息数*/
	public void loadData() {
		AjaxParams params = new AjaxParams();
		params.put("user_id", getUid());
		params.put("type", "num");
		params.put("lasttime",
				getValue(SP_LASTTIME, System.currentTimeMillis()) + "");
		sendGet(ABOUT_ME + "&" + params.toString(), null);
	}
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.layer_tab_takecamera:
			startCamera();
			break;
		case R.id.tab_takecamera:
			startCamera();
			break;
		case R.id.tab_selection:
			showLayer();
			break;
		case R.id.layer_tab_selection:
			hidenLayer();
			break;
		case R.id.pop_all:
			saveint(SP_TYPE, TYPE_ALL);
			break;
		case R.id.pop_boy:
			saveint(SP_TYPE, TYPE_MAN);
			break;
		case R.id.pop_gril:
			saveint(SP_TYPE, TYPE_WOMEN);
			break;
		}
	}
	
	
	

	public void checkUpdate() {
		sendGet(UPDATE_URL, null);
	}

	public void ClickTopic(View v) {
		Intent in = new Intent(this, TopicPhotoListActivity.class);
		switch (v.getId()) {
		case R.id.tag_3:// 寻找另一半
			in.putExtra("id", 3);
			in.putExtra("name", "#寻找另一半#");
			break;
		case R.id.tag_4:// 萌萌哒
			in.putExtra("id", 4);
			in.putExtra("name", "#萌萌哒#");
			break;
		case R.id.tag_5:// 男神、女神
			in.putExtra("id", 5);
			in.putExtra("name", "#男神、女神#");
			break;
		case R.id.tag_6:// 恶搞无下线
			in.putExtra("id", 6);
			in.putExtra("name", "#恶搞无下线#");
			break;
		case R.id.tag_7:// 创意神合拍
			in.putExtra("id", 7);
			in.putExtra("name", "#创意神合拍#");
			break;
		case R.id.tag_8:// 2B专区
			in.putExtra("id", 8);
			in.putExtra("name", "#2B专区#");
			break;
		default:
			break;
		}
		startActivity(in);
	}

	public class MyPagerAdapter extends FragmentPagerAdapter {

		private final String[] TITLES = { "人气", "主题", "合拍" };

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return TITLES[position];
		}

		@Override
		public int getCount() {
			return TITLES.length;
		}

		@Override
		public Fragment getItem(int position) {
			return fragments.get(position);
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
	}

	@Override
	public void onSuccess(String url, Object obj) {
		super.onSuccess(url, obj);
		if (url.startsWith(ABOUT_ME) && JsonUtil.isSuccess(obj)) {
			try {
				int count = new JSONObject(obj.toString()).getInt("count");
				if (count > 0) {
					iv_badage.setVisibility(View.VISIBLE);
					tv_badagecount.setVisibility(View.VISIBLE);
					tv_badagecount.setText(count + "");
				} else {
					tv_badagecount.setVisibility(View.GONE);
					iv_badage.setVisibility(View.GONE);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (url.startsWith(UPDATE_URL) && JsonUtil.isSuccess(obj)) {

			try {
				PackageInfo info = getPackageManager().getPackageInfo(
						getPackageName(), PackageManager.GET_CONFIGURATIONS);
				JSONObject jsonObject = new JSONObject(obj.toString());
				String version_code = jsonObject.getString("version_code");
				int force_update = jsonObject.getInt("force_update");
				String downurl = jsonObject.getString("url");
				String describe = jsonObject.getString("describe");
				
				/**
				 *version_code  1.1.0 
				 *force_update 0
				 *url  下载地址
				 *describe 新增合拍滤镜功能
				 *         修改部分拍照机型适配
				 *         修改本地上传图片限制
				 *         优化拍照上传
				 */
				if (!version_code.equals(info.versionName)) {
					showUpdateDialog(force_update, downurl, describe);
				}
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO: handle exception
			}
		} else if (url.equals(URL_DOWNLOAD)) {
			File f = new File(obj.toString());
			// 安装apk文件
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(f),
					"application/vnd.android.package-archive");
			startActivity(intent);
			saveBool(isUpdate, true);
		}
	}
	@Override
	public void onLoading(String url, long count, long current) {
		super.onLoading(url, count, current);
		long p = current * 100 / count;
		if (downloaddialog != null) {
			dialogBar.setProgress((int)p);
			tvcurrent.setText(p+"%");
		}
	}
	public void showUpdateDialog(int force_update, final String downurl,
			String describe) {
		boolean isforce = force_update == 1 ? false : true;
		CustomDialog.Build build = new CustomDialog.Build().setTitle("版本更新");
		build.setContent(describe);
		if (!isforce) {// 如果不强制升级加上取消
		
		}
		build.setCancleAble(true);
		build.setLeft("取消").setOnLeftClick(new OnLeftClick() {
			
			@Override
			public void onItemClick(CustomDialog dialog) {
				dialog.cancel();
				iv_update_badage.setVisibility(View.VISIBLE);
				saveBool(isUpdate, false);
			}
		});
		build.setRight("确定").setOnRightClikc(new OnRightClick() {
			@Override
			public void onItemClick(CustomDialog dialog) {
				downloadFile(downurl, PhotoFileManger.getAPKdir());
				showLodingDilog();
				dialog.cancel();
			}
		});
		build.build(mContext).show();
	}

	ProgressBar dialogBar;
	TextView tvcurrent;
	CustomDialog.Build downloaddialog; 
	public void showLodingDilog() {
		View current = getLayoutInflater().inflate(
				R.layout.dilog_progress_logding, null);
		dialogBar = (ProgressBar) current.findViewById(R.id.dialog_progress);
		tvcurrent = (TextView) current.findViewById(R.id.dialog_tv_current);
		downloaddialog = new Build().setTitle("下载中")
				.setSub_title("下载过程中请不要退出").setContentView(current)
				.setCancleAble(false);
		downloaddialog.build(mContext).show();
	}
	
	@Override
	public void onError(String url, String msg) {
		super.onError(url, msg);
		if(downloaddialog!=null){
			downloaddialog.cancle();
		}
	}
	/****
	 * 
	 PopupWindow rightpopupWindow;// 筛选的popwindow TextView
	 * pop_all,pop_wan,pop_women; public void initPopChose() { View popview =
	 * getLayoutInflater().inflate(R.layout.popright, null); rightpopupWindow =
	 * new PopupWindow(popview, LayoutParams.WRAP_CONTENT,
	 * LayoutParams.WRAP_CONTENT); rightpopupWindow.setFocusable(true);
	 * rightpopupWindow.setBackgroundDrawable(new BitmapDrawable());
	 * pop_all=(TextView) popview.findViewById(R.id.pop_all);
	 * pop_all.setOnClickListener(this); pop_wan=(TextView)
	 * popview.findViewById(R.id.pop_boy); pop_wan.setOnClickListener(this);
	 * pop_women=(TextView) popview.findViewById(R.id.pop_gril);
	 * pop_women.setOnClickListener(this);
	 * 
	 * if (getValue(SP_TYPE, TYPE_ALL) == TYPE_ALL) { pop_all.setSelected(true);
	 * } else if (getValue(SP_TYPE, TYPE_ALL) == TYPE_MAN) {
	 * pop_wan.setSelected(true); } else { pop_women.setSelected(true); } }
	 * 
	 * public void showpop(View v) { if (rightpopupWindow == null) {
	 * initPopChose(); } if (rightpopupWindow.isShowing()) {
	 * rightpopupWindow.dismiss(); } else { rightpopupWindow.showAsDropDown(v,
	 * -120, 0); pop_all.setSelected(false); pop_wan.setSelected(false);
	 * pop_women.setSelected(false); if (getValue(SP_TYPE, TYPE_ALL) ==
	 * TYPE_ALL) { pop_all.setSelected(true); } else if (getValue(SP_TYPE,
	 * TYPE_ALL) == TYPE_MAN) { pop_wan.setSelected(true); } else {
	 * pop_women.setSelected(true); } } }
	 * 
	 * @Override public void onResume() { super.onResume(); }
	 * 
	 *           public void changeType() { if (rightpopupWindow != null &&
	 *           rightpopupWindow.isShowing()) { int index=
	 *           pager.getCurrentItem(); if(index==0){ ((MergePhotoFragment)
	 *           fragments.get(0)).typeChange(); }else{ ((NewPhotoFragment)
	 *           fragments.get(1)).typeChange(); } rightpopupWindow.dismiss(); }
	 *           }
	 * 
	 */
}
