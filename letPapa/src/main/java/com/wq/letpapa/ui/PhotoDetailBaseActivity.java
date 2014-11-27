package com.wq.letpapa.ui;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import net.tsz.afinal.http.AjaxParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextPaint;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXVideoObject;
import com.wq.letpapa.MyApplication;
import com.wq.letpapa.R;
import com.wq.letpapa.bean.XPhotos;
import com.wq.letpapa.cache.PhotoFileManger;
import com.wq.letpapa.customview.CustomDialog;
import com.wq.letpapa.ui.MyDialogListener.OnLeftClick;
import com.wq.letpapa.utils.BitmapUtil;
import com.wq.letpapa.utils.Util;
import com.wq.letpapa.wxapi.onekeyshare.OnDismissListener;
import com.wq.letpapa.wxapi.onekeyshare.OneKeyShareCallback;
import com.wq.letpapa.wxapi.onekeyshare.OnekeyShare;
import com.wq.letpapa.wxapi.onekeyshare.ShareContentCustomizeCallback;

public class PhotoDetailBaseActivity extends SwipeBackActivity implements
		Callback {

	Handler handler;
	IWXAPI api;
	XPhotos beans;

	Typeface typeface;
	
	String sharepath;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		handler = new Handler(this);
		api = WXAPIFactory.createWXAPI(this, WX_AppID);
		api.registerApp(WX_AppID);
		typeface=Typeface.createFromAsset(getAssets(), "Kaiti.ttf");
	}
	
	public Bitmap split(Bitmap bitmap, int l) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix m = new Matrix();
		m.setRotate(90, (float) bitmap.getWidth() / 2,
				(float) bitmap.getHeight() / 2);
		Bitmap bitmap2 = null;
		if (l == TOP) {// 上
			bitmap2 = Bitmap.createBitmap(bitmap, 0, h / 2, w, h / 2, null,
					false);
		} else if (l == DOWN) {// 下
			bitmap2 = Bitmap.createBitmap(bitmap, 0, 0, w, h / 2, null, false);
		} else if (l == LEFT) {// 左
			bitmap2 = Bitmap.createBitmap(bitmap, w / 2, 0, w / 2, h, m, false);
		} else if (l == RIGHT) {// 右
			bitmap2 = Bitmap.createBitmap(bitmap, 0, 0, w / 2, h, m, false);
		}
		return bitmap2;
	}

	/*** 隐藏键盘 */
	public void showSoftInput(EditText editText) {
		InputMethodManager inputmangers = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputmangers.showSoftInput(editText, 0);
	}

	/*** 隐藏键盘 */
	public void hideSoftInput(EditText editText) {
		InputMethodManager inputmangers = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputmangers.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}
	
	

	class ShareRunable implements Runnable {
		Context context;
		String path;

		public ShareRunable(Context context, String path) {
			this.context = context;
			this.path = path;
		}

		@Override
		public void run() {
			Bitmap newbit = null;
			sharepath = PhotoFileManger.getSharePath(path);
			if (new File(sharepath).exists()) {
				newbit = BitmapFactory.decodeFile(sharepath);
//				shreWhithUri(savpath, "EXTRA_SUBJECT");
				showSharePop(newbit);
			} else {
				Bitmap or = BitmapFactory.decodeFile(path);
				try {
					Bitmap bg = BitmapFactory.decodeStream(context.getAssets()
							.open("share_bg.jpg"));
					newbit = createShareBitmap(bg, or,getUser().getName());
					if (BitmapUtil.saveBitmap(sharepath, newbit)) {
//						shreWhithUri(savpath, "EXTRA_SUBJECT");
						showSharePop(newbit);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	
	}
	public void shreWhithUri(String filepath, String txt) {
		Intent it = new Intent(Intent.ACTION_SEND);
		it.putExtra(Intent.EXTRA_SUBJECT, txt);
		it.putExtra(Intent.EXTRA_TEXT,
				"来自@i合拍App 看到我的的合拍闪瞎你的24K金眼，震惊你对美的认识！！！让你瞬间对哥佩服的好几体投地");
		it.putExtra(Intent.EXTRA_TITLE, "标题");
		it.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + filepath));
		it.setType("image/jpeg");
		startActivity(Intent.createChooser(it, "分享"));
	}
	
	PopupWindow sharpop;
	public void showSharePop(Bitmap bit){
		View root=findViewById(R.id.rootview).getRootView();
		View contentView=getLayoutInflater().inflate(R.layout.pop_share_view, null);
		ImageView share_iv=(ImageView) contentView.findViewById(R.id.share_iv);
		ImageView btn_share=(ImageView) contentView.findViewById(R.id.btn_share);
		
		share_iv.setImageBitmap(bit);
		if(sharpop==null){
			sharpop=new PopupWindow(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT);
			sharpop.setContentView(contentView);
			sharpop.setBackgroundDrawable(new BitmapDrawable());
			sharpop.setFocusable(true);
			btn_share.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					sharpop.dismiss();
					shreWhithUri(sharepath, "EXTRA_SUBJECT");
				}
			});
			contentView.findViewById(R.id.pop_sharebg).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					sharpop.dismiss();
				}
			});
		}
		
		if(sharpop.isShowing()){
			sharpop.dismiss();
		}else{
			sharpop.showAtLocation(root, Gravity.CENTER, 0, 0);
		}
	}
	public  Bitmap createShareBitmap(Bitmap src, Bitmap watermark,String title) {
		String tag = "createBitmap";
		Log.d(tag, "create a new bitmap");
		if (src == null) {
			return null;
		}
		int w = src.getWidth();
		int h = src.getHeight();
		Bitmap newb = Bitmap.createBitmap(w, h, Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
		Canvas cv = new Canvas(newb);
		cv.setDrawFilter(new PaintFlagsDrawFilter(0,Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
		// draw src into
		cv.drawBitmap(src, 0, 0, null);// 在 0，0坐标开始画入src
		// draw watermark into
		cv.drawBitmap(watermark, 0, 0, null);// 在src的右下角画入水印
		
		TextPaint paint=new TextPaint();
		paint.setAntiAlias(true);
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
//		paint.setDither(true); //获取跟清晰的图像采样  
//		paint.setFilterBitmap(true);//过滤一些  
		paint.setColor(Color.parseColor("#ca0000"));
		paint.setTypeface(typeface);
		if(title.length()>10){
			paint.setTextSize(28);
			cv.drawText(title, 505, 888, paint);
		}else{
			paint.setTextSize(32);
			cv.drawText(title, 505, 884, paint);
		}
//		StaticLayout layout=new StaticLayout(title, paint, 720, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
//		layout.draw(cv);
		// save all clip
		cv.save(Canvas.ALL_SAVE_FLAG);// 保存
		// store
		cv.restore();// 存储
		return newb;
	}

	class saveRunable implements Runnable {
		Context context;
		String path;

		public saveRunable(Context context, String path) {
			this.context = context;
			this.path = path;
		}
		@Override
		public void run() {
			Bitmap newbit = null;
			String savpath = PhotoFileManger.getSavePath(path);
			if(new File(savpath).exists()){
				t("已经保存");
			}else{
				Bitmap or = BitmapFactory.decodeFile(path);
				try {
					Bitmap bg = BitmapFactory.decodeStream(context.getAssets()
							.open("share_bg.jpg"));
					newbit = createShareBitmap(bg, or,getUser().getName());
					if (BitmapUtil.saveBitmap(savpath, newbit)) {
						if (newbit != null) {
							save(savpath);
						} else {
							t("保存失败");
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
				
		}

		public void save(String path) {
			MediaScannerConnection.scanFile(PhotoDetailBaseActivity.this,
					new String[] { path }, null,
					new MediaScannerConnection.OnScanCompletedListener() {
						@Override
						public void onScanCompleted(final String path,
								final Uri uri) {
							handler.sendEmptyMessage(WHAT_SAVE_SUCCESS);
						}
					});
		}
	}

	/** 截图片 */
	Bitmap canyubitmap;

	public class splitRunable implements Runnable {
		Bitmap bitmap;
		int layer;

		public splitRunable(Bitmap bitmap, int layer) {
			this.bitmap = bitmap;
			this.layer = layer;
		}

		@Override
		public void run() {
			if (layer < 1) {
				handler.sendEmptyMessage(WHAT_SPLITE_FAILD);
				return;
			}
			logi("切割完毕" + layer);
			canyubitmap = split(bitmap, layer);
			if (canyubitmap == null) {
				handler.sendEmptyMessage(WHAT_SPLITE_FAILD);
				return;
			}
			handler.sendEmptyMessage(WHAT_SPLITE_SUCCESS);
		}
	}

	PopupWindow pop_sharemore;

	// 筛选区域
	public void showShareMore(View v) {
		if (pop_sharemore == null) {
			View popview = getLayoutInflater().inflate(R.layout.popshare_more,
					null);
			pop_sharemore = new PopupWindow(popview, LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			pop_sharemore.setFocusable(true);
			pop_sharemore.setBackgroundDrawable(new BitmapDrawable());
			popview.findViewById(R.id.pop_set_save).setOnClickListener(
					new areClickListener());
			popview.findViewById(R.id.pop_set_share).setOnClickListener(
					new areClickListener());
			TextView tv = (TextView) popview.findViewById(R.id.pop_set_report);
			if (beans.getUser().getUid().equals(getUid())) {
				tv.setText("删除");
			} else {
				tv.setText("举报");
			}
			tv.setOnClickListener(new areClickListener());
		}
		if (pop_sharemore != null && pop_sharemore.isShowing()) {
			pop_sharemore.dismiss();
		} else {
			pop_sharemore.showAsDropDown(v, 15, 3);
		}
	}

	class areClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.pop_set_save:
				SendSaveSDcard();
				break;
			case R.id.pop_set_share:
				SendShare();
				break;
			case R.id.pop_set_report:
				if (beans.getUser().getUid().equals(getUid())) {
					showDeleteDialog();
				} else {
					sendReport(beans.getUser().getUid(), beans.getId() + "",
							beans.getType());
				}
				break;
			}
			pop_sharemore.dismiss();
		}
	}

	public void saveRunable(String path) {

	}

	protected void SendSaveSDcard() {
		String path = MyApplication.getInstance()
				.getCacheFile(beans.getImage());
		if (path != null && !path.equals("")) {
			handler.post(new saveRunable(mContext, path));
		} else {
			t("图片不纯在");
		}
	}

	protected void SendShare() {
		String path = MyApplication.getInstance()
				.getCacheFile(beans.getImage());
		if (path != null && !path.equals("")) {
			handler.post(new ShareRunable(mContext, path));
		} else {
			t("图片没有下载完哦");
		}
	}

	public void sendReport(String xuser_id, String xid, String type) {
		AjaxParams ajaxParams = new AjaxParams();
		ajaxParams.put("uid", getUid());
		ajaxParams.put("xuser_id", xuser_id);
		ajaxParams.put("xid", xid);
		ajaxParams.put("type", type);
		sendPost(REPORT_URL, ajaxParams);
	}

	public void deleteHepai(String xid, String type) {
		AjaxParams ajaxParams = new AjaxParams();
		ajaxParams.put("uid", getUid());
		ajaxParams.put("xid", xid);
		ajaxParams.put("type", type);
		sendPost(DELETE_URL, ajaxParams);
	}

	public void showDeleteDialog() {
		CustomDialog.Build build = new CustomDialog.Build();
		build.setTitle("删除").setContent("     删除之后将无法恢复,与该条合拍关联的评论也一并删除。")
				.setCancleAble(true).setLeft("确定")
				.setOnLeftClick(new OnLeftClick() {
					@Override
					public void onItemClick(CustomDialog dialog) {
						deleteHepai(beans.getId() + "", beans.getType());
						dialog.dismiss();
					}
				}).setRight("取消").build(this).show();
	}

	/****
	 * -------------------------------分享----------------------------------------
	 * ------
	 */

	public void showShare(boolean silent, String platform,
			final XPhotos mergeBean) {
		final OnekeyShare oks = new OnekeyShare();
		oks.setNotification(R.drawable.ic_launcher, "来吧");
		oks.setTitle("看看你和你的男神/女生有没有夫妻相");
		oks.setTitleUrl("");
		oks.setText("分享内容");
		String url = MyApplication.getInstance().getCacheFile(
				mergeBean.getImage1());
		if (url != null) {
			System.out.println("设置本地图片路径");
			oks.setImagePath(url);
		}
		oks.setImageUrl(mergeBean.getImage1());
		oks.setUrl(mergeBean.getImage1());
		// Qzone支持
		oks.setSite("来吧");
		oks.setSiteUrl(mergeBean.getImage());
		// 是否直接分享（true则直接分享）
		oks.setSilent(silent);
		if (platform != null) {
			oks.setPlatform(platform);
		}
		// 去除注释，可令编辑页面显示为Dialog模式
		// oks.setDialogMode();
		// 去除注释，在自动授权时可以禁用SSO方式
		// oks.disableSSOWhenAuthorize();
		// 去除注释，则快捷分享的操作结果将通过OneKeyShareCallback回调
		oks.setCallback(new OneKeyShareCallback() {
			@Override
			public void onComplete(Platform plat, int action,
					HashMap<String, Object> res) {
				super.onComplete(plat, action, res);
				Message message = handler.obtainMessage();
				message.what = 101;
				message.obj = "分享成功";
				handler.sendMessage(message);
			}

			@Override
			public void onError(Platform plat, int action, Throwable t) {
				super.onError(plat, action, t);
				Message message = handler.obtainMessage();
				message.what = 102;
				message.obj = "分享失败";
				handler.sendMessage(message);
			}

			@Override
			public void onCancel(Platform plat, int action) {
				super.onCancel(plat, action);
				Message message = handler.obtainMessage();
				message.what = 103;
				message.obj = "取消分享";
				handler.sendMessage(message);
			}
		});

		oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {

			@Override
			public void onShare(Platform platform, ShareParams paramsToShare) {
				if (platform.getName().equals("Wechat")) {
					try {
						System.out.println("发送威信分享");
						sendWechat(mergeBean.getImage(), mergeBean.getImage1(),
								false);
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else if (platform.getName().equals("WechatMoments")) {
					paramsToShare.setShareType(Platform.SHARE_IMAGE);
					System.out.println("发送威信分享朋友圈");
					// sendWechat(Constant.shareVidURL(video.getVid()),
					// Constant.NEINEI_MAIN+video.getVideo_thumb(),true);
				}
			}
		});

		// 去除注释，演示在九宫格设置自定义的图标
		Bitmap logo = BitmapFactory.decodeResource(getResources(),
				R.drawable.logo_jubao);
		String laber = "举报";
		OnClickListener listener = new OnClickListener() {
			public void onClick(View v) {
				oks.finish();
				sendReport(mergeBean.getUser_id(), mergeBean.getId() + "",
						mergeBean.getType());
			}
		};
		if (mergeBean.getUser().getUid().equals(getUid())) {// 自己的合拍
			logo = BitmapFactory.decodeResource(getResources(),
					R.drawable.logo_delete);
			laber = "删除";
			listener = new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (mergeBean.getType().equals(TYPE_PHOTO)) {
						t("删除失败");
						return;
					}
					deleteHepai(mergeBean.getId() + "", mergeBean.getType());
				}
			};
		}
		oks.setCustomerLogo(logo, laber, listener);
		// 去除注释，则快捷分享九宫格中将隐藏新浪微博和腾讯微博
		// oks.addHiddenPlatform(SinaWeibo.NAME);
		// oks.addHiddenPlatform(TencentWeibo.NAME);
		oks.setOnDismiss(new OnDismissListener() {

			@Override
			public void onDisMiss() {
				Message message = handler.obtainMessage();
				message.what = 105;
				message.obj = "取消分享";
				handler.sendMessage(message);

			}
		});
		oks.show(this);
	}

	public void sendWechat(String url, String thubmurl, boolean isTimeline)
			throws MalformedURLException, IOException {
		String path = MyApplication.getInstance().getCacheFile(thubmurl);
		Bitmap bmp = BitmapFactory.decodeFile(path);
		if (bmp == null) {
			bmp = BitmapFactory.decodeResource(getResources(),
					R.drawable.ic_launcher);
		}
		WXVideoObject videoObject = new WXVideoObject();
		videoObject.videoUrl = url;
		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = videoObject;
		msg.title = "来吧视频分享";
		msg.description = "我分享了一个狂拽酷炫吊炸天的视频给你";

		Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 150, 150, true);
		bmp.recycle();
		msg.thumbData = Util.bmpToByteArray(thumbBmp, true); // 设置缩略图

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("img");
		req.message = msg;
		if (isTimeline) {
			req.scene = SendMessageToWX.Req.WXSceneTimeline;
		} else {
			req.scene = SendMessageToWX.Req.WXSceneSession;
		}
		api.sendReq(req);
	}

	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis())
				: type + System.currentTimeMillis();
	}

	@Override
	public boolean handleMessage(Message msg) {
		return false;
	}

}
