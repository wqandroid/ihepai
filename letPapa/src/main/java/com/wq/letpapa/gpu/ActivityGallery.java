/*
 * Copyright (C) 2012 CyberAgent
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wq.letpapa.gpu;

import java.io.File;

import jp.co.cyberagent.android.gpuimage.GPUImage.OnPictureSaveListener;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageView;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.wq.letpapa.R;
import com.wq.letpapa.cache.PhotoFileManger;
import com.wq.letpapa.gpu.GPUImageFilterTools.FilterType;
import com.wq.letpapa.ui.SendImgActivity;
import com.wq.letpapa.ui.base.BaseActivity;
import com.wq.letpapa.utils.BitmapUtil;
import com.wq.letpapa.utils.DensityUtil;

public class ActivityGallery extends BaseActivity implements OnClickListener {
	private GPUImageFilter mFilter;
	private GPUImageView mGPUImageView;
	FrameLayout gpu_parent;
	LinearLayout hz_layout, bottom_layout;
	Bitmap cubit;

	Handler handle = new Handler();
	ImageView iv_chosefilterover;
	int doflag, topic_id, location;
	String path;
	long photo_id;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);
		gpu_parent = (FrameLayout) findViewById(R.id.gpu_parent);
		hz_layout = (LinearLayout) findViewById(R.id.hz_layout);
		bottom_layout = (LinearLayout) findViewById(R.id.bottom_layout);
		findViewById(R.id.iv_chosefilterover).setOnClickListener(this);
		;
		setLayout();
		mGPUImageView = new GPUImageView(this);
		gpu_parent.addView(mGPUImageView);
		path = getIntent().getStringExtra("path");
		mGPUImageView.setImage(new File(path));

		doflag = getIntent().getIntExtra("doflag", FLAG_TAKE_HEPAI);
		topic_id = getIntent().getIntExtra("topic_id", FLAG_TAKE_HEPAI);
		location = getIntent().getIntExtra("location", FLAG_TAKE_HEPAI);
		photo_id = getIntent().getLongExtra("photo_id", 0);

		cubit = BitmapUtil.zoomBitmap(BitmapFactory.decodeFile(path),
				DensityUtil.dip2px(this, 64), DensityUtil.dip2px(this, 64));
	}

	public void setLayout() {
		int scw = getScreenWidth(this);
		LayoutParams params = new LayoutParams(scw, scw);
		gpu_parent.setLayoutParams(params);
	}

	class GPURunable implements Runnable {
		@Override
		public void run() {
			int n = GPUImageFilterTools.filters.filters.size();
			for (int i = 0; i < n; i++) {
				View v = getLayoutInflater().inflate(R.layout.gpu_item_view,
						null);
				GPUImageView gpuImageView = (GPUImageView) v
						.findViewById(R.id.gpuimage);
				gpuImageView.setImage(cubit);

				FilterType type = GPUImageFilterTools.filters.filters.get(i);
				if (type != FilterType.NORMORE) {
					gpuImageView.setFilter(GPUImageFilterTools
							.createFilterForType(mContext, type));
					gpuImageView.requestRender();
				}
				v.setOnClickListener(new ChoseFilterClick(type));
				hz_layout.addView(v);
			}
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		handle.post(new GPURunable());
	}

	class ChoseFilterClick implements OnClickListener {
		FilterType type;

		public ChoseFilterClick(FilterType type) {
			this.type = type;
		}

		@Override
		public void onClick(View v) {
			switchFilterTo(GPUImageFilterTools.createFilterForType(mContext,
					type));
			mGPUImageView.requestRender();
		}

	}

	@Override
	public void onClick(final View v) {
		switch (v.getId()) {
		case R.id.iv_chosefilterover:
			saveImage();
			break;
		default:
			break;
		}
	}

	private void saveImage() {
		path = PhotoFileManger.getCapturePath();
		mGPUImageView.saveToPictures("GPUImage", path,
				new OnPictureSaveListener() {
					@Override
					public void onPictureSaved(String path, Uri uri) {
						Intent in = new Intent(ActivityGallery.this,
								SendImgActivity.class);
						in.putExtra("location", location);
						in.putExtra("doflag", FLAG_TAKE_HEPAI);
						in.putExtra("photo_id", photo_id);
						in.putExtra("topic_id", topic_id);
						in.putExtra("path", path);
						startActivity(in);
						finish();
					}
				});
	}

	private void switchFilterTo(final GPUImageFilter filter) {
		if (mFilter == null
				|| (filter != null && !mFilter.getClass().equals(
						filter.getClass()))) {
			mFilter = filter;
			mGPUImageView.setFilter(mFilter);
		}
	}

}
