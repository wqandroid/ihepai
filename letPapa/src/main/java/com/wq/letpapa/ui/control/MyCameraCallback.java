package com.wq.letpapa.ui.control;

import android.hardware.Camera.PictureCallback;

public interface MyCameraCallback {

	void onPause();
	boolean onResume();
	void takePicture(PictureCallback pictureCallback);
}
