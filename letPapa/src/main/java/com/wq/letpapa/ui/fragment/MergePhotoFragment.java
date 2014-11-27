package com.wq.letpapa.ui.fragment;

import java.util.ArrayList;

import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshBase.State;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.handmark.pulltorefresh.library.extras.SoundPullEventListener;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.wq.letpapa.R;
import com.wq.letpapa.adapter.XphotAdapter;
import com.wq.letpapa.bean.XPhotos;
import com.wq.letpapa.ui.base.BaseFragment;
import com.wq.letpapa.utils.JsonUtil;

public class MergePhotoFragment extends BaseFragment implements Callback {

	Handler handler;

	PullToRefreshGridView listView;
	XphotAdapter adapter;
	ProgressBar progressBar;
	int nowpage = 1;// 当前页数
	int allcount = 0;// 一共多少条
	int allpage = 0;// 中共多少页

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		beans = new ArrayList<XPhotos>();
		loadCache();
	}
	public void loadCache() {
		JSONObject object = getCache(getClass());
		if (object != null) {
			JsonUtil.parseXPhotosBeans(beans, object);
			allpage = JsonUtil.getPagenum(object);
		}
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fm_mergephoto_layout, null);
		handler = new Handler(this);
		return view;
	}
	ArrayList<XPhotos> beans;
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		listView = (PullToRefreshGridView) view
				.findViewById(R.id.pull_refresh_list);
		progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
		adapter = new XphotAdapter(getActivity(), beans,handler);
		listView.setAdapter(adapter);
		listView.getRefreshableView().setOnScrollListener(
				new PauseOnScrollListener(getImageLoader(), true, true));
		listView.setOnRefreshListener(new OnRefreshListener2<GridView>() {
			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<GridView> refreshView) {
				nowpage = 1;
				refrashDate();
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<GridView> refreshView) {
				if (nowpage == allpage) {
					listView.onRefreshComplete();
					Toast.makeText(getActivity(), "已经到底了", 0).show();
					return;
				}
				nowpage++;
				refrashDate();

			}
		});
		SoundPullEventListener<GridView> soundListener = new SoundPullEventListener<GridView>(
				getActivity());
		soundListener.addSoundEvent(State.RESET, R.raw.feed_pull_refresh);
		listView.setOnPullEventListener(soundListener);
		progressBar.setVisibility(View.VISIBLE);
		nowpage=1;
		refrashDate();
	}

	/***
	 * page 否 当前页码 默认1 pagesize 否 每页显示条数 默认5 sex 否 性别 lon 否 经度 lat 否 纬度
	 */
	public void refrashDate() {
		AjaxParams params = new AjaxParams();
		params.put("page", nowpage + "");
		params.put("pagesize", 20 + "");
		params.put("user_id", getUid());
		if (getValue(SP_TYPE, TYPE_ALL) > 0) {
			if (getValue(SP_TYPE, TYPE_ALL) == TYPE_MAN) {
				params.put("sex", "1");
			} else {
				params.put("sex", "2");
			}
		}
		sendGet(BEST_HEPAI_LIST + "&" + params.getParamString(), null);
	}

	/**
	 * 赞
	 */
	public void sendParise(long id,String xuid) {
		AjaxParams ajaxParams = new AjaxParams();
		ajaxParams.put("uid", getUid());
		ajaxParams.put("type", "mphoto");
		ajaxParams.put("xid", id + "");
		ajaxParams.put("xuser_id", xuid);
		sendPost(SEND_PRAISE ,ajaxParams);
	}

	@Override
	public void onResume() {
		super.onResume();
	}
	@Override
	public void onSuccess(String url, Object obj) {
		super.onSuccess(url, obj);
		if (url.startsWith(BEST_HEPAI_LIST)) {
			listView.onRefreshComplete();
			progressBar.setVisibility(View.GONE);
			if (JsonUtil.isSuccess(obj)) {
				if (nowpage == 1) {
					beans.clear();
				}
				try {
					JSONObject object = new JSONObject(obj.toString());
					JsonUtil.parseXPhotosBeans(beans, object);
					allcount = JsonUtil.getCount(object);
					allpage = JsonUtil.getPagenum(object);
					if(nowpage==1){
						// 将最数据保存到缓存中
						addCache(getClass(), object);
					}
					adapter.notifyDataSetChanged();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} else if (url.startsWith(SEND_PRAISE)) {
			if (JsonUtil.isSuccess(obj)) {
				
			} else {
				t(JsonUtil.getMessage(obj));
			}
		}
	}
	public void typeChange() {
		nowpage = 1;
		refrashDate();
	}

	@Override
	public void onError(String url, String msg) {
		super.onError(url, msg);
		t(msg);
		listView.onRefreshComplete();
	}
	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case WHAT_PARISE:
			XPhotos bean = (XPhotos) msg.obj;
			sendParise(bean.getId(),bean.getUser().getUid());
			break;
		default:
			break;
		}
		return false;
	}

}
