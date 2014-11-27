package com.wq.letpapa.ui;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.ScrollView;

import com.wq.letpapa.R;
import com.wq.letpapa.customview.StickyScrollView;
import com.wq.letpapa.customview.WQListView;
import com.wq.letpapa.ui.base.BaseActivity;
import com.wq.letpapa.utils.DensityUtil;

public class UserPageActivity extends BaseActivity implements
		OnCheckedChangeListener {

	// private PagerSlidingTabStrip tabs;
	// private MyPageView pager;
	// private MyPagerAdapter adapter;
	StickyScrollView scrollView;

	List<Fragment> fragments = new ArrayList<Fragment>();

	String[] datas = new String[] { "test", "wangqiong", "BaseFragment",
			"onCreateView", "letpapa", "ProgressBar", "ListView",
			"fm_mergephoto_layout", "findViewById", "LayoutInflater",
			"fm_mergephoto_layout", "findViewById", "LayoutInflater",
			"fm_mergephoto_layout", "findViewById", "LayoutInflater",
			"fm_mergephoto_layout", "findViewById", "LayoutInflater",
			"ViewGroup", "container" };

	ArrayList<String> lists = new ArrayList<String>();
	ArrayAdapter<String> adapter;
	ProgressBar progressBar;
	WQListView listView;
	RadioButton rb1, rb2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_user_page_layout);
		scrollView = (StickyScrollView) findViewById(R.id.ScrollView);
		scrollView.setScrollContainer(true);
		scrollView.setOverScrollMode(ScrollView.OVER_SCROLL_NEVER);
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		rb1 = (RadioButton) findViewById(R.id.rb1);
		rb2 = (RadioButton) findViewById(R.id.rb2);
		rb1.setOnCheckedChangeListener(this);
		rb2.setOnCheckedChangeListener(this);
		// tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		// tabs.setShouldExpand(true);
		// pager = (MyPageView) findViewById(R.id.pager);
		// fragments.add(new MergePhotoFragment());
		// fragments.add(new NewPhotoFragment());
		// adapter = new MyPagerAdapter(getSupportFragmentManager());
		// pager.setAdapter(adapter);
		// final int pageMargin = (int) TypedValue.applyDimension(
		// TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
		// .getDisplayMetrics());
		// pager.setPageMargin(pageMargin);
		// tabs.setViewPager(pager);
		// tabs.setOnPageChangeListener(this);

		 listView = (WQListView) findViewById(R.id.wQListView1);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1, lists);
		listView.setAdapter(adapter);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				for (String string : datas) {
					lists.add(string);
				}
				adapter.notifyDataSetChanged();
				hideView(progressBar);
			}
		}, 3000);

		scrollView.smoothScrollTo(0, DensityUtil.dip2px(this, 200));
	}

	@Override
	public void onCheckedChanged(CompoundButton button, boolean ischecked) {
		if (ischecked) {
			button.setTextColor(R.color.theme_blue);
			switch (button.getId()) {
			case R.id.rb1:
				rb2.setChecked(false);
				showView(listView);
				rb2.setTextColor(R.color.theme_black);
				break;
			case R.id.rb2:
				hideView(listView);
				rb1.setChecked(false);
				rb1.setTextColor(R.color.theme_black);
				break;
			}
		}

	}

}
