package com.wq.letpapa.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.wq.letpapa.R;
import com.wq.letpapa.bean.CommentsBean;
import com.wq.letpapa.utils.Base64Util;
import com.wq.letpapa.utils.Constant;
import com.wq.letpapa.utils.DateUtil;

public class CommentsAdapter extends LBBaseAdapter {

	Handler handler;
	public CommentsAdapter(Context context, ArrayList<?> datas,Handler handler) {
		super(context, datas);
		this.handler=handler;
		// 使用DisplayImageOptions.Builder()创建DisplayImageOptions
        options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.white_bg) // 设置图片下载期间显示的图片
		.showImageForEmptyUri(R.drawable.white_bg) // 设置图片Uri为空或是错误的时候显示的图片
		.showImageOnFail(R.drawable.white_bg) // 设置图片加载或解码过程中发生错误显示的图片
		.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
		.cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
		.displayer(new RoundedBitmapDisplayer(8)) // 设置成圆角图片
		// .displayer(new FadeInBitmapDisplayer(600))
		.build(); // 创建配置过得DisplayImageOption对象
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		CommentHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_comments_layout, null);
			holder = new CommentHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (CommentHolder) convertView.getTag();
		}
	final	CommentsBean bean = (CommentsBean) getItem(position);
		getImageLoader().displayImage(bean.getUser().getIcon(), holder.iv_icon,
				options);
		holder.tv_name.setText(bean.getUser().getName());
		holder.tv_coment_txt.setText(Base64Util.decode(bean.getContent()));
		holder.tv_time.setText(DateUtil.getDiffTime(Long.parseLong(bean.getInp_time())));

		
		if(bean.getRuser_id()>0){//说明是回复人的
			holder.reply_layout.setVisibility(VISIBLE);
			holder.tv_reply_name.setText(bean.getRuser()+" : ");
			holder.tv_huifu.setText("回复");
		}else{
			holder.reply_layout.setVisibility(INVISIBLE);
			holder.tv_huifu.setText("");
			holder.tv_reply_name.setText("");
		}
//		if (position % 2 == 0 || position == 0) {
//			holder.relativeLayout.setBackgroundResource(R.drawable.pos_eb);
//		} else {
			holder.relativeLayout.setBackgroundResource(R.drawable.pos_f5);
//		}
		holder.relativeLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Message message=handler.obtainMessage();
                message.what=Constant.WHAT_COMMENTS;
                message.obj=bean;
				handler.sendMessage(message);
			}
		});
		
		return convertView;
	}

	static class CommentHolder {
		ImageView iv_icon;
	
		TextView tv_reply_name, tv_name, tv_time, tv_coment_txt,tv_huifu;
		RelativeLayout relativeLayout;
		LinearLayout reply_layout;
		public CommentHolder(View v) {
			iv_icon = (ImageView) v.findViewById(R.id.iv_icon);
			tv_name = (TextView) v.findViewById(R.id.tv_name);
			tv_time = (TextView) v.findViewById(R.id.tv_time);
			tv_reply_name = (TextView) v.findViewById(R.id.tv_reply_name);
			tv_coment_txt = (TextView) v.findViewById(R.id.tv_coment_txt);
			relativeLayout = (RelativeLayout) v.findViewById(R.id.item_root);
			reply_layout=(LinearLayout) v.findViewById(R.id.reply_layout);
			tv_huifu=(TextView) v.findViewById(R.id.tv_huifu);
		}
	}

}
