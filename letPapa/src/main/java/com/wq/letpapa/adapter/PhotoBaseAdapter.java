package com.wq.letpapa.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.wq.letpapa.R;
import com.wq.letpapa.bean.XPhotos;
import com.wq.letpapa.ui.UsersDetailActivity;
import com.wq.letpapa.ui.XPhotoDetailActivity;
import com.wq.letpapa.utils.Constant;
import com.wq.letpapa.utils.DensityUtil;

public class PhotoBaseAdapter extends LBBaseAdapter {
	/** 合拍展示适配器 */
	static int pading = 0;
	static int linerarpading = 0;
	static int dp12 = 0;
	DisplayImageOptions iconoOptions;
	DisplayImageOptions CircleOptions;
	Handler handler;
	static int screenWidth;
	public static int THEME_SHOWICON = 101;
	public static int THEME_SHOW_HEADTXT = 102;
	
	public static int THEME_TYPE_PHOTO = 103;
	public static int THEME_TYPE_MERGEPHOTO = 104;
	// 判断当前主题是否需要显示头像栏目
	
	
	
	int them = THEME_SHOWICON;
	int themtype=THEME_TYPE_MERGEPHOTO;
	public PhotoBaseAdapter(Context context, ArrayList<?> datas, Handler handler) {
		super(context, datas);
		this.handler = handler;
		screenWidth = getScreenWidth((Activity) context);
		pading = DensityUtil.dip2px(context, 11);
		options = getFadin(500);
		CircleOptions = getRoundedBitmapDisplayer(-1);
		iconoOptions=getRoundedBitmapDisplayer(3);
	}

	/***
	 * 设置当前
	 * @param theme
	 */
	public void setTheme(int theme,boolean isNotifyDataSetChanged) {
		them=theme;
		if(isNotifyDataSetChanged){
			notifyDataSetChanged();
		}
	}
	
	public void setThemType(int themtype,boolean isNotifyDataSetChanged){
		if(themtype==THEME_TYPE_MERGEPHOTO){
			iconoOptions = getRoundedBitmapDisplayer(-1);
		}else{
			iconoOptions = getRoundedBitmapDisplayer(3);
		}
		if(isNotifyDataSetChanged){
			notifyDataSetChanged();
		}
	}
	

	class OnItemChildViewOnClick implements OnClickListener {
		int pos;

		public OnItemChildViewOnClick(int pos) {
			this.pos = pos;
		}

		@Override
		public void onClick(View arg0) {
			XPhotos bean = (XPhotos) datas.get(pos);
			switch (arg0.getId()) {
			case R.id.parise_layout:
				Message msg = handler.obtainMessage();
				msg.what = Constant.WHAT_PARISE;
				msg.obj = bean;
				if(!bean.isIsparise()){
					bean.setIsparise(true);
					((ImageView) arg0.findViewById(R.id.iv_parise))
							.setImageResource(R.drawable.item_zan_over);
					int num = bean.getPraise_num();
					bean.setPraise_num(++num);
					bean.setIsparise(true);
					((TextView) arg0.findViewById(R.id.tv_praise_count))
							.setText(bean.getPraise_num() + "");
					handler.sendMessage(msg);
				}
				break;
			case R.id.commit_layout:
				Intent in = new Intent(context, XPhotoDetailActivity.class);
				in.putExtra("bean", bean);
				in.putExtra("type", bean.getType());
				// in.putExtra("dowaht", "");
				context.startActivity(in);
				break;
			case R.id.iv_icon:
				Intent userin = new Intent(context, UsersDetailActivity.class);
				userin.putExtra("user", bean.getUser());
				context.startActivity(userin);
				break;
			}
		}
	}

	static class ViewHolder {
		TextView tvname, tvtime, tv_com_count, tv_praise_count;
		ImageView iv_icon, iv_image, iv_parise;
		LinearLayout rootview, parise_layout, commit_layout;
		View top_empty, bottom_empty;
		RelativeLayout headlayout;
		TextView tv_head_txt;

		public ViewHolder(View v) {
			rootview = (LinearLayout) v.findViewById(R.id.rootview);
			tvname = (TextView) v.findViewById(R.id.tv_name);
			tvtime = (TextView) v.findViewById(R.id.tv_time);
			iv_icon = (ImageView) v.findViewById(R.id.iv_icon);
			iv_image = (ImageView) v.findViewById(R.id.iv_image);
			iv_parise = (ImageView) v.findViewById(R.id.iv_parise);
			tv_com_count = (TextView) v.findViewById(R.id.tv_commont_count);
			tv_praise_count = (TextView) v.findViewById(R.id.tv_praise_count);
			parise_layout = (LinearLayout) v.findViewById(R.id.parise_layout);
			commit_layout = (LinearLayout) v.findViewById(R.id.commit_layout);
			top_empty = v.findViewById(R.id.top_empty);
			bottom_empty = v.findViewById(R.id.bottom_empty);
			headlayout = (RelativeLayout) v.findViewById(R.id.head_layout);
			tv_head_txt = (TextView) v.findViewById(R.id.tv_head_txt);
			
			LayoutParams params = new LayoutParams(screenWidth / 2 - pading,
					screenWidth / 2 - pading);
			
			iv_image.setLayoutParams(params);
		}
	}

}
