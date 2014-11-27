package com.wq.letpapa.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SimpleTypeAdapter extends BaseAdapter {

	List<Typeface> typefaces;
	Context context;
	public SimpleTypeAdapter(Context context, List<Typeface> typefaces) {
		this.typefaces = typefaces;
		this.context=context
				;
		
	}

	@Override
	public int getCount() {
		return typefaces.size();
	}

	@Override
	public Object getItem(int arg0) {
		return typefaces.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		TextView textView=new TextView(context);
		if(arg0==0){
			textView.setText("合拍");
		}else{
			textView.setText("hepai");
		}
		textView.setTextColor(Color.WHITE);
		textView.setGravity(Gravity.CENTER_VERTICAL);
		textView.setTextSize(32);
		textView.setTypeface(typefaces.get(arg0));
		return textView;
	}

	
	
	
	
	
	
	
	
	
	
	
	
}
