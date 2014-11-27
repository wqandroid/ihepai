package com.wq.letpapa.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;
import com.wq.letpapa.R;
import com.wq.letpapa.cache.PhotoFileManger;
import com.wq.letpapa.customview.CustomDialog;
import com.wq.letpapa.customview.WQRotateImageView;
import com.wq.letpapa.ui.MyDialogListener.OnLeftClick;
import com.wq.letpapa.ui.base.BaseActivity;
import com.wq.letpapa.utils.Base64Util;
import com.wq.letpapa.utils.JsonUtil;
import com.wq.letpapa.wxapi.onekeyshare.OneKeyShareCallback;
import com.wq.letpapa.wxapi.onekeyshare.OnekeyShare;

public class SendImgActivity extends BaseActivity implements Callback,
		OnClickListener, EmojiconGridFragment.OnEmojiconClickedListener,
		EmojiconsFragment.OnEmojiconBackspaceClickedListener {

	WQRotateImageView iv_shot;
	TextView send;
	String savepath = "";
	Handler handler;
	boolean ischoseFromGallery;
	public int or;// 当前覆盖朝向
	EmojiconEditText describe;
	CheckBox ck_location;
	String city = "";
	Bitmap orbitmap;
	TextView tvcurrent, tv_address;
	ImageView iv_face;
	TextView tv_topic;
	EmojiconsFragment emojiconsFragment;
	CheckBox share_friend_ring, share_qq_zone, share_sina;
	boolean is_hepai = false;
	long photo_id = 0;
	int topic_id = 8;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_sendimg_layout);
		savepath = getIntent().getExtras().getString("path");
		or = getIntent().getIntExtra("location", 0);
		items = getResources().getStringArray(R.array.theme_data);
		if (getIntent().getIntExtra("doflag", 0) == FLAG_TAKE_HEPAI) {// 表示是由参与合拍进入该界面的
			is_hepai = true;
			photo_id = getIntent().getLongExtra("photo_id", -1);
			topic_id = getIntent().getIntExtra("topic_id", -1);
		}
		share_friend_ring = (CheckBox) findViewById(R.id.share_friend_ring);
		share_qq_zone = (CheckBox) findViewById(R.id.share_qq_zone);
		share_sina = (CheckBox) findViewById(R.id.share_sina);
		tv_topic = (TextView) findViewById(R.id.tv_topic);
		iv_shot = (WQRotateImageView) findViewById(R.id.iv_shot);
		send = (TextView) findViewById(R.id.send);
		tv_address = (TextView) findViewById(R.id.tv_address);
		describe = (EmojiconEditText) findViewById(R.id.describe);
		ck_location = (CheckBox) findViewById(R.id.ck_location);
		tvcurrent = (TextView) findViewById(R.id.textView1);
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 10;
		Bitmap bitmap = BitmapFactory.decodeFile(savepath, options);
		iv_shot.setImageBitmap(bitmap);
		if (is_hepai) {
			tv_topic.setTextColor(Color.GRAY);
			tv_topic.setText(getTopicTxt(String.valueOf(topic_id)));
		} else {
			tv_topic.setOnClickListener(this);
		}
		handler = new Handler(this);
		initLocation();
		initListener();
		// 创建表情输入的碎片
		emojiconsFragment = (EmojiconsFragment) getSupportFragmentManager()
				.findFragmentById(R.id.emojicons);
		hideEmojion();
	}

	private void initLocation() {
		// 定位
		mLocationClient = new LocationClient(getApplicationContext());
		mLocationClient.registerLocationListener(locationListener);
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Battery_Saving);// 设置定位模式
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度，默认值gcj02
		option.setScanSpan(5000);// 设置发起定位请求的间隔时间为5000ms
		option.setAddrType("all");
		mLocationClient.setLocOption(option);
		mLocationClient.start();
	}
	public CharSequence getTopicTxt(String id) {
		for (String str : items) {
			if (str.startsWith(id)) {
				return str.subSequence(1, str.length());
			}
		}
		return "#2B主题#";
	}
	public void initListener() {
		send.setOnClickListener(this);
		iv_shot.setOnClickListener(this);
		findViewById(R.id.dimissview).setOnClickListener(this);
		findViewById(R.id.iv_face).setOnClickListener(this);
		findViewById(R.id.iv_topic).setOnClickListener(this);
		describe.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				hideEmojion();
			}
		});
		ck_location.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {

				if (arg1 && arg0.isChecked()) {
					tv_address.setVisibility(View.VISIBLE);
				} else {
					tv_address.setVisibility(View.GONE);
				}

			}
		});

		describe.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				int current = 144 - arg0.length();
				tvcurrent.setText(current + "");
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {

			}
		});
	}

	/*** 隐藏表情视图 */
	public void hideEmojion() {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		if (!emojiconsFragment.isHidden()) {
			ft.hide(emojiconsFragment);
		}
		ft.commit();
	}

	/*** 隐藏键盘 */
	public void hideSoftInput() {
		InputMethodManager inputmangers = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputmangers.hideSoftInputFromWindow(describe.getWindowToken(), 0);
	}

	PopupWindow popupWindow;
	ImageView pop_iv;

	@SuppressWarnings("deprecation")
	public void showPop() {
		if (popupWindow == null) {
			View view = getLayoutInflater().inflate(
					R.layout.pop_full_showimage, null);
			popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
			popupWindow.setBackgroundDrawable(new BitmapDrawable());
			popupWindow.setFocusable(true);
			popupWindow.setOutsideTouchable(true);
			pop_iv = (ImageView) view.findViewById(R.id.pop_iv);
			if (orbitmap == null) {
				orbitmap = BitmapFactory.decodeFile(savepath);
			}
			pop_iv.setImageBitmap(orbitmap);
			view.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					if (popupWindow != null && popupWindow.isShowing()) {
						popupWindow.dismiss();
					}
					return false;
				}
			});
		}
		if (popupWindow.isShowing()) {
			popupWindow.dismiss();
		} else {
			popupWindow.showAtLocation(iv_shot.getRootView(), Gravity.CENTER,
					0, 0);
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		return false;
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.iv_shot:
			showPop();
			break;
//		case R.id.cancle:
//			finish();
//			break;
		case R.id.send:
			sendPicture();
			hideEmojion();
			// handler.post(new SendRunable());
			break;
		case R.id.iv_face:
			FragmentTransaction ft = getSupportFragmentManager()
					.beginTransaction();
			hideSoftInput();
			if (emojiconsFragment.isHidden()) {
				ft.show(emojiconsFragment);
			} else {
				ft.hide(emojiconsFragment);
			}
			ft.commit();
			break;
		case R.id.tv_topic:
			hideEmojion();
			showTopic();
			break;
		case R.id.dimissview:
			hideEmojion();
			hideSoftInput();
			break;
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
				tv_topic.setText(items[which].substring(1, txt.length()));
				topic_id = Integer.parseInt(txt.substring(0, 1));
				dialog.dismiss();
			}
		});
		build.build(this).show();
	}

	public void addTopic() {
		// 获取光标所在位置
		int index = describe.getSelectionStart();
		Editable editable = describe.getText();
		editable.insert(index, "##");
		describe.setSelection(index + 1);
		describe.requestFocus();
		describe.setCursorVisible(true);
		// 显示键盘
		InputMethodManager inputManager = (InputMethodManager) describe
				.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.showSoftInput(describe, 0);

	}

	public void sendPicture() {
		AjaxParams params = new AjaxParams();
		params.put("uid", getUid());
		File f = new File(savepath);
		params.put("describe", Base64Util.encode(describe.getText().toString()));
		try {
			params.put("image", f);
			System.out.println("图片大小" + (f.length() / 1024) + "kb");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			t("糟糕图片不见了,从新发送吧");
			finish();
		}
		if (ck_location.isChecked()) {
			params.put("areas", city);
		} else {
			params.put("areas", "");
		}
		params.put("topic_id", topic_id + "");
		params.put("lon", longitude + "");
		params.put("lat", latitude + "");
		params.put("laver", or + "");
		if (is_hepai) {// 参与合拍
			params.put("photo_id", photo_id + "");
			if (photo_id < 0) {
				return;
			}
			sendPostWhithDialod(SEND_HEPAI_PICTURE, params, "图片正在坐火箭飞向服务器...");
		} else {
			sendPostWhithDialod(SEND_PICTURE, params, "图片正在坐火箭飞向服务器...");
		}
	}

	@Override
	public void onSuccess(String url, Object obj) {
		super.onSuccess(url, obj);
		logi(obj.toString());
		if (JsonUtil.isSuccess(obj)) {
			t("^0^亲的图片已成功飞往服务器");
             try {
				String urls=new JSONObject(obj.toString()).getString("image1");
				// 如果分享到微信朋友圈
				if (share_friend_ring.isChecked()) {
					showShare("WechatMoments", true, urls);
				}
				if (share_qq_zone.isChecked()) {
					showShare(QZone.NAME, true, urls);
				}
				if (share_sina.isChecked()) {
					showShare(SinaWeibo.NAME, true, urls);
				}
				//删除拍的一些数据
				new Thread(new Runnable() {
					@Override
					public void run() {
						PhotoFileManger.delteALLTemp();
					}
				}).start();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			finish();
		} else {
			t("-_-!糟糕飞到火星了" + JsonUtil.getMessage(obj));
		}
	}

	public LocationClient mLocationClient = null;
	MyLocationListener locationListener = new MyLocationListener();
	double latitude, longitude;

	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation arg0) {
			latitude = arg0.getLatitude();
			longitude = arg0.getLongitude();
			System.out.println(latitude + ":lon:" + longitude);
			city = arg0.getCity();
			System.out.println(arg0.getCity() + "-=======" + arg0.getAddrStr());
			tv_address.setVisibility(View.VISIBLE);
			tv_address.setText(arg0.getAddrStr());
			if (Math.abs(latitude) > 0 && Math.abs(longitude) > 0) {
				mLocationClient.stop();
				mLocationClient.unRegisterLocationListener(locationListener);
			}
			ck_location.setEnabled(true);
			ck_location.setChecked(true);
		}

		@Override
		public void onReceivePoi(BDLocation arg0) {
		}
	}

	@Override
	public void onStar(String url) {
		super.onStar(url);
		if (mLocationClient != null && mLocationClient.isStarted())
			mLocationClient.requestLocation();
		else
			Log.i("LocSDK3", "locClient is null or not started");
	}

	@Override
	public void onStop() {
		super.onStop();
		if (mLocationClient != null) {
			mLocationClient.stop();
			mLocationClient.unRegisterLocationListener(locationListener);
		}
	}

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			showExit();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void showExit() {
		CustomDialog.Build build = new CustomDialog.Build();
		build.setTitle("退出?").setContent("这么好的照片你确定就不发了？").setCancleAble(false);
		build.setLeft("确定").setOnLeftClick(new OnLeftClick() {
			@Override
			public void onItemClick(CustomDialog dialog) {
				dialog.dismiss();
				finish();
			}
		});
		build.setRight("取消").setOnRightClikc(null);
		build.build(mContext).show();
	}

	@Override
	public void onEmojiconClicked(Emojicon emojicon) {
		EmojiconsFragment.input(describe, emojicon);
	}

	@Override
	public void onEmojiconBackspaceClicked(View v) {
		EmojiconsFragment.backspace(describe);
	}

	public void showShare(String platform, boolean silent, String url) {
		OnekeyShare oks = new OnekeyShare();
		// 分享时Notification的图标和文字
		oks.setNotification(R.drawable.ic_launcher,
				getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle("合拍-让你离女神更近");
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//		oks.setTitleUrl("http://laibaapp.com");
		// text是分享文本，所有平台都需要这个字段
		oks.setText("我分享了一张我刚拍的合拍，如果你也喜欢，那你赶紧来啊？@i合拍App");
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		// imageUrl是图片的网络路径，新浪微博、人人网、QQ空间、
		oks.setImageUrl(url);
		oks.setUrl(url);
		if (platform.equals(QZone.NAME)) {
			oks.setComment("我的评论");
			oks.setSite("来吧APP");
			oks.setSiteUrl(url);
		}
		oks.setSilent(silent);
		if (platform != null) {
			oks.setPlatform(platform);
		}
		oks.setCallback(new OneKeyShareCallback() {
			@Override
			public void onComplete(Platform plat, int action,
					HashMap<String, Object> res) {
				super.onComplete(plat, action, res);
				System.out.println("分享成功");
			}

			@Override
			public void onError(Platform plat, int action, Throwable t) {
				super.onError(plat, action, t);
				System.out.println("分享失败" + action + t.getMessage());
			}

			@Override
			public void onCancel(Platform plat, int action) {
				super.onCancel(plat, action);
				System.out.println("取消分享");
			}
		});
		oks.show(this);
	}

}

/**
 * {"code":"1","id":"57","sex":"0",
 *    "image":"http://hepai.dianjin169.com/Uploads/mphoto/53fd8e1767221.jpg",
 *     "image1":"http://hepai.dianjin169.com/Uploads/mphoto/30_53fd8e1767221.jpg","msg":"提交成功"}

 * */
