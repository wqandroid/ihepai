package com.wq.letpapa.customview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wq.letpapa.R;
import com.wq.letpapa.ui.MyDialogListener;

/**
 * <p>
 * Title: CustomDialog
 * </p>
 * <p>
 * Description:自定义Dialog（参数传入Dialog样式文件，Dialog布局文件）
 * </p>
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * @author archie
 * @version 1.0
 */
public class CustomDialog extends Dialog {
	int layoutRes;// 布局文件
	Context context;

	public CustomDialog(Context context) {
		super(context);
		this.context = context;
	}

	/**
	 * 自定义布局的构造方法
	 * 
	 * @param context
	 * @param resLayout
	 */
	public CustomDialog(Context context, int resLayout) {
		super(context);
		this.context = context;
		this.layoutRes = resLayout;
	}

	/**
	 * 自定义主题及布局的构造方法
	 * 
	 * @param context
	 * @param theme
	 * @param resLayout
	 */
	public CustomDialog(Context context, int theme, int resLayout) {
		super(context, theme);
		this.context = context;
		this.layoutRes = resLayout;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(dialog);
	}

	TextView title, sub_title, tv_content;
	LinearLayout content_layout;
	LinearLayout click_view;
	View dialog;
	Button dialog_left, dialog_right;
	LinearLayout dialog_button_layout;

	public CustomDialog(Context context, final Build build) {
		super(context, R.style.customDialog);
		dialog = LayoutInflater.from(context).inflate(
				R.layout.dialog_chose_theme, null);
		title = (TextView) dialog.findViewById(R.id.dialog_title);
		sub_title = (TextView) dialog.findViewById(R.id.dialog_subtitle);
		content_layout = (LinearLayout) dialog
				.findViewById(R.id.dialog_conteng_layout);
		tv_content = (TextView) dialog.findViewById(R.id.dialog_content);
		dialog_left = (Button) dialog.findViewById(R.id.dialog_left);
		dialog_right = (Button) dialog.findViewById(R.id.dialog_right);
		dialog_button_layout = (LinearLayout) dialog
				.findViewById(R.id.dialog_button_layout);

		if (isNull(build.title)) {
			title.setText("标题");
		} else {
			title.setText(build.title);
		}

		if (isNull(build.sub_title)) {
			sub_title.setVisibility(View.GONE);
		} else {
			sub_title.setText(build.sub_title);
		}

		if (isNull(build.content)) {
			tv_content.setVisibility(View.GONE);
		} else {
			tv_content.setText(build.content);
		}
		
		if(build.contentview!=null){
			content_layout.removeAllViews();
			content_layout.addView(build.contentview);
		}
		if (build.items != null && build.items.length > 0) {
			content_layout.removeAllViews();
			int n = build.items.length;
			for (int i = 0; i < n; i++) {
				View v = creatView(context, build.items[i], i);
				if (build.onitemClikc != null) {
					v.setOnClickListener(new ViewClick(CustomDialog.this, i,
							build));
				}
				content_layout.addView(v);
			}
		}
		this.setCancelable(build.cancleable);
		if (isNull(build.right)) {
			dialog_right.setVisibility(View.GONE);
		} else {
			dialog_right.setText(build.right);
			if (build.onRightClikc != null) {
				dialog_right.setOnClickListener(new RightViewClick(
						CustomDialog.this, build));
			} else {
				dialog_right.setOnClickListener(new DissmissClick(
						CustomDialog.this));
			}
		}
		if (isNull(build.left)) {
			dialog_left.setVisibility(View.GONE);
		} else {
			dialog_left.setText(build.left);
			if (build.onleftClikc != null) {
				dialog_left.setOnClickListener(new LeftViewClick(
						CustomDialog.this, build));
			} else {
				dialog_left.setOnClickListener(new DissmissClick(
						CustomDialog.this));
			}

		}
		if (isNull(build.left) && isNull(build.right)) {
			dialog_button_layout.setVisibility(View.GONE);
		}
	}
	
	

	public class ViewClick implements View.OnClickListener {

		int i;
		Build build;
		CustomDialog dialog;

		public ViewClick(CustomDialog dialog, int i, Build build) {
			this.i = i;
			this.dialog = dialog;
			this.build = build;
		}

		@Override
		public void onClick(View v) {
			build.onitemClikc.onItemClick(dialog, i);
		}
	}

	public class LeftViewClick implements View.OnClickListener {
		Build build;
		CustomDialog dialog;

		public LeftViewClick(CustomDialog dialog, Build build) {
			this.dialog = dialog;
			this.build = build;
		}

		@Override
		public void onClick(View v) {
			build.onleftClikc.onItemClick(dialog);
		}
	}

	public class RightViewClick implements View.OnClickListener {
		Build build;
		CustomDialog dialog;

		public RightViewClick(CustomDialog dialog, Build build) {
			this.dialog = dialog;
			this.build = build;
		}

		@Override
		public void onClick(View v) {
			build.onRightClikc.onItemClick(dialog);
		}
	}

	public class DissmissClick implements View.OnClickListener {
		CustomDialog dialog;

		public DissmissClick(CustomDialog dialog) {
			this.dialog = dialog;
		}

		@Override
		public void onClick(View v) {
			dialog.dismiss();
		}
	}

	public View creatView(Context context, String txt, final int which) {
		View v = LayoutInflater.from(context).inflate(
				R.layout.dialog_item_list1, null);
		TextView textView = (TextView) v.findViewById(R.id.textview1);
		textView.setText(txt.substring(1, txt.length()));
		return v;
	}

	public boolean isNull(String str) {
		if (str == null || str.equals("")) {
			return true;
		}
		return false;
	}

	public static class Build {
		String title;
		String sub_title;
		String content;
		String[] items;
		String left;
		String right;
		boolean cancleable;
        View contentview;
		public Build() {
		}

		MyDialogListener.OnitemClikc onitemClikc;

		public Build setOnitemClikc(MyDialogListener.OnitemClikc onitemClikc) {
			this.onitemClikc = onitemClikc;
			return this;
		}

		MyDialogListener.OnLeftClick onleftClikc;

		public Build setOnLeftClick(MyDialogListener.OnLeftClick onleftClikc) {
			this.onleftClikc = onleftClikc;
			return this;
		}

		MyDialogListener.OnRightClick onRightClikc;

		public Build setOnRightClikc(MyDialogListener.OnRightClick onRightClikc) {
			this.onRightClikc = onRightClikc;
			return this;
		}

		public Build setTitle(String title) {
			this.title = title;
			return this;
		}

		public Build setSub_title(String sub_title) {
			this.sub_title = sub_title;
			return this;
		}

		
		public Build setContentView(View contentview) {
			this.contentview = contentview;
			return this;
		}
		
		public Build setContent(String content) {
			this.content = content;
			return this;
		}

		public Build setItems(String[] items) {
			this.items = items;
			return this;
		}

		public Build setLeft(String left) {
			this.left = left;
			return this;
		}

		public Build setRight(String right) {
			this.right = right;
			return this;
		}

		public Build setCancleAble(boolean cancleable) {
			this.cancleable = cancleable;
			return this;
		}
		
        CustomDialog dialog;
		public CustomDialog build(Context context) {
			dialog= new CustomDialog(context, this);
			return dialog;
		}
		public void cancle(){
			if(dialog!=null){
				dialog.cancel();
			}
		}
	}

}