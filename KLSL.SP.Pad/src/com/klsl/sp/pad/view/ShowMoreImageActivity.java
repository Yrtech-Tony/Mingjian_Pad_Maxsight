package com.klsl.sp.pad.view;

import com.klsl.sp.pad.adapter.ImageAdapter;
import com.klsl.sp.pad.utility.CustomGallery;
import com.klsl.sp.pad.R;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;
import android.widget.Toast;

public class ShowMoreImageActivity extends Activity {

	private String imagePath;
	private String[] pictures;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showmoreimage);
		Intent intent = getIntent();
		//图片所处的路径
		imagePath =  intent.getStringExtra("imagePath");
		//路径下包含的图片名字
		pictures = intent.getStringArrayExtra("pictures");
		
		Integer index = intent.getIntExtra("index", 0);
		
		final CustomGallery gallery=(CustomGallery) findViewById(R.id.gallery);
		final TextView textview = (TextView) findViewById(R.id.textName);
        gallery.setAdapter(new ImageAdapter(this,imagePath,pictures));
        gallery.setSelection(index);
        gallery.setOnItemSelectedListener(new OnItemSelectedListener(){
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				if (textview != null) {
					textview.setText("第 "+ (arg2+1) +" 张/共 " + gallery.getCount() + " 张");
				}
			}
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
        });
        gallery.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if (textview != null) {
					Intent intent = new Intent();
			        intent.setClass(ShowMoreImageActivity.this, ZoomImageActivity.class);
					intent.putExtra("imagePath", imagePath +"/"+ pictures[arg2]);
					startActivity(intent);
				}
			}
		});
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if( imagePath == null || pictures.length == 0){
        	Toast.makeText(ShowMoreImageActivity.this,"无任何照片！",Toast.LENGTH_SHORT).show();
        	ShowMoreImageActivity.this.finish();
        }
	}

}
