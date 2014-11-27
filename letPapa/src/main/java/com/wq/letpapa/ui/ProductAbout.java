package com.wq.letpapa.ui;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tencent.connect.avatar.ImageActivity;
import com.wq.letpapa.R;
import com.wq.letpapa.cache.PhotoFileManger;
import com.wq.letpapa.customview.CustomDialog;
import com.wq.letpapa.customview.CustomDialog.Build;
import com.wq.letpapa.ui.MyDialogListener.OnLeftClick;
import com.wq.letpapa.ui.MyDialogListener.OnRightClick;
import com.wq.letpapa.utils.JsonUtil;

public class ProductAbout extends SwipeBackActivity {

	int count = 0;
	int nowshow = 0;
	String[] data = new String[] { "哈哈不服气啊？", "不服气也没用,上面的关闭按钮都没有了-_-·.", "还想返回你觉得有用吗？",
			"是不是觉得很无语", "无语就对了", "我也只是天天写代码写的无语了", "算了放过你了",
			"记得上新浪微博 @ios_android技术宅拯救世界 和我一起整人吧？" };
	String[] dataright = new String[] { "还是继续吧", "那就顺从吧", "继续往下看", "看完吧",
			"下面有惊喜", "可怜可怜我呗", "其实我挺好的", "再见，不要想我" };
	boolean issee = true;

	ImageView iv_newbadage;
	TextView tv_versionname;
	PackageInfo info;

	ImageView finsh;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.act_in, R.anim.act_exit);
		setContentView(R.layout.act_aboutus_layout);
		finsh=(ImageView) findViewById(R.id.finsh);
		iv_newbadage = (ImageView) findViewById(R.id.iv_newbadage);
		tv_versionname = (TextView) findViewById(R.id.tv_versionname);
		// 如果检查更新没有提示
		if (!getValue("isUpdate", true)) {
			iv_newbadage.setVisibility(View.VISIBLE);
		} else {
			iv_newbadage.setVisibility(View.INVISIBLE);
		}
		try {
			info = getPackageManager().getPackageInfo(getPackageName(),
					PackageManager.GET_CONFIGURATIONS);
			tv_versionname.setText("版本v"+info.versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
	public void haha(View v) {
		count++;
		if (count > 3) {
			showDialog();
			issee = false;
		}
	}

	public void checkcode(View v) {
		sendGet(UPDATE_URL, null);
	}

	@Override
	public void onSuccess(String url, Object obj) {
		super.onSuccess(url, obj);
		if (url.startsWith(UPDATE_URL) && JsonUtil.isSuccess(obj)) {
			try {
				PackageInfo info = getPackageManager().getPackageInfo(
						getPackageName(), PackageManager.GET_CONFIGURATIONS);
				JSONObject jsonObject = new JSONObject(obj.toString());
				String version_code = jsonObject.getString("version_code");
				int force_update = jsonObject.getInt("force_update");
				String downurl = jsonObject.getString("url");
				String describe = jsonObject.getString("describe");
				/**
				 * version_code 1.0.1 force_update 0 url 下载地址 describe
				 * 修复点赞的bug,以及个人页面bug
				 */
				if (!version_code.equals(info.versionName)) {
					showUpdateDialog(force_update, downurl, describe);
				}else{
					t("已是最新版本");
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
			saveBool("isUpdate", true);
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
				saveBool("isUpdate", false);
				iv_newbadage.setVisibility(View.VISIBLE);
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
		downloaddialog = new Build().setTitle("下载中").setSub_title("下载过程中请不要退出")
				.setContentView(current).setCancleAble(false);
		downloaddialog.build(mContext).show();
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
	@Override
	public void onError(String url, String msg) {
		super.onError(url, msg);
		if (downloaddialog != null) {
			downloaddialog.cancle();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (downloaddialog != null) {
			downloaddialog.cancle();
		}
	}
	
	/**---hah逗比模式--**/
	public void showDialog() {
		count=0;
		nowshow=0;
		finsh.setVisibility(View.GONE);
		ImageView contentview = new ImageView(this);
		contentview.setImageResource(R.drawable.haha);
		CustomDialog.Build build = new CustomDialog.Build();
		build.setTitle("没错就是我!").setSub_title("这个项目就是由下面这位帅哥开发的")
				.setCancleAble(false).setContentView(contentview)
				.setRight("进入逗比环节").setOnRightClikc(new OnRightClick() {
					@Override
					public void onItemClick(CustomDialog dialog) {
						showhaha();
						dialog.cancel();
					}
				}).build(mContext).show();
	}

	public void showhaha() {
		CustomDialog.Build build = new CustomDialog.Build();
		build.setTitle("哈哈哈").setSub_title("逗你玩环节").setCancleAble(false)
				.setContent(data[nowshow]).setRight(dataright[nowshow])
				.setOnRightClikc(new OnRightClick() {
					@Override
					public void onItemClick(CustomDialog dialog) {
						if (nowshow == data.length - 1) {
							dialog.cancel();
							issee = true;
							finsh.setVisibility(View.VISIBLE);
							count=0;
							nowshow=0;
						} else {
							nowshow++;
							dialog.cancel();
							showhaha();
						}
					}
				}).build(mContext).show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				|| keyCode == KeyEvent.KEYCODE_MENU) {
			if (issee) {
				finish();
			}
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

}
