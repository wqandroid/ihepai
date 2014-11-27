package com.wq.letpapa.adapter;

import java.util.ArrayList;

import com.wq.letpapa.R;
import com.wq.letpapa.bean.XPhotos;
import com.wq.letpapa.ui.XPhotoDetailActivity;
import com.wq.letpapa.utils.DateUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

public class XphotAdapter extends PhotoBaseAdapter {

	public XphotAdapter(Context context, ArrayList<?> datas, Handler handler) {
		super(context, datas, handler);

	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_xphotolist_layout,
					null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		XPhotos beans = (XPhotos) getItem(position);
		if (them == THEME_SHOW_HEADTXT) {// 显示主题内容
			hideView(holder.headlayout);
			holder.tv_head_txt.setText("#" + beans.getTopic_title() + "#");
			showView(holder.tv_head_txt);
		} else {// 显示头像
			showView(holder.headlayout);
			hideView(holder.tv_head_txt);
			if (beans.isMerge()) {
				displayImage(holder.iv_icon, beans.getUser().getIcon(),
						CircleOptions);
			} else {
				displayImage(holder.iv_icon, beans.getUser().getIcon(),
						iconoOptions);
			}
			holder.tvname.setText(beans.getUser().getName());
			holder.tvtime.setText(DateUtil.getDiffTime(Long.parseLong(beans
					.getTime())));
			holder.iv_icon.setOnClickListener(new OnItemChildViewOnClick(
					position));
		}
		holder.tv_com_count.setText(beans.getCon_num() + "");
		holder.tv_praise_count.setText(beans.getPraise_num() + "");
		if (beans.isIsparise()) {// 如果已经赞过
			holder.iv_parise.setImageResource(R.drawable.item_zan_over);
		} else {
			holder.iv_parise.setImageResource(R.drawable.item_zan);
		}
		getImageLoader().displayImage(beans.getImage1(), holder.iv_image,
				options);
		holder.parise_layout.setOnClickListener(new OnItemChildViewOnClick(
				position));
		holder.commit_layout.setOnClickListener(new OnItemChildViewOnClick(
				position));
		holder.iv_image.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent in = new Intent(context, XPhotoDetailActivity.class);
				in.putExtra("bean", (XPhotos) getItem(position));
				in.putExtra("type", ((XPhotos) getItem(position)).getType());
				context.startActivity(in);
			}
		});

		if (position == 0 || position == 1) {
			showView(holder.top_empty);
		} else {
			hideView(holder.top_empty);
		}
		// 判断末尾是否显示2个item
		if (position % 2 == 0 && datas.size() > 2) {
			if (position == datas.size() || position == datas.size() - 1) {
				showView(holder.bottom_empty);
			} else {
				hideView(holder.bottom_empty);
			}
		} else if (position % 2 != 0 && datas.size() > 2) {
			if (position == datas.size()) {
				showView(holder.bottom_empty);
			} else {
				hideView(holder.bottom_empty);
			}
		}
		return convertView;
	}

}
