package com.wq.letpapa.cache;

import java.io.File;
import java.io.IOException;

import com.wq.letpapa.utils.MD5;

import android.net.Uri;
import android.os.Environment;

public class PhotoFileManger {

	public static final String FOLDER_NAME = "letpp";
	public static final String SHARE_FOLDER_NAME = "share";

	// public static String getSavePhotDir() {
	// return getSavePath("");
	// }

	public static String getCapturePath() {
		File path = Environment.getExternalStorageDirectory();
		File file = new File(path, FOLDER_NAME + "/temp/"
				+ System.currentTimeMillis() + ".jpeg");
		if (!file.exists()) {
			file.getParentFile().mkdirs();
		}
		return file.getAbsolutePath();
	}

	public static void delteALLTemp() {
		File path = Environment.getExternalStorageDirectory();
		File file = new File(path, FOLDER_NAME + "/temp/");
		if(file.exists()&&file.list().length>2){
			for (File f : file.listFiles()) {
				System.out.println("删除"+f.getAbsolutePath());
				f.delete();
			}
		}
	}

	public static String getSavepath() {
		File path = Environment.getExternalStorageDirectory();
		File file = new File(path, FOLDER_NAME + "/share/share_"
				+ System.currentTimeMillis() + ".jpeg");
		if (!file.exists()) {
			file.getParentFile().mkdirs();
		}
		return file.getAbsolutePath();
	}

	public static String getSharePath(String orpath) {
		File path = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File file = new File(path, "/合拍相册/share_" + MD5.getMD5(orpath)
				+ ".jpeg");
		if (!file.exists()) {
			file.getParentFile().mkdirs();
		}
		return file.getAbsolutePath();
	}

	public static String getSavePath(String orpath) {
		File path = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File file = new File(path, "合拍相册/" + MD5.getMD5(orpath) + ".jpeg");
		if (!file.exists()) {
			file.getParentFile().mkdirs();
		}
		return file.getAbsolutePath();
	}

	// public static Uri getPhotoUri() {
	// String path = getSavePhotDir();
	// File file = new File(path);
	// if (file.exists()) {
	// try {
	// file.createNewFile();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	// return Uri.fromFile(file);
	// }

	public static String getDICM() {
		File path = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
		File file = new File(path, System.currentTimeMillis() + "_temp.jpg");
		file.getParentFile().mkdirs();
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file.getAbsolutePath();
	}

//	public static String getTemp() {
//		File path = Environment
//				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//		File file = new File(path, FOLDER_NAME + "/."
//				+ System.currentTimeMillis() + "_temp.jpg");
//		file.getParentFile().mkdirs();
//		if (file.exists()) {
//			file.delete();
//		}
//		try {
//			file.createNewFile();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return file.getAbsolutePath();
//	}

	/** apk的安装更新文件路径 */
	public static String getAPKdir() {
		File SD_ROOT = Environment.getExternalStorageDirectory();
		File file = new File(SD_ROOT, FOLDER_NAME + "/apk/laiba.apk");
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file.getAbsolutePath();
	}

}
