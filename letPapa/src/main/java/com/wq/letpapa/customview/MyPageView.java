package com.wq.letpapa.customview;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
public class MyPageView extends ViewPager{

	public MyPageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyPageView(Context context) {
		super(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.EXACTLY);
		super.onMeasure(widthMeasureSpec, 720);
	}
	
}
