package com.wq.letpapa.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.wq.letpapa.R;
import com.wq.letpapa.customview.util.LOCATION;
import com.wq.letpapa.utils.BitmapUtil;
import com.wq.letpapa.utils.Constant;

public class TouchOverlayView extends LinearLayout {

	public static final int MIN_DIS = 10;//
	public static final int MAX_DIS = 30;// 最大划动标准
	LOCATION nowlocation = LOCATION.TOP;

	public TouchOverlayView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	public TouchOverlayView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public TouchOverlayView(Context context) {
		super(context);
		init(context, null);
	}

	FrameLayout root;
	LinearLayout overlay;
	int screenWidth;
	ImageView imageView;
	WQRotateImageView overlay_iv;
	boolean islandspace = true;
	boolean isanimation = true;

	public void init(Context context, AttributeSet attributeSet) {
		if (attributeSet != null) {
			TypedArray mTypedArray = context.obtainStyledAttributes(
					attributeSet, R.styleable.TouchOverlayView);
			islandspace = mTypedArray.getBoolean(
					R.styleable.TouchOverlayView_islandspace, true);
			mTypedArray.recycle();
		}
		if (getOrientation() == LinearLayout.HORIZONTAL) {
			islandspace = false;
		} else {
			islandspace = true;
		}
		View v = LayoutInflater.from(context).inflate(R.layout.touchoverlay,
				this);
		setLongClickable(true);
		setFocusable(true);
		root = (FrameLayout) v.findViewById(R.id.rootview);
		imageView = (ImageView) v.findViewById(R.id.imageView);
		overlay = (LinearLayout) v.findViewById(R.id.overlay_view);
		overlay_iv = (WQRotateImageView) v.findViewById(R.id.overlay_iv);
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getResources().getDisplayMetrics();
		if (islandspace) {
			screenWidth = dm.heightPixels;
		} else {
			screenWidth = dm.widthPixels;
		}
		LayoutParams layoutParams = new LayoutParams(screenWidth, screenWidth);
		root.setLayoutParams(layoutParams);
		FrameLayout.LayoutParams overParams = new FrameLayout.LayoutParams(
				screenWidth, screenWidth / 2);
		overlay.setLayoutParams(overParams);
	}

	// 在需要根据环境条件进行动态设定屏幕朝向需求时可以这样实现：
	// private void changeOritation(int n){
	// if(n>0&&this.getRequestedOrientation() ==
	// ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
	// this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	// else if(n<0&&this.getRequestedOrientation() ==
	// ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
	// this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	// }

	boolean isTouchable = true;

	public void setNoTouch() {
		isTouchable = false;
	}

	public void setNoAnim() {
		isanimation = false;
	}

	public LOCATION getNowlocation() {
		return nowlocation;
	}

	public int getLocation() {
		int oc = 0;
		if (nowlocation == LOCATION.TOP) {
			oc = Constant.RIGHT;
		} else if (nowlocation == LOCATION.LEFT) {
			oc = Constant.TOP;
		} else if (nowlocation == LOCATION.DOWN) {
			oc = Constant.LEFT;
		} else if (nowlocation == LOCATION.RIGHT) {
			oc = Constant.DOWN;
		}
		return oc;
	}

	/**
	 * 设置呀参与合拍覆盖相片
	 * 
	 * @param bitmap
	 * @param or
	 */
	public void setoverlayIv(Bitmap bitmap, int or) {
		if (or == Constant.LEFT) {
			overlay_iv.setImageBitmap(BitmapUtil.PhotoRotation(bitmap, 180));
		} else if (or == Constant.TOP) {
			overlay_iv.setImageBitmap(BitmapUtil.PhotoRotation(bitmap, 180));
		} else {
			overlay_iv.setImageBitmap(bitmap);
		}
		starlocation(or);
	}

	public void rotateNext() {
		if (isTouchable) {
			if (nowlocation == LOCATION.TOP) {
				Move(LOCATION.TOP, LOCATION.LEFT);
			} else if (nowlocation == LOCATION.LEFT) {
				// Move(LOCATION.LEFT, LOCATION.DOWN);
				StartAnim(-90, -180);
				nowlocation = LOCATION.DOWN;
			} else if (nowlocation == LOCATION.DOWN) {
				Move(LOCATION.DOWN, LOCATION.RIGHT);
			} else if (nowlocation == LOCATION.RIGHT) {
				Move(LOCATION.RIGHT, LOCATION.TOP);
			}
		}
	}

	public void starlocation(int or) {
		nowlocation = LOCATION.TOP;
		switch (or) {
		case 1:
			Move(nowlocation, LOCATION.TOP);
			break;
		case 2:
			Move(nowlocation, LOCATION.LEFT);
			break;
		case 3:
			Move(nowlocation, LOCATION.DOWN);
			break;
		case 4:
			Move(nowlocation, LOCATION.RIGHT);
			break;
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		return super.dispatchTouchEvent(ev);
	}

	public void setImageNone() {
		imageView.setVisibility(View.GONE);
	}

	public void setImage(Bitmap bitmap) {
		imageView.setVisibility(View.VISIBLE);
		imageView.setImageBitmap(bitmap);
	}

	public Point downpoint;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downpoint = new Point();
			downpoint.x = (int) event.getX();
			downpoint.y = (int) event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			Point point = new Point((int) event.getX(), (int) event.getY());
			DoAction(downpoint, point);
			break;
		}
		return super.onTouchEvent(event);
	}

	public void DoAction(Point dp, Point up) {
		// 误点 不做处理
		if (!isTouchable || Math.abs(dp.x - up.x) < MIN_DIS
				&& Math.abs(dp.y - up.y) < MIN_DIS) {
			return;
		}
		// 像下
		if (up.x - dp.x > MAX_DIS
				&& Math.abs(dp.x - up.x) > Math.abs(dp.y - up.y)) {
			System.out.println("向右");
			Move(nowlocation, LOCATION.RIGHT);
		} else if (dp.x - up.x > MAX_DIS
				&& Math.abs(dp.x - up.x) > Math.abs(dp.y - up.y)) {
			System.out.println("向左");
			Move(nowlocation, LOCATION.LEFT);
		} else if (dp.y - up.y > MAX_DIS
				&& Math.abs(dp.x - up.x) < Math.abs(dp.y - up.y)) {
			System.out.println("向上");
			Move(nowlocation, LOCATION.TOP);
		} else if (up.y - dp.y > MAX_DIS
				&& Math.abs(dp.x - up.x) < Math.abs(dp.y - up.y)) {
			System.out.println("向下");
			Move(nowlocation, LOCATION.DOWN);
		}
	}

	public void Move(LOCATION old, LOCATION next) {
		if (old == next) {
			System.out.println("方向重复...");
			return;
		}
		if (next == LOCATION.TOP) {
			Move(0);
		} else if (next == LOCATION.RIGHT) {
			Move(90);
		} else if (next == LOCATION.DOWN) {
			Move(180);
		} else if (next == LOCATION.LEFT) {
			Move(-90);
		}
		nowlocation = next;
	}

	/**
	 * 旋转角度
	 * 
	 * @param angle
	 */
	public void Move(int angle) {
		float fromDegrees = 0;
		if (nowlocation == LOCATION.TOP) {
			fromDegrees = 0;
		} else if (nowlocation == LOCATION.LEFT) {
			fromDegrees = -90;
		} else if (nowlocation == LOCATION.DOWN) {
			fromDegrees = 180;
		} else if (nowlocation == LOCATION.RIGHT) {
			fromDegrees = 90;
		}
		StartAnim(fromDegrees, angle);
	}

	/**
	 * 
	 * @param angle
	 *            移动多少度
	 * @param fromDegrees
	 *            从多少度
	 */
	private void StartAnim(float fromDegrees, int angle) {
		// System.out.println("从"+fromDegrees+"旋转到:"+angle);
		RotateAnimation animation = new RotateAnimation(fromDegrees, angle,
				Animation.RELATIVE_TO_PARENT, 0.5f,
				Animation.RELATIVE_TO_PARENT, 0.5f);
		if (isanimation) {
			if (Math.abs(angle) / 90 == 1) {
				animation.setDuration(600);
			} else {
				animation.setDuration(1200);
			}
		} else {
			animation.setDuration(10);
		}
		animation.setInterpolator(new DecelerateInterpolator(1f));
		animation.setFillAfter(true);
		overlay.startAnimation(animation);
	}

	public void setOverlay() {
		LayoutParams layoutParams = (LayoutParams) overlay.getLayoutParams();
		if (nowlocation == LOCATION.DOWN) {
			layoutParams.width = screenWidth;
			layoutParams.height = screenWidth / 2;
			layoutParams.setMargins(0, screenWidth / 2, 0, 0);
		} else if (nowlocation == LOCATION.TOP) {
			layoutParams.width = screenWidth;
			layoutParams.height = screenWidth / 2;
			// layoutParams.setMargins(0, screenWidth/2, 0, 0);
		} else if (nowlocation == LOCATION.LEFT) {
			layoutParams.width = screenWidth / 2;
			layoutParams.height = screenWidth;
			layoutParams.setMargins(0, 0, screenWidth / 2, 0);
		} else if (nowlocation == LOCATION.RIGHT) {
			layoutParams.width = screenWidth / 2;
			layoutParams.height = screenWidth;
			layoutParams.setMargins(screenWidth / 2, 0, 0, 0);
		}
		overlay.setLayoutParams(layoutParams);
		requestLayout();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		System.out.println("onLayout" + changed + ":l" + l);
		super.onLayout(changed, l, t, r, b);
	}

}
