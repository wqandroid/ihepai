package com.wq.letpapa.customview;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.wq.letpapa.R;



/***
 * @date 2014-5-23
 */
public class PageControl extends LinearLayout {

	public PageControl(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	public PageControl(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public PageControl(Context context) {
		super(context);
		init(context, null);
	}

	int size = 3;
	int pading = 8;

	public void init(Context context, AttributeSet attrs) {
		if (attrs != null) {
			TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
					R.styleable.PageControl);
			size = mTypedArray.getInteger(R.styleable.PageControl_pagesize, 1);
			if (size > 5) {
				pading = 6;
			}
			mTypedArray.recycle();

		}
		loadviews(size, context);
	}

	public void setPageSize(int size){
		this.size=size;
		invalidate();
	}
	
	List<ImageView> imageViews = new ArrayList<ImageView>();

	public void loadviews(int pageSize, Context context) {
		setOrientation(LinearLayout.HORIZONTAL);
		setGravity(Gravity.CENTER);
		ImageView imageView = null;
		for (int i = 0; i < pageSize; i++) {
			imageView = new ImageView(context);
			if (i == 0) {
				imageView.setImageResource(R.drawable.popup_page_active);
			} else {
				imageView.setImageResource(R.drawable.popup_page_inactive);
			}
			imageView.setPadding(pading, 0, pading, 0);
			imageViews.add(imageView);
			this.addView(imageView);
		}
	}

	/**
	 * 
	 * @param index
	 */
	public void chagePage(int index) {
		for (int i = 0; i < size; i++) {
			ImageView imageView = imageViews.get(i);
			if (i == index) {
				imageView.setImageResource(R.drawable.popup_page_active);
			} else {
				imageView.setImageResource(R.drawable.popup_page_inactive);
			}
		}
	}

}
