package com.xhx.dsat.pad.view;

import com.xhx.dsat.pad.R;
import com.xhx.dsat.pad.utility.ImageUtil;
import com.xhx.dsat.pad.utility.ZoomImageView;

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
