package com.xhx.yiqi.pad.view;

import com.xhx.yiqi.pad.R;
import com.xhx.yiqi.pad.utility.ImageUtil;
import com.xhx.yiqi.pad.utility.ZoomImageView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Toast;

public class ZoomImageActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zoom_image);
		ZoomImageView view = (ZoomImageView) findViewById(R.id.zoomImageView);
		Intent intent = getIntent();
		String imagePath =  intent.getStringExtra("imagePath");
		Bitmap bitmap = ImageUtil.GetImage(imagePath);
		if(bitmap != null){
			view.setImage(bitmap);
		}else{
			Toast.makeText(ZoomImageActivity.this,"Œﬁ»Œ∫Œ’’∆¨£°",Toast.LENGTH_SHORT).show();
		}
	}
	
}
