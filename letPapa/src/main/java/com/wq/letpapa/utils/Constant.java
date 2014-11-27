package com.wq.letpapa.utils;

public interface Constant {

	int CODE_TAKE_HEPAI = 101;
	int FLAG_TAKE_HEPAI = 102;

	int WHAT_PARISE = 10;// 赞
	int WHAT_COMMENTS = 11;// 评论

	int WHAT_SPLITE_SUCCESS = 12;
	int WHAT_SPLITE_FAILD = 13;// 切图失败
	int WHAT_SAVE_SUCCESS = 14;

	String TYPE_PHOTO = "photo";
	String TYPE_MERGE_PHOTO = "mphoto";
	String TYPE_PARISE = "praise";

	String SP_LIST = "islist";// 列表 还是网格显示
	String SP_TYPE = "type";// 筛选类型 男 女 全部
	String SP_TOPIC_TYPE = "topictype";// 筛选合拍或者参与合拍
	String SP_IS_SHADOW = "isshadow";// 是否显示阴影

	String SP_LASTTIME = "lasttime";// 最后查看与我相关的时间

	int TYPE_ALL = 0;
	int TYPE_MAN = 1;
	int TYPE_WOMEN = 2;

	// 照片覆盖图层的位置
	final int TOP = 4;
	final int DOWN = 2;
	final int LEFT = 1;
	final int RIGHT = 3;
	// String IP="http://hp.ccmima.com";
	String IP = "http://hepai.dianjin169.com";
	/** 登录 */
	String LOGIN_URL = IP + "/api.php?m=Index&a=login";
	/** 发布合拍 */
	String SEND_PICTURE = IP + "/api.php?m=Index&a=photo";
	/** 参与合拍 */
	String SEND_HEPAI_PICTURE = IP + "/api.php?m=Index&a=mphoto";
	/** 最新发布 */
	String NEW_HEPAI_LIST = IP + "/api.php?m=Index&a=photo_list";
	/** 最佳合拍 */
	String BEST_HEPAI_LIST = IP + "/api.php?m=Index&a=mphoto_list";
	/** 评论列表 */
	String CONMMITS_LIST = IP + "/api.php?m=Index&a=comments_list";
	/** 发送评论 */
	String SEND_CONMMIT = IP + "/api.php?m=Index&a=comments";
	/** 赞 */
	String SEND_PRAISE = IP + "/api.php?m=Index&a=praise";
	/** 与我相关 提醒 **/
	String ABOUT_ME = IP + "/api.php?m=Index&a=at_list";
	/*** 主题 */
	String THEME_URL = IP + "/api.php?m=Index&a=topic_list";
	/** 参与合拍详情 */
	String DETAIL_MERGRPHOTOD_URL = IP + "/api.php?m=Index&a=mphoto_show";
	/** 发布合拍详情 */
	String DETAIL_NEWPHOTOD_URL = IP + "/api.php?m=Index&a=photo_show";
	/** 用户发布的信息 **/
	String USER_PHOTO_DETAIL_URL = IP + "/api.php?m=Index&a=user_x_list";
	/** 主题的合拍列表 **/
	String THEME_MERGEPHOTO_LIST = IP + "/api.php?m=Index&a=topic_mphoto_list";
	/** 主题的发布列表 **/
	String THEME_PHOTO_LIST = IP + "/api.php?m=Index&a=topic_photo_list";
	/** 举报意见反馈 **/
	String REPORT_URL = IP + "/api.php?m=Index&a=report";
	/** 删除合拍 **/
	String DELETE_URL = IP + "/api.php?m=Index&a=user_x_delete";
	/** 版本更新 */
	String UPDATE_URL = IP + "/api.php?m=Index&a=update";

	
	String URL_DOWNLOAD=IP+"/down";
	/**** share **/
	String WX_AppID = "wx7340075a55df74b5";
	String WX_AppSecret = "cbad252ff529f7906f97c97a49d65a2d";
}
