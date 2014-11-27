package com.wq.letpapa.customview;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wq.letpapa.R;

public class PopToast {

	static PopupWindow popupWindow;
	static ProgressBar progressBar;
	static TextView textView;
	static ImageView imageView;

	public static void init(Context context) {
		View v = LayoutInflater.from(context).inflate(R.layout.pop_view, null);
		int w = context.getResources().getDisplayMetrics().widthPixels;
		popupWindow = new PopupWindow(v, w, w);
		progressBar = (ProgressBar) v.findViewById(R.id.pop_pb);
		textView = (TextView) v.findViewById(R.id.pop_tv);
		imageView = (ImageView) v.findViewById(R.id.pop_iv);
	}

	public static void showText(Context context, View v, String txt) {
		if (popupWindow == null) {
			init(context);
		}
		textView.setText(txt);
		popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
	}

	public void changeText(String txt) {
		if (popupWindow != null) {
			textView.setText(txt);
		}
	}

	public static void dismiss(String txt) {
		textView.setText(txt);
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
		}
		popupWindow = null;
	}

	public static void dismiss() {
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
		}
		popupWindow = null;
	}

}
