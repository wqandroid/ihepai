package com.wq.letpapa.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.wq.letpapa.R;
import com.wq.letpapa.adapter.SimpleTypeAdapter;
import com.wq.letpapa.cache.PhotoFileManger;
import com.wq.letpapa.customview.ColorPickerDialog;
import com.wq.letpapa.customview.ColorPickerDialog.OnColorChangedListener;
import com.wq.letpapa.ui.base.BaseActivity;
import com.wq.letpapa.utils.BitmapUtil;
import com.wq.letpapa.utils.Blur;
import com.wq.letpapa.utils.Constant;

public class PhotoEditeActivity extends BaseActivity implements Constant,
		Callback, OnClickListener, OnColorChangedListener {

	public int radius = 88;// 高斯模糊度

	public static final int DO_NOCOLOR = 11;
	public static final int DO_BLUR = 12;
	public static final int DO_MODE = 13;
	public static final int DO_WHITE = 14;
	public static final int DO_BLACK = 15;

	public int now_do = DO_NOCOLOR;

	public class TaskBean {
		String url;
		Bitmap bitmap;
		Bitmap originalbitmap;
		Bitmap blurbitmap;
		int or = 0;// 覆盖位置 1 左 2下 3 右 4 上
		boolean isSplitOver = false;

		public void recycle() {
			if (bitmap != null) {
				bitmap.recycle();
				bitmap = null;
			}
			if (originalbitmap != null) {
				originalbitmap.recycle();
				originalbitmap = null;
			}
			if (blurbitmap != null) {
				blurbitmap.recycle();
				blurbitmap = null;
			}
		}
	}

	Bitmap editebleBitmap;
	ImageView imageView;
	ProgressBar bar;

	ImageView or_imageview, blur_imageiew;
	RelativeLayout rootview;
	FrameLayout iv_fram;
	// 覆盖层以及输入框 2个bitmap容器
	LinearLayout layer_layout, bitmap_layer;
	RadioGroup radioGroup, theme_group;
	CheckBox ck_txtsize;
	SeekBar seekBar1;
	boolean isChecked = false;
	Typeface mFont;
	Handler handler;
	public TaskBean nowtaskbena;
	AssetManager assetManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_photo_edite_layout);
		handler = new Handler(this);
		initviews();
		String url = getIntent().getExtras().getString("path");
		Bitmap bitmap = BitmapUtil.PhotoRotation(BitmapFactory.decodeFile(url),
				90);
		imageView.setImageBitmap(bitmap);
		mFont = Typeface.createFromAsset(getAssets(), "Kaiti.ttf");

		setLayoutFram(getIntent().getIntExtra("location", TOP));
		assetManager = getAssets();
		TaskBean bean = new TaskBean();
		bean.bitmap = bitmap;
		bean.url = url;
		bean.or = getIntent().getIntExtra("location", TOP);

		new spliteTask().execute(bean);
		initListener();
	}

	private void initviews() {
		imageView = (ImageView) findViewById(R.id.imageView);
		iv_fram = (FrameLayout) findViewById(R.id.iv_fram);
		layer_layout = (LinearLayout) findViewById(R.id.layer_layout);
		bitmap_layer = (LinearLayout) findViewById(R.id.bitmap_layer);
		radioGroup = (RadioGroup) findViewById(R.id.color_group);
		theme_group = (RadioGroup) findViewById(R.id.theme_group);
		ck_txtsize = (CheckBox) findViewById(R.id.ck_txtsize);
		seekBar1 = (SeekBar) findViewById(R.id.seekBar1);
		rootview = (RelativeLayout) findViewById(R.id.rootview);
		bar = (ProgressBar) findViewById(R.id.progressBar1);
	}

	public void initListener() {
		ck_txtsize
				.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton arg0,
							boolean isChecked) {
						showPop();
					}
				});

		seekBar1.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
			}

			@Override
			public void onProgressChanged(SeekBar seekabr, int arg1,
					boolean arg2) {
				if (arg2) {
					EditText editText = (EditText) layer_layout
							.findViewWithTag("editText");
					if (editText == null) {
						return;
					}
					float size = seekabr.getProgress();
					if (size <= 10) {
						size = 10;
						seekBar1.setProgress(10);
					}
					editText.setTextSize(size);
				}
			}
		});

		theme_group.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int checkedId) {
				if (blur_imageiew == null) {
					return;
				}
				bar.setVisibility(View.VISIBLE);
				switch (checkedId) {
				case R.id.ck_theme_noclor:
					new ImageEditeTask(PhotoEditeActivity.this)
							.execute(DO_NOCOLOR);
					break;
				case R.id.ck_theme_blur:
					new ImageEditeTask(PhotoEditeActivity.this)
							.execute(DO_BLUR);
					break;
				case R.id.ck_theme_mode:
					new ImageEditeTask(PhotoEditeActivity.this)
							.execute(DO_MODE);
					break;
				case R.id.ck_theme_white:
					new ImageEditeTask(PhotoEditeActivity.this)
							.execute(DO_WHITE);
					break;
				case R.id.ck_theme_black:
					new ImageEditeTask(PhotoEditeActivity.this)
							.execute(DO_BLACK);
					break;
				}
			}
		});

		findViewById(R.id.ck_blue).setOnClickListener(this);
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int checkedId) {
				EditText editText = (EditText) layer_layout
						.findViewWithTag("editText");
				if (editText == null) {
					return;
				}
				switch (checkedId) {
				case R.id.ck_white:
					editText.setTextColor(Color.WHITE);
					break;
				case R.id.ck_black:
					editText.setTextColor(Color.BLACK);
					break;
				// case R.id.ck_blue:
				// editText.setTextColor(Color.parseColor("#479cff"));
				// break;
				case R.id.ck_fen:
					editText.setTextColor(Color.parseColor("#ea68a2"));
					break;
				case R.id.ck_green:
					editText.setTextColor(Color.parseColor("#009944"));
					break;
				case R.id.ck_red:
					editText.setTextColor(Color.parseColor("#ca0000"));
					break;
				case R.id.ck_yellow:
					editText.setTextColor(Color.parseColor("#fff100"));
					break;
				case R.id.ck_gray:
					editText.setTextColor(Color.parseColor("#898989"));
					break;
				}
			}
		});
		findViewById(R.id.success).setOnClickListener(this);
	}

	EditText editText;

	@SuppressWarnings("deprecation")
	public void setLayoutFram(int or) {
		int w = getScreenWidth(this);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				w, w);
		iv_fram.setLayoutParams(layoutParams);
		bitmap_layer.removeAllViews();
		layer_layout.removeAllViews();
		/** 覆盖层 输入框初始化 */
		LinearLayout layer = new LinearLayout(this);
		layer.setOrientation(LinearLayout.HORIZONTAL);
		layer.setBackgroundColor(R.color.theme_black);
		layer.setTag("layer");
		editText = new EditText(this);
		editText.setTag("editText");
		editText.setGravity(Gravity.CENTER);
		editText.setTextSize(32);
		editText.setBackground(new BitmapDrawable());
		editText.setTextColor(Color.WHITE);
		editText.setCursorVisible(false);
		editText.setTypeface(mFont);
		if (getValue(SP_IS_SHADOW, true)) {
			editText.setShadowLayer(1, 2, 1, Color.parseColor("#90181818"));
		}
		/***/
		or_imageview = new ImageView(this);
		blur_imageiew = new ImageView(this);
		switch (or) {// 判断覆盖层位置
		case TOP:
			layer_layout.setOrientation(LinearLayout.VERTICAL);
			layer.setLayoutParams(new LinearLayout.LayoutParams(w, w / 2));
			editText.setLayoutParams(new LinearLayout.LayoutParams(w, w / 2));
			layer_layout.addView(layer);
			layer_layout.addView(editText);
			/** ----------------------- */
			bitmap_layer.setOrientation(LinearLayout.VERTICAL);
			or_imageview
					.setLayoutParams(new LinearLayout.LayoutParams(w, w / 2));
			blur_imageiew.setLayoutParams(new LinearLayout.LayoutParams(w,
					w / 2));
			bitmap_layer.addView(blur_imageiew);
			bitmap_layer.addView(or_imageview);
			break;
		case DOWN:
			layer_layout.setOrientation(LinearLayout.VERTICAL);
			layer.setLayoutParams(new LinearLayout.LayoutParams(w, w / 2));
			editText.setLayoutParams(new LinearLayout.LayoutParams(w, w / 2));
			layer_layout.addView(editText);
			layer_layout.addView(layer);
			/** ----------------------- */
			bitmap_layer.setOrientation(LinearLayout.VERTICAL);
			bitmap_layer.setOrientation(LinearLayout.VERTICAL);
			or_imageview
					.setLayoutParams(new LinearLayout.LayoutParams(w, w / 2));
			blur_imageiew.setLayoutParams(new LinearLayout.LayoutParams(w,
					w / 2));
			bitmap_layer.addView(or_imageview);
			bitmap_layer.addView(blur_imageiew);
			break;
		case LEFT:
			layer_layout.setOrientation(LinearLayout.HORIZONTAL);
			layer.setLayoutParams(new LinearLayout.LayoutParams(w / 2, w));
			editText.setLayoutParams(new LinearLayout.LayoutParams(w / 2, w));
			layer_layout.addView(layer);
			layer_layout.addView(editText);
			/** ----------------------- */
			bitmap_layer.setOrientation(LinearLayout.HORIZONTAL);
			or_imageview
					.setLayoutParams(new LinearLayout.LayoutParams(w / 2, w));
			blur_imageiew.setLayoutParams(new LinearLayout.LayoutParams(w / 2,
					w));
			bitmap_layer.addView(blur_imageiew);
			bitmap_layer.addView(or_imageview);
			break;
		case RIGHT:
			layer_layout.setOrientation(LinearLayout.HORIZONTAL);
			layer.setLayoutParams(new LinearLayout.LayoutParams(w / 2, w));
			editText.setLayoutParams(new LinearLayout.LayoutParams(w / 2, w));
			layer_layout.addView(editText);
			layer_layout.addView(layer);
			/** ----------------------- */
			bitmap_layer.setOrientation(LinearLayout.HORIZONTAL);
			or_imageview
					.setLayoutParams(new LinearLayout.LayoutParams(w / 2, w));
			blur_imageiew.setLayoutParams(new LinearLayout.LayoutParams(w / 2,
					w));
			bitmap_layer.addView(or_imageview);
			bitmap_layer.addView(blur_imageiew);
			break;
		}

	}

	public void changeIndex(LinearLayout result) {
		if (result.getChildCount() <= 1) {
			return;
		}
		View v1 = result.getChildAt(0);
		View v2 = result.getChildAt(1);
		result.removeAllViews();
		result.addView(v2);
		result.addView(v1);
	}

	public class ImageEditeTask extends AsyncTask<Integer, Void, Bitmap> {

		Context context;

		public ImageEditeTask(Context context) {
			this.context = context;
		}

		@Override
		protected Bitmap doInBackground(Integer... arg0) {
			if (now_do == arg0[0]) {
				return null;
			}
			Bitmap bitmap = null;
			switch (arg0[0]) {
			case DO_NOCOLOR:
				bitmap = BitmapUtil.convertGreyImg(editebleBitmap);
				now_do = DO_NOCOLOR;
				break;
			case DO_BLUR:
				bitmap = Blur.fastblur(context, editebleBitmap, radius);
				now_do = DO_BLUR;
				break;
			case DO_WHITE:
				int ww = nowtaskbena.bitmap.getWidth();
				if (nowtaskbena.or == TOP || nowtaskbena.or == DOWN) {
					bitmap = BitmapUtil.drawbitmap(Color.WHITE, ww, ww / 2);
				} else {
					bitmap = BitmapUtil.drawbitmap(Color.WHITE, ww / 2, ww);
				}
				now_do = DO_WHITE;
				break;
			case DO_BLACK:
				int w = nowtaskbena.bitmap.getWidth();
				if (nowtaskbena.or == TOP || nowtaskbena.or == DOWN) {
					bitmap = BitmapUtil.drawbitmap(Color.BLACK, w, w / 2);
				} else {
					bitmap = BitmapUtil.drawbitmap(Color.BLACK, w / 2, w);
				}
				now_do = DO_BLACK;
				break;
			case DO_MODE:
				if (nowtaskbena.or == TOP || nowtaskbena.or == DOWN) {
					bitmap = readBitmap(context, R.drawable.land_720);
				} else {
					bitmap = readBitmap(context, R.drawable.port_720);
				}
				now_do = DO_MODE;
				break;
			}
			return bitmap;
		}

		public Bitmap readBitmap(Context context, int resId) {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inPreferredConfig = Config.RGB_565;
			opts.inPurgeable = true;
			opts.inInputShareable = true;
			InputStream is = context.getResources().openRawResource(resId);
			return BitmapFactory.decodeStream(is, null, opts);
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			if (result != null) {
				nowtaskbena.blurbitmap = result;
			}
			bar.setVisibility(View.GONE);
			blur_imageiew.setImageBitmap(result);
		}
	}

	/** 将原始图片剪裁成2个图片 */
	public class spliteTask extends AsyncTask<TaskBean, Integer, TaskBean> {

		@Override
		protected TaskBean doInBackground(TaskBean... arg0) {
			TaskBean bean = arg0[0];
			Bitmap[] bitmaps = splits(bean.bitmap, bean.or);
			editebleBitmap = bitmaps[0];
			int w = bean.bitmap.getWidth();
			if (bean.or == TOP || bean.or == DOWN) {
				bean.blurbitmap = BitmapUtil.drawbitmap(Color.BLACK, w, w / 2);
			} else {
				bean.blurbitmap = BitmapUtil.drawbitmap(Color.BLACK, w / 2, w);
			}
			bean.originalbitmap = bitmaps[1];
			typefaces.add(Typeface.createFromAsset(assetManager, "Kaiti.ttf"));
			typefaces.add(Typeface.createFromAsset(assetManager,
					"Roboto-Thin.ttf"));
			typefaces.add(Typeface.createFromAsset(assetManager,
					"Chalkduster.ttf"));
			return bean;
		}

		@Override
		protected void onPostExecute(TaskBean result) {
			super.onPostExecute(result);
			nowtaskbena = result;
			layer_layout.findViewWithTag("layer").setBackgroundResource(
					R.drawable.transparent);
			changeIndex(layer_layout);
			bar.setVisibility(View.GONE);
			if (or_imageview == null || blur_imageiew == null) {
				return;
			}
			or_imageview.setImageBitmap(result.originalbitmap);
			blur_imageiew.setImageBitmap(result.blurbitmap);
		}

		public Bitmap split(Bitmap bitmap, int l) {
			int w = bitmap.getWidth();
			int h = bitmap.getHeight();
			Bitmap bitmap2 = null;
			if (l == TOP) {// 上
				bitmap2 = Bitmap.createBitmap(bitmap, 0, 0, w, h / 2, null,
						false);
			} else if (l == DOWN) {// 下
				bitmap2 = Bitmap.createBitmap(bitmap, 0, h / 2, w, h / 2, null,
						false);
			} else if (l == LEFT) {// 左
				bitmap2 = Bitmap.createBitmap(bitmap, 0, 0, w / 2, h, null,
						false);
			} else if (l == RIGHT) {// 右
				bitmap2 = Bitmap.createBitmap(bitmap, w / 2, 0, w / 2, h, null,
						false);
			}
			return bitmap2;
		}

		public Bitmap[] splits(Bitmap bitmap, int l) {
			int w = bitmap.getWidth();
			int h = bitmap.getHeight();
			Bitmap[] bitmaps = new Bitmap[2];
			if (l == TOP) {// 上
				bitmaps[0] = Bitmap.createBitmap(bitmap, 0, 0, w, h / 2, null,
						false);
				bitmaps[1] = Bitmap.createBitmap(bitmap, 0, h / 2, w, h / 2,
						null, false);
			} else if (l == DOWN) {// 下
				bitmaps[0] = Bitmap.createBitmap(bitmap, 0, h / 2, w, h / 2,
						null, false);
				bitmaps[1] = Bitmap.createBitmap(bitmap, 0, 0, w, h / 2, null,
						false);
			} else if (l == LEFT) {// 左
				bitmaps[0] = Bitmap.createBitmap(bitmap, 0, 0, w / 2, h, null,
						false);
				bitmaps[1] = Bitmap.createBitmap(bitmap, w / 2, 0, w / 2, h,
						null, false);
			} else if (l == RIGHT) {// 右
				bitmaps[0] = Bitmap.createBitmap(bitmap, w / 2, 0, w / 2, h,
						null, false);
				bitmaps[1] = Bitmap.createBitmap(bitmap, 0, 0, w / 2, h, null,
						false);
			}
			return bitmaps;
		}
	}

	List<Typeface> typefaces = new ArrayList<Typeface>();
	PopupWindow popupWindow = null;
	CheckBox ck_choseshadow, ck_nochoseshadow;

	@SuppressWarnings("deprecation")
	public void initpopChose() {
		if (popupWindow == null) {
			if (typefaces.size() < 1) {
				return;
			}
			View view = getLayoutInflater().inflate(R.layout.popchose_typeface,
					null);
			ListView listView = (ListView) view.findViewById(R.id.typefaceview);
			listView.setAdapter(new SimpleTypeAdapter(PhotoEditeActivity.this,
					typefaces));
			popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
			popupWindow.setBackgroundDrawable(new BitmapDrawable());
			popupWindow.setFocusable(true);
			ck_choseshadow = (CheckBox) view.findViewById(R.id.ck_choseshadow);
			ck_nochoseshadow = (CheckBox) view
					.findViewById(R.id.ck_nochoseshadow);
			ck_choseshadow
					.setOnCheckedChangeListener(new ck_ChoseShadowListener());
			ck_nochoseshadow
					.setOnCheckedChangeListener(new ck_ChoseShadowListener());
			view.findViewById(R.id.typeface_bglayout).setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							if (popupWindow.isShowing()) {
								popupWindow.dismiss();
							}
						}
					});
			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					changeTypeface((Typeface) arg0.getAdapter().getItem(arg2));
					if (popupWindow.isShowing()) {
						popupWindow.dismiss();
					}
				}
			});
		}
	}

	class ck_ChoseShadowListener implements
			android.widget.CompoundButton.OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			if (editText == null) {
				return;
			}
			if (arg1 && arg0.getId() == R.id.ck_choseshadow) {// 如果设置阴影被选择
				ck_choseshadow.setChecked(true);
				ck_nochoseshadow.setChecked(false);
				saveBool(SP_IS_SHADOW, true);
				editText.setShadowLayer(1, 2, 1, Color.parseColor("#90181818"));
			} else if (arg1 && arg0.getId() == R.id.ck_nochoseshadow) {
				ck_choseshadow.setChecked(false);
				ck_nochoseshadow.setChecked(true);
				saveBool(SP_IS_SHADOW, false);
				editText.setShadowLayer(0, 0, 0, Color.parseColor("#90181818"));
			}
			if (popupWindow.isShowing()) {
				popupWindow.dismiss();
			}

		}

	}

	public void showPop() {
		if (popupWindow == null) {
			initpopChose();
		}
		if (popupWindow.isShowing()) {
			popupWindow.dismiss();
		} else {
			popupWindow.showAtLocation(rootview.getRootView(), Gravity.CENTER,
					0, 0);
		}
	}

	public void changeTypeface(Typeface typeface) {
		EditText editText = (EditText) layer_layout.findViewWithTag("editText");
		if (editText == null) {
			return;
		}
		editText.setTypeface(typeface);
	}

	@Override
	public boolean handleMessage(Message arg0) {
		return false;
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.success) {// 完成
			bar.setVisibility(View.VISIBLE);
			EditText editText = (EditText) layer_layout
					.findViewWithTag("editText");
			if (editText == null || nowtaskbena == null) {
				return;
			}
			Bitmap bitmap = BitmapUtil.getViewBitmap(editText);
			new ImageMergeTask(nowtaskbena).execute(bitmap);
		} else if (view.getId() == R.id.ck_blue) {
			// case R.id.ck_blue:
			// editText.setTextColor(Color.parseColor("#479cff"));
			ColorPickerDialog colorPickerDialog = new ColorPickerDialog(this,
					"自定义颜色", this);
			colorPickerDialog.show();
		}
	}

	public class ImageMergeTask extends AsyncTask<Bitmap, Integer, Bitmap> {

		TaskBean taskBean;

		public ImageMergeTask(TaskBean taskBean) {
			this.taskBean = taskBean;
		}

		@Override
		protected Bitmap doInBackground(Bitmap... arg0) {
			Bitmap water = arg0[0];
			int w = taskBean.originalbitmap.getWidth() > taskBean.originalbitmap
					.getHeight() ? taskBean.originalbitmap.getWidth()
					: taskBean.originalbitmap.getHeight();
			// 等比放大水印
			water = BitmapUtil.zoomBitmap(water, (water.getWidth() * w) / 720,
					(water.getHeight() * w) / 720);
			taskBean.blurbitmap = createBitmap(taskBean.blurbitmap, water);
			Bitmap bitmap = Merge(taskBean.originalbitmap, taskBean.blurbitmap,
					taskBean.or);
			File file = new File(PhotoFileManger.getCapturePath());
			try {
				bitmap.compress(CompressFormat.JPEG, 100, new FileOutputStream(
						file));
				if (new File(taskBean.url).exists()) {
					new File(taskBean.url).delete();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			taskBean.url = file.getPath();
			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			taskBean.recycle();
			bar.setVisibility(View.GONE);
			bitmap_layer.setVisibility(View.GONE);
			layer_layout.setVisibility(View.GONE);
			imageView.setImageBitmap(result);
			Intent in = new Intent(PhotoEditeActivity.this,
					SendImgActivity.class);
			in.putExtra("path", taskBean.url);
			in.putExtra("location", taskBean.or);
			startActivity(in);
			finish();
			super.onPostExecute(result);
		}

		/**
		 * 生成水印图片
		 * 
		 * @param src
		 *            the bitmap object you want proecss
		 * @param watermark
		 *            the water mark above the src
		 * @return return a bitmap object ,if paramter's length is 0,return null
		 */
		public Bitmap createBitmap(Bitmap src, Bitmap watermark) {
			if (src == null) {
				return null;
			}
			int w = src.getWidth();
			int h = src.getHeight();
			int ww = watermark.getWidth();
			int wh = watermark.getHeight();
			// create the new blank bitmap
			Bitmap newb = Bitmap.createBitmap(w, h, Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
			System.out.println("createBitmap水印" + "w:" + newb.getWidth() + ":"
					+ newb.getHeight());
			Canvas cv = new Canvas(newb);
			// draw src into
			cv.drawBitmap(src, 0, 0, null);// 在 0，0坐标开始画入src
			// draw watermark into
			cv.drawBitmap(watermark, w / 2 - ww / 2, h / 2 - wh / 2, null);// 在src的右下角画入水印
			// save all clip
			cv.save(Canvas.ALL_SAVE_FLAG);// 保存
			// store
			cv.restore();// 存储
			return newb;
		}

		public Bitmap Merge(Bitmap originalbitmap, Bitmap blurbitmap, int tl) {
			// 图片的宽度
			int w = originalbitmap.getWidth() > originalbitmap.getHeight() ? originalbitmap
					.getWidth() : originalbitmap.getHeight();
			System.out.println("or:" + originalbitmap.getWidth() + "h:"
					+ originalbitmap.getHeight());
			System.out.println("blur:" + blurbitmap.getWidth() + "h:"
					+ blurbitmap.getHeight());

			// 08-06 22:04:53.260: I/System.out(6242): or:612h:1224
			// 08-06 22:04:53.260: I/System.out(6242): blur:360h:720

			Bitmap bitmap3 = Bitmap.createBitmap(w, w, Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap3);
			switch (tl) {
			case TOP:
				canvas.drawBitmap(blurbitmap, 0, 0, null);
				canvas.drawBitmap(originalbitmap, 0, w / 2, null);
				break;
			case DOWN:
				canvas.drawBitmap(originalbitmap, 0, 0, null);
				canvas.drawBitmap(blurbitmap, 0, w / 2, null);
				break;
			case LEFT:
				canvas.drawBitmap(blurbitmap, 0, 0, null);
				canvas.drawBitmap(originalbitmap, w / 2, 0, null);
				break;
			case RIGHT:
				canvas.drawBitmap(originalbitmap, 0, 0, null);
				canvas.drawBitmap(blurbitmap, w / 2, 0, null);
				break;
			}
			return bitmap3;
		}

	}
	public void dofinish(View v){
		this.finish();
		setResult(RESULT_OK);
		overridePendingTransition(R.anim.roll, R.anim.roll_down);
	}
	@Override
	public void colorChanged(int color) {
		EditText editText = (EditText) layer_layout.findViewWithTag("editText");
		if (editText == null) {
			return;
		}
		System.out.println("color" + color);
		editText.setTextColor(color);
	}

}
