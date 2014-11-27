package com.wq.letpapa.ui;

import java.util.ArrayList;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.wq.letpapa.R;
import com.wq.letpapa.adapter.CommentsAdapter;
import com.wq.letpapa.adapter.XphotAdapter;
import com.wq.letpapa.bean.CommentsBean;
import com.wq.letpapa.bean.DataFactory;
import com.wq.letpapa.bean.XPhotos;
import com.wq.letpapa.customview.StickyScrollView;
import com.wq.letpapa.ui.UsersDetailActivity.areClickListener;
import com.wq.letpapa.utils.Base64Util;
import com.wq.letpapa.utils.DateUtil;
import com.wq.letpapa.utils.DensityUtil;
import com.wq.letpapa.utils.JsonUtil;

public class XPhotoDetailActivity extends PhotoDetailBaseActivity implements
		OnClickListener {

	// 判断当前是显示合拍还是显示评论
	boolean isshowHepai = true;
	/** 合拍详情界面 */
	Handler handler;
	ListView mCommentListView;
	GridView grid_hepaiview;
	ImageView iv_takehepai;
	View emptyview;
	EditText et_comment;
	StickyScrollView stickyScrollView;
	LinearLayout bootom_layout;
	TextView tv_nodata;
	Button share;
	
	/** head view buju */
	TextView tv_nowlist, tv_topic;
	ImageView iv_icon, iv_picture, iv_parise, iv_comments;
	TextView chose_comments, chose_hepai;
	TextView tv_name, tv_time, emojicontv_descrption;
	TextView tv_hepai_count, tv_commount_count, tv_share_count,
			tv_parise_count;
	View index1, index2;
	ProgressBar progressBar1;
	FrameLayout fram_layout;
	private ArrayList<CommentsBean> comBeans;
	private ArrayList<XPhotos> mergeBeans = new ArrayList<XPhotos>();
	private CommentsAdapter adapter;
	private XphotAdapter mergePhotoAdapter;

	
	
	
	boolean isNeedHiden = false;
	RelativeLayout rootview;
	long xid = 0;
	String type = TYPE_MERGE_PHOTO;// 判断当前合拍是参与合拍还是发布合拍
	private SwipeBackLayout mSwipeBackLayout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.act_in, R.anim.act_exit);
		setContentView(R.layout.act_mergephotodetail_layout);
		handler = new Handler(this);
		beans = (XPhotos) getIntent().getSerializableExtra("bean");
		type = getIntent().getStringExtra("type");
		setUpViews();
		mergePhotoAdapter = new XphotAdapter(this, mergeBeans, handler);
		grid_hepaiview.setAdapter(mergePhotoAdapter);
		comBeans = new ArrayList<CommentsBean>();
		adapter = new CommentsAdapter(this, comBeans, handler);
		mCommentListView.setAdapter(adapter);
		if (beans == null) {
			xid = getIntent().getLongExtra("xid", -1);
			if (xid == -1) {
				finish();
			} else {
				loadMergePhotoDetail(xid);
			}
		}
		if (beans == null) {
			return;
		}
		loadHeadData();
	}

	private void setUpViews() {
		mSwipeBackLayout = getSwipeBackLayout();
		share=(Button) findViewById(R.id.share);
		mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
		grid_hepaiview = (GridView) findViewById(R.id.hepaiview);
		bootom_layout = (LinearLayout) findViewById(R.id.bootom_layout);
		emptyview = findViewById(R.id.emptyview);
		fram_layout = (FrameLayout) findViewById(R.id.fram_layout);
		rootview = (RelativeLayout) findViewById(R.id.rootview);
		et_comment = (EditText) findViewById(R.id.et_comment);
		stickyScrollView = (StickyScrollView) findViewById(R.id.scrollView1);
		tv_nodata = (TextView) findViewById(R.id.tv_nodata);
		iv_parise = (ImageView) findViewById(R.id.iv_parise);
		mCommentListView = (ListView) findViewById(R.id.comment_list);
		mCommentListView.setDividerHeight(0);
		findViewById(R.id.send).setOnClickListener(this);
		iv_takehepai = (ImageView) findViewById(R.id.iv_takehepai);
		iv_parise.setOnClickListener(this);
		iv_takehepai.setOnClickListener(this);
		iv_takehepai.setEnabled(false);
		findViewById(R.id.iv_show_more).setOnClickListener(this);
		findViewById(R.id.share).setOnClickListener(this);
		findViewById(R.id.iv_comment).setOnClickListener(this);
		initheadview();
	}

	private void loadMergePhotoDetail(long xid2) {
		if (type.equals(TYPE_MERGE_PHOTO)) {
			sendGet(DETAIL_MERGRPHOTOD_URL + "&id=" + xid, null);
		} else {
			sendGet(DETAIL_NEWPHOTOD_URL + "&id=" + xid, null);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (beans != null) {
			loadMergePhotos();
			loadCommit();
		}

	}

	@Override
	public void onPause() {
		super.onPause();
	}

	private void initheadview() {
		tv_nowlist = (TextView) findViewById(R.id.tv_nowlist);
		tv_topic = (TextView) findViewById(R.id.tv_topic);
		iv_icon = (ImageView) findViewById(R.id.iv_icon);
		tv_time = (TextView) findViewById(R.id.tv_time);
		iv_picture = (ImageView) findViewById(R.id.iv_picture);
		tv_hepai_count = (TextView) findViewById(R.id.tv_hepai_count);
		tv_commount_count = (TextView) findViewById(R.id.tv_commont_count);
		tv_parise_count = (TextView) findViewById(R.id.tv_parise_count);
		chose_comments = (TextView) findViewById(R.id.chose_comments);
		chose_hepai = (TextView) findViewById(R.id.chose_hepai);
		resvertImageview(iv_picture);
		emojicontv_descrption = (TextView) findViewById(R.id.emojicontv_descrption);
		tv_name = (TextView) findViewById(R.id.tv_name);
		progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
		progressBar1.setVisibility(View.VISIBLE);
		chose_comments.setOnClickListener(this);
		tv_topic.setOnClickListener(this);
		chose_hepai.setOnClickListener(this);
		iv_icon.setOnClickListener(this);
	}

	public void resvertImageview(ImageView imageView) {
		int w = getScreenWidth(this) - DensityUtil.dip2px(this, 12);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				w, w + 1);
		imageView.setLayoutParams(layoutParams);
	}

	private void loadHeadData() {
		tv_name.setText(beans.getUser().getName());
		tv_time.setText(DateUtil.getDiffTime(Long.parseLong(beans.getInp_time())));
		tv_hepai_count.setText(beans.getRe_num()+"");
		tv_commount_count.setText(beans.getCon_num() + "");
		tv_parise_count.setText(beans.getPraise_num() + "");
		tv_topic.setText(beans.getTopic_title());
		emojicontv_descrption.setText(Base64Util.decode(beans.getDescribe()));
		displayImage(iv_icon, beans.getUser().getIcon(),
				getRoundedBitmapDisplayer(-1));
		if (beans.isIsparise()) {
			iv_parise.setImageResource(R.drawable.item_zan_over);
		} else {
			iv_parise.setImageResource(R.drawable.item_zan);
		}
		displayImage(iv_picture, beans.getImage(), new ImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
			}
			@Override
			public void onLoadingFailed(String imageUri, View view,
					FailReason failReason) {
				progressBar1.setVisibility(View.GONE);
				share.setEnabled(false);
			}
			@Override
			public void onLoadingComplete(String imageUri, View view,
					Bitmap loadedImage) {
				progressBar1.setVisibility(View.GONE);
				share.setEnabled(true);
				new Thread(new splitRunable(loadedImage, beans.getLaver()))
						.start();
			}
			@Override
			public void onLoadingCancelled(String imageUri, View view) {
				progressBar1.setVisibility(View.GONE);
			}
		});
		if (beans.isMerge()) {
			iv_picture.setOnClickListener(this);
		}

	}

	@Override
	public void onClick(View arg0) {
		if (beans == null) {
			return;
		}
		switch (arg0.getId()) {
		case R.id.iv_picture:
			Intent orin = new Intent(mContext, XPhotoDetailActivity.class);
			orin.putExtra("xid", beans.getPhoto_id());
			orin.putExtra("type", TYPE_PHOTO);
			startActivity(orin);
			break;
		case R.id.iv_takehepai:
			if (DataFactory.bitmap == null) {
				t("获取图片失败...");
				return;
			}
			Intent in = new Intent(XPhotoDetailActivity.this,
					CaptureActivity.class);
			in.putExtra("code", CODE_TAKE_HEPAI);
			in.putExtra("or", beans.getLaver());
			in.putExtra("topic_id", beans.getTopic_id());
			if (type.equals(TYPE_MERGE_PHOTO)) {
				in.putExtra("photo_id", beans.getPhoto_id());
			} else {
				in.putExtra("photo_id", beans.getId());
			}
			startActivity(in);
			break;
		case R.id.tv_topic:
			Intent tin=new Intent(mContext, TopicPhotoListActivity.class);
			tin.putExtra("id", beans.getTopic_id());
			tin.putExtra("name", "#"+beans.getTopic_title()+"#");
			startActivity(tin);
			break;
		case R.id.chose_comments:
			tv_nowlist.setText("评论");
			isshowHepai = false;
			changeList();
			chose_comments.setTextColor(Color.parseColor("#ca0000"));
			chose_hepai.setTextColor(Color.parseColor("#9e9e9e"));
			break;
		case R.id.chose_hepai:
			tv_nowlist.setText("合拍");
			isshowHepai = true;
			changeList();
			chose_hepai.setTextColor(Color.parseColor("#ca0000"));
			chose_comments.setTextColor(Color.parseColor("#9e9e9e"));
			break;
		case R.id.iv_show_more:// 分享旁边的菜单
			showShareMore(arg0);
			break;
		case R.id.share:
			SendShare();
			break;
		case R.id.iv_parise:
			if (!beans.isIsparise()) {
				iv_parise.setImageResource(R.drawable.item_zan_over);
				sendParise(beans);
			} else {
				t("已赞");
			}
			break;
		case R.id.iv_comment:
			et_comment.setFocusable(true);
			et_comment.requestFocus();
			stickyScrollView.smoothScrollTo(0, 360);
			showSoftInput(et_comment);
			break;
		case R.id.send:
			String txt = et_comment.getText().toString();
			if (isNull(txt)) {
				t("你好歹说点啥吧?");
				return;
			}
			sendCommit(txt);
			hideSoftInput(et_comment);
			break;
		case R.id.iv_icon:
			Intent uin = new Intent(XPhotoDetailActivity.this,
					UsersDetailActivity.class);
			uin.putExtra("user", beans.getUser());
			startActivity(uin);
			break;
		}
	}

	public void changeList() {
		if (isshowHepai) {
			mCommentListView.setVisibility(View.GONE);
			grid_hepaiview.setVisibility(View.VISIBLE);
			if (mergeBeans.size() == 0) {
				tv_nodata.setText("还没有人参与合拍,赶紧拿个一血");
			}
			
		} else {
			mCommentListView.setVisibility(View.VISIBLE);
			grid_hepaiview.setVisibility(View.GONE);
			if (comBeans.size() == 0) {
				tv_nodata.setText(" -_-! 还没有人评论...");
			}
		}
		resetFramHight();
	}

	int com_page = 1;
	int com_pagenum = 50;

	public void loadCommit() {
		AjaxParams ajaxParams = new AjaxParams();
		ajaxParams.put("page", com_page + "");
		ajaxParams.put("pagesize", com_pagenum + "");
		ajaxParams.put("xid", beans.getId() + "");
		sendGet(CONMMITS_LIST + "&" + ajaxParams.getParamString(), null);
	}

	int mer_page = 1;

	public void loadMergePhotos() {
		AjaxParams params = new AjaxParams();
		params.put("page", mer_page + "");
		params.put("pagesize", 30 + "");
		params.put("user_id", getUid());
		params.put("photo_id", beans.getPhoto_id() + "");
		sendGet(BEST_HEPAI_LIST + "&" + params.getParamString(), null);
	}

	/**
	 * uid 是 用户id type 是 被评论信息类型（photo、mphoto、mem） photo|合拍 mphoto|参与合拍 mem|会员
	 * xid 是 被评论信息id content 是 评论内容 rid 被回复评论id ruser_id 被评论人的id ruser 被评论人的名字
	 * xuser_id 你评论的是合拍就传合拍的发布的用户id 评论的是参与合拍就传这个参与合拍的发布的用户id
	 * 
	 * @param content
	 */
	long rid = -1;
	String ruser_id;
	String ruser;

	public void sendCommit(String content) {
		AjaxParams ajaxParams = new AjaxParams();
		ajaxParams.put("uid", getUid());
		ajaxParams.put("type", type);
		ajaxParams.put("xid", beans.getId() + "");
		ajaxParams.put("xuid", beans.getId() + "");
		ajaxParams.put("xuser_id", beans.getUser().getUid());
		if (rid >= 0) {// 判断是否是回复谁？
			ajaxParams.put("rid", rid + "");
			ajaxParams.put("ruser_id", ruser_id);
			ajaxParams.put("ruser", ruser);
		}
		ajaxParams.put("content", Base64Util.encode(content));
		sendPost(SEND_CONMMIT, ajaxParams);
	}

	/**
	 * 赞
	 */
	public void sendParise(XPhotos photos) {
		AjaxParams ajaxParams = new AjaxParams();
		ajaxParams.put("uid", getUid());
		ajaxParams.put("type", photos.getType());
		ajaxParams.put("xid", photos.getId() + "");
		ajaxParams.put("xuser_id", photos.getUser().getUid());
		sendPost(SEND_PRAISE, ajaxParams);
	}

	public void resetFramHight() {
		LinearLayout.LayoutParams params = (LayoutParams) fram_layout
				.getLayoutParams();
		int w = getScreenWidth(this);
		if (isshowHepai) {
			int h = (w / 2 - DensityUtil.dip2px(this, 10) + DensityUtil.dip2px(
					this, 92));
			params.width = w;
			int size = mergeBeans.size();
			int item = 0;
			if (size > 2) {// 小于2条
				item = size % 2 == 0 ? size / 2 : size / 2 + 1;
			} else {
				item = 1;
			}
			params.height = h * item;
			if (size == 0) {
				emptyview.setVisibility(View.VISIBLE);
				params.height = DensityUtil.dip2px(this, 200);
			} else {
				emptyview.setVisibility(View.GONE);
			}

		} else {
			int h = DensityUtil.dip2px(this, 62) * comBeans.size() - 1;
			params.width = w;
			params.height = h;
			if (comBeans.size() == 0) {
				emptyview.setVisibility(View.VISIBLE);
				params.height = DensityUtil.dip2px(this, 200);
			} else {
				emptyview.setVisibility(View.GONE);
			}
		}
		params.setMargins(0, 0, 0, DensityUtil.dip2px(this, 10));
		fram_layout.setLayoutParams(params);
	}

	@Override
	public void onSuccess(String url, Object obj) {
		super.onSuccess(url, obj);
		if (url.startsWith(DETAIL_NEWPHOTOD_URL) && JsonUtil.isSuccess(obj)) {
			try {
				JSONObject jobj = new JSONObject(obj.toString());
				beans = new XPhotos(jobj.getJSONObject("data"));
				loadHeadData();
				loadCommit();
				loadMergePhotos();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (url.startsWith(DETAIL_MERGRPHOTOD_URL)
				&& JsonUtil.isSuccess(obj)) {
			try {
				JSONObject jobj = new JSONObject(obj.toString());
				beans = new XPhotos(jobj.getJSONObject("data"));
				loadHeadData();
				loadCommit();
				loadMergePhotos();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (url.startsWith(CONMMITS_LIST)) {// 获取评论列表成功
			if (JsonUtil.isSuccess(obj)) {
				comBeans.clear();
				try {
					JSONArray array = new JSONObject(obj.toString())
							.getJSONArray("list");
					int n = array.length();
					JSONObject object;
					for (int i = 0; i < n; i++) {
						object = array.getJSONObject(i);
						comBeans.add(new CommentsBean(object));
					}
					if (comBeans.size() > 0) {
						adapter.notifyDataSetChanged();
					}
					resetFramHight();
					stickyScrollView.smoothScrollTo(0, -1);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				t(JsonUtil.getMessage(obj));
			}
		} else if (url.startsWith(SEND_PRAISE)) {// 赞*成功
			if (JsonUtil.isSuccess(obj)) {
				int num = beans.getPraise_num();
				beans.setPraise_num(num);
				iv_parise.setImageResource(R.drawable.item_zan_over);
				tv_parise_count.setText((++num) + "");
			}
		} else if (url.equals(SEND_CONMMIT)) {
			if (JsonUtil.isSuccess(obj)) {
				et_comment.setText("");
				et_comment.setHint("说点啥...");
				loadCommit();
				isshowHepai = false;
				changeList();
			} else {
				t(JsonUtil.getMessage(obj));
			}
		} else if (url.startsWith(BEST_HEPAI_LIST)) {// 合拍列表
			if (JsonUtil.isSuccess(obj)) {
				if (mer_page == 1) {
					mergeBeans.clear();
				}
				try {
					JSONObject object = new JSONObject(obj.toString());

					/*** 判断当前是合拍还是原始合拍 */
					if (type.equals(TYPE_MERGE_PHOTO)) {
						/** 如果是参与合拍的 则在列表不暂时当前的大图合拍 */
						JsonUtil.parseXPhotosBeans(mergeBeans, object,
								beans.getId());
					} else {
						JsonUtil.parseXPhotosBeans(mergeBeans, object);
					}
					if (mergeBeans.size() > 0) {
						mergePhotoAdapter.notifyDataSetChanged();
					} else {
						tv_nodata.setText("还没有人参与合拍,赶紧拿个一血");
					}
					resetFramHight();
					stickyScrollView.smoothScrollTo(0, -1);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} else if (url.equals(REPORT_URL)) {
			if (JsonUtil.isSuccess(obj)) {
				t("举报成功");
			}
		} else if (url.equals(DELETE_URL)) {
			if (JsonUtil.isSuccess(obj)) {
				t("删除成功");
				finish();
			}
		}
	}

	public void onback(View v) {
		finish();
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case WHAT_COMMENTS:// 点击回复谁
			CommentsBean bean = (CommentsBean) msg.obj;
			et_comment.setHint("回复:" + bean.getUser().getName());
			rid = bean.getId();
			ruser_id = bean.getUser().getUid();
			ruser = bean.getUser().getName();
			et_comment.setFocusable(true);
			et_comment.requestFocus();
			stickyScrollView.smoothScrollTo(0, 320);
			showSoftInput(et_comment);
			break;
		case WHAT_PARISE:// 点击列表中的合拍赞
			XPhotos photos = (XPhotos) msg.obj;
			sendParise(photos);
		case WHAT_SAVE_SUCCESS:
			t("保存成功");
			break;
		case WHAT_SPLITE_SUCCESS:// 切割图片完毕
			iv_takehepai.setEnabled(true);
			DataFactory.bitmap = canyubitmap;
			break;
		case WHAT_SPLITE_FAILD:// 切割图片完毕
			iv_takehepai.setEnabled(true);
			DataFactory.bitmap = null;
			break;
		}
		return false;
	}

}












/***
 * 	case 101:// 成功
			t(msg.obj.toString());
			rootview.clearAnimation();
			// rootview.startAnimation(AnimationUtils.loadAnimation(this,
			// R.anim.act_resum));
			break;
		case 102:// 失败
			t(msg.obj.toString());
			rootview.clearAnimation();
			// rootview.startAnimation(AnimationUtils.loadAnimation(this,
			// R.anim.act_resum));
			break;
		case 103:// 取消
			t(msg.obj.toString());
			rootview.clearAnimation();
			// rootview.startAnimation(AnimationUtils.loadAnimation(this,
			// R.anim.act_resum));
			break;
		case 105:
			// 关闭分享框
			rootview.clearAnimation();
			rootview.requestFocus();
			break;
 * */
