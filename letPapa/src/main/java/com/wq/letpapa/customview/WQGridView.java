package com.wq.letpapa.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class WQGridView extends GridView {

	boolean haveScrollbars;
	public WQGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public WQGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public WQGridView(Context context) {
		super(context);
	}
	 /**  
     * 设置是否有ScrollBar，当要在ScollView中显示时，应当设置为false。 默认为 true  
     *   
     * @param haveScrollbars  
     */   
    public void setHaveScrollbar(boolean haveScrollbar) {   
        this.haveScrollbars = haveScrollbar;   
    }   
    @Override   
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {   
        if (haveScrollbars == false) {   
            int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);   
            super.onMeasure(widthMeasureSpec, expandSpec);   
        } else {   
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);   
        }   
    }   

}
