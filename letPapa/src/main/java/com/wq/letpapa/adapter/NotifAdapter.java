package com.wq.letpapa.adapter;

import java.util.ArrayList;

import com.wq.letpapa.R;
import com.wq.letpapa.bean.NotifBean;
import com.wq.letpapa.utils.Base64Util;
import com.wq.letpapa.utils.DateUtil;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class NotifAdapter extends LBBaseAdapter {

	public NotifAdapter(Context context, ArrayList<?> datas) {
		super(context, datas);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		NotiHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_notif_layout, null);
			holder = new NotiHolder(convertView);
			convertView.setTag(holder);
			;
		} else {
			holder = (NotiHolder) convertView.getTag();
		}
		NotifBean bean = (NotifBean) datas.get(position);

		holder.tv_name.setText(bean.getUser().getName());
		holder.tv_time.setText(DateUtil.getDiffTime(bean.getInp_time()));
		holder.tv_contnet.setText("");
		if (bean.getData_type().equals("praise")) {// 赞
			holder.iv_typeinfo.setImageResource(R.drawable.item_zan);
		} else if (bean.getData_type().equals("mphoto")) {// 参与合拍
			holder.tv_contnet.setText("参与了你的合拍");
			holder.iv_typeinfo.setImageResource(R.drawable.item_camera);
		} else if (bean.getData_type().equals("reply")) {// 回复
			holder.iv_typeinfo.setImageResource(R.drawable.item_commints);
			holder.tv_contnet.setText("回复你:"+Base64Util.decode(bean.getContent()));
		} else if (bean.getData_type().equals("comments")) {// 评论
			holder.iv_typeinfo.setImageResource(R.drawable.item_commints);
			holder.tv_contnet.setText("评论:"+Base64Util.decode(bean.getContent()));
		}
		displayImage(holder.iv_icon, bean.getUser().getIcon(), getRoundedBitmapDisplayer(-1));
		displayImage(holder.iv_thumb, bean.getThumb());
		return convertView;
	}

	static class NotiHolder {
		TextView tv_name, tv_time, tv_contnet;
		ImageView iv_icon, iv_thumb, iv_typeinfo;

		public NotiHolder(View v) {
			tv_name = (TextView) v.findViewById(R.id.tv_name);
			tv_time = (TextView) v.findViewById(R.id.tv_time);
			tv_contnet = (TextView) v.findViewById(R.id.tv_content);
			iv_icon = (ImageView) v.findViewById(R.id.iv_icon);
			iv_thumb = (ImageView) v.findViewById(R.id.iv_thumb);
			iv_typeinfo = (ImageView) v.findViewById(R.id.iv_typeinfo);
		}
	}

}
