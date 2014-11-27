package com.wq.letpapa.ui.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.wq.letpapa.R;
import com.wq.letpapa.bean.DataFactory;
import com.wq.letpapa.bean.ThemeTopic;
import com.wq.letpapa.ui.TopicPhotoListActivity;
import com.wq.letpapa.ui.base.BaseFragment;
import com.wq.letpapa.utils.DensityUtil;

public class ThemeTopicFragment extends BaseFragment {

	ArrayList<ThemeTopic> ThemeTopic = DataFactory.getThemeTopicLists();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// sendGet(THEME_URL, null);
		return inflater.inflate(R.layout.fm_themmtopic_layout, null);
	}

	LinearLayout rootview;
	int sw;
	int sh;

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		rootview = (LinearLayout) view.findViewById(R.id.rootview);
		sw = getScreenWidth(getActivity())
				- DensityUtil.dip2px(getActivity(), 16);
		sh = sw / 2 - DensityUtil.dip2px(getActivity(), 4);
	}

	@Override
	public void onSuccess(String url, Object obj) {
		super.onSuccess(url, obj);
		try {
			JSONObject object = new JSONObject(obj.toString());
			JSONArray array = object.getJSONArray("list");
			for (int i = 0; i < array.length(); i++) {
				ThemeTopic.add(new ThemeTopic(array.getJSONObject(i)));
			}
			// loadview();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/***
	 * 动态生产view
	 */
	public void loadview() {
		int n = ThemeTopic.size();
		HashMap<Integer, ArrayList<ThemeTopic>> map = new HashMap<Integer, ArrayList<ThemeTopic>>();
		for (int i = 0; i < n; i++) {
			ThemeTopic topic = ThemeTopic.get(i);
			if (map.containsKey(topic.getWeight())) {
				map.get(topic.getWeight()).add(topic);
			} else {
				ArrayList<ThemeTopic> ThemeTopic = new ArrayList<ThemeTopic>();
				ThemeTopic.add(topic);
				map.put(topic.getWeight(), ThemeTopic);
			}
		}

		Set<Integer> keys = map.keySet();
		for (Integer integer : keys) {
			ArrayList<ThemeTopic> ThemeTopic = map.get(integer);
			if (ThemeTopic.size() > 1) {
				LinearLayout layout = new LinearLayout(getActivity());
				layout.setOrientation(LinearLayout.HORIZONTAL);
				layout.addView(creatview(ThemeTopic.get(0)));
				TextView tv = new TextView(getActivity());
				LayoutParams param = new LayoutParams(DensityUtil.dip2px(
						getActivity(), 8), 10);
				tv.setLayoutParams(param);
				layout.addView(tv);
				layout.addView(creatview(ThemeTopic.get(1)));
				rootview.addView(layout);
			} else {
				rootview.addView(creatview(ThemeTopic.get(0)));
			}
		}

	}

	public LinearLayout creatview(ThemeTopic themeTopic) {
		int weight = themeTopic.getWeight();
		LinearLayout layout = new LinearLayout(getActivity());
		layout.setOrientation(LinearLayout.HORIZONTAL);
		LayoutParams layoutParams = null;
		if (weight == 1) {
			layoutParams = new LayoutParams(sw, sh);
		} else if (weight == 2) {
			layoutParams = new LayoutParams(sh, sh);
		} else if (weight == 3) {
			layoutParams = new LayoutParams(sw, sh / 2);
		} else if (weight == 4) {
			layoutParams = new LayoutParams(sh, sh / 2);
		}
		layoutParams.setMargins(0, 0, 0, DensityUtil.dip2px(getActivity(), 8));
		layout.setLayoutParams(layoutParams);
		layout.setGravity(Gravity.CENTER);
		layout.setBackgroundColor(Color.parseColor("#" + themeTopic.getColor()));
		TextView textView = new TextView(getActivity());
		textView.setText("#" + themeTopic.getTitle() + "#");
		if (themeTopic.getWeight() == 2 || themeTopic.getWeight() == 4) {
			if (themeTopic.getTitle().length() > 9) {
				textView.setText(18);
			}
		} else {
			textView.setTextSize(28);
		}
		textView.setTextColor(Color.WHITE);
		layout.addView(textView);
		return layout;
	}

}
