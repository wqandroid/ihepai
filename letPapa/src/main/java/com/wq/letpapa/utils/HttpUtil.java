package com.wq.letpapa.utils;

import java.io.File;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import com.wq.letpapa.utils.log.Trace;

public class HttpUtil {

	/**
	 * 不需要回调网络请求
	 * @param url
	 * @param params
	 */
	public static void sendPost(String url, AjaxParams params) {
		sendhttp(url,null, true, params, null);
	}
	/**
	 * 不需要回调网络请求
	 * 
	 * @param url
	 * @param params
	 */
	public static void sendGet(String url, AjaxParams params) {
		sendhttp(url, null,false, params, null);
	}

	public static void sendPost(String url,String tag, AjaxParams params, LoadCallBack back) {
		sendhttp(url,tag,true, params, back);
	}
	
	public static void sendPost(String url, AjaxParams params, LoadCallBack back) {
		sendhttp(url, null,true, params, back);
	}

	public static void sendGet(String url, AjaxParams params, LoadCallBack back) {
		sendhttp(url, null,false, params, back);
	}

	
	public static void sendGet(String url, String tag,AjaxParams params, LoadCallBack back) {
		sendhttp(url,tag,false, params, back);
	}
	
	public static void sendhttp(final String url, final String tag,boolean isPost,
			AjaxParams params, final LoadCallBack loadCallBack) {
		if (params != null) {
			params.put("device", "android");
			Trace.i("hepai", "请求:url" + url + "&" + params.getParamString());
		} else {
			Trace.i("hepai", "请求:url" + url);
		}
		FinalHttp finalHttp = new FinalHttp();
		finalHttp.configTimeout(5000);
		finalHttp.configRequestExecutionRetryCount(2);
		if (loadCallBack == null) {
			if (isPost) {
				finalHttp.post(url, params, new AjaxCallBack<Object>() {
				});
			} else {
				finalHttp.get(url, params, new AjaxCallBack<Object>() {
					@Override
					public void onSuccess(Object t) {
						super.onSuccess(t);
					}
				});
			}
		} else {
			if (isPost) {
				finalHttp.post(url, params, new AjaxCallBack<Object>() {
					@Override
					public void onSuccess(Object t) {
						super.onSuccess(t);
						if(tag==null){
							loadCallBack.onSuccess(url, t);
						}else{
							loadCallBack.onSuccess(url, tag,t);
						}
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						super.onFailure(t, errorNo, strMsg);
						loadCallBack.onError(url, strMsg);
					}

					@Override
					public void onLoading(long count, long current) {
						super.onLoading(count, current);
						loadCallBack.onLoading(url, count, current);
					}

					@Override
					public void onStart() {
						super.onStart();
						loadCallBack.onStar(url);
					}
				});
			} else {
				finalHttp.get(url, params, new AjaxCallBack<Object>() {
					@Override
					public void onSuccess(Object t) {
						super.onSuccess(t);
						if(tag==null){
							loadCallBack.onSuccess(url, t);
						}else{
							loadCallBack.onSuccess(url, tag,t);
						}
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						super.onFailure(t, errorNo, strMsg);
						loadCallBack.onError(url, strMsg);
					}

					@Override
					public void onStart() {
						super.onStart();
						loadCallBack.onStar(url);
					}
				});
			}
		}
	}

	/**
	 * 下载文件
	 * 
	 * @param url
	 *            文件网络路径
	 * @param filepath
	 *            本地下载路径
	 * @param loadCallBack
	 */
	public static void downLoadFile(final String url, String filepath,
			final LoadCallBack loadCallBack) {
		FinalHttp finalHttp = new FinalHttp();
		finalHttp.download(url, filepath, new AjaxCallBack<File>() {
			@Override
			public void onSuccess(File t) {
				loadCallBack.onSuccess(Constant.URL_DOWNLOAD, t.getAbsolutePath());
				super.onSuccess(t);
			}

			@Override
			public void onLoading(long count, long current) {
				super.onLoading(count, current);
				loadCallBack.onLoading(Constant.URL_DOWNLOAD, count, current);
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				loadCallBack.onError(Constant.URL_DOWNLOAD, strMsg);
			}

		});

	}

	public abstract interface LoadCallBack {
		void onStar(String url);

		void onSuccess(String url, String tag,Object obj);
		
		void onSuccess(String url, Object obj);

		void onLoading(String url, long count, long current);

		void onError(String url, String msg);
	}

}
