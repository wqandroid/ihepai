package com.wq.letpapa.ui;

import com.wq.letpapa.customview.CustomDialog;

public class MyDialogListener {

	public interface OnitemClikc{
		void onItemClick(CustomDialog dialog,int which);
	}
	public interface OnLeftClick{
		void onItemClick(CustomDialog dialog);
	}
	public interface OnRightClick{
		void onItemClick(CustomDialog dialog);
	}
}
