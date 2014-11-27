package com.wq.letpapa.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ViewSwitcher.ViewFactory;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.utils.UIHandler;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;

import com.wq.letpapa.R;
import com.wq.letpapa.adapter.MypageAdapter;
import com.wq.letpapa.bean.User;
import com.wq.letpapa.customview.PageControl;
import com.wq.letpapa.customview.PopToast;
import com.wq.letpapa.ui.base.BaseActivity;
import com.wq.letpapa.utils.JsonUtil;

public class FirstActivity extends BaseActivity implements ViewFactory,
		PlatformActionListener, Callback {

	List<View> views = new ArrayList<View>();
	ViewPager viewPager;
	PageControl pageControl;
//	int resid[] = new int[] { R.drawable.hpage_1,R.drawable.hpage_2,R.drawable.hpage_3,R.drawable.hpage_5,R.drawable.hpage_5};
	int resid[]=new int[]{1};
	ImageSwitcher imageSwitcher;
	int currentindex = 0;
	Animation in, out;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_main);
		pageControl = (PageControl) findViewById(R.id.pageControl1);
		viewPager = (ViewPager) findViewById(R.id.welcome_viewpager);
		imageSwitcher = (ImageSwitcher) findViewById(R.id.imageSwitcher1);
		imageSwitcher.setFactory(this);

		View v1 = getLayoutInflater().inflate(R.layout.pageitem, null);
		View v2 = getLayoutInflater().inflate(R.layout.pageitem1, null);
		View v3 = getLayoutInflater().inflate(R.layout.pageitem2, null);
		View v4 = getLayoutInflater().inflate(R.layout.pageitem3, null);
		View v5 = getLayoutInflater().inflate(R.layout.pageitem4, null);
		views.add(v1);
		views.add(v2);
		views.add(v3);
		views.add(v4);
		views.add(v5);
		in = AnimationUtils.loadAnimation(this, R.anim.fadin);
		out = AnimationUtils.loadAnimation(this, R.anim.fadout);
		MypageAdapter adapter = new MypageAdapter(views);
		viewPager.setAdapter(adapter);

		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				pageControl.chagePage(arg0);
				currentindex = arg0;
				imageSwitcher.setInAnimation(in);
				imageSwitcher.setOutAnimation(out);
				imageSwitcher.setImageResource(resid[arg0]);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				isUpdate = false;
				lastTouch = System.currentTimeMillis();
			}
		});

		new Thread(new Runnable() {

			@Override
			public void run() {
				while (!isFinsh) {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					// 如果2秒钟 没有划动界面 则继续轮播
					if (System.currentTimeMillis() - lastTouch > 2000) {
						isUpdate = true;
					}
					if (isUpdate) {
						UIHandler.sendEmptyMessage(UPDATE, FirstActivity.this);
					}
				}
			}
		}).start();
	}

	boolean isFinsh = false;
	boolean isUpdate = true;
	long lastTouch = 0;

	@Override
	public View makeView() {
		ImageView imageView = new ImageView(this);
		imageView.setScaleType(ScaleType.CENTER_CROP);
		imageView.setImageResource(resid[currentindex]);
		return imageView;
	}

	/** -----------login oauth 相关操作--------------------------------------- */
	@Override
	protected void onStart() {
		super.onStart();
		checkLogin();
	}

	public static final int UPDATE = 100;
	public static final int OAUTH_ERROR = 101;
	public static final int OAUTH_SUCCESS = 102;
	public static final int OAUTH_CANCLE = 103;
	public static final int OAUTH_OVER = 104;

	Platform platform;

	public void checkLogin() {
		/*PopToast.dismiss();
		startActivity(WQTabActivity.class);
		finish();
		 return;
		 */
		if (getPlat().equals(SinaWeibo.NAME)) {
			platform = new SinaWeibo(this);
		} else {
			platform = new QZone(this);
		}
		if (platform.isValid() && getUid() != null) {
			// 跳转到主界面
			PopToast.dismiss();
			saveBool("isfirst", false);
			startActivity(SquarActivity.class);
			// initReciver();
			finish();
		} else {
			removeAccount();
		}
	}

	public void login(View v) {
		switch (v.getId()) {
		case R.id.sina_login:
			PopToast.showText(FirstActivity.this, viewPager.getRootView(),
					"登陆中....");
			authorize(new SinaWeibo(mContext));
			break;
		case R.id.qq_login:
			PopToast.showText(FirstActivity.this, viewPager.getRootView(),
					"登陆中....");
			authorize(new QZone(mContext));
			break;
		}
	}

	private void authorize(Platform plat) {
		if (plat == null) {
			return;
		}
		if (plat.isValid()) {
			String userId = plat.getDb().getUserId();
			if (userId != null) {
				Message message = new Message();
				message.obj = plat;
				message.what = OAUTH_OVER;
				UIHandler.sendMessage(message, this);
				return;
			}
		}
		plat.setPlatformActionListener(this);
		plat.showUser(null);
	}

	@Override
	public boolean handleMessage(Message msg) {
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
			Platform platform = (Platform) msg.obj;
			login(platform);
			break;
		case OAUTH_OVER:
			Platform plat = (Platform) msg.obj;
			login(plat);
			break;
		case UPDATE:
			currentindex++;
			if (currentindex == resid.length) {
				currentindex = 0;
			}
			viewPager.setCurrentItem(currentindex);
			break;
		}
		return false;
	}

	AjaxParams params = new AjaxParams();

	private void login(Platform platform) {
		String uid = platform.getDb().getUserId();
		String username = platform.getDb().getUserName();
		String icont = platform.getDb().getUserIcon();
		String sex = platform.getDb().getUserGender();
		if (isNull(sex) || isNull(uid)) {
			removeAccount();
			return;
		}
		params.put("openid", uid);
		params.put("name", username);
		if (sex.equals("m") || sex.equals("f")) {
			if (sex.equals("m")) {
				sex = "1";
			} else {
				sex = "0";
			}
		}
		params.put("sex", sex);
		if (!icon.equals("")) {
			params.put("image", this.icon);
		} else {
			params.put("image1", icont);
		}
		params.put("plat", platform.getName());
		logi(params.toString());
		sendPost(LOGIN_URL, params);
	}

	@Override
	public void onCancel(Platform plat, int action) {
		Message msg = new Message();
		msg.what = OAUTH_CANCLE;
		msg.arg2 = action;
		msg.obj = plat;
		UIHandler.sendMessage(msg, this);
	}

	String icon = "";
	@Override
	public void onComplete(Platform plat, int action,
			HashMap<String, Object> res) {
		if(plat.getName().equals(QZone.NAME)||plat.getName().equals(QQ.NAME)){
			Set<String> key = res.keySet();
			savePlat(plat.getName());
			for (String string : key) {
				logi("key:" + string + ":" + res.get(string).toString());
				if (string.equals("figureurl_qq_2")) {
					this.icon= res.get(string).toString();
					params.put("image", res.get(string).toString());
				} else if (string.equals("figureurl_qq_1")) {
					params.put("image1", res.get(string).toString());
				}
			} // figureurl_qq_2
		}else{//如果是新浪微博登陆在设置头像
			
		}
		Message msg = new Message();
		msg.what = OAUTH_SUCCESS;
		msg.arg2 = action;
		msg.obj = plat;
		UIHandler.sendMessage(msg, this);
	}

	@Override
	public void onError(Platform plat, int action, Throwable t) {
		System.out.println(t.getMessage());
		Message msg = new Message();
		msg.what = OAUTH_ERROR;
		msg.arg2 = action;
		msg.obj = plat;
		UIHandler.sendMessage(msg, this);
	}

	@Override
	public void onSuccess(String url, Object obj) {
		super.onSuccess(url, obj);
		if (url.equals(LOGIN_URL)) {
			if (JsonUtil.isSuccess(obj)) {
				JSONObject jsonObject;
				try {
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

	@Override
	protected void onDestroy() {
		isFinsh = true;
		PopToast.dismiss();
		super.onDestroy();
	}

	/**
	 * public void initReciver() { Resources resource = this.getResources();
	 * String pkgName = this.getPackageName(); // Push:
	 * 以apikey的方式登录，一般放在主Activity的onCreate中。 //
	 * 这里把apikey存放于manifest文件中，只是一种存放方式， //
	 * 您可以用自定义常量等其它方式实现，来替换参数中的Utils.getMetaValue(PushDemoActivity.this, //
	 * "api_key") // 通过share preference实现的绑定标志开关，如果已经成功绑定，就取消这次绑定 if
	 * (!Utils.hasBind(getApplicationContext())) {
	 * PushManager.startWork(getApplicationContext(),
	 * PushConstants.LOGIN_TYPE_API_KEY, Utils.getMetaValue(FirstActivity.this,
	 * "api_key")); logi("push", "绑定成功" + Utils.getMetaValue(FirstActivity.this,
	 * "api_key")); // Log.d("YYY", "after start work at " // +
	 * Calendar.getInstance().getTimeInMillis()); // // Push:
	 * 如果想基于地理位置推送，可以打开支持地理位置的推送的开关 //
	 * PushManager.enableLbs(getApplicationContext()); // Log.d("YYY",
	 * "after enableLbs at " // + Calendar.getInstance().getTimeInMillis());
	 * MessageDBHelper dbHelper=new MessageDBHelper(this); } // Push:
	 * 设置自定义的通知样式，具体API介绍见用户手册，如果想使用系统默认的可以不加这段代码 //
	 * 请在通知推送界面中，高级设置->通知栏样式->自定义样式，选中并且填写值：1， // 与下方代码中
	 * PushManager.setNotificationBuilder(this, 1, cBuilder)中的第二个参数对应
	 * CustomPushNotificationBuilder cBuilder = new
	 * CustomPushNotificationBuilder( getApplicationContext(),
	 * resource.getIdentifier( "notification_custom_builder", "layout",
	 * pkgName), resource.getIdentifier("notification_icon", "id", pkgName),
	 * resource.getIdentifier("notification_title", "id", pkgName),
	 * resource.getIdentifier("notification_text", "id", pkgName));
	 * cBuilder.setNotificationFlags(Notification.FLAG_AUTO_CANCEL);
	 * cBuilder.setNotificationDefaults(Notification.DEFAULT_SOUND |
	 * Notification.DEFAULT_VIBRATE);
	 * cBuilder.setStatusbarIcon(this.getApplicationInfo().icon);
	 * cBuilder.setLayoutDrawable(resource.getIdentifier(
	 * "simple_notification_icon", "drawable", pkgName));
	 * PushManager.setNotificationBuilder(this, 1, cBuilder);
	 * 
	 * }
	 */

}
